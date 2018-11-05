package algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.hsh.parser.Dataset;
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

			Bat[] batSwarm= new Bat[SWARM_SIZE];
			initalBatSwarm(batSwarm,dataset);
			//System.out.println(batSwarm[5].toString());
			iterationsBatAlgoritm(batSwarm,dataset);
			//dataset.getNodeByID(23).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void iterationsBatAlgoritm(Bat[] swarm, Dataset set){
		ArrayList<Integer> bestRoute = new ArrayList<Integer>();
		Random random = new Random();
		for(int i=0;i<set.getSize();i++){
			for(Bat t: swarm){
				int hemming = hemmingDistanz(t.getP(),bestRoute);
				if(hemming!=0) {
					t.setV(random.nextInt(hemming) + 1);
				}else{
					t.setV(1);
				}
				System.out.println(t.toString());
			}

		}
	}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initalBatSwarm(Bat[] swarm, Dataset set) {
		Random rand = new Random();
		for(int i=0;i<SWARM_SIZE;i++){
			swarm[i]= new Bat(rand.nextInt(set.getSize())+1,LOUDNESS,PULSEMISSION);

		}
	}
	private static int hemmingDistanz(ArrayList route, ArrayList bestRoute){
		int counter =0;
		for(int i=0;i<bestRoute.size();i++){
			if(route.get(i)==bestRoute.get(i))
				counter++;
		}
		return counter;
	}

}
