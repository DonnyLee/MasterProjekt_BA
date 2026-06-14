package fitness;

import java.util.ArrayList;

public class EvaluationResult extends Evaluable {
    private final ArrayList<Integer> path;

    public EvaluationResult(int[] path) {
        super();
        this.path = new ArrayList<>();
        if (path != null) {
            for (int val : path) {
                this.path.add(val);
            }
        }
    }

    public EvaluationResult(ArrayList<Integer> path) {
        super();
        if (path != null) {
            this.path = new ArrayList<>(path);
        } else {
            this.path = new ArrayList<>();
        }
    }

    @Override
    public ArrayList<Integer> getPath() {
        return path;
    }
}
