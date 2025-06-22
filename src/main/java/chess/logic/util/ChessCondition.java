package chess.logic.util;

import chess.logic.ChessPosition;
import chess.logic.ChessPiece;
import chess.logic.ChessMove;

public interface ChessCondition {
    public boolean test(ChessPosition position, int start, int end);
}
