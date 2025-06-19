package chess.display;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import chess.logic.ChessPosition;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private GridPane checkerboard;
    private double squareSize;
    private Color[] squareColors;
    private ChessPosition chessPosition;
    private Point2D origin;

    /**
     * Initializes a new chess board.
     * @param origin the top-left corner of the chess board.
     * @param boardSize the width/height of the chess board
     * @param lightSquareColor the color of the light squares of the chess board
     * @param darkSquareColor the color of the dark squares of the chess board
     */
    public ChessBoard(Point2D origin, double boardSize, String lightSquareColor, String darkSquareColor) {
        this.origin = origin;
        this.squareSize = boardSize / 8.0;
        checkerboard = new GridPane();
        checkerboard.setPrefSize(boardSize, boardSize);

        initializeColors(lightSquareColor, darkSquareColor);

        makeSquares();

        this.chessPosition = new ChessPosition();

        loadPosition();
    }

    private void initializeColors(String lightSquareColor, String darkSquareColor) {
        this.squareColors = new Color[2];
        this.squareColors[0] = (Color) Paint.valueOf(lightSquareColor);
        this.squareColors[1] = (Color) Paint.valueOf(darkSquareColor);
    }

    /**
     * Draws the squares in the appropriate locations for the checkerboard.
     */
    private void makeSquares() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                checkerboard.add(new Rectangle(this.squareSize, this.squareSize, squareColors[(row + col) % 2]), col, row);
            }
        }
    }

    /**
     * Loads the current position onto the board.
     */
    private void loadPosition() {
        
    }

    public Point2D getOrigin() {
        return this.origin;
    }

    public ChessPosition getChessPosition() {
        return this.chessPosition;
    }

    public GridPane getCheckerboard() {
        return this.checkerboard;
    }
}
