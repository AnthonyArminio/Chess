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
    
    public ChessGame(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.board = null;
        this.playerToMove = null;
    }

    public ChessGame(Player whitePlayer, Player blackPlayer, ChessBoard board) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.board = board;
        this.playerToMove = null;
    }

    public void start() {
        this.playerToMove = this.whitePlayer;
        while (!this.isWaitingForPlayer) {

        }
    }

    public void makeMove(ChessMove move) {
        if (this.board != null) {
            this.board.makeMove(move);
        }
        this.position.makeMove(move);
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
}
