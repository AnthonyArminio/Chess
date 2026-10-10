package chess.intel.strategy.util;

/**
 * Represents a confidence level in an Evaluation; higher confidence Evaluations are always preferred over lower confidence evaluations.
 */
public class EvaluationConfidence implements Comparable<EvaluationConfidence> {

    public static EvaluationConfidence OBJECTIVE = new EvaluationConfidence(3);
    public static EvaluationConfidence TECHNICAL = new EvaluationConfidence(2);
    public static EvaluationConfidence SUBJECTIVE = new EvaluationConfidence(1);
    public static EvaluationConfidence INCONCLUSIVE = new EvaluationConfidence(0);

    // Always nonnegative
    private final int value;

    public EvaluationConfidence(int value) {
        this.value = Math.max(value, 0);
    }

    public int getValue() {
        return this.value;
    }

    @Override public int compareTo(EvaluationConfidence other) {
        if (this.value > other.value) {
            return 1;
        } else if (this.value < other.value) {
            return -1;
        } else {
            return 0;
        }
    }
}
