package algorithm;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;
import entities.Bat;

public class BatAlgorithm {
private  static final int SWARM_SIZE=50;
private  static final double LOUDNESS=0.9;
private  static final double PULSE_EMISSION =0.9;
private  static final double THRESHOLD =0.05;
//Alpha for decreasing Loudness for best Solution in iteration
private  static final double ALPHA =0.8;
private  static final double GAMMA =0.75;
private static final Random rand = new Random();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  try {
			Dataset dataset = readDataSet(args[0]);
			ArrayList<Evaluable> batSwarm =new ArrayList<>();

			initialBatSwarm(batSwarm,dataset);
              long start = System.currentTimeMillis();
			iterationsBatAlgorithm(batSwarm,dataset);
              long stop = System.currentTimeMillis();
              System.out.println("BA finifhed after "+(stop-start)/1000+" s");
			//dataset.getNodeByID(23).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void iterationsBatAlgorithm(ArrayList<Evaluable> swarm, Dataset set){
		//ArrayList<Integer> bestRoute = new ArrayList<Integer>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Fitness fitness = new Fitness(set);
        fitness.evaluate(swarm);
        sortSwarm(swarm);

        //A random bat is picked as best Bat.
        Evaluable bestSwarmFitness=fitness.getAbsolutBest();

        Bat bestBat=new Bat();

        //Iteration Counter
        int i=1;
        do {
            //begin Algorithm
            //for each bat in the population do...
            for (Evaluable t : swarm) {
                Bat b = (Bat) t;

                //Generate new solution...
                int hemming = hemmingDistanz(b.getPath(), bestSwarmFitness.getPath());
                //hemming is count of all different element of current bat route and best route
                if (hemming != 0) {
                    //when there is/are any different between two route...
                    //set velocity of a bat to random distance of [1, hemming]
                    b.setV(random.nextInt(hemming) + 1);
                } else {
                    //A flying bat cannot stop, even if it found current best solution
                    //It's velocity minimum is 1, an indicator that bat never stop flying.
                    b.setV(1);
                }

                //according to their velocity set new route
                newHeuristicSolution(b, set.getSize(), i, fitness);


                if (random.nextDouble() > b.getR()) {
                    int topSolutionOf = 3;
                    //This number must be higher then swarm size otherwise can cause indexOutOfBoundException.
                    //Select one solution among the best Ones
                    ArrayList<Integer> randomBest = swarm.get(random.nextInt(topSolutionOf)).getPath();

                    hemming = hemmingDistanz(b.getPath(), randomBest);
                    if (hemming != 0) {
                        b.setV(random.nextInt(hemming) + 1);
                    } else {
                        b.setV(1);
                    }
                    newHeuristicSolution(b, set.getSize(), i, fitness);
                    //every bat b are closing into random solution route
                }

                if (random.nextDouble(LOUDNESS) < b.getA() && fitness.evaluate(b, i).getFitness() < fitness.evaluate(bestSwarmFitness, i).getFitness()) {
                    bestSwarmFitness = fitness.evaluate(b, i);
                    b.setA(ALPHA * b.getA());
                    b.setR(PULSE_EMISSION * (1 - Math.exp(-GAMMA * i)));
                    bestBat = b;
                    //System.out.println(fitness.evaluate(b, i).getFitness());
                    //System.out.println("     " + b.toString(false));
                }
            }
            fitness.evaluate(swarm);
            sortSwarm(swarm);
            i++;
            //}while(bestBat.getA()>= THRESHOLD && bestBat.getR()< PULSE_EMISSION);
            //}while(bestBat.getR()< PULSE_EMISSION);
        }while(bestBat.getR()< PULSE_EMISSION);

            //fitness.finish();
	}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initialBatSwarm(ArrayList<Evaluable> swarm, Dataset set) {
	    ArrayList<Integer> allCityNodes = new ArrayList<>();
		for(Node n:set.getNodes()){
		    allCityNodes.add(n.getId());
        }
        //System.out.println(allCityNodes.toString());
		for(int i=0;i<SWARM_SIZE;i++){
            //TODO Implementation of better initial Solutions
            shuffleArray(allCityNodes);
            //System.out.println(allCityNodes.toString());
			swarm.add(new Bat(allCityNodes));
		}
	}
    private static void shuffleArray(ArrayList<Integer> ar)
    {
        long seed = System.nanoTime();
        Collections.shuffle(ar,new Random(seed));
    }
    private static void sortSwarm(ArrayList<Evaluable>swarm){
        Collections.sort(swarm, new Comparator<Evaluable>() {
            @Override
            public int compare(Evaluable o1, Evaluable o2) {
                return Integer.compare(o1.getFitness(),o2.getFitness());
            }
        });
    }

    private static void newHeuristicSolution(Bat b, double n, int iterationBA, Fitness fitness){
        // b each bat
        // n number of Nodes
        // i iteration
        // f current fitness
        if(b.getV()<n/2.0){
            //If the bat b is closer to global best
            //In this case bat b have to search in the solution space
            twoOptHeuristic(b.getPath(),b.getV(),fitness,iterationBA);
        }else{
            //If the bat b is presumably far away from global best or even from the swarm
            threeOptHeuristic(b.getPath(),fitness);

        }
    }


