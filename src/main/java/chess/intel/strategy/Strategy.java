package chess.intel.strategy;

import java.util.ArrayList;

import chess.intel.technique.Technique;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.condition.MoveFilter;

/**
 * Abstract class that represents a strategy for evaluating a ChessPosition
 */
public abstract class Strategy {

    protected ArrayList<Technique> techniques = new ArrayList<>();
    protected Technique activatedTechnique = null;

    public Evaluation evaluate(ChessPosition position) {
        Evaluation baseEval = objectiveEvaluate(position);
        if (baseEval == Evaluation.UNDECIDED) {
            return subjectiveEvaluate(position);
        }
        return baseEval;
    }

    public Evaluation objectiveEvaluate(ChessPosition position) {
        Evaluation eval = ChessLogic.getBaseEvaluation(position);
        for (Technique technique : this.techniques) {
            if (eval != Evaluation.UNDECIDED) return eval;
            if (technique.isActive()) {
                eval = technique.evaluate(position);
            }
        }
        return eval;
    }

    public boolean evaluateStability(ChessPosition position, ChessMove previousMove) {
        return true;
    }

    public MoveFilter unstableCaseFilter(ChessPosition position, ChessMove previousMove) {
        return (p, m) -> false;
    }

    public void addTechnique(Technique t) {
        this.techniques.add(t);
    }

    /**
     * Updates the current active technique
     * @param position
     */
    public void tryActivateTechniques(ChessPosition position) {
        for (Technique t : this.techniques) {
            t.tryActivate(position);
        }
        /**
        if (this.activatedTechnique != null && !this.activatedTechnique.tryActivate(position)) {
            this.activatedTechnique = null;
        }
        
        int t = 0;
        while (this.activatedTechnique == null && t < this.techniques.size()) {
            if (this.techniques.get(t).tryActivate(position)) {
                this.activatedTechnique = this.techniques.get(t);
            }
            t++;
        }
            */
    }

    /**
     * Returns a double value corresponding to this Strategy's evaluation of a specified ChessPosition.
     * Positive values are advantageous for White, negative values are advantageous for Black.
     * @param position the position to evaluate.
     * @return A double evaluation of the position.
     */
    public abstract Evaluation subjectiveEvaluate(ChessPosition position);
}
