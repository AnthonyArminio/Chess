package chess.intel.strategy;

/**
 * Represents an evaluation of a ChessPosition
 */
public class Evaluation implements Comparable<Evaluation> {

    public static final Evaluation CHECKMATE_FOR_WHITE = new Evaluation(Float.POSITIVE_INFINITY, 0);
    public static final Evaluation CHECKMATE_FOR_BLACK = new Evaluation(Float.NEGATIVE_INFINITY, 0);
    public static final Evaluation DRAW = new Evaluation(0);

    // raw value of the position (positive means advantageous for White, negative means advantageous for Black).
    private float value;
    // checkmate clock (number of ply until checkmate). If this is not equal to -1, value should be infinite.
    private int clock;

    public Evaluation(float value) {
        this.value = value;
        this.clock = -1;
    }

    public Evaluation(float value, int clock) {
        if (clock >= 0) {
            this.clock = clock;
            if (value >= 0) {
                this.value = Float.POSITIVE_INFINITY;
            } else {
                this.value = Float.NEGATIVE_INFINITY;
            }
        } else {
            this.clock = -1;
            this.value = value;
        }
    }

    public Evaluation step() {
        if (this.clock >= 0) {
            return new Evaluation(this.value, this.clock + 1);
        }
        return this;
    }

    /**
     * Compares this Evaluation to a specified other Evaluation. If this Evaluation is more advantageous
     * for White, this returns 1. If it is more advantageous for Black, this returns -1. Otherwise, this
     * returns 0.
     * @param other the Evaluation to compare
     * @return 1 if this Evaluation is better for White, -1 if it is better for Black, or 0 otherwise
     */
    public int compareTo(Evaluation other) {
        if (this.clock < 0) {
            if (other.clock < 0) {
                if (this.value > other.value) {
                    return 1;
                } else if (this.value < other.value) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                if (other.value >= 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        } else {
            if (other.clock < 0) {
                if (this.value >= 0) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                if (this.clock < other.clock) {
                    return 1;
                } else if (this.clock > other.clock) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    public String toString() {
        if (this.clock >= 0) {
            if (this.value < 0) {
                return "-M" + this.clock;
            } else {
                return "M" + this.clock;
            }
        }
        return "" + this.value;
    }
}
