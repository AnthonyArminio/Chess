package chess.logic.util.condition;

import chess.logic.ChessPiece;
import chess.logic.ChessPosition;

/**
 * Functional interface which keeps track of and updates a running total while iterating over all ChessPieces in a position.
 */
public interface Accumulator {
    /**
     * Updates the running total based on the next piece encountered
     * @param value
     * @param piece
     * @param square
     * @return the updated value
     */
    public abstract float update(float value, ChessPosition position, ChessPiece piece, int square);
}
