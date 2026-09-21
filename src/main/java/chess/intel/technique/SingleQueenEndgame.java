package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;

public class SingleQueenEndgame extends Technique {
    
    public SingleQueenEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.containsMatchingPiece(pos, 'Q', pos.colorToMove());
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        return null; // TO DO: implement queen endgame logic
    }
}
