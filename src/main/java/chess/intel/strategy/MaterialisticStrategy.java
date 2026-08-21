package chess.intel.strategy;

import chess.logic.ChessPosition;

public class MaterialisticStrategy extends Strategy {

    @Override public Evaluation subjectiveEvaluate(ChessPosition position) {

        return new Evaluation(position.getMaterialEvaluation());
    }
}
