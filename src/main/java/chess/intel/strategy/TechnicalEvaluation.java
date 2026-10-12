package chess.intel.strategy;

import chess.intel.strategy.util.EvaluationConfidence;

public class TechnicalEvaluation extends Evaluation {

    public static final TechnicalEvaluation TECHNICAL_INCONCLUSIVE = new TechnicalEvaluation(0, true).setConfidence(EvaluationConfidence.INCONCLUSIVE);

    // Represents the color towards whom the uncertainty of the Evaluation leans.
    private final char optimist;

    public static TechnicalEvaluation mateIn(int clock, char color, char optimist) {
        return new TechnicalEvaluation(clock, color, optimist);
    }

    public static TechnicalEvaluation technicalDraw(char optimist) {
        return new TechnicalEvaluation(0, optimist);
    }

    public TechnicalEvaluation(float value, int clock, char optimist) {
        super(value, clock);
        this.optimist = optimist;
        this.confidence = EvaluationConfidence.TECHNICAL;
    }
    
    public TechnicalEvaluation(int clock, char color, char optimist) {
        super(clock, color);
        this.optimist = optimist;
        this.confidence = EvaluationConfidence.TECHNICAL;
    }

    public TechnicalEvaluation(float value, char optimist) {
        super(value);
        this.optimist = optimist;
        this.confidence = EvaluationConfidence.TECHNICAL;
    }

    public TechnicalEvaluation(float value, boolean mustCopy) {
        super(value, mustCopy);
        this.optimist = 'w';
        this.confidence = EvaluationConfidence.TECHNICAL;
    }

    public char getOptimist() {
        return this.optimist;
    }

    @Override public TechnicalEvaluation setConfidence(EvaluationConfidence confidence) {
        this.confidence = confidence;
        return this;
    }
}
