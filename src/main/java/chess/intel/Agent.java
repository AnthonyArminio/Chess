package chess.intel;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import chess.application.ChessGame;
import chess.intel.strategy.Evaluation;
import chess.intel.strategy.Strategy;
import chess.intel.util.DataMath;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.condition.MoveFilter;

/**
 * Class that represents a computer player.
 */
public class Agent extends Player {
    @Expose private Strategy strategy;
    @Expose private int depth;
    @Expose private boolean printMoves;

    public Agent(Strategy strategy, int depth, boolean printMoves) {
        super(false);
        this.strategy = strategy;
        this.depth = depth;
        this.printMoves = printMoves;
    }

    @Override public void alertToMove(ChessGame game) {
        this.currentGame = game;
        this.hasTurn = true;

        makeMove(findBestMove(game.getPosition()));
    }

    @Override public void makeMove(ChessMove move) {
        if (this.hasTurn) {
            this.hasTurn = false;

            this.currentGame.makeMove(move);
        }
    }

    /**
     * Finds the best move in a specified position according to this Agent's Strategy. If there are no moves in the
     * position, returns null.
     * @param position The position to analyze
     * @return The best move in the position, or null if there are no moves.
     */
    public ChessMove findBestMove(ChessPosition position) {

        char color = position.colorToMove();

        Evaluation alphabeta;
        if (color == 'w') {
            alphabeta = Evaluation.CHECKMATE_FOR_WHITE;
        } else {
            alphabeta = Evaluation.CHECKMATE_FOR_BLACK;
        }

        Evaluation eval = minimaxEvaluate(position, null, color, this.depth, alphabeta);

        if (this.printMoves) {
            System.out.println("\n" + eval.pathString(this.currentGame.getMoveNumber(), color) + " (" + eval.evalString() + ")");
        }

        return eval.getBestMove();
    }

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
    private Evaluation minimaxEvaluate(ChessPosition position, ChessMove previousMove, char color, int depth, Evaluation alphabeta) {

        // standard evaluation/apply techniques
        Evaluation objectiveEvaluation = this.strategy.objectiveEvaluate(position);
        if (objectiveEvaluation != Evaluation.UNDECIDED) {
            return objectiveEvaluation;
        }

        MoveFilter filter = (p, m) -> true;
        if (depth <= 0) {
            if (!this.strategy.evaluateStability(position, previousMove)) {
                filter = this.strategy.unstableCaseFilter(position, previousMove);
            } else {
                // base case 1: reached the max depth and the position is stable
                return this.strategy.subjectiveEvaluate(position);
            }
        }

        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position, filter);
        if (possibleMoves.isEmpty()) { // only occurs when a filter is used; otherwise, there will always be at least one legal move at this point
            // base case 2: reached an unstable position with no way to respond
            return this.strategy.subjectiveEvaluate(position);
        }

        if (color == 'w') {
            // maximizing case

            // sort moves before analyzing them
            
            ArrayList<Evaluation> staticEvaluations = new ArrayList<>();
            for (ChessMove move : possibleMoves) {
                staticEvaluations.add(this.strategy.evaluate(position.afterMove(move)).step(move));
            }
            DataMath.quickSort(staticEvaluations, true);
            possibleMoves.clear();
            for (Evaluation staticEval : staticEvaluations) {
                possibleMoves.add(staticEval.getBestMove());
                //staticEval.getBestMove().printMove();
            }
            

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_BLACK;
            Evaluation eval;
            ChessMove bestMove = possibleMoves.get(0);
            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval);
                
                // alpha-beta pruning
                if (eval.compareTo(alphabeta) >= 0) {
                    return eval.step(move);
                }

                if (eval.compareTo(bestEval) > 0) {
                    bestMove = move;
                    bestEval = eval;
                }
            }

            return bestEval.step(bestMove);

        } else { 
            // minimizing case

            // sort moves before analyzing them
            
            ArrayList<Evaluation> staticEvaluations = new ArrayList<>();
            for (ChessMove move : possibleMoves) {
                staticEvaluations.add(this.strategy.evaluate(position.afterMove(move)).step(move));
            }
            DataMath.quickSort(staticEvaluations, false);
            possibleMoves.clear();
            for (Evaluation staticEval : staticEvaluations) {
                possibleMoves.add(staticEval.getBestMove());
                //staticEval.getBestMove().printMove();
            }
            

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_WHITE;
            Evaluation eval;
            ChessMove bestMove = possibleMoves.get(0);
            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), move, ChessLogic.opponentOf(color), depth - 1, bestEval);

                // alpha-beta pruning
                if (eval.compareTo(alphabeta) <= 0) {
                    return eval.step(move);
                }

                if (eval.compareTo(bestEval) < 0) {
                    bestEval = eval;
                    bestMove = move;
                }
            }

            return bestEval.step(bestMove);
        }
    }

    public Strategy getStrategy() {
        return this.strategy;
    }
}
