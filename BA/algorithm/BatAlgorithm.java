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
			//System.out.println(dataset.toString());
			initalBatSwarm(batSwarm,dataset);
			//dataset.getNodeByID(23).getId();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initalBatSwarm(Bat[] swarm, Dataset set) {
		Random rand = new Random();
		for(int i=0;i<SWARM_SIZE;i++){
			swarm[i]= new Bat(rand.nextInt(set.getSize())+1,LOUDNESS,PULSEMISSION);
			System.out.println(swarm[i].toString());
		}
	}

}
