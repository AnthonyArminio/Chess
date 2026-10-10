package chess.intel.strategy;

import chess.logic.ChessMove;
import chess.logic.util.ChessLogic;
import chess.intel.strategy.util.EvaluationConfidence;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an evaluation of a ChessPosition
 */
public class Evaluation implements Comparable<Evaluation> {

    public static final Evaluation CHECKMATE_FOR_WHITE = new Evaluation(Float.POSITIVE_INFINITY, 0, true).setConfidence(EvaluationConfidence.OBJECTIVE);
    public static final Evaluation CHECKMATE_FOR_BLACK = new Evaluation(Float.NEGATIVE_INFINITY, 0, true).setConfidence(EvaluationConfidence.OBJECTIVE);
    public static final Evaluation DRAW = new Evaluation(0, 0, true).setConfidence(EvaluationConfidence.OBJECTIVE);
    public static final Evaluation TECHNICAL_DRAW = new Evaluation(0, true).setConfidence(EvaluationConfidence.TECHNICAL);
    public static final Evaluation INCONCLUSIVE = new Evaluation(0, true).setConfidence(EvaluationConfidence.INCONCLUSIVE);

    // raw value of the position (positive means advantageous for White, negative means advantageous for Black).
    private float value;
    // checkmate clock (number of ply until checkmate). If this is not equal to -1, value should be infinite.
    private int clock;
    // the sequence of moves that led to this Evaluation (stored in reverse)
    private List<ChessMove> movePath;
    // the best move according to the evaluation (updates as this Evaluation steps up the tree)
    private ChessMove bestMove = null;
    private EvaluationConfidence confidence = EvaluationConfidence.SUBJECTIVE;
    private boolean mustCopy = false;

    public static Evaluation mateIn(int clock, char color) {
        return new Evaluation(clock, color);
    }

    public static Evaluation drawIn(int clock) {
        return new Evaluation(0, clock);
    }

    public Evaluation(float value) {
        this.value = value;
        this.clock = -1;
        this.movePath = new ArrayList<>();
    }

    public Evaluation(float value, boolean mustCopy) {
        this.value = value;
        this.clock = -1;
        this.movePath = new ArrayList<>();
        this.mustCopy = mustCopy;
    }

    public Evaluation(float value, char color) {
        if (color == 'w') {
            this.value = Math.abs(value);
        } else {
            this.value = -Math.abs(value);
        }
        this.clock = -1;
        this.movePath = new ArrayList<>();
    }

    public Evaluation(int clock, char color) {
        if (color == 'w') {
            initialize(Float.POSITIVE_INFINITY, clock);
        } else {
            initialize(Float.NEGATIVE_INFINITY, clock);
        }
        this.movePath = new ArrayList<>();
    }

    public Evaluation(float value, int clock) {
        initialize(value, clock);
        this.movePath = new ArrayList<>();
    }

    public Evaluation(float value, int clock, boolean mustCopy) {
        initialize(value, clock);
        this.movePath = new ArrayList<>();
        this.mustCopy = mustCopy;
    }

    /**
     * Corrects value and clock behaviour for the constructors.
     * @param value
     * @param clock
     */
    private void initialize(float value, int clock) {
        if (clock >= 0) {
            this.clock = clock;
            if (value > 0) {
                this.value = Float.POSITIVE_INFINITY;
            } else if (value > 0) {
                this.value = Float.NEGATIVE_INFINITY;
            } else {
                this.value = value;
            }
        } else {
            this.clock = -1;
            this.value = value;
        }
    }

    public Evaluation setConfidence(EvaluationConfidence confidence) {
        this.confidence = confidence;
        return this;
    }

    public Evaluation step() {
        Evaluation eval = mustCopy ? this.copy() : this;
        if (eval.clock >= 0) {
            eval.clock++;
        }
        return eval;
    }

    public Evaluation step(ChessMove move) {
        Evaluation eval = mustCopy ? this.copy() : this;
        if (eval.clock >= 0) {
            eval.clock++;
        }
        return eval.addMove(move);
    }

    public Evaluation addMove(ChessMove move) {
        this.bestMove = move;
        this.movePath.add(move);
        return this;
    }

    public ChessMove getBestMove() {
        return this.bestMove;
    }

    public List<ChessMove> getMovePath() {
        return this.movePath;
    }

    public EvaluationConfidence getConfidence() {
        return this.confidence;
    }

    public Evaluation copy() {
        return new Evaluation(this.value, this.clock, false).setConfidence(this.confidence);
    }

    /**
     * Compares this Evaluation to a specified other Evaluation. If this Evaluation is more advantageous
     * for White, this returns 1. If it is more advantageous for Black, this returns -1. Otherwise, this
     * returns 0.
     * @param other the Evaluation to compare
     * @return 1 if this Evaluation is better for White, -1 if it is better for Black, or 0 otherwise
     */
    @Override public int compareTo(Evaluation other) {
        
        if (this.compareConfidence(other) > 0) {
            if (this.value > 0) {
                return 1;
            } else if (this.value < 0) {
                return -1;
            }
        } else if (this.compareConfidence(other) < 0) {
            if (other.value > 0) {
                return -1;
            } else if (other.value < 0) {
                return 1;
            }
        }
        
        if (this.value > other.value) {
            return 1;
        } else if (this.value < other.value) {
            return -1;
        } else if (!(this.clock < 0 || other.clock < 0)) {
            if (this.clock == other.clock || this.value == 0) {
                return 0;
            } else if (this.clock < other.clock ^ this.value < 0) {
                return 1;
            } else {
                return -1;
            }
        } else {
            return 0;
        }
    }

    public int compareConfidence(Evaluation other) {
        return this.confidence.compareTo(other.confidence);
    }

    /**
     * Compares two Evaluations. If both are equal, this function favors the Evaluation with the greater clock.
     * @param other
     * @param color
     * @return
     */
    /**
    public int compareAndProlong(Evaluation other, char color) {
        int comparison = this.compareTo(other);
        if (comparison == 0) {
            if (this.clock == other.clock) {
                return 0;
            } else if (!(this.clock < 0 || other.clock < 0)) {
                return ChessLogic.colorToInt(color) * (this.clock < other.clock ? -1 : 1);
            } else {
                return ChessLogic.colorToInt(color) * (this.clock < other.clock ? 1 : -1);
            }
        } else {
            return comparison;
        }
    }
    */

    /**
     * Returns the sum of this evaluation and another evaluation.
     * @param other
     * @return
     */
    public Evaluation add(Evaluation other) {
        int newClock;
        if (!(this.clock < 0 || other.clock < 0)) {
            newClock = other.clock < this.clock ? other.clock : this.clock;
        } else {
            newClock = other.clock > this.clock ? other.clock : this.clock;
        }
        return new Evaluation(this.value + other.value, newClock);
    }

    public String evalString() {
        if (this.clock >= 0) {
            if (this.value < 0) {
                return "-M" + this.clock;
            } else if (this.value > 0) {
                return "M" + this.clock;
            }
        }
        return "" + this.value;
    }

    public String pathString(int startingTurn, char startingColor) {

        int turnNum = startingTurn;
        char color = startingColor;

        String base = "";
        if (color == 'b') {
            base += "" + turnNum + ". ...";
        }

        for (ChessMove move : this.movePath.reversed()) {

            if (color == 'w') {
                base += " " + turnNum + ".";
            } else {
                turnNum++;
            }
            base += " " + move.getNotation();
            color = ChessLogic.opponentOf(color);
        }

        return base;
    }
}
