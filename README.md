# MasterProjekt_BA
developed in Java 21 LTS.
required Java 21 to execute.

Bat algorithm solves problems or contribute solution for given problem.
In this Project, the algorithm try to solve well known traveling sales men problem (tsp).
Where nodes are visited only once and builds a cycle. 
The goal of this problem is to find out the shortest route of hamiltonian cycle.
In our Algorithm, we try to solve tsp using hamming distance, 2-opt and 3-opt optimization.
The hamming distance results the number of different elements of given two routes (new one vs best one).
The velocity of a bat is set with result of the hamming distance.
Depends on velocity a bat chooses optimization sequence 2-Opt or 3-Opt.
3-Opt optimization is not fully polished but let swarm result better fitness.
The fitness in this case is the distance von A to A over hamiltonian path, so lower the better.

## How to Run?
You can run the program in three different ways:

### Variant A: IDE Run
Add the path of a `.tsp` file (e.g., `./testData/tsp/berlin52.tsp`) as program arguments in your IDE's Run Configuration, and execute the [Main.java](file:///C:/Users/Lee%20Private/IdeaProjects/MasterProjekt_BA/src/main/java/Main.java) class.

### Variant B: Maven Exec Plugin
Run the program directly from your terminal using Maven:
```cmd
mvn exec:java "-Dexec.args=testData/tsp/<any .tsp file name>"
```

### Variant C: Build and Run Runnable JAR
1. Compile and build a runnable fat JAR containing all dependencies:
   ```cmd
   mvn clean package
   ```
2. Run the generated JAR file:
   ```cmd
   java -jar target/MasterProjekt_BA-1.0-SNAPSHOT-jar-with-dependencies.jar TestData/<any .tsp file name>
   ```

Currently, this works only with TSP files using `EDGE_WEIGHT_TYPE : EUC_2D` (this can be verified inside the `.tsp` file).
