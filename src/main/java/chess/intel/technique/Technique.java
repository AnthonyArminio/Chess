package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.condition.PositionalCondition;

/**
 * Represents a technique (known position or type of position) that enables an Agent to convert a basic winning position.
 */
public abstract class Technique {

    protected boolean active = false;
    protected PositionalCondition activateCondition = (pos) -> pos.countPieces() <= 4;

    public void tryActivate(ChessPosition position) {
        if (!this.active) {
            this.active = activateCondition.test(position);
        }
    }

    /**
     * Returns the known evaluation of a position relevant to this Technique. If the position is unknown, returns `Evaluation.UNDECIDED`.
     * @param position The position to evaluate
     * @return The known evaluation of the position, or `Evaluation.UNDECIDED` if the position is unknown.
     */
    public abstract Evaluation evaluate(ChessPosition position);
}
