package chess.intel.technique;

import chess.logic.util.ChessLogic;

public class SinglePawnEndgame extends Technique {

    public SinglePawnEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'P') >= 0;
        };
    }
}
