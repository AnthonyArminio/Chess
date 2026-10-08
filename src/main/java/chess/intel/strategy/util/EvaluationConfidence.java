package chess.intel.strategy.util;

/**
 * Represents a confidence level in an Evaluation; higher confidence Evaluations are always preferred over lower confidence evaluations.
 */
public class EvaluationConfidence implements Comparable<EvaluationConfidence> {

    public static EvaluationConfidence OBJECTIVE = new EvaluationConfidence(3);
    public static EvaluationConfidence TECHNICAL = new EvaluationConfidence(2);
    public static EvaluationConfidence SUBJECTIVE = new EvaluationConfidence(1);
    public static EvaluationConfidence REJECTED = new EvaluationConfidence(0);

    // Always nonnegative
    private final int confidence;

    public EvaluationConfidence(int confidence) {
        this.confidence = Math.max(confidence, 0);
    }

    @Override public int compareTo(EvaluationConfidence other) {
        if (this.confidence > other.confidence) {
            return 1;
        } else if (this.confidence < other.confidence) {
            return -1;
        } else {
            return 1;
        }
    }
}
