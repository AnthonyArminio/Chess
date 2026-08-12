package chess.intel;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import chess.application.ChessGame;
import chess.intel.strategy.Evaluation;
import chess.intel.strategy.Strategy;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;

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

        Evaluation eval = minimaxEvaluate(position, color, this.depth, alphabeta);

        if (this.printMoves) {
            System.out.println("\n" + eval.pathString(this.currentGame.getMoveNumber(), color) + " (" + eval.evalString() + ")");
        }

        return eval.getBestMove();
    }

    /**
     * Performs a recursive minimax search of all possible moves from a given starting position to a certain
     * depth, with alpha-beta pruning. Returns an evaluation of the position based on the search.
     * @param position the position to evaluate
     * @param color the color to play for ('w' to maximize, 'b' to minimize).
     * @param depth the depth to search (ply)
     * @param alphabeta the best achieved value of the siblings of this instance of the method call.
     * @return An Evaluation representing how advantageous the given position is for one player
     */
    private Evaluation minimaxEvaluate(ChessPosition position, char color, int depth, Evaluation alphabeta) {

        // standard evaluation
        if (ChessLogic.isCheckmate(position)) {
            if (position.colorToMove() == 'b') {
                return Evaluation.CHECKMATE_FOR_WHITE;
            } else {
                return Evaluation.CHECKMATE_FOR_BLACK;
            }
        } else if (ChessLogic.isStalemate(position)) {
            return Evaluation.DRAW;
        } else if (ChessLogic.isThreefoldRepetition(position)) {
            return Evaluation.DRAW;
        } else if (ChessLogic.isInsufficientMaterial(position)) {
            return Evaluation.DRAW;
        }

        // base case
        if (depth == 0) {
            return this.strategy.evaluate(position);
        }

        // due to the standard evaluation, the size of this list is expected to be nonzero.
        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position);

        if (color == 'w') {
            // maximizing case

            // sort moves before analyzing them (apparently slower)
            /* 
            ArrayList<Evaluation> staticEvaluations = new ArrayList<Evaluation>();
            for (ChessMove move : possibleMoves) {
                staticEvaluations.add(this.strategy.evaluate(position.afterMove(move)).step(move));
            }
            DataMath.quickSort(staticEvaluations, true);
             */

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_BLACK;
            Evaluation eval = null;
            ChessMove bestMove = possibleMoves.get(0);
            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);
                
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

            // sort moves before analyzing them (apparently slower)
            /* 
            ArrayList<Evaluation> staticEvaluations = new ArrayList<Evaluation>();
            for (ChessMove move : possibleMoves) {
                staticEvaluations.add(this.strategy.evaluate(position.afterMove(move)).step(move));
            }
            DataMath.quickSort(staticEvaluations, false);
             */

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_WHITE;
            Evaluation eval = null;
            ChessMove bestMove = possibleMoves.get(0);
            //ChessMove move = null;
            for (ChessMove move : possibleMoves) {
            //for (Evaluation staticEval : staticEvaluations) {
                //move = staticEval.getBestMove();
                eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);                

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