	private static int hemmingDistanz(ArrayList<Integer> route, ArrayList<Integer> bestRoute){
		int counter =0;
		for(int i=0;i<bestRoute.size();i++){
			if(route.get(i)!=bestRoute.get(i))
				counter++;
		}
		return counter;
	}



	private static void twoOptHeuristic(ArrayList<Integer> route, double iterations, Fitness fitness, int iterationBA) {
	    int iter=0;
        int overall=0;
        int swaps=0;
        ArrayList<Evaluable> bestWays =new ArrayList<>();
        ArrayList<Integer> tmp = new ArrayList<>(route);
         do{
             swaps=0;
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

                        int[] tmpRoute = swapCities(route,x,y);
                        Evaluable evTmp = fitness.evaluate(tmpRoute,iterationBA);

                        if(evTmp.isValid()) {
                            //System.out.println("Original Fitness: " + evOrg.getFitness() + " Swap Fitness: " + evTmp.getFitness());
                            if (evOrg.getFitness() >= evTmp.getFitness()) {

                                //System.out.println("Improvement " + (evOrg.getFitness() - evTmp.getFitness()));
                                //System.out.println(route.toString());

                                route.clear();
                                route.addAll(evTmp.getPath());
                                //for (int ij = 0; ij < tmpRoute.length; ij++) {
                                    //route.add(tmpRoute[ij]);
                                //}

                                if(evOrg.getFitness() > evTmp.getFitness()) {
                                    swaps++;
                                    for (int ij = 0; ij < route.size(); ij++)
                                        arr[ij] = route.get(ij);
                                    evOrg = fitness.evaluate(arr, iterationBA);

                                }
                                //System.out.println(route.toString());
                            }
                        }else{
                            System.out.println("Not valid!!");
                            System.out.println("Swap at: "+x+" and "+y);
                            System.out.println("Original Path: "+evOrg.getPath());
                            System.out.println("Temp Path: "+evTmp.getPath());
                        }
                    }
                }
            }
            if(swaps==0){
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
        }while (iter<iterations);
         //if(overall>=4)
          // System.out.println("2-opt executions: "+overall+ " with 0 swaps for "+iter+" IterationsS"  );
    }

    private static void threeOptHeuristic(ArrayList<Integer> route, Fitness fit) {
        int route_size = route.size();
        int id_city_a;
        int id_city_b;
        int id_city_c;
        int[] distances = new int[6]; // six nodes or city is a segment

        for (int a = 0; a < route_size; a++) {
            int b = a + 1;
            if (b >= route_size) b = 0;    // if b exceed current route size.

            int c = b + 1;
            if (c >= route_size) c = 0;    // if b exceed current route size.

            //city ids are redefined here
            id_city_a = route.get(a);
            id_city_b = route.get(b);
            id_city_c = route.get(c);


            //distance are summed into this int[6] array in following sequence...
            //these 3 distance are for general tsp, a symmetrical edges
            int ab = fit.distance(id_city_a, id_city_b);
            int bc = fit.distance(id_city_b, id_city_c);
            int ac = fit.distance(id_city_a, id_city_c);

            //these 3 distance are for a-tsp and sop...
            int ba = fit.distance(id_city_b, id_city_a);
            int cb = fit.distance(id_city_c, id_city_b);
            int ca = fit.distance(id_city_c, id_city_a);

            distances[0] = ab + bc;
            distances[1] = ac + cb;
            distances[2] = ba + ac;
            distances[3] = bc + ca;
            distances[4] = ca + ab;
            distances[5] = cb + ba;

            //find index of shortest distance from distances:int[6]...
            int minIndex = 0;
            for (int i = 0; i < 6; i++) {
                if (distances[i] < distances[minIndex]) minIndex = i;
            }

            //More random choices, required for symmetric tsp.
            int swapCase = 0;
            if (minIndex == 0) {
                swapCase = rand.nextBoolean() ? 0:5;
            } else if (minIndex == 1) {
                swapCase = rand.nextBoolean() ? 1:3;
            } else if (minIndex == 2) {
                swapCase = rand.nextBoolean() ? 2:4;
            }

            //swap cities after case
            switch (swapCase) {
                case 0: //no change, route may be optimal from beginning...
                    break;
                case 1: // from ABC to ACB, swap b and c
                    //like 2 opt.
                    Collections.swap(route, b, c);
                    break;
                case 2: // from ABC to BAC, swap a and b
                    //like 2 opt.
                    Collections.swap(route, a, b);
                    break;
                case 3: // from ABC to BCA, swap a and c then c and b
                    Collections.swap(route, a, c);
                    Collections.swap(route, c, b);
                    break;
                case 4: // from ABC to CAB, swap a and c then b and a
                    Collections.swap(route, a, c);
                    Collections.swap(route, b, a);
                    break;
                case 5: // from ABC to CBA, swap a and c
                    Collections.swap(route, a, c);
                    break;
            }
        }
	}

    private static int[] swapCities(ArrayList<Integer> route,int x, int y){
	   int[] newRoute = new int[route.size()];
        for (int i = 0; i <= x-1; i++) {

            newRoute[i]=route.get(i);
        }

        int dec=0;
        for (int i = x; i <= y; i++){
            newRoute[i]=route.get(y-dec);
            dec++;
        }
        for (int i = y+1; i < route.size(); i++) {
            newRoute[i]=route.get(i);
        }

        return  newRoute;
    }

}
