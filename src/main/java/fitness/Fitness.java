package fitness;

import fitness.parser.Dataset;
import fitness.parser.Node;
import fitness.parser.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Fitness {
    private ConcurrentHashMap<Integer, Evaluable> bestOf;
    private int iteration;
    private Dataset dataset;
    private boolean output;
    private int count;

    public Fitness(Dataset dataset, boolean output) {
        this.output = true;
        this.count = 0;
        this.dataset = dataset;
        this.bestOf = new ConcurrentHashMap<>();
        this.iteration = 0;
        this.output = output;
    }

    public Fitness(Dataset dataset) {
        this(dataset, true);
    }

    public Fitness(String filePath) throws IOException {
        this(Parser.read(filePath), true);
    }

    public Fitness(String filePath, boolean output) throws IOException {
        this(Parser.read(filePath), output);
    }

    public final Evaluable evaluate(Evaluable evaluable, int iter) {
        ArrayList<Integer> path = evaluable.getPath();
        singleEvaluate(evaluable, path, iter);
        return evaluable;
    }

    public final Evaluable evaluate(ArrayList<Integer> path, int iter) {
        EvaluationResult res = new EvaluationResult(path);
        evaluate(res, iter);
        return res;
    }

    public final Evaluable evaluate(int[] path, int iter) {
        EvaluationResult res = new EvaluationResult(path);
        evaluate(res, iter);
        return res;
    }

    public final Evaluable[] evaluate(int[][] paths) {
        ArrayList<Evaluable> list = new ArrayList<>();
        for (int[] path : paths) {
            list.add(new EvaluationResult(path));
        }
        return evaluate(list);
    }

    public final Evaluable[] evaluate(ArrayList<Evaluable> list) {
        return evaluate(list.toArray(new Evaluable[0]));
    }

    public final Evaluable[] evaluate(Evaluable[] array) {
        Arrays.stream(array).forEach(this::lambda$evaluate$0);
        
        for (Evaluable evaluable : array) {
            if (bestOf.get(iteration) == null) {
                bestOf.put(iteration, evaluable.copy());
            } else if (!bestOf.get(iteration).isValid() && evaluable.isValid()) {
                bestOf.put(iteration, evaluable.copy());
            } else if (bestOf.get(iteration).getFitness() > evaluable.getFitness()) {
                if (evaluable.isValid() || (!evaluable.isValid() && !bestOf.get(iteration).isValid())) {
                    bestOf.put(iteration, evaluable.copy());
                }
            }
        }
        
        if (output) {
            printBest(iteration);
        }
        
        count += array.length;
        iteration++;
        return array;
    }

    public final Evaluable validate(Evaluable evaluable) {
        validate(evaluable, evaluable.getPath());
        return evaluable;
    }

    public final Evaluable validate(int[] path) {
        EvaluationResult res = new EvaluationResult(path);
        validate(res, res.getPath());
        return res;
    }

    private final void validate(Evaluable evaluable, ArrayList<Integer> path) {
        if (path.size() != dataset.getSize()) {
            evaluable.setValid(false, "Es müssen alle Knoten besucht werden! (" + path.size() + " != " + dataset.getSize() + ")");
            return;
        }
        HashMap<Integer, Boolean> visited = new HashMap<>();
        for (Integer nodeId : path) {
            if (visited.containsKey(nodeId)) {
                evaluable.setValid(false, "Der Knoten mit der ID " + nodeId + " wird häufiger als 1x besucht!");
                return;
            }
            visited.put(nodeId, true);
            
            Node node = dataset.getNodeByID(nodeId);
            if (node == null) {
                evaluable.setValid(false, "Unbekannter Knoten mit der ID " + nodeId + " wurde in ihrem Weg gefunden!");
                return;
            }
            
            if ("SOP".equals(dataset.getType())) {
                ArrayList<Integer> constraints = node.getConstraints();
                if (!visited.keySet().containsAll(constraints)) {
                    evaluable.setValid(false, "Der Weg verletzt die Einschränkungen des Knoten " + nodeId);
                    return;
                }
            }
        }
        
        if ("SOP".equals(dataset.getType())) {
            if (path.get(0) != 1 || path.get(path.size() - 1) != dataset.getSize()) {
                evaluable.setValid(false, "Der Start und/oder Endknoten ist nicht korrekt!");
                return;
            }
        }
        
        evaluable.setValid(true, "");
    }

    private final void calculateFitness(Evaluable evaluable, ArrayList<Integer> path) {
        boolean hasInvalidNode = false;
        int totalDistance = 0;
        Node prevNode = dataset.getNodeByID(path.get(0));
        Node currNode = null;
        for (int i = 1; i < path.size(); i++) {
            currNode = dataset.getNodeByID(path.get(i));
            if (prevNode == null || currNode == null) {
                hasInvalidNode = true;
                break;
            }
            totalDistance += prevNode.distance(currNode);
            prevNode = currNode;
        }
        
        if (!hasInvalidNode && "TSP".equals(dataset.getType())) {
            totalDistance += distance(path.get(path.size() - 1), path.get(0));
        }
        
        if (!hasInvalidNode) {
            evaluable.setFitness(totalDistance);
        } else {
            evaluable.setFitness(-1);
        }
    }

    public final int distance(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        return (int) (Math.sqrt(dx * dx + dy * dy) + 0.5);
    }

    public final int distance(int id1, int id2) {
        return dataset.getNodeByID(id1).distance(dataset.getNodeByID(id2));
    }

    private final void printBest(int iter) {
        Evaluable best = bestOf.get(iter);
        System.out.println(String.format("Iteration: %5d -- Fitness: %3d -- Valid: %6s -- Path: %s",
                iter, best.getFitness(), String.valueOf(best.isValid()), best.getPath().toString()));
    }

    public void finish() {
        for (Integer iter : bestOf.keySet()) {
            printBest(iter);
        }
    }

    public Evaluable getBest(int iter) {
        Evaluable best = bestOf.get(iter);
        return best != null ? best.copy() : null;
    }

    public ArrayList<Evaluable> getBests() {
        ArrayList<Evaluable> list = new ArrayList<>();
        for (Evaluable best : bestOf.values()) {
            list.add(best.copy());
        }
        return list;
    }

    public Evaluable getAbsolutBest() {
        Evaluable absoluteBest = null;
        for (Evaluable evaluable : bestOf.values()) {
            if (absoluteBest == null && evaluable.isValid()) {
                absoluteBest = evaluable;
            } else if (evaluable.isValid() && evaluable.getFitness() < absoluteBest.getFitness()) {
                absoluteBest = evaluable;
            }
        }
        return absoluteBest != null ? absoluteBest.copy() : null;
    }

    public Dataset getDataset() {
        return dataset;
    }

    private Evaluable singleEvaluate(Evaluable evaluable, ArrayList<Integer> path, int iter) {
        if (iter >= 0) {
            count++;
        }
        if (path != null && path.size() > 1 && path.get(0).equals(path.get(path.size() - 1))) {
            path.remove(path.size() - 1);
        }
        calculateFitness(evaluable, path);
        validate(evaluable, path);
        
        if (iter >= 0) {
            if (!bestOf.containsKey(iter)) {
                bestOf.put(iter, evaluable.copy());
            } else {
                Evaluable currentBest = bestOf.get(iter);
                if (currentBest.getFitness() > evaluable.getFitness()) {
                    bestOf.put(iter, evaluable.copy());
                }
            }
        }
        return evaluable;
    }

    public int getCount() {
        return count;
    }

    private void lambda$evaluate$0(Evaluable evaluable) {
        ArrayList<Integer> path = evaluable.getPath();
        if (path != null && path.size() > 1 && path.get(0).equals(path.get(path.size() - 1))) {
            path.remove(path.size() - 1);
        }
        calculateFitness(evaluable, path);
        validate(evaluable, path);
    }
}
