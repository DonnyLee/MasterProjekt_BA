package fitness.parser;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Node {
    private final int id;
    protected ConcurrentHashMap<Integer, Integer> cost;
    protected ArrayList<Integer> constraints;

    public Node(int id) {
        this.id = id;
        this.cost = new ConcurrentHashMap<>();
        this.constraints = new ArrayList<>();
    }

    public abstract int distance(Node other);

    protected void setCost(int costValue, int otherId) {
        this.cost.put(otherId, costValue);
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getConstraints() {
        return new ArrayList<>(constraints);
    }

    @Override
    public String toString() {
        return "id=" + id;
    }
}
