package algorithm;

import java.io.IOException;
import java.util.ArrayList;

import com.hsh.parser.Dataset;
import com.hsh.parser.Parser;

public class BatAlgorithm {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		  try {
			Dataset dataset = readDataSet(args[0]);
			
			System.out.println(dataset.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Dataset readDataSet(String pathToTestData) throws IOException {
		return Parser.read(pathToTestData);
	}
	private static void initalBatSwarm() {
		
	}

}
