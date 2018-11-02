package example;

import com.hsh.Evaluable;
import com.hsh.EvaluationResult;
import com.hsh.Fitness;
import com.hsh.parser.Dataset;
import com.hsh.parser.Node;
import com.hsh.parser.Parser;
import com.hsh.parser.TSPNode;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.ArrayList;


/*
* ALLGEMEIN!
* 1. EUER PROGRAMM MUSS ALS ERSTES PROGRAMM-ARGUMENT (args[0]) DEN WEG ZUR DATEN-DATEI AKZEPIEREN!
* 2. DIE FITNESS FUNKTION MUSS WIE FOLGT INITIALISIERT SEIN:
*   2.1 Fitness fitness = new Fitness(x, true)
 *   oder
 *  2.2 Fitness fitness = new Fitness(x)
*    oder
*   2.3 Fitness fitness = new Fitness(x, false) - (Euer Programm) - fitness.finish()
*
*
*   Falls Unklarheiten oder Fragen sind könnt ihr euch bei mir melden
*   Email: lennard.roessig@stud.hs-hannover.de
* */

public class Beispiele {
    public static void main(String [] args) throws IOException{
        String pathToData = "TestData/a280.tsp";
        example1(pathToData);
        example2(pathToData);
        example3(pathToData);
        tippsFitness(pathToData);
        tippsDataset(pathToData);
    }


    /*
    * EMPFOHLEN:
    * */
    public static void example1(String pathToData) throws IOException {
        System.out.println("\n------------------Example 1------------------");
        // Einlesen des Datensatzes per Parser.read()
        Dataset dataset = Parser.read(pathToData);
        // Initialisierung der Fitness-Klasse
        Fitness fitness = new Fitness(dataset);

        // Anlegen von Beispiellösungen (Wege)
        // 1 Weg = 1 Array mit Knoten ID´s
        // ExamplePath ist unten im Dokument definiert
        ArrayList<Evaluable> examples = new ArrayList<>();
        ExamplePath examplePath1 = new ExamplePath(new int[]{1, 3, 5});
        ExamplePath examplePath2 = new ExamplePath(new int[]{1, 3, 10, 7});
        examples.add(examplePath1);
        examples.add(examplePath2);

        // Übergabe von allen möglichen Lösungen
        // Das Ergebnis der Evaluierung wird an die ExamplePath angehängt und kann
        // von dort ausgelesen werden
        fitness.evaluate(examples);
        // Die evaluate Funktion gibt auf der Konsole immer das beste Ergebnis der Iteration aus
        // (kann abgeschaltet werden durch Übergabe von false im Fitness Konstruktor)


        for(Evaluable x : examples){
            System.out.println(String.format("%s -- %5d -- %s", x.getPath() , x.getFitness() , x.isValid()));
        }
    }


    /*
    * Alternative zur Nutzung der Evaluable-Klasse!
    * Ihr könnt auch alle Lösungen einer Iteration in einem int[][] angeben,
    * wobei die 1 Dimension die Anzahl eurer Lösungen in der Iteration und
    * die 2 Dimension jeweils eine Lösungen (ein Weg) ist.
    * */
    public static void example2(String pathToData) throws IOException {
        System.out.println("\n------------------Example 2------------------");

        // Die Initialisierung kann auch so stattfinden, da spart ihr euch den Parser
        Fitness fitness = new Fitness(pathToData );
        Dataset dataset = fitness.getDataset();

        // Anlegen von Beispiellösungen (Wege)
        // 1. Dimension = Anzahl der Lösungen
        // 2. Dimension = mögliche Lösung (Weg)
        int[][] examples = new int[2][];
        examples[0] = new int[]{1, 3, 5};
        examples[1] = new int[]{1, 3, 10, 7};

        // Da die Auswertung nicht direkt an das Objekt angehängt werden kann, wird
        // euch ein Array von Evaluable zurück gegeben.
        Evaluable[] results = fitness.evaluate(examples);

        //z.B.
        for(Evaluable x : results){
            System.out.println(String.format("%s -- %5d -- %s", x.getPath() , x.getFitness() , x.isValid()));
        }
    }


