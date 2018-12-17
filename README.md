# MasterProjekt_BA
developed in java 11 LTS.
required java 11 to execute.

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

how to run?
1.  You have to build a jar with java file which contains main.
    /BA/algorithm/BatAlgorithm
    
    or just take what you can find in director /jar

2.  Once you have jar file open terminal or command and type
    java -jar <name of jarfile>.jar <directory where TSP file is>
    
    Example
    java -jar TSP_30_Bats.jar ../../Problems/TSP/a280.tsp

Or you can just execute as your favorite IDE by just cloning it

Currently this works only with tsp file with EDGE_WEIGHT_TYPE : EUC_2D (You can read it in the tsp file)
