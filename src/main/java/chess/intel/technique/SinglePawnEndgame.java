package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;

public class SinglePawnEndgame extends Technique {

    public SinglePawnEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'P') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        return Evaluation.UNDECIDED;
    }
}