    /*
    * Falls es nicht möglich ist alle Lösungen einer Iteration gleichzeitig auswerten zu lassen,
    * gibt es die Möglichkeit dieses auch einzeln zu machen.
    * Folgendes muss eingehalten werden:
    *   1. Bei Initialisierung der Fitness-Klasse müsst ihr als zweiten Parameter "false" übergeben
    *   2. Beim Aufruf von fitness.evaluate() müsst ihr als zweiten Parameter die Iteration angeben,
    *       zu welcher die Lösung (der Weg) zählt
    *   3. Am Ende eures Algorithmus (NICHT AM ENDE EINER ITERATION!) MUSS ein fitness.finish() stehen!
    * */
    public static void example3(String pathToData) throws IOException {
        System.out.println("\n------------------Example 3------------------");
        // WICHTIG! Hier muss als zweiter Parameter ein false übergeben werden
        Fitness fitness = new Fitness(pathToData, false );
        Dataset dataset = fitness.getDataset();

        // Ihr habt dabei die Verantwortung eure Lösungen der richtigen Iteration zuzuordnen
        Evaluable result1 = fitness.evaluate(new int[]{1, 3, 5}, 0);

        System.out.println(String.format("%s -- %5d -- %s", result1.getPath() , result1.getFitness() , result1.isValid()));

        // WICHTIG! AM ENDE EURES PROGRAMMES !!
        fitness.finish();
    }


    /*
    * Tipps Fitness
    * */
    public static void tippsFitness(String pathToData) throws IOException {
        Fitness fitness = new Fitness(pathToData);

        // Beispiellösungen
        ExamplePath examplePath1 = new ExamplePath(new int[]{1, 3, 5});
        int[] examplePath2 = new int[]{1, 3, 5};

        // Um schnell eine Lösungen evaluieren zu lassen, könnte ihr folgendes nutzen:
        fitness.evaluate(examplePath1, -1);  // WICHTIG die -1, damit wird die Lösung nicht als mögliches "best" betrachtet
        Evaluable evaluable = fitness.evaluate(examplePath2, -1);
        // Die Auswertung zieht diese Lösung nicht als ein mögliches "best" in betrachet!
        // -- FALLS DIE NUTZUNG NUR SO IST, MÜSST IHR NICHT DIE BEDINGUNGEN VOM EXAMPLE 3 EINHALTEN! --


        // Die Validierung von einzelnen Lösungen ohne eine Bewertung ist wie folgt möglich:
        fitness.validate(examplePath1);
        Evaluable evaluable2 = fitness.validate(examplePath2);

        // Die besten Ergebnisse jeder vergangenen Iteration:
        ArrayList<Evaluable> bests = fitness.getBests();
        // oder auch das beste Ergebnis eine speziellen Iteration
        Evaluable evaluableBest = fitness.getBest(0); // von der jeweiligen Iteration
        // oder auch das absolute beste Ergebnis (unabhängig von der Iteration)
        Evaluable evaluableAbsolutBest = fitness.getAbsolutBest();
    }

    /*
    * Tipps Dataset
    * */
    public static void tippsDataset(String pathToData) throws IOException{
        System.out.println("\n------------------Example Dataset------------------");
        Dataset dataset = Parser.read(pathToData);
        /*
        * Dataset beinhaltet:
        *   - type   (TSP oder SOP)
        *   - size   (Anzahl an Knoten)
        *   - nodes  (Knoten)
        * */

        // Aus dem Dataset können Knoten mittels der ID geholt werden
        // Die id´s starten bei 1 bis N (Größe des Datensatzes)
        Node node = dataset.getNodeByID(1);
        // oder direkt alle Knoten in Form einer Liste
        Node[] nodes = dataset.getNodes();


        /*
        * Nodes beinhalten
        *  - id
        *  - x, y (nur bei TSP)
        *  - constraints (nur bei SOP)
        * */

        // Um an X, Y zu gelangen, muss ein cast auf TSPNode erfolgen
        // WICHTIG! Ihr müsste euch dabei sicher sein, dass das Problem ein TSP-Problem ist!
        if(dataset.getType().equals("TSP")) {
            System.out.println("TSP Node: X - Y: ");
            System.out.println(((TSPNode)node).getX());
            System.out.println(((TSPNode)node).getY());
        }

        // Die Kosten von einem Knoten zu einem anderen Knoten (kein Unterschied zwischen TSP und SOP)
        Node node2 = dataset.getNodeByID(2);
        int distance = node.distance(node2);
        System.out.println("Kosten von node zu node1: " + distance);

        // Die Constraints (Abhängigkeiten von Knoten zu anderen Knoten)
        // ist nur im SOP wichtig. In TSP ist dieses Array einfach leer.
        // Der Inhalt des Constraints-Array ist dabei die ID der jeweiligen Knoten
        // Constraints können auch durch die distance funktion erkannt werden, da diese dann eine -1 zurück gibt.
        // bsp: node.distance(node2) ==  -1, dass bedeutet node2 muss vor node1 besucht werden
        ArrayList<Integer> constraints = node.getConstraints();
    }




}

class ExamplePath extends Evaluable{
    ArrayList<Integer> path;
    public ExamplePath(int[] path) {
        // wandelt int[] in eine ArrayList um
        this.path = new ArrayList<>();
        for(int x : path){
            this.path.add(x);
        }
    }

    @Override
    public ArrayList<Integer> getPath() {
        return path;
    }
}

