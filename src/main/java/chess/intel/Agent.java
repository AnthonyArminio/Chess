package chess.intel;

import chess.application.ChessGame;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.intel.strategy.Strategy;
import chess.intel.strategy.Evaluation;
import chess.logic.ChessMove;

import java.util.ArrayList;

/**
 * Class that represents a computer player.
 */
public class Agent extends Player {
    private Strategy strategy;
    private int depth;

    public Agent(char color, Strategy s, int depth) {
        super(color, false);
        this.strategy = s;
        this.depth = depth;
    }

    @Override public void alertToMove(ChessGame game) {
        this.currentGame = game;
        this.hasTurn = true;

        Thread t = new Thread(() -> makeMove(findBestMove(game.getPosition())));
        t.setDaemon(true);
        t.start();
    }

    public ChessMove findBestMove(ChessPosition position) {

        Evaluation alphabeta;
        if (this.color == 'w') {
            alphabeta = Evaluation.CHECKMATE_FOR_WHITE;
        } else {
            alphabeta = Evaluation.CHECKMATE_FOR_BLACK;
        }

        Evaluation eval = minimaxEvaluate(position, this.color, this.depth, alphabeta);

        System.out.println("\n" + eval.pathString(this.currentGame.getMoveNumber(), this.color) + " (" + eval.evalString() + ")");

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
        }

        // base case
        if (depth == 0) {
            return this.strategy.evaluate(position);
        }

        // due to the standard evaluation, the size of this list is expected to be nonzero.
        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position);

        if (color == 'w') { 
            // maximizing case

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_BLACK;
            Evaluation eval = null;
            ChessMove bestMove = possibleMoves.get(0);
            for (ChessMove move : possibleMoves) {
                eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);
                
                // alpha-beta pruning
                if (eval.compareTo(alphabeta) > 0) {
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

            Evaluation bestEval = Evaluation.CHECKMATE_FOR_WHITE;
            Evaluation eval = null;
            ChessMove bestMove = possibleMoves.get(0);
            for (ChessMove move : possibleMoves) {
                eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);                

                // alpha-beta pruning
                if (eval.compareTo(alphabeta) < 0) {
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
}
