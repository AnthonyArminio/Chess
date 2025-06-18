package chess.display;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private GridPane checkerboard;
    private double squareSize;
    private Color[] squareColors;

    public ChessBoard(double boardSize, String lightSquareColor, String darkSquareColor) {
        this.squareSize = boardSize / 8.0;
        checkerboard = new GridPane();
        checkerboard.setPrefSize(boardSize, boardSize);

        this.squareColors = new Color[2];
        initializeColors(lightSquareColor, darkSquareColor);

        makeSquares();
    }

    private void initializeColors(String lightSquareColor, String darkSquareColor) {
        this.squareColors[0] = (Color) Paint.valueOf(lightSquareColor);
        this.squareColors[1] = (Color) Paint.valueOf(darkSquareColor);
    }

    private void makeSquares() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                checkerboard.add(new Rectangle(this.squareSize, this.squareSize, squareColors[(row + col) % 2]), col, row);
            }
        }
    }

    public GridPane getCheckerboard() {
        return this.checkerboard;
    }
}
