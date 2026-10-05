package chess.intel.strategy;

import java.util.ArrayList;

import chess.intel.technique.Technique;
import chess.intel.util.DataMath;
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
        if (baseEval == Evaluation.INCONCLUSIVE) {
            baseEval = technicalEvaluate(position);
        }
        if (baseEval == Evaluation.INCONCLUSIVE) {
            baseEval = subjectiveEvaluate(position);
        }
        return baseEval;
    }

    public Evaluation objectiveEvaluate(ChessPosition position) {
        Evaluation baseEval = ChessLogic.getBaseEvaluation(position);
        if (baseEval == Evaluation.INCONCLUSIVE && this.activatedTechnique != null) {
            baseEval = this.activatedTechnique.objectiveEvaluate(position);
        }
        return baseEval;
    }

    private Evaluation technicalEvaluate(ChessPosition position) {
        if (this.activatedTechnique != null) {
            return this.activatedTechnique.evaluate(position);
        }
        return Evaluation.INCONCLUSIVE;
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
    }

    /**
     * Returns a double value corresponding to this Strategy's evaluation of a specified ChessPosition.
     * Positive values are advantageous for White, negative values are advantageous for Black.
     * @param position the position to evaluate.
     * @return A double evaluation of the position.
     */
    public abstract Evaluation subjectiveEvaluate(ChessPosition position);

    /**
     * Performs a recursive minimax search of all possible moves from a given starting position to a certain
     * depth, with alpha-beta pruning. Returns an evaluation of the position based on the search.
     * @param position the position to evaluate
     * @param previousMove the move that was most recently made in the position (can be null)
     * @param color the color to play for ('w' to maximize, 'b' to minimize).
     * @param depth the depth to search (ply)
     * @param alphabeta the best achieved value of the siblings of this instance of the method call.
     * @return An Evaluation representing how advantageous the given position is for one player
     */
    public Evaluation minimaxEvaluate(ChessPosition position, ChessMove previousMove, char color, int depth, Evaluation alphabeta) {

        // objective evaluation
        Evaluation objectiveEval = this.objectiveEvaluate(position);
        if (objectiveEval != Evaluation.INCONCLUSIVE) {
            return objectiveEval;
        }

        MoveFilter filter = (p, m) -> true;
        if (depth <= 0) {
            if (previousMove != null && !this.evaluateStability(position, previousMove)) {
                filter = this.unstableCaseFilter(position, previousMove);
            } else {
                // base case 1: reached the max depth and the position is stable
                return this.evaluate(position);
            }
        }

        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position, filter);
        if (possibleMoves.isEmpty()) { // only occurs when a filter is used; otherwise, there will always be at least one legal move at this point
            // base case 2: reached an unstable position with no way to respond
            return this.evaluate(position);
        }

        // sort moves before analyzing them
        ArrayList<Evaluation> staticEvaluations = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            staticEvaluations.add(this.evaluate(position.afterMove(move)).step(move));
        }
        DataMath.quickSort(staticEvaluations, color == 'w');
        possibleMoves.clear();
        for (Evaluation staticEval : staticEvaluations) {
            possibleMoves.add(staticEval.getBestMove());
            //staticEval.getBestMove().printMove();
        }

        Evaluation eval;
        Evaluation bestEval = Evaluation.REJECTED;
        ChessMove bestMove = null;

        if (color == 'w') {
            // maximizing case            

            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval);
                
                if (eval != Evaluation.REJECTED) {
                    // alpha-beta pruning
                    if (alphabeta != Evaluation.REJECTED && eval.compareTo(alphabeta) >= 0) {
                        return eval.step(move);
                    }

                    if (bestEval == Evaluation.REJECTED || eval.compareTo(bestEval) > 0) {
                        bestMove = move;
                        bestEval = eval;
                    }
                }
            }

        } else { 
            // minimizing case            

            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval);

                if (eval != Evaluation.REJECTED) {
                    // alpha-beta pruning
                    if (alphabeta != Evaluation.REJECTED && eval.compareTo(alphabeta) <= 0) {
                        return eval.step(move);
                    }

                    if (bestEval == Evaluation.REJECTED || eval.compareTo(bestEval) < 0) {
                        bestEval = eval;
                        bestMove = move;
                    }
                }
            }
        }

        if (bestEval == Evaluation.REJECTED) {
            return this.evaluate(position);
        }

        return bestEval.step(bestMove);
    }
}
