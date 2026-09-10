package chess.intel.strategy;

import chess.intel.strategy.util.PositionalWeights;
import chess.logic.ChessPosition;

public class PositionalStrategy extends MaterialisticStrategy {

    private PositionalWeights weights;

    public PositionalStrategy(PositionalWeights weights) {
        this.weights = weights;
    }

    @Override public Evaluation subjectiveEvaluate(ChessPosition position) {

        return new Evaluation(position.getMaterialEvaluation());
    }
}
