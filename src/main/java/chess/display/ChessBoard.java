package chess.display;

import javafx.scene.layout.GridPane;
import javafx.geometry.Point2D;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import chess.logic.ChessPosition;
import chess.logic.ChessMove;
import chess.logic.util.GridMath;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private GridPane checkerboard;
    private Square[] squares;
    private int selectedSquare;
    private ChessPosition chessPosition;

    private double squareSize;
    private Color[] squareColors;
    private Point2D origin;

    /**
     * Initializes a new chess board.
     * @param origin the top-left corner of the chess board.
     * @param boardSize the width/height of the chess board
     * @param lightSquareColor the color of the light squares of the chess board
     * @param darkSquareColor the color of the dark squares of the chess board
     */
    public ChessBoard(Point2D origin, double boardSize, String darkSquareColor, String lightSquareColor) {
        this.origin = origin;
        this.squareSize = boardSize / 8.0;
        this.checkerboard = new GridPane();
        this.checkerboard.setPrefSize(boardSize, boardSize);
        this.checkerboard.setHgap(0);
        this.checkerboard.setVgap(0);

        initializeColors(darkSquareColor, lightSquareColor);

        makeSquares();

        this.chessPosition = new ChessPosition();

        loadPosition();

        this.selectedSquare = -1;
    }

    private void initializeColors(String darkSquareColor, String lightSquareColor) {
        this.squareColors = new Color[2];
        this.squareColors[0] = (Color) Paint.valueOf(darkSquareColor);
        this.squareColors[1] = (Color) Paint.valueOf(lightSquareColor);
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
                this.squares[GridMath.index(file, rank)] = new Square(squareOrigin, this.squareSize, this.squareColors[(file + rank) % 2], 
                                                              this.checkerboard, file, rank);
            }
        }
    }

    /**
     * Loads the current position onto the board.
     */
    private void loadPosition() {
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                if (this.chessPosition.getPieceAt(file, rank) != null) {                
                    this.squares[GridMath.index(file, rank)].setPiece(this.chessPosition.getPieceAt(file, rank));
                }
            }
        }
    }

    public void makeMove(ChessMove move) {
        this.chessPosition.makeMove(move);
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
        }
        if (move.isKingsideCastle()) {
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
    }

    public void setSelectedSquare(int index) {
        if (index < 0 || index >= 64) {
            throw new IllegalArgumentException("setSelectedSquare: index must be between 0 and 63, inclusive.");
        }
        this.selectedSquare = index;
    }

    //public void deselectSquare() {
    //    this.selectedSquare = -1;
    //}

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

    public ChessPosition getChessPosition() {
        return this.chessPosition;
    }

    public GridPane getCheckerboard() {
        return this.checkerboard;
    }
}
