package chess.intel;

import com.google.gson.annotations.Expose;

import chess.application.ChessGame;
import chess.intel.strategy.Evaluation;
import chess.intel.strategy.Strategy;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;

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
        this.strategy.tryActivateTechniques(game.getCurrentPosition());

        ChessMove bestMove = findBestMove(game.getCurrentPosition());
        if (bestMove != null) {
            makeMove(bestMove);
        }
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

        try {
            Evaluation eval = this.strategy.minimaxEvaluate(position, null, color, this.depth, Evaluation.INCONCLUSIVE, Evaluation.INCONCLUSIVE, true);

            if (this.printMoves) {
                System.out.println("\n" + eval.pathString(this.currentGame.getMoveNumber(), color) + " (" + eval.evalString() + ")");
            }

            return eval.getBestMove();
        } catch (InterruptedException ex) {
            System.out.println("Agent succesfully interrupted.");
            return null;
        }
    }

    public Strategy getStrategy() {
        return this.strategy;
    }

    @Override public void interrupt() {
        this.hasTurn = false;
        this.strategy.interrupt();
    }
}
