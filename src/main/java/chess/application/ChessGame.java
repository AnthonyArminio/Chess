package chess.application;

import java.util.ArrayList;
import java.util.List;

import chess.display.ChessBoard;
import chess.intel.Player;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import javafx.application.Platform;

/**
 * Class that manages a game between two Players.
 */
public class ChessGame {

    private Player whitePlayer;
    private Player blackPlayer;
    private ChessPosition currentPosition;
    private List<ChessPosition> positions;
    private List<ChessMove> moves;
    private ChessBoard board;
    private Player playerToMove;
    private int moveNumber;
    private int plyNumber;
    private boolean isIdle;
    private boolean isOver;
    private boolean printMoves;
    
    public ChessGame(Player whitePlayer, Player blackPlayer, boolean printMoves) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPosition = new ChessPosition();
        this.positions = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.playerToMove = null;

        this.printMoves = printMoves;

        this.board = null;

        this.moveNumber = 0;
        this.plyNumber = 0;

        this.isIdle = true;
        this.isOver = false;
    }

    public ChessGame(Player whitePlayer, Player blackPlayer, ChessPosition startingPosition, boolean printMoves) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.currentPosition = startingPosition;
        this.positions = new ArrayList<>();
        this.moves = new ArrayList<>();
        this.playerToMove = null;

        this.printMoves = printMoves;

        this.board = null;

        this.moveNumber = 0;
        this.plyNumber = 0;

        this.isIdle = true;
        this.isOver = false;
    }

    public void start() {
        this.isIdle = false;
        this.moveNumber = 1;
        this.playerToMove = this.currentPosition.colorToMove() == 'w' ? this.whitePlayer : this.blackPlayer;
        this.positions.add(this.currentPosition.copy());
        if (!handleGameEnd()) {
            Thread t = new Thread(() -> this.playerToMove.alertToMove(this));
            t.setDaemon(true);
            t.start();
        }
    }

    public void stop() {
        this.isOver = true;
        this.whitePlayer.interrupt();
        this.blackPlayer.interrupt();
    }

    /**
     * Makes a move and advances the game. The move is assumed to be legal.
     * @param move the ChessMove to make
     */
    public void makeMove(ChessMove move) {
        this.currentPosition.makeMove(move);
        this.plyNumber++;
        this.positions.add(this.currentPosition.copy());
        this.moves.add(move);
        
        if (this.printMoves) {
            if (this.currentPosition.colorToMove() == 'b') {
                System.out.printf("%d. %s ", this.moveNumber, move.getNotation());
            } else {
                System.out.printf("%s\n", move.getNotation());
            }
        }

        handleGameEnd();

        if (this.board != null) {
            Platform.runLater(() -> this.board.update(move));
        }

        if (!this.isOver) {
            advanceGame();
        }
    }

    protected void advanceGame() {
        passTurn();
        if (this.playerToMove == this.whitePlayer) {
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
    protected boolean handleGameEnd() {
        boolean gameEnd = false;
        if (ChessLogic.isCheckmate(this.currentPosition)) {
            if (this.currentPosition.colorToMove() == 'b') {
                onWinForWhite();
            } else {
                onWinForBlack();
            }
            gameEnd = true;
        } else if (ChessLogic.isStalemate(this.currentPosition)) {
            onDraw(0);
            gameEnd = true;
        } else if (ChessLogic.isThreefoldRepetition(currentPosition)) {
            onDraw(1);
            gameEnd = true;
        } else if (ChessLogic.isFiftyMoveRule(currentPosition)) {
            onDraw(2);
            gameEnd = true;
        } else if (ChessLogic.isInsufficientMaterial(currentPosition)) {
            onDraw(3);
            gameEnd = true;
        }

        this.isOver = gameEnd;
        return gameEnd;
    }

    public Player getPlayer(char color) {
        if (color == 'w') {
            return this.whitePlayer;
        }
        return this.blackPlayer;
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

    public ChessPosition getCurrentPosition() {
        return this.currentPosition;
    }

    public ChessPosition getPosition(int ply) {
        if (ply < 0 || ply >= this.positions.size()) {
            throw new IndexOutOfBoundsException("getPosition: Game position index out of bounds.");
        }
        return this.positions.get(ply);
    }

    public ChessMove getMove(int ply) {
        if (ply <= 0 || ply - 1 >= this.moves.size()) {
            throw new IndexOutOfBoundsException("getMove: Game move index out of bounds.");
        }
        return this.moves.get(ply - 1);
    }

    public int getMoveNumber() {
        return this.moveNumber;
    }

    public int getPlyNumber() {
        return this.plyNumber;
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

    public boolean isOver() {
        return this.isOver;
    }
}
