package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.intel.strategy.TechnicalEvaluation;
import chess.logic.ChessPosition;
import chess.logic.util.condition.PositionalCondition;

/**
 * Represents a technique (known position or type of position) that enables an Agent to convert a basic winning position.
 */
public abstract class Technique {

    protected boolean active = false;
    protected PositionalCondition activateCondition = (pos) -> pos.countPieces() <= 4;

    public boolean tryActivate(ChessPosition position) {
        this.active = activateCondition.test(position);
        return this.active;
    }
    public boolean isActive() {
        return this.active;
    }

    /**
     * Optional function that improves performance by evaluating a position before branching. If a conclusion is reached,
     * the agent will not branch.
     * @param position
     * @return
     */
    public Evaluation objectiveEvaluate(ChessPosition position) {
        return Evaluation.INCONCLUSIVE;
    }

    /**
     * Returns the known evaluation of a position relevant to this Technique. If the position is unknown, returns `TechnicalEvaluation.TECHNICAL_INCONCLUSIVE`.
     * To be useful, mathematically determine a worst-case evaluation which still strictly decreases as the computer makes progress towards a
     * terminal state.
     * @param position The position to evaluate
     * @return The known evaluation of the position, or `TechnicalEvaluation.TECHNICAL_INCONCLUSIVE` if the position is unknown.
     */
    public abstract TechnicalEvaluation evaluate(ChessPosition position);
}
