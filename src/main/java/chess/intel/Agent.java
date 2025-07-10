package chess.intel;

import chess.application.ChessGame;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.intel.strategy.Strategy;
import chess.logic.ChessMove;

import java.util.ArrayList;

/**
 * Class that represents a computer player.
 */
public class Agent extends Player {
    private Strategy strategy;
    private int depth;

    public Agent(char color, Strategy s, int depth) {
        super(color);
        this.isUser = false;
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

    /**
     * Returns what this Agent perceives as the best move in a given ChessPosition. The ChessPosition is assumed not
     * to be checkmate already.
     * @param position the position to analyze
     * @return the best ChessMove for this Agent
     */
    public ChessMove findBestMove(ChessPosition position) {
        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position);
        if (possibleMoves.size() == 0) {
            return null;
        }

        if (this.color == 'w') {
            double bestEval = Double.NEGATIVE_INFINITY;
            ChessMove bestMove = possibleMoves.get(0);
            for (ChessMove move : possibleMoves) {
                double eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), this.depth - 1, bestEval);
                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove = move;
                }
            }

            return bestMove;

        } else {
            double bestEval = Double.POSITIVE_INFINITY;
            ChessMove bestMove = possibleMoves.get(0);
            for (ChessMove move : possibleMoves) {
                double eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);
                if (eval < bestEval) {
                    bestEval = eval;
                    bestMove = move;
                }
            }

            return bestMove;
        }
    }

    /**
     * Performs a recursive minimax search of all possible moves from a given starting position to a certain
     * depth, with alpha-beta pruning. Returns an evaluation of the position based on the search.
     * @param position the position to evaluate
     * @param color the color to play for ('w' to maximize, 'b' to minimize).
     * @param depth the depth to search (ply)
     * @param alphabeta the best achieved value of the siblings of this instance of the method call.
     * @return A double value representing how advantageous the given position is for one player (positive for White,
     * negative for Black).
     */
    private double minimaxEvaluate(ChessPosition position, char color, int depth, double alphabeta) {
        if (depth == 0) {
            return this.strategy.evaluate(position);
        }

        ArrayList<ChessMove> possibleMoves = ChessLogic.generateLegalMoves(position);

        if (color == 'w') { 
            // maximizing case

            double bestEval = Double.NEGATIVE_INFINITY;
            for (ChessMove move : possibleMoves) {
                double eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);

                // alpha-beta pruning
                if (eval > alphabeta) {
                    return eval;
                }

                if (eval > bestEval) {
                    bestEval = eval;
                }
            }

            return bestEval;

        } else { 
            // minimizing case

            double bestEval = Double.POSITIVE_INFINITY;
            for (ChessMove move : possibleMoves) {
                double eval = minimaxEvaluate(position.afterMove(move), ChessLogic.opponentOf(color), depth - 1, bestEval);

                // alpha-beta pruning
                if (eval < alphabeta) {
                    return eval;
                }

                if (eval < bestEval) {
                    bestEval = eval;
                }
            }

            return bestEval;
        }
    }
}
