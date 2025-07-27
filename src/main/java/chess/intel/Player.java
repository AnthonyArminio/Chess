package chess.intel;

import chess.application.ChessGame;
import chess.logic.ChessMove;

import com.google.gson.annotations.Expose;

/**
 * Class that represents a chess player.
 */
public class Player {

    //protected char color;
    protected ChessGame currentGame;
    @Expose protected boolean hasTurn;
    @Expose protected boolean isUser;

    public Player(boolean isUser) {
        this.hasTurn = false;
        this.isUser = isUser;
    }

    public void alertToMove(ChessGame game) {
        this.currentGame = game;
        this.hasTurn = true;
    }

    public void makeMove(ChessMove move) {
        if (this.hasTurn) {
            this.hasTurn = false;

            Thread t = new Thread(() -> this.currentGame.makeMove(move));
            t.setDaemon(true);
            t.start();
        }
    }

    public ChessGame getCurrentGame() {
        return this.currentGame;
    }

    public boolean isUser() {
        return this.isUser;
    }
}
