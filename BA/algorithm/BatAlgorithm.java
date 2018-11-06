package algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.hsh.Evaluable;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;
import entities.Bat;

public class BatAlgorithm {
private  static final int SWARM_SIZE=50;
private  static final double LOUDNESS=0.7;
private  static final double PULSEMISSION=0.7;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  try {
			Dataset dataset = readDataSet(args[0]);

			ArrayList<Evaluable> batSwarm =new ArrayList<>();
			initalBatSwarm(batSwarm,dataset);
			iterationsBatAlgoritm(batSwarm,dataset);

			//dataset.getNodeByID(23).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void iterationsBatAlgoritm(ArrayList<Evaluable> swarm, Dataset set){
		//ArrayList<Integer> bestRoute = new ArrayList<Integer>();
        ThreadLocalRandom random = ThreadLocalRandom.current();

        Fitness fitness = new Fitness(set);
		fitness.evaluate(swarm);

       Evaluable bestBat=fitness.getAbsolutBest();

        //System.out.println(bestRoute.toString());
		for(int i=1;i<=10;i++){
          
			for(Evaluable t: swarm){
			    Bat b = (Bat) t;
				int hemming = hemmingDistanz(b.getPath(),bestBat.getPath());
				if(hemming!=0) {
                        b.setV(random.nextInt(hemming) + 1);
				}else{
					b.setV(1);
				}
				//System.out.println(b.toString(true));
				if(b.getV()<set.getSize()/2){
				       twoOptHeursistc(b.getPath(),b.getV(),fitness,i);
                }else{
				   // System.out.println("Nothing to Do!!!");
                    //TODO Implementation of 3-opt
                }
                if(random.nextDouble()>b.getR()){
                    //System.out.println("Search for better Solution");
                    //TODO Implementation of loacl Search
                }
                if(random.nextDouble(LOUDNESS)<b.getA() && fitness.evaluate(b,i).getFitness() < fitness.evaluate(bestBat,i).getFitness()){
                    b.setA(0.9*b.getA());
                    b.setR(PULSEMISSION*(1-Math.exp(-0.9*i+1)));
                    bestBat =  fitness.evaluate(b,i);
                    System.out.println(fitness.evaluate(b,i).getFitness());
                    System.out.println("Better Solution: "+b.toString(false));
                }
			}
            fitness.evaluate(swarm);


		}
		//fitness.finish();
	}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initalBatSwarm(ArrayList<Evaluable> swarm, Dataset set) {
	    ArrayList<Integer> allCityNodes = new ArrayList<>();
		for(Node n:set.getNodes()){
		    allCityNodes.add(n.getId());
        }
        //System.out.println(allCityNodes.toString());
		for(int i=0;i<SWARM_SIZE;i++){
            //TODO Implementation of better inital Solutions
            shuffleArray(allCityNodes);
            //System.out.println(allCityNodes.toString());
			swarm.add(new Bat(allCityNodes,LOUDNESS));
		}
	}
    private static void shuffleArray(ArrayList<Integer> ar)
    {
        long seed = System.nanoTime();
        Collections.shuffle(ar,new Random(seed));
    }


	private static int hemmingDistanz(ArrayList<Integer> route, ArrayList<Integer> bestRoute){
		int counter =0;
		for(int i=0;i<bestRoute.size();i++){
			if(route.get(i)!=bestRoute.get(i))
				counter++;
		}
		return counter;
	}
	private static void twoOptHeursistc(ArrayList<Integer> route,double iterations,Fitness fitness,int iterationBA) {
        int swaps=0;
         for(int i=0;i < iterations;i++){

            for (int x = 1; x < route.size() - 2; x++) {
                for (int y = x + 1; y < route.size() - 1; y++) {

                    //check distance of line A,B + line C,D against A,C + B,D
                    if ((fitness.distance(route.get(x), route.get(x - 1)) + fitness.distance(route.get(y + 1), route.get(y))) >=
                            (fitness.distance(route.get(x), route.get(y + 1)) + fitness.distance(route.get(x - 1), route.get(y)))) {

                        int[] arr = new int[route.size()];
                        for (int ij = 0; ij < route.size(); ij++)
                            arr[ij] = route.get(ij);
                        Evaluable evOrg = fitness.evaluate(arr, iterationBA);

                        int[] tmpRoute = swapCities(route,x,y);
                        Evaluable evTmp = fitness.evaluate(tmpRoute,iterationBA);

                        if(evTmp.isValid()) {


                            //System.out.println("Original Fitness: " + evOrg.getFitness() + " Swap Fitness: " + evTmp.getFitness());
                            if (evOrg.getFitness() > evTmp.getFitness()) {

                                //System.out.println("Improvement " + (evOrg.getFitness() - evTmp.getFitness()));
                                //System.out.println(route.toString());

                                route.clear();
                                for (int ij = 0; ij < tmpRoute.length; ij++) {
                                    //System.out.println(tmpRoute[ij]);
                                    route.add(tmpRoute[ij]);
                                }
                                swaps++;
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
                //System.out.println("No swaps");
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
