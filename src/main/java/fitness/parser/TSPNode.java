package fitness.parser;

import java.text.DecimalFormat;

public class TSPNode extends Node {
    private final double x;
    private final double y;

    public TSPNode(double x, double y, int id) {
        super(id);
        this.x = x;
        this.y = y;
    }

    @Override
    public int distance(Node other) {
        if (!this.cost.containsKey(other.getId())) {
            double dx = this.x - ((TSPNode) other).getX();
            double dy = this.y - ((TSPNode) other).getY();
            int dist = (int) (Math.sqrt(dx * dx + dy * dy) + 0.5);
            this.setCost(dist, other.getId());
            return dist;
        } else {
            return this.cost.get(other.getId());
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return String.format("%8s %8s %8s", super.toString(), "x=" + new DecimalFormat("#.0#").format(x), "y=" + new DecimalFormat("#.0#").format(y));
    }
}
