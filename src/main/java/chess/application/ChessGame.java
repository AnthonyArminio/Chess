package chess.application;

import chess.intel.Player;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.display.ChessBoard;

/**
 * Class that manages a game between two Players.
 */
public class ChessGame {

    private Player whitePlayer;
    private Player blackPlayer;
    private ChessPosition position;
    private ChessBoard board;
    private Player playerToMove;
    private boolean isIdle;
    
    public ChessGame(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.playerToMove = null;

        this.board = null;

        this.isIdle = true;

        start(); //
    }

    public ChessGame(Player whitePlayer, Player blackPlayer, ChessBoard board) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.playerToMove = null;

        this.board = board;
        this.board.loadGame(this);

        this.isIdle = true;

        start(); //
    }

    public void start() {
        this.isIdle = false;
        this.playerToMove = this.whitePlayer;
        this.playerToMove.alertToMove(this);
    }

    public void makeMove(ChessMove move) {
        if (this.board != null) {
            this.board.makeMove(move);
        }
        this.position.makeMove(move);

        advanceGame();
    }

    private void advanceGame() {
        passTurn();
        // increment move counter here
        this.playerToMove.alertToMove(this);

    }

    private void passTurn() {
        if (this.playerToMove == this.whitePlayer) {
            this.playerToMove = this.blackPlayer;
        } else {
            this.playerToMove = this.whitePlayer;
        }
    }

    public Player getPlayerToMove() {
        return this.playerToMove;
    }

    public ChessPosition getPosition() {
        return this.position;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    public ChessBoard getBoard() {
        return this.board;
    }

    public boolean isIdle() {
        return this.isIdle;
    }
}
