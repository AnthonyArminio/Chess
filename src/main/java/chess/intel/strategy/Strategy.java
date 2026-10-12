package chess.intel.strategy;

import java.util.ArrayList;

import chess.intel.strategy.util.EvaluationConfidence;
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
    protected boolean interrupt = false;

    public Evaluation evaluate(ChessPosition position) {
        Evaluation eval = Strategy.baseEvaluate(position);
        if (eval.getConfidence() == EvaluationConfidence.INCONCLUSIVE) {
            eval = objectiveEvaluate(position);
        }
        if (eval.getConfidence() == EvaluationConfidence.INCONCLUSIVE) {
            eval = technicalEvaluate(position);
        }
        if (eval.getConfidence() == EvaluationConfidence.INCONCLUSIVE) {
            eval = subjectiveEvaluate(position);
        }
        return eval;
    }

    public static Evaluation baseEvaluate(ChessPosition position) {
        return ChessLogic.getBaseEvaluation(position);
    }

    public Evaluation objectiveEvaluate(ChessPosition position) {
        if (this.activatedTechnique != null) {
            Evaluation objectiveEval = this.activatedTechnique.objectiveEvaluate(position);
            if (objectiveEval.getConfidence() != EvaluationConfidence.INCONCLUSIVE) {
                return objectiveEval.setConfidence(EvaluationConfidence.OBJECTIVE);
            }
        }
        return Evaluation.INCONCLUSIVE;
    }

    public TechnicalEvaluation technicalEvaluate(ChessPosition position) {
        if (this.activatedTechnique != null) {
            TechnicalEvaluation technicalEval = this.activatedTechnique.evaluate(position);
            if (technicalEval.getConfidence() != EvaluationConfidence.INCONCLUSIVE) {
                return technicalEval;
            }
        }
        return TechnicalEvaluation.TECHNICAL_INCONCLUSIVE;
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
     * @throws InterruptedException If calculation is interrupted by another Thread.
     */
    public Evaluation minimaxEvaluate(ChessPosition position, ChessMove previousMove, char color, int depth, Evaluation alphabeta, boolean rootcall) throws InterruptedException {

        if (this.interrupt) {
            this.interrupt = false;
            throw new InterruptedException("Calculation interrupted.");
        }

        // base and objective evaluations
        Evaluation baseEval = Strategy.baseEvaluate(position);
        if (baseEval.getConfidence() != EvaluationConfidence.INCONCLUSIVE) {
            return baseEval;
        }
        Evaluation objectiveEval = objectiveEvaluate(position);
        if (!rootcall && objectiveEval.getConfidence() != EvaluationConfidence.INCONCLUSIVE) {
            return objectiveEval;
        }

        MoveFilter filter = (p, m) -> true;
        if (depth <= 0) {
            if (previousMove != null && !evaluateStability(position, previousMove)) {
                filter = unstableCaseFilter(position, previousMove);
            } else {
                // base case 1: reached the max depth and the position is stable
                return evaluate(position);
            }
        }

        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position, filter);
        if (possibleMoves.isEmpty()) { // only occurs when a filter is used; otherwise, there will always be at least one legal move at this point
            // base case 2: reached an unstable position with no way to respond
            return evaluate(position);
        }

        // sort moves before analyzing them
        ArrayList<Evaluation> staticEvaluations = new ArrayList<>();
        for (ChessMove move : possibleMoves) {
            staticEvaluations.add(evaluate(position.afterMove(move)).step(move));
        }
        DataMath.quickSort(staticEvaluations, color == 'w');
        possibleMoves.clear();
        for (Evaluation staticEval : staticEvaluations) {
            possibleMoves.add(staticEval.getBestMove());
            //staticEval.getBestMove().printMove();
        }

        Evaluation eval;
        Evaluation bestEval = Evaluation.INCONCLUSIVE;
        if (!rootcall) {
            TechnicalEvaluation technicalEval = technicalEvaluate(position);
            if (color == technicalEval.getOptimist()) {
                bestEval = technicalEval;
            } else if (color == 'w' && technicalEval.compareTo(alphabeta) < 0 || color == 'b' && technicalEval.compareTo(alphabeta) > 0) {
                alphabeta = technicalEval;
            }
        }
        ChessMove bestMove = null;

        if (color == 'w') {
            // maximizing case

            ChessMove move;
            boolean abExit = false;
            for (int mvIdx = 0; mvIdx < possibleMoves.size() && !abExit; mvIdx++) {
            //for (Evaluation staticEval : staticEvaluations) {
                move = possibleMoves.get(mvIdx);
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval, false);
                
                // alpha-beta pruning
                if (alphabeta.getConfidence() != EvaluationConfidence.INCONCLUSIVE && eval.compareTo(alphabeta) >= 0) {
                    bestMove = null;
                    bestEval = alphabeta;
                    abExit = true;
                    
                } else if (bestEval.getConfidence() == EvaluationConfidence.INCONCLUSIVE || eval.compareTo(bestEval) > 0) {
                    bestMove = move;
                    bestEval = eval;
                }
                
            }

        } else { 
            // minimizing case

            ChessMove move;
            boolean abExit = false;
            for (int mvIdx = 0; mvIdx < possibleMoves.size() && !abExit; mvIdx++) {
            //for (Evaluation staticEval : staticEvaluations) {
                move = possibleMoves.get(mvIdx);
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval, false);

                // alpha-beta pruning
                if (alphabeta.getConfidence() != EvaluationConfidence.INCONCLUSIVE && eval.compareTo(alphabeta) <= 0) {
                    bestMove = null;
                    bestEval = alphabeta;
                    abExit = true;
                    
                } else if (bestEval.getConfidence() == EvaluationConfidence.INCONCLUSIVE || eval.compareTo(bestEval) < 0) {
                    bestEval = eval;
                    bestMove = move;
                }
                
            }
        }

        return bestMove == null ? bestEval : bestEval.step(bestMove);
    }

    /**
     * Interrupts the long-running `minimaxEvaluate` method
     * @return
     */
    public void interrupt() {
        this.interrupt = true;
    }
}
