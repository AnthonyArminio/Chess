package chess.intel.strategies;

import chess.logic.ChessPosition;

/**
 * Abstract class that represents a strategy for evaluating a ChessPosition
 */
public abstract class Strategy {

    /**
     * Returns a double value corresponding to this Strategy's evaluation of a specified ChessPosition.
     * Positive values are advantageous for White, negative values are advantageous for Black.
     * @param position the position to evaluate.
     * @return A double evaluation of the position.
     */
    public abstract double evaluate(ChessPosition position);
}
