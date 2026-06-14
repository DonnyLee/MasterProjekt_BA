package fitness.parser;

import java.util.HashMap;

public class Dataset {
    private final String type;
    private final String name;
    private final int size;
    private final HashMap<Integer, Node> nodes;

    public Dataset(String type, int size, String name) {
        this.type = type;
        this.size = size;
        this.name = name;
        this.nodes = new HashMap<>();
    }

    protected void addNode(String line) {
        String[] tokens = line.trim().split("\\s+");
        Node node;
        if ("TSP".equals(type)) {
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            int id = Integer.parseInt(tokens[0]);
            node = new TSPNode(x, y, id);
        } else {
            node = new SOPNode();
            for (int i = 0; i < tokens.length; i++) {
                int cost = Integer.parseInt(tokens[i]);
                int otherId = i + 1;
                node.setCost(cost, otherId);
            }
        }
        nodes.put(node.getId(), node);
    }

    public Node getNodeByID(int id) {
        return nodes.get(id);
    }

    public int getSize() {
        return nodes.size();
    }

    public String getType() {
        return type;
    }

    public Node[] getNodes() {
        if ("SOP".equals(type)) {
            return nodes.values().toArray(new SOPNode[0]);
        } else {
            return nodes.values().toArray(new TSPNode[0]);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Node node : nodes.values()) {
            str.append(node.toString()).append("\n");
        }
        return "Dataset{\n type='" + type + '\'' +
                ",\n name='" + name + '\'' +
                ",\n size=" + size +
                ",\n Nodes: \n" + str + "}";
    }
}
