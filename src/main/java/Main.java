import algorithm.BatAlgorithm;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please append directory path of a TSP file from public benchmark TSPLIB (https://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/index.html)");
            System.err.println("HOW TO RUN this program?, please also check README.md");
            System.out.println("Variant A: Over any of your IDE add directory ./testData/tsp/<any .tsp file name>");
            System.out.println("Variant B: Over terminal using mvn exec run in command: mvn exec:java \"-Dexec.args=testData/tsp/<any .tsp file name>\"");
            System.out.println("Variant C: Build runnable JAR using: mvn clean package");
            System.out.println("           Then run it using: java -jar target/MasterProjekt_BA-1.0-SNAPSHOT-jar-with-dependencies.jar TestData/<any .tsp file name>");
            System.out.println("!! Warning: currently, this works only with tsp file with EDGE_WEIGHT_TYPE : EUC_2D (You can read it in the tsp file)\n");
            return;
        }

        try {
            long start = System.currentTimeMillis();
            BatAlgorithm.run(args[0]);
            long stop = System.currentTimeMillis();
            System.out.println("Bat Algorithm finished took " + (stop - start) / 1000.0 + " s");
        } catch (IOException e) {
            System.err.println("Error running Bat Algorithm: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
