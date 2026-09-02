package chess.intel.strategy;

import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.condition.MoveFilter;

/**
 * Abstract class that represents a strategy for evaluating a ChessPosition
 */
public abstract class Strategy {

    public Evaluation evaluate(ChessPosition position) {
        Evaluation baseEval = ChessLogic.getBaseEvaluation(position);
        if (baseEval == Evaluation.UNDECIDED) {
            return subjectiveEvaluate(position);
        }
        return baseEval;
    }

    public boolean evaluateStability(ChessPosition position, ChessMove previousMove) {
        return true;
    }

    public MoveFilter unstableCaseFilter(ChessPosition position, ChessMove previousMove) {
        return (p, m) -> false;
    }

    /**
     * Returns a double value corresponding to this Strategy's evaluation of a specified ChessPosition.
     * Positive values are advantageous for White, negative values are advantageous for Black.
     * @param position the position to evaluate.
     * @return A double evaluation of the position.
     */
    public abstract Evaluation subjectiveEvaluate(ChessPosition position);
}
