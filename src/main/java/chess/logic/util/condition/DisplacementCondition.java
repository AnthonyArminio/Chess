package chess.logic.util.condition;

import chess.logic.ChessPosition;

public interface DisplacementCondition {

    /**
     * Test a condition as a function of a ChessPosition along with a start and end location.
     * Should return true if the condition is satisfied, and false otherwise.
     * @param position the context of the move to test
     * @param start the start point of the move
     * @param end the end point of the move
     * @return true if a condition is satisfied, and false otherwise.
     */
    public boolean test(ChessPosition position, int start, int end);
}
