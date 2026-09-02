package chess.logic.util.condition;

import chess.logic.ChessMove;
import chess.logic.ChessPosition;

/**
 * Similar to a ChessCondition; however, only legal moves can be passed through a MoveFilter.
 */
public interface MoveFilter {
    /**
     * Test whether a move passes through the filter.
     * @param position the context of the move
     * @param move the move to test
     * @return true if the move passes the condition, or false otherwise.
     */
    public boolean test(ChessPosition position, ChessMove move);
}
