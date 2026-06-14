package fitness.parser;

public class SOPNode extends Node {
    static int ID_COUNT = 1;

    public SOPNode() {
        super(ID_COUNT++);
    }

    @Override
    public int distance(Node other) {
        Integer dist = this.cost.get(other.getId());
        return dist != null ? dist : 0;
    }

    @Override
    protected void setCost(int costValue, int otherId) {
        super.setCost(costValue, otherId);
        if (costValue == -1) {
            this.constraints.add(otherId);
        }
    }

    @Override
    public String toString() {
        String str = "";
        for (Integer val : cost.values()) {
            str += String.format("%5d", val);
        }
        return String.format("%8s |%s|", super.toString(), str);
    }
}
