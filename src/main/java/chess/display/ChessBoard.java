package chess.display;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private Pane pane;

    public ChessBoard(String lightSquareColor, String darkSquareColor) {
        
    }

    public Pane getPane() {
        return this.pane;
    }
}
