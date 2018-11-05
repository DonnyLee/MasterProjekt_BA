package algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
		Random random = new Random();

        Fitness fitness = new Fitness(set);
		fitness.evaluate(swarm);
        ArrayList<Integer> bestRoute = fitness.getAbsolutBest().getPath();
        //System.out.println(bestRoute.toString());
		//for(int i=0;i<set.getSize();i++){
			for(Evaluable t: swarm){
			    Bat b = (Bat) t;
				int hemming = hemmingDistanz(b.getPath(),bestRoute);
				if(hemming!=0) {
                    b.setV(random.nextInt(hemming) + 1);
				}else{
					b.setV(1);
				}
				System.out.println(b.toString(false));
				if(b.getV()<set.getSize()/2){
				    //2-opt
                }else{
				    // 3- opt
                }
			}

		}
	//}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initalBatSwarm(ArrayList<Evaluable> swarm, Dataset set) {
	    ArrayList<Integer> allCityNodes = new ArrayList<>();
		for(Node n:set.getNodes()){
		    allCityNodes.add(n.getId());
        }
        System.out.println(allCityNodes.toString());
		for(int i=0;i<SWARM_SIZE;i++){
            shuffleArray(allCityNodes);
            //System.out.println(allCityNodes.toString());
			swarm.add(new Bat(allCityNodes,LOUDNESS,PULSEMISSION));
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

}
