package chess.display;

public class BoardUISettings {

    private static final String DEFAULT_LIGHT_SQUARE_COLOR = "#999999";
    private static final String DEFAULT_DARK_SQUARE_COLOR = "#333333";
    private static final String DEFAULT_LIGHT_SQUARE_HIGHLIGHT_COLOR = "#CCCC00";
    private static final String DEFAULT_DARK_SQUARE_HIGHLIGHT_COLOR = "#888800";
    private static final String DEFAULT_LEGAL_MOVE_HIGHLIGHT_COLOR = "#0066FF";
    private static final double DEFAULT_BOARD_SIZE = 504.0;

    private double boardSize;
    private String lightSquareColor;
    private String darkSquareColor;
    private String lightSquareHighlightColor;
    private String darkSquareHighlightColor;
    private boolean doPreviousMoveHighlights;
    private boolean doLegalMoveHighlights;
    private String legalMoveHighlightColor;

    public BoardUISettings(double boardSize, String lightSquareColor, String darkSquareColor, String lightSquareHighlightColor, String darkSquareHighlightColor, boolean doPreviousMoveHighlights, boolean doLegalMoveHighlights, String legalMoveHighlightColor) {
        this.boardSize = boardSize;
        this.lightSquareColor = lightSquareColor;
        this.darkSquareColor = darkSquareColor;
        this.lightSquareHighlightColor = lightSquareHighlightColor;
        this.darkSquareHighlightColor = darkSquareHighlightColor;
        this.doPreviousMoveHighlights = doPreviousMoveHighlights;
        this.doLegalMoveHighlights = doLegalMoveHighlights;
        this.legalMoveHighlightColor = legalMoveHighlightColor;
    }

    public BoardUISettings() {
        this.boardSize = DEFAULT_BOARD_SIZE;
        this.lightSquareColor = DEFAULT_LIGHT_SQUARE_COLOR;
        this.darkSquareColor = DEFAULT_DARK_SQUARE_COLOR;
        this.lightSquareHighlightColor = DEFAULT_LIGHT_SQUARE_HIGHLIGHT_COLOR;
        this.darkSquareHighlightColor = DEFAULT_DARK_SQUARE_HIGHLIGHT_COLOR;
        this.doPreviousMoveHighlights = true;
        this.doLegalMoveHighlights = true;
        this.legalMoveHighlightColor = DEFAULT_LEGAL_MOVE_HIGHLIGHT_COLOR;
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

    public String getLightSquareHighlightColor() {
        return this.lightSquareHighlightColor;
    }

    public String getDarkSquareHighlightColor() {
        return this.darkSquareHighlightColor;
    }

    public boolean doLegalMoveHighlights() {
        return this.doLegalMoveHighlights;
    }

    public boolean doPreviousMoveHighlights() {
        return this.doPreviousMoveHighlights;
    }

    public String getLegalMoveHighlightColor() {
        return this.legalMoveHighlightColor;
    }

    public void setBoardSize(double boardSize) {
        this.boardSize = boardSize;
    }

    public void setLightSquareColor(String color) {
        this.lightSquareColor = color;
    }

    public void setDarkSquareColor(String color) {
        this.darkSquareColor = color;
    }

    public void setLightSquareHighlightColor(String color) {
        this.lightSquareHighlightColor = color;
    }

    public void setDarkSquareHighlightColor(String color) {
        this.darkSquareHighlightColor = color;
    }

    public void setPreviousMoveHighlights(boolean doPreviousMoveHighlights) {
        this.doPreviousMoveHighlights = doPreviousMoveHighlights;
    }

    public void setLegalMoveHighlights(boolean doLegalMoveHighlights) {
        this.doLegalMoveHighlights = doLegalMoveHighlights;
    }

    public void setLegalMoveHighlightColor(String color) {
        this.legalMoveHighlightColor = color;
    }
}
