package chess.display;

import java.util.ArrayList;

import chess.application.ChessGame;
import chess.intel.Player;
import chess.logic.ChessMove;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.GridPane;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private final Group chessBoard;
    private final GridPane checkerboard;
    private Square[] squares;
    private ArrayList<Square> legalHighlightedSquares;
    private int selectedSquare;
    private boolean isFlipped;
    private ChessGame game;
    private ArrayList<ChessMove> highlightedMoves;
    private BoardUISettings settings;

    private double squareSize;
    private Point2D origin;

    private boolean waitingForPromotion;
    private PromotionUI promotionUI;

    /**
     * Initializes a new chess board.
     * @param origin the top-left corner of the chess board.
     * @param flipped whether the board starts flipped (black on bottom)
     * @param settings board interface settings
     */
    public ChessBoard(Point2D origin, boolean flipped, BoardUISettings settings) {
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

        this.legalHighlightedSquares = new ArrayList<>();
        this.highlightedMoves = new ArrayList<>();

        makeSquares();
        this.isFlipped = flipped;
        drawSquares(this.isFlipped);

        this.selectedSquare = -1;
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
        loadPosition(game.getPosition());
    }

    /**
     * Loads a given position onto the board.
     */
    private void loadPosition(ChessPosition position) {
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {                
                this.squares[GridMath.index(file, rank)].setPiece(position.getPieceAt(file, rank));
            }
        }
    }

    public void makeMove(ChessMove move) {

        Square startSquare = getSquareAt(move.getStart());
        Square releaseSquare = getSquareAt(move.getEnd());

        startSquare.removePiece();
        
        if (move.isCapture()) {
            releaseSquare.capture();
        }
        if (move.isEnPassant()) {
            if (move.getColor() == 'w') {
                getSquareAt(move.getEnd() - 8).capture();
            } else {
                getSquareAt(move.getEnd() + 8).capture();
            }
        } else if (move.isPromotion()) {
            // special effects, if desired

        } else if (move.isKingsideCastle()) {
            if (move.getColor() == 'w') {
                getSquareAt(5).setPiece(getSquareAt(7).removePiece());
            } else {
                getSquareAt(61).setPiece(getSquareAt(63).removePiece());
            }
        } else if (move.isQueensideCastle()) {
            if (move.getColor() == 'w') {
                getSquareAt(3).setPiece(getSquareAt(0).removePiece());
            } else {
                getSquareAt(59).setPiece(getSquareAt(56).removePiece());
            }
        }
        
        releaseSquare.setPiece(move.getPiece());
        unhighlightMoves();
        if (settings.doPreviousMoveHighlights()) {
            highlightMove(move);
        }
    }

    public void setSelectedSquare(int index) {
        if (index < 0 || index >= 64) {
            throw new IllegalArgumentException("setSelectedSquare: index must be between 0 and 63, inclusive.");
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
            throw new IllegalArgumentException("getSquareAt: index must be between 0 and 63, inclusive.");
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
        return this.game.getPosition();
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

    public void waitForPromotion() {
        this.waitingForPromotion = true;
    }

    public boolean isWaitingForPromotion() {
        return this.waitingForPromotion;
    }

    public void openPromotionUI(ChessMove move) {
        this.promotionUI = new PromotionUI(this, move);
        this.chessBoard.getChildren().add(this.promotionUI.getDisplay());
    }

    public void closePromotionUI() {
        if (this.promotionUI != null) {
            this.chessBoard.getChildren().remove(this.promotionUI.getDisplay());
            Player player = this.game.getPlayerToMove();
            if (player.isUser()) {
                player.makeMove(this.promotionUI.getMove());
            }
            this.promotionUI = null;
            this.waitingForPromotion = false;
        }
    }
}
