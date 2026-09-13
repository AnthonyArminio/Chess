package chess.display;

public class BoardUISettings {

    private static final String DEFAULT_LIGHT_SQUARE_COLOR = "#999999";
    private static final String DEFAULT_DARK_SQUARE_COLOR = "#333333";
    private static final double DEFAULT_BOARD_SIZE = 504.0;

    private double boardSize;
    private String lightSquareColor;
    private String darkSquareColor;
    private boolean doLegalMoveHighlights;

    public BoardUISettings(double boardSize, String lightSquareColor, String darkSquareColor, boolean doLegalMoveHighlights) {
        this.boardSize = boardSize;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        this.doLegalMoveHighlights = doLegalMoveHighlights;
    }

    public BoardUISettings(String lightSquareColor, String darkSquareColor, boolean doLegalMoveHighlights) {
        this.boardSize = DEFAULT_BOARD_SIZE;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        this.doLegalMoveHighlights = doLegalMoveHighlights;
    }

    public BoardUISettings(boolean doLegalMoveHighlights) {
        this.boardSize = DEFAULT_BOARD_SIZE;
        this.lightSquareColor = DEFAULT_LIGHT_SQUARE_COLOR;
        this.darkSquareColor = DEFAULT_DARK_SQUARE_COLOR;
        this.doLegalMoveHighlights = doLegalMoveHighlights;
    }

    public BoardUISettings() {
        this.boardSize = DEFAULT_BOARD_SIZE;
        this.lightSquareColor = DEFAULT_LIGHT_SQUARE_COLOR;
        this.darkSquareColor = DEFAULT_DARK_SQUARE_COLOR;
        this.doLegalMoveHighlights = true;
    }

    public double getBoardSize() {
        return this.boardSize;
    }

    public String getLightSquareColor() {
        return this.lightSquareColor;
    }

    public String getDarkSquareColor() {
        return this.darkSquareColor;
    }

    public boolean doLegalMoveHighlights() {
        return this.doLegalMoveHighlights;
    }

    public void setLegalMoveHighlights(boolean doLegalMoveHighlights) {
        this.doLegalMoveHighlights = doLegalMoveHighlights;
    }
}
