package algorithm;

import java.io.IOException;
import java.util.*;

import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;
import entities.Bat;

public class BatAlgorithm {

    // Recommended size is from 10 to 50
    private static final int SWARM_SIZE = 50;

    // LOUDNESS, initial "Motivation" that triggers bat wants to explore.
    private static final double LOUDNESS = 0.90;

    // PULSE_EMISSION, 1 is when bat is same location as the target, 0.98 is pretty close to the target
    // Target pulse emission, seems not very useful in case of TSP.
    private static final double PULSE_EMISSION = 0.90;

    // Alpha is a Rate of Loudness decrement each best Solution in main iteration
    // Higher Alpha let bats more actively contribute their solution
    // Low Alpha may cause longer iteration
    private static final double ALPHA = 0.9;

    // Gamma is a Rate of Pulse-Emission increment..
    // Higher Gamma does not result better solution
    // Low Gamma cause longer iteration, if necessary (for instance demonstration).
    private static final double GAMMA = 0.98;


    private static final Random rand = new Random();

    //  Threshold was for debug termination criteria
    // private static final double THRESHOLD = 0.05;


    public static void main(String[] args) {
        try {
            Dataset dataset = readDataSet(args[0]);
            ArrayList<Evaluable> batSwarm = new ArrayList<>();

            initialBatSwarm(batSwarm, dataset);
            long start = System.currentTimeMillis();
            iterationsBatAlgorithm(batSwarm, dataset);
            long stop = System.currentTimeMillis();
            System.out.println("Bat Algorithm finished took " + (stop - start) / 1000 + " s");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void iterationsBatAlgorithm(ArrayList<Evaluable> swarm, Dataset set) {
        Fitness fitness = new Fitness(set);
        fitness.evaluate(swarm);
        sortSwarm(swarm);

        //A random bat is picked as best Bat.
        Evaluable bestSwarmFitness = fitness.getAbsolutBest();
        int bestFitness = bestSwarmFitness.getFitness();
        Bat bestBat = (Bat) swarm.get(rand.nextInt(SWARM_SIZE));

        //Iteration Counter
        int i = 1;
        do {
            //begin Algorithm
            for (Evaluable t : swarm) {     //  for each bat in the population do...
                Bat b = (Bat) t;
                if (!b.isBest()) {  // One of the Optimization, only not best ones are iterated.
                    //Generate new solution...
                    int hamming = hammingDistance(b.getPath(), bestSwarmFitness.getPath());
                    if (hamming != 0) {                     //  when there is/are any different between two route...
                        b.setV(rand.nextInt(hamming) + 1);  //  set velocity of a bat to random distance of [1, hamming]
                    } else {
                        //  A flying bat cannot stop!
                        //  It's velocity minimum is 1, an indicator that bat never stop flying.
                        b.setV(1);
                        // Some tests results velocity of 0 will be quickly over written in next iteration.
                        // means velocity of 0 will not cause flying bat falling down to the ground.
                    }
                    //according to their velocity set new route
                    newHeuristicSolution(b, set.getSize(), i, fitness);

                    if (rand.nextDouble() > b.getR()) {     //  random bat follows swarm best bat.
                        int topSolutionOf = rand.nextInt(SWARM_SIZE / 10);  //  Tip: This number must be higher then swarm size otherwise causes indexOutOfBoundException.
                        ArrayList<Integer> randomBest = swarm.get(topSolutionOf).getPath();     //  Select one solution among the best Ones

                        hamming = hammingDistance(b.getPath(), randomBest);
                        if (hamming != 0) {
                            b.setV(rand.nextInt(hamming) + 1);
                        } else {
                            b.setV(1);
                        }
                        newHeuristicSolution(b, set.getSize(), i, fitness);

                        //every bat b are closing into random solution route
                    }
                    double randomA = 0 + (LOUDNESS - 0) * rand.nextDouble();    //  random of [0, A0]
                    if (randomA < b.getA() && fitness.evaluate(b, i).getFitness() <= bestFitness) {
                        if (fitness.evaluate(b, i).getFitness() < bestFitness) {
                            bestFitness = fitness.evaluate(b, i).getFitness();  //  meta information for evaluation
                        }
                        //bestSwarmFitness.getFitness();                        //  for record
                        b.setA(ALPHA * b.getA());                               //  new solution, decrease loudness
                        b.setR(PULSE_EMISSION * (1 - Math.exp(-GAMMA * i)));    //  new solution, increase pulse
                        bestBat = b;                                            //  after attribute change record new best one
                        bestSwarmFitness = fitness.evaluate(bestBat, i);        //  record current best Fitness
                    }
                }
            }
            //fitness.evaluate(swarm);  //it's fun to look at.
            sortSwarm(swarm);
            i++;
        } while (bestBat.getR() < PULSE_EMISSION);
        fitness.finish();
    }

    private static Dataset readDataSet(String pathToTestData) throws IOException {
        return Parser.read(pathToTestData);
    }

    private static void initialBatSwarm(ArrayList<Evaluable> swarm, Dataset set) {
        ArrayList<Integer> allCityNodes = new ArrayList<>();
        for (Node n : set.getNodes()) {
            allCityNodes.add(n.getId());
        }
        for (int i = 0; i < SWARM_SIZE; i++) {
            shuffleArray(allCityNodes);
            swarm.add(new Bat(allCityNodes));
        }
    }

    private static void shuffleArray(ArrayList<Integer> ar) {
        long seed = System.nanoTime();
        Collections.shuffle(ar, new Random(seed));
    }

    private static void sortSwarm(ArrayList<Evaluable> swarm) {
        Collections.sort(swarm, new Comparator<Evaluable>() {
            @Override
            public int compare(Evaluable o1, Evaluable o2) {
                return Integer.compare(o1.getFitness(), o2.getFitness());
            }
        });
    }

    private static void newHeuristicSolution(Bat b, double n, int iterationBA, Fitness fitness) {
        // b each bat
        // n number of Nodes
        // i iteration
        // f current fitness

        if (b.getV() < n / 2.0) {
            //If the bat b is closer to global best
            //In this case bat b have to search in the solution space
            twoOptHeuristic(b.getPath(), b.getV(), fitness, iterationBA);   // exploit
        } else {
            //If the bat b is presumably far away from global best or even from the swarm
            threeOptHeuristicRandom(b.getPath(), fitness, b.getV());    // explore the total random nodes
            threeOptHeuristicIteration(b.getPath(), fitness); // ...exploit
        }
    }

    private static int hammingDistance(ArrayList<Integer> route, ArrayList<Integer> bestRoute) {
        int counter = 0;
        //here begins ruling operation exp.
        //[A,B,C,D] vs [D,C,A,B] to [A,B,C,D] vs [A,B,D,C]
        ArrayList<Integer> compare = new ArrayList<>(bestRoute);
        for (int i = 0; i < route.size(); i++) {
            if (bestRoute.get(0) == route.get(i)) {
                if (0 < i) {
                    compare.clear();
                    compare.addAll(route.subList(i, route.size()));
                    compare.addAll(route.subList(0, i));
                }
            }
        }
        for (int i = 0; i < bestRoute.size(); i++) {
            if (compare.get(i).equals(bestRoute.get(i))) {
                counter++;
            }
        }
        return counter;
    }

    private static void twoOptHeuristic(ArrayList<Integer> route, double iterations, Fitness fitness, int iterationBA) {
        int iter = 0;
        int overall = 0;
        int swaps = 0;
        ArrayList<Evaluable> bestWays = new ArrayList<>();
        ArrayList<Integer> tmp = new ArrayList<>(route);
        do {
            swaps = 0;
            overall++;
            int[] arr = new int[route.size()];
            for (int ij = 0; ij < route.size(); ij++)
                arr[ij] = route.get(ij);
            Evaluable evOrg = fitness.evaluate(arr, iterationBA);

            for (int x = 1; x < route.size() - 2; x++) {
                for (int y = x + 1; y < route.size() - 1; y++) {

                    //check distance of line A,B + line C,D against A,C + B,D
                    if ((fitness.distance(route.get(x), route.get(x - 1)) + fitness.distance(route.get(y + 1), route.get(y))) >=
                            (fitness.distance(route.get(x), route.get(y + 1)) + fitness.distance(route.get(x - 1), route.get(y)))) {
                        iter++;

                        int[] tmpRoute = swapCities(route, x, y);
                        Evaluable evTmp = fitness.evaluate(tmpRoute, iterationBA);

                        if (evTmp.isValid()) {
                            //System.out.println("Original Fitness: " + evOrg.getFitness() + " Swap Fitness: " + evTmp.getFitness());
                            if (evOrg.getFitness() >= evTmp.getFitness()) {

                                //System.out.println("Improvement " + (evOrg.getFitness() - evTmp.getFitness()));
                                //System.out.println(route.toString());

                                route.clear();
                                route.addAll(evTmp.getPath());
                                //for (int ij = 0; ij < tmpRoute.length; ij++) {
                                //route.add(tmpRoute[ij]);
                                //}

                                if (evOrg.getFitness() > evTmp.getFitness()) {
                                    swaps++;
                                    for (int ij = 0; ij < route.size(); ij++)
                                        arr[ij] = route.get(ij);
                                    evOrg = fitness.evaluate(arr, iterationBA);

                                }
                                //System.out.println(route.toString());
                            }
                        } else {
                            System.out.println("Not valid!!");
                            System.out.println("Swap at: " + x + " and " + y);
                            System.out.println("Original Path: " + evOrg.getPath());
                            System.out.println("Temp Path: " + evTmp.getPath());
                        }
                    }
                }
            }
            if (swaps == 0) {
                //System.out.println("Iterations"+ iter);
                break;
            }
            // bestFitness=evOrg.getFitness();
            //for(Evaluable e:bestWays){
            //  if(bestFitness>=e.getFitness()){
            //    bestFitness=e.getFitness();
            //   route.clear();
            //   route.addAll(e.getPath());
            //  }
            //    }
        } while (iter < iterations);
        //if(overall>=4)
        // System.out.println("2-opt executions: "+overall+ " with 0 swaps for "+iter+" IterationsS"  );
    }

    private static void threeOptHeuristicIteration(ArrayList<Integer> route, Fitness fit) {
        int route_size = route.size();
        int id_city_p;
        int id_city_a;
        int id_city_b;
        int id_city_c;
        int id_city_q;
        int[] distances = new int[6]; // six nodes or city is a segment

        for (int a = 0; a < route_size; a++) {
            int p = a - 1;
            if (p < 0) p = route_size - 1;
            int b = a + 1;
            if (b >= route_size) b = 0;    // if b exceed current route size.
            int c = b + 1;
            if (c >= route_size) c = 0;    // if b exceed current route size.
            int q = c + 1;
            if (q >= route_size) q = 0;

            //city ids are redefined here
            id_city_p = route.get(p);
            id_city_a = route.get(a);
            id_city_b = route.get(b);
            id_city_c = route.get(c);
            id_city_q = route.get(q);

            //distance are summed into this int[6] array in following sequence...
            //these 3 distance are for general tsp, a symmetrical edges
            int ab = fit.distance(id_city_a, id_city_b);
            int bc = fit.distance(id_city_b, id_city_c);
            int ac = fit.distance(id_city_a, id_city_c);

            int pa = fit.distance(id_city_p, id_city_a);
            int pb = fit.distance(id_city_p, id_city_b);
            int pc = fit.distance(id_city_p, id_city_c);

            int aq = fit.distance(id_city_a, id_city_q);
            int bq = fit.distance(id_city_b, id_city_q);
            int cq = fit.distance(id_city_c, id_city_q);

            //these 3 distance are for a-tsp and sop...
            int ba = fit.distance(id_city_b, id_city_a);
            int cb = fit.distance(id_city_c, id_city_b);
            int ca = fit.distance(id_city_c, id_city_a);

/*          with out p and q.
            distances[0] = ab + bc ;
            distances[1] = ac + cb ;
            distances[2] = ba + ac ;
            distances[3] = bc + ca ;
            distances[4] = ca + ab ;
            distances[5] = cb + ba ;
*/
            distances[0] = pa + ab + bc + cq;
            distances[1] = pa + ac + cb + bq;
            distances[2] = pb + ba + ac + cq;
            distances[3] = pb + bc + ca + aq;
            distances[4] = pc + ca + ab + bq;
            distances[5] = pc + cb + ba + aq;


            //find index of shortest distance from distances:int[6]...
            int minIndex = 0;
            ArrayList<Integer> minis = new ArrayList<>();
            for (int i = 1; i < distances.length; i++) {
                if (distances[i] < distances[minIndex]) {
                    if (distances[minIndex] == distances[i]) {
                        System.out.println("same");
                        minis.add(i);
                    }
                    minIndex = i;
                }
            }

            if (minis.size() > 1) {
                minIndex = minis.get(rand.nextInt(minis.size()));
            }

            //swap cities after case
            switch (minIndex) {
                case 0: //no change, P-ABC-Q route may be optimal from beginning...
                    break;
                case 1: // from P-ABC-Q to P-ACB-Q, swap b and c
                    //like 2 opt.
                    Collections.swap(route, b, c);

//                    System.out.println("Case 1");
//                    System.out.println("Ist   ["+id_city_p+", "+ id_city_a+", "+ id_city_b+", "+ id_city_c+", "+ id_city_q +"]" +" distance of "+ (pa+ab+bc+cq));
//                    System.out.println("Soll  ["+id_city_p+", "+ id_city_a+", "+ id_city_c+", "+ id_city_b+", "+ id_city_q +"]" +" distance of "+ (pa+ac+cb+bq));
//                    System.out.println("Curr"+route);

                    break;
                case 2: // from ABC to BAC, swap a and b
                    //like 2 opt.

                    Collections.swap(route, a, b);
//                    System.out.println("Case 2");
//                    System.out.println("Ist   ["+id_city_p+", "+ id_city_a+", "+ id_city_b+", "+ id_city_c+", "+ id_city_q +"]"+" distance of "+ (pa+ab+bc+cq));
//                    System.out.println("Soll  ["+id_city_p+", "+ id_city_b+", "+ id_city_a+", "+ id_city_c+", "+ id_city_q +"]"+" distance of "+ (pb+ba+ac+cq));
//                    System.out.println("Curr"+route);
                    break;
                case 3: // from ABC to BCA, swap a and c then c and b
                    Collections.swap(route, a, c);
                    Collections.swap(route, c, b);
//                    System.out.println("Case 3");
//                    System.out.println("Ist   ["+id_city_p+", "+ id_city_a+", "+ id_city_b+", "+ id_city_c+", "+ id_city_q +"]"+" distance of "+ (pa+ab+bc+cq));
//                    System.out.println("Soll  ["+id_city_p+", "+ id_city_b+", "+ id_city_c+", "+ id_city_a+", "+ id_city_q +"]"+" distance of "+ (pb+bc+ca+aq));
//                    System.out.println("Curr"+route);
                    break;
                case 4: // from ABC to CAB, swap a and c then b and a
                    Collections.swap(route, a, c);
                    Collections.swap(route, b, a);
//                    System.out.println("Case 4");
//                    System.out.println("Ist   ["+id_city_p+", "+ id_city_a+", "+ id_city_b+", "+ id_city_c+", "+ id_city_q +"]"+" distance of "+ (pa+ab+bc+cq));
//                    System.out.println("Soll  ["+id_city_p+", "+ id_city_c+", "+ id_city_a+", "+ id_city_b+", "+ id_city_q +"]"+" distance of "+ (pc+ca+ab+bq));
//                    System.out.println("Curr"+route);
                    break;
                case 5: // from ABC to CBA, swap a and c
                    Collections.swap(route, a, c);
//                    System.out.println("Case 5");
//                    System.out.println("Ist   ["+id_city_p+", "+ id_city_a+", "+ id_city_b+", "+ id_city_c+", "+ id_city_q +"]"+" distance of "+ (pa+ab+bc+cq));
//                    System.out.println("Soll  ["+id_city_p+", "+ id_city_c+", "+ id_city_b+", "+ id_city_a+", "+ id_city_q +"]"+" distance of "+ (pc+cb+ba+aq));
//                    System.out.println("Curr"+route);
                    break;
            }
        }
        //Junkyard
        /*
            for (int i = 0; i < distances.length ; i++) {
                if (distances[minIndex] == distances[i] && minIndex != i) {
                    minIndex = rand.nextBoolean() ? minIndex:i;

                }
            }
        */
        //More random choices, required for symmetric tsp.
        /*
            if (minIndex == 0) {
                minIndex = rand.nextBoolean() ? 0:5;
            } else if (minIndex == 1) {
                minIndex = rand.nextBoole   an() ? 1:3;
            } else if (minIndex == 2) {
                minIndex = rand.nextBoolean() ? 2:4;
            }
        */
    }

    private static void threeOptHeuristicRandom(ArrayList<Integer> route, Fitness fit, double velocity) {
        int a;
        int aa;
        int b;
        int bb;
        int c;
        int cc;
        int routeSize = route.size();
        int[] distances = new int[6]; // six nodes or city is a segment
        boolean chained = false;
        //city ids are redefined here
        for (int v = 0; v < (int) velocity; v++) {
            a = route.get(rand.nextInt(routeSize) % (routeSize));
            a = a % routeSize;
            aa = (a + 1) % routeSize;

            b = route.get(rand.nextInt(routeSize) % (routeSize));
            b = b % routeSize;
            //if (b == aa) b = (aa + 1)%routeSize;
            bb = (b + 1) % routeSize;

            c = route.get(rand.nextInt(routeSize) % (routeSize));
            c = c % routeSize;
            //if (c == bb) c = (bb + 1)%routeSize;
            cc = (c + 1) % routeSize;

            //distances, aaa means a to aa, where aa is index a+1.
            int aaa = fit.distance(route.get(a), route.get(aa));
            int abb = fit.distance(route.get(a), route.get(bb));
            int acc = fit.distance(route.get(a), route.get(cc));
            int baa = fit.distance(route.get(b), route.get(aa));
            int bbb = fit.distance(route.get(b), route.get(bb));
            int bcc = fit.distance(route.get(b), route.get(cc));
            int caa = fit.distance(route.get(c), route.get(aa));
            int cbb = fit.distance(route.get(c), route.get(bb));
            int ccc = fit.distance(route.get(c), route.get(cc));

            distances[0] = aaa + bbb + ccc;
            distances[1] = abb + baa + ccc;
            distances[2] = acc + baa + cbb;
            distances[3] = bcc + aaa + cbb;
            distances[4] = abb + bcc + caa;
            distances[5] = acc + bbb + caa;

            int minIndex = 0;
            for (int i = 1; i < distances.length; i++) {
                if (distances[i] < distances[minIndex]) {
                    minIndex = i;
                }
            }
            switch (minIndex) {
                case 0: //no change, P-ABC-Q route may be optimal from beginning...
                    break;
                case 1:
                    // from aaa bbb ccc
                    // to   abb baa ccc
                    Collections.swap(route, a, b);
                    break;
                case 2:
                    // from aaa bbb ccc
                    // to   acc bcc cbb
                    Collections.swap(route, a, c);
                    Collections.swap(route, bb, aa);
                    break;
                case 3:
                    // from aaa bbb ccc
                    // to   aaa bcc cbb
                    Collections.swap(route, b, c);
                    break;
                case 4:
                    // from aaa bbb ccc
                    // to   abb bcc caa
                    Collections.swap(route, a, c);
                    Collections.swap(route, bb, cc);
                    break;
                case 5:
                    // from aaa bbb ccc
                    // to   acc bbb caa
                    Collections.swap(route, a, c);
                    break;
            }
        }
    }

    private static int[] swapCities(ArrayList<Integer> route, int x, int y) {
        int[] newRoute = new int[route.size()];
        for (int i = 0; i <= x - 1; i++) {
            newRoute[i] = route.get(i);
        }
        int dec = 0;
        for (int i = x; i <= y; i++) {
            newRoute[i] = route.get(y - dec);
            dec++;
        }
        for (int i = y + 1; i < route.size(); i++) {
            newRoute[i] = route.get(i);
        }

        return newRoute;
    }

}
// Junkyard
  /*
        if(chained){
                        int m=0;
                        System.out.println("["+route.indexOf(a)+", "+route.indexOf(aa)+", "+route.indexOf(b)+", "+route.indexOf(bb)+", "+route.indexOf(c)+", "+route.indexOf(cc)+"]");
                        if(aa==b){
                        m=aa;
                        int cm=fit.distance(route.get(c),route.get(m));
                        int mcc=fit.distance(route.get(m),route.get(cc));
                        distances[0]=aaa+bbb+ccc;
                        distances[1]=abb+cm+mcc;
                        if(distances[1]<distances[0]){
        System.out.println(route);
        route.add(c+1,m);
        route.remove(aa);
        System.out.println(route);
        System.exit(0);
        }
        }else if(bb==c){
        m=bb;
        int am=fit.distance(route.get(a),route.get(m));
        int maa=fit.distance(route.get(m),route.get(aa));
        distances[0]=aaa+bbb+ccc;
        distances[1]=bcc+am+maa;
        if(distances[1]<distances[0]){
        route.add(a+1,m);
        route.remove(c);
        }
        }else if(cc==a){
        m=cc;
        int bm=fit.distance(route.get(b),route.get(m));
        int mbb=fit.distance(route.get(m),route.get(bb));
        distances[0]=aaa+bbb+ccc;
        distances[1]=caa+bm+mbb;
        if(distances[1]<distances[0]){
        route.add(b+1,m);
        route.remove(cc);
        }
        }

*/