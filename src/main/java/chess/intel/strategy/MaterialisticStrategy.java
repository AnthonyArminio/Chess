package chess.intel.strategy;

import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.condition.MoveFilter;

public class MaterialisticStrategy extends Strategy {

    @Override public Evaluation subjectiveEvaluate(ChessPosition position) {

        return new Evaluation(position.getMaterialEvaluation());
    }

    @Override public boolean evaluateStability(ChessPosition position, ChessMove previousMove) {
        // If a capture happens, continue looking ahead
        return !(previousMove.isCapture() || previousMove.isPromotion());
    }

    @Override public MoveFilter unstableCaseFilter(ChessPosition position, ChessMove previousMove) {
        // If a capture happens past the max depth, only look at direct recaptures
        return (p, m) -> m.getEnd() == previousMove.getEnd();
    }
}
