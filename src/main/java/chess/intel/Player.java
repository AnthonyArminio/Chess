package chess.intel;

import chess.application.ChessGame;
import chess.logic.ChessMove;

/**
 * Class that represents a chess player.
 */
public class Player {

    protected char color;
    protected ChessGame currentGame;
    protected boolean hasTurn;

    public Player(char color) {
        this.color = color;
        this.hasTurn = false;
    }

    public void alertToMove(ChessGame game) {
        this.currentGame = game;
        this.hasTurn = true;
    }

    public void makeMove(ChessMove move) {
        if (this.hasTurn) {
            this.hasTurn = false;
            this.currentGame.makeMove(move);
        }
    }

    public ChessGame getCurrentGame() {
        return this.currentGame;
    }
}
