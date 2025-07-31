package chess.application;

import chess.intel.Player;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.display.ChessBoard;

import chess.logic.util.ChessLogic;

/**
 * Class that manages a game between two Players.
 */
public class ChessGame {

    private Player whitePlayer;
    private Player blackPlayer;
    private ChessPosition position;
    private ChessBoard board;
    private Player playerToMove;
    private int moveNumber;
    private boolean isIdle;
    private boolean printMoves;
    
    public ChessGame(Player whitePlayer, Player blackPlayer, boolean printMoves) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.playerToMove = null;

        this.printMoves = printMoves;

        this.board = null;

        this.moveNumber = 0;

        this.isIdle = true;
    }

    public ChessGame(Player whitePlayer, Player blackPlayer, ChessBoard board, boolean printMoves) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.position = new ChessPosition();
        this.playerToMove = null;

        this.printMoves = printMoves;

        this.board = board;
        this.board.loadGame(this);

        this.moveNumber = 0;

        this.isIdle = true;
    }

    public void start() {
        this.isIdle = false;
        this.moveNumber = 1;
        this.playerToMove = this.whitePlayer;
        this.playerToMove.alertToMove(this);
    }

    /**
     * Makes a move and advances the game. The move is assumed to be legal.
     * @param move the ChessMove to make
     */
    public void makeMove(ChessMove move) {
        if (this.board != null) {
            this.board.makeMove(move);
        }
        this.position.makeMove(move);

        if (this.printMoves) {
            if (this.position.colorToMove() == 'b') {
                System.out.printf("%d. %s ", this.moveNumber, move.getNotation());
            } else {
                System.out.printf("%s\n", move.getNotation());
            }
        }

        if (!handleGameEnd()) {
            advanceGame();
        }
    }

    private void advanceGame() {
        passTurn();
        if (playerToMove == this.whitePlayer) {
            this.moveNumber++;
        }
        this.playerToMove.alertToMove(this);

    }

    private void passTurn() {
        if (this.playerToMove == this.whitePlayer) {
            this.playerToMove = this.blackPlayer;
        } else {
            this.playerToMove = this.whitePlayer;
        }
    }

    /**
     * Evaluates the position to determine if the game should end and executes the corresponding method. 
     * Returns false if the game is not over.
     * @return true if the game is over, and false otherwise.
     */
    private boolean handleGameEnd() {
        if (ChessLogic.isCheckmate(this.position)) {
            if (this.position.colorToMove() == 'w') {
                onWinForWhite();
            } else {
                onWinForBlack();
            }
            return true;
        } else if (ChessLogic.isStalemate(this.position)) {
            onDraw(0);
            return true;
        } else if (ChessLogic.isThreefoldRepetition(position)) {
            onDraw(1);
            return true;
        } else if (ChessLogic.isFiftyMoveRule(position)) {
            onDraw(2);
            return true;
        } else if (ChessLogic.isInsufficientMaterial(position)) {
            onDraw(3);
            return true;
        }

        return false;
    }

    protected void onWinForWhite() {
        System.out.println("White wins. Move number: " + this.moveNumber);
    }

    protected void onWinForBlack() {
        System.out.println("Black wins. Move number: " + this.moveNumber);
    }

    protected void onDraw(int code) {
        System.out.println("The game is a draw. Move number: " + this.moveNumber);
    }

    public Player getPlayerToMove() {
        return this.playerToMove;
    }

    public ChessPosition getPosition() {
        return this.position;
    }

    public int getMoveNumber() {
        return this.moveNumber;
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
