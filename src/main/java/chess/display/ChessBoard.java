package chess.display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import chess.application.ChessGame;
import chess.intel.Player;
import chess.logic.ChessMove;
import chess.logic.ChessPiece;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;
import chess.application.MouseActions;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private final Group chessBoard;
    private final GridPane checkerboard;
    private Square[] squares;
    private Set<Square> legalHighlightedSquares;
    private int selectedSquare;
    private boolean isFlipped;
    private boolean flip;
    private ChessGame game;
    private Set<ChessMove> highlightedMoves;
    private BoardUISettings settings;

    private double squareSize;
    private Point2D origin;

    private boolean waitingForPromotion;
    private PromotionUI promotionUI;
    private int view;
    private int home;

    /**
     * Initializes a new chess board.
     * @param origin the top-left corner of the chess board.
     * @param flipped whether the board starts flipped (black on bottom)
     * @param flip whiether the board should flip after every move
     * @param settings board interface settings
     */
    public ChessBoard(Point2D origin, boolean flipped, boolean flip, BoardUISettings settings) {
        this.origin = origin;
        this.settings = settings;
        this.squareSize = settings.getBoardSize() / 8.0;

        this.chessBoard = new Group();
        this.checkerboard = new GridPane();
        this.chessBoard.getChildren().add(this.checkerboard);
        this.checkerboard.setPrefSize(settings.getBoardSize(), settings.getBoardSize());
        this.checkerboard.setHgap(0);
        this.checkerboard.setVgap(0);
        this.promotionUI = null;

        this.view = 0;
        this.home = 0;

        this.legalHighlightedSquares = new HashSet<>();
        this.highlightedMoves = new HashSet<>();

        makeSquares();
        this.isFlipped = flipped;
        this.flip = flip;
        drawSquares(this.isFlipped);

        this.selectedSquare = -1;

        initializeEventHandlers();
    }

    private void initializeEventHandlers() {
        this.chessBoard.setOnMousePressed(e -> onMousePressed(e));
        this.chessBoard.setOnMouseDragged(e -> onMouseDragged(e));
        this.chessBoard.setOnMouseReleased(e -> onMouseReleased(e));
        this.chessBoard.addEventFilter(KeyEvent.KEY_PRESSED, e -> onKeyPressed(e));
    }

    /**
     * Draws the squares in the appropriate locations for the checkerboard.
     */
    private void makeSquares() {

        this.squares = new Square[64];

        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                Point2D squareOrigin = new Point2D(this.origin.getX() + this.squareSize * (file - 1), 
                                                   this.origin.getY() + this.squareSize * (8 - rank));
                this.squares[GridMath.index(file, rank)] = new Square(squareOrigin, this.squareSize, file, rank, this.settings);
            }
        }
    }

    private void drawSquares(boolean flipped) {
        this.checkerboard.getChildren().clear();
        for (Square square : squares) {
            int rfile = flipped ? 9 - square.getFile() : square.getFile();
            int rrank = flipped ? 9 - square.getRank() : square.getRank();
            square.draw(this.checkerboard, rfile, rrank);
        }
    }

    /**
     * Flips the board perspective
     */
    public void flip() {
        this.isFlipped = !this.isFlipped;
        drawSquares(this.isFlipped);
    }

    /**
     * Flips the board perspective to that of a specified player ('w' or 'b')
     * @param color the color to switch to.
     */
    public void flipTo(char color) {
        if (color == 'b' ^ this.isFlipped) {
            flip();
        }
    }

    public void loadGame(ChessGame game) {
        this.game = game;
        game.setBoard(this);
        loadPosition(game.getCurrentPosition());
    }

    /**
     * Loads a given position onto the board.
     */
    private void loadPosition(ChessPosition position) {
        for (int s = 0; s < 64; s++) {             
            this.squares[s].setPiece(position.getPieceAt(s));
        }
    }

    /**
     * Loads a given position onto the board, optimized so that only the squares affected by `move` are updated.
     */
    private void loadPosition(ChessPosition position, ChessMove move) {
        for (int s : move.getAffectedSquares()) {
            this.squares[s].setPiece(position.getPieceAt(s));
        }
    }

    public void update(ChessMove move) {

        System.out.printf("Before:%d\n", this.home);
        this.home = this.game.getPlyNumber();
        System.out.printf("After:%d\n", this.home);

        if (!this.setView(this.game.getPlyNumber())) {
            unhighlightMoves();
            loadPosition(this.game.getCurrentPosition(), move);
            this.view++;
            if (settings.doPreviousMoveHighlights()) {
                highlightMove(move);
            }
        }
    
        if (!this.game.isOver() && this.flip) {
            this.flipTo(this.game.getCurrentPosition().colorToMove());
        }
    }

    public boolean setView(int ply) {
        if (this.view != ply) {
            if (this.home != ply) {
                cancelPromotion();
            }
            if (ply == this.view - 1) {
                loadPosition(this.game.getPosition(ply), this.game.getMove(ply + 1));
            } else if (ply == this.view + 1) {
                loadPosition(this.game.getPosition(ply), this.game.getMove(ply));
            } else {
                loadPosition(this.game.getPosition(ply));
            }
            this.view = ply;
            unhighlightMoves();
            if (ply > 0 && settings.doPreviousMoveHighlights()) {
                highlightMove(this.game.getMove(ply));
            }
            return true;
        }
        return false;
    }

    /**
     * Changes the view of this chess board to one ply back.
     * @return true if the step was successful, false otherwise
     */
    public boolean stepBack() {
        if (this.view > 0) {
            return setView(this.view - 1);
        }
        return false;
    }

    /**
     * Changes the view of this chess board to one ply forward.
     * @return true if the step was successful, false otherwise
     */
    public boolean stepForward() {
        if (this.view < this.home) {
            return setView(this.view + 1);
        }
        return false;
    }

    public boolean isAtHome() {
        return this.view == this.home;
    }

    public void setSelectedSquare(int index) {
        if (index < 0 || index >= 64) {
            throw new IndexOutOfBoundsException("setSelectedSquare: index must be between 0 and 63, inclusive.");
        }
        this.selectedSquare = index;
        if (this.settings.doLegalMoveHighlights()) {
            unhighlightLegalMoves();
            highlightLegalMoves();
        }
    }

    public void deselectSquare() {
        if (this.settings.doLegalMoveHighlights()) {
            unhighlightLegalMoves();
        }
        this.selectedSquare = -1;
    }

    private void highlightMove(ChessMove move) {
        this.getSquareAt(move.getStart()).highlight();
        this.getSquareAt(move.getEnd()).highlight();
        this.highlightedMoves.add(move);
    }

    private void unhighlightMoves() {
        for (ChessMove move : this.highlightedMoves) {
            this.getSquareAt(move.getStart()).unhighlight();
            this.getSquareAt(move.getEnd()).unhighlight();
        }
        this.highlightedMoves.clear();
    }

    private void highlightLegalMoves() {
        ArrayList<ChessMove> relevantMoves = ChessLogic.generateLegalMoves(this.getPosition(), (p, m) -> m.getStart() == this.selectedSquare);
        for (ChessMove move : relevantMoves) {
            Square square = this.getSquareAt(move.getEnd());
            square.legalMoveHighlight();
            this.legalHighlightedSquares.add(square);
        }
    }

    private void unhighlightLegalMoves() {
        for (Square square : this.legalHighlightedSquares) {
            square.legalMoveUnhighlight();
        }
        this.legalHighlightedSquares.clear();
    }

    public int getSelectedSquareIndex() {
        return this.selectedSquare;
    }

    /**
     * Returns the currently selected Square. Returns null if no Square is selected.
     */
    public Square getSelectedSquare() {
        if (this.selectedSquare < 0) {
            return null;
        }
        return getSquareAt(this.selectedSquare);
    }

    public Square getSquareAt(int index) {
        if (index < 0 || index >= 64) {
            throw new IndexOutOfBoundsException("getSquareAt: index must be between 0 and 63, inclusive.");
        }
        return this.squares[index];
    }

    public Point2D getOrigin() {
        return this.origin;
    }

    public double getSquareSize() {
        return this.squareSize;
    }

    public ChessPosition getPosition() {
        return this.game.getCurrentPosition();
    }

    public Group getRoot() {
        return this.chessBoard;
    }

    public ChessGame getGame() {
        return this.game;
    }

    public boolean isFlipped() {
        return this.isFlipped;
    }

    public boolean isWaitingForPromotion() {
        return this.waitingForPromotion;
    }

    public void openPromotionUI() {
        if (this.promotionUI != null) {
            this.promotionUI.open();
        }
    }

    public void closePromotionUI() {
        if (this.promotionUI != null) {
            this.promotionUI.close();
        }
    }

    public void requestPromotion(ChessMove move) {
        this.waitingForPromotion = true;
        this.promotionUI = new PromotionUI(this, move);
        openPromotionUI();
    }

    public void cancelPromotion() {
        //System.out.println("DEBUG: CANCEL PROMOTION");
        if (this.waitingForPromotion) {
            closePromotionUI();
            this.waitingForPromotion = false;
            this.promotionUI = null;
        }
    }

    public void submitPromotion(ChessMove move) {
        if (this.waitingForPromotion) {
            closePromotionUI();
            this.game.getPlayerToMove().makeMove(move);
            this.waitingForPromotion = false;
            this.promotionUI = null;
        }
    }

    private void onMousePressed(MouseEvent e) {

        this.chessBoard.requestFocus();
        
        if (!this.getGame().isIdle()) {

            int selectedSquareIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                                    getOrigin(), getSquareSize(), isFlipped());
            
            //System.out.println(e.getX() + " " + e.getY());

            if (selectedSquareIndex >= 0) {
                Square selectedSquare = getSquareAt(selectedSquareIndex);

                MouseActions.adjustDimensions(selectedSquare.getSize(), selectedSquare.getSize());

                //System.out.println("Mouse pressed on square " + selectedSquare.getFile() + " " + selectedSquare.getRank());

                ChessPiece piece = selectedSquare.getPiece();
                if (piece != null && piece.getColor() == getPosition().colorToMove() && isAtHome() && !selectedSquare.imageDetached()) {
                    if (this.game.getPlayer(piece.getColor()).isUser()) {
                        this.setSelectedSquare(selectedSquareIndex);
                        MouseActions.attachImage(selectedSquare.detachImage());
                        moveImageToMouse(e);
                    }
                }
            } else {
                //System.out.println("Clicked out of bounds of the chess board.");
            }
        }

        cancelPromotion();
    }

    private void onMouseDragged(MouseEvent e) {
        if (MouseActions.pieceInMouse && !this.game.isIdle() && !isWaitingForPromotion()) {
            moveImageToMouse(e);
        }
    }

    private void onMouseReleased(MouseEvent e) {
        //System.out.println("Mouse released");
        if (MouseActions.pieceInMouse && !this.getGame().isIdle() && !isWaitingForPromotion()) {
            
            int releaseIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                               getOrigin(), getSquareSize(), isFlipped());
            int startIndex = getSelectedSquareIndex();

            MouseActions.detachImage();

            if (releaseIndex >= 0) {
                ChessMove move = new ChessMove(this.game.getCurrentPosition(), startIndex, releaseIndex);
                Player player = this.game.getPlayerToMove();

                //if (true && move.isLegal()) {
                if (player.isUser() && move.isLegal()) {
                    // move attempt succeeded; move the image and make the corresponsing move in the ChessPosition.
                    if (!move.isPromotion()) {
                        player.makeMove(move);
                    } else {
                        requestPromotion(move);
                    }
                    
                } else {
                    // move attempt failed; snap the image back.
                    getSelectedSquare().reattachImage();
                }
            } else {
                // move attempt failed; snap the image back.
                getSelectedSquare().reattachImage();
            }
            deselectSquare();
        }
    }

    private void moveImageToMouse(MouseEvent e) {
        double offset = getSquareSize() / 2;
        MouseActions.move(e.getX() - offset, e.getY() - offset);
    }

    private void onKeyPressed(KeyEvent e) {
        //System.out.println("KEY PRESSED");
        if (e.getCode() == KeyCode.LEFT) {
            //System.out.println("LEFT");
            if (isWaitingForPromotion()) {
                cancelPromotion();
            } else {
                stepBack();
            }
        } else if (e.getCode() == KeyCode.RIGHT) {
            stepForward();
        }
        e.consume();
    }
}
