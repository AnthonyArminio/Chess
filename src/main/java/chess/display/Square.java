package chess.display;

import chess.logic.ChessPiece;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Square {

    private final double LEGAL_MOVE_HIGHLIGHT_RADIUS = 0.15;
    private final double LEGAL_CAPTURE_HIGHLIGHT_RADIUS = 0.3;
    private final double LEGAL_CAPTURE_HIGHLIGHT_STOKE_WIDTH = 10.0;

    private ChessPiece piece;
    private final ImageView imageView;
    private Image image;
    private final Point2D origin;
    private Color color;
    private final double size;
    private final int file;
    private final int rank;
    private Circle legalMoveHighlight;
    private Circle legalCaptureHighlight;
    private final Color legalMoveHighlightColor;

    public Square(Point2D origin, double size, int file, int rank, BoardUISettings settings) {

        this.file = file;
        this.rank = rank;
        
        this.origin = origin;
        this.size = size;
        this.color = Color.web(Square.isDarkSquare(file, rank) ? settings.getDarkSquareColor() : settings.getLightSquareColor());
        this.legalMoveHighlightColor = Color.web(settings.getLegalMoveHighlightColor());

        this.piece = null;

        this.imageView = new ImageView();
        this.imageView.setX(origin.getX());
        this.imageView.setY(origin.getY());
        this.imageView.setFitHeight(size);
        this.imageView.setFitWidth(size);
        this.image = null;

        initializeLegalMoveHighlights();

    }

    private void initializeLegalMoveHighlights() {
        this.legalMoveHighlight = new Circle(origin.getX() + size / 2, origin.getY() + size / 2, size * LEGAL_MOVE_HIGHLIGHT_RADIUS, legalMoveHighlightColor);
        this.legalMoveHighlight.setVisible(false);
        
        this.legalCaptureHighlight = new Circle(origin.getX() + size / 2, origin.getY() + size / 2, size * LEGAL_CAPTURE_HIGHLIGHT_RADIUS, legalMoveHighlightColor);
        this.legalCaptureHighlight.setFill(null);
        this.legalCaptureHighlight.setStroke(legalMoveHighlightColor);
        this.legalCaptureHighlight.setStrokeWidth(LEGAL_CAPTURE_HIGHLIGHT_STOKE_WIDTH);
        this.legalCaptureHighlight.setVisible(false);
    }

    /**
     * Draw the Rectangle that gives color to the square and set up the ImageView there.
     * @param checkerboard the GridPane container to draw the square in.
     * @param color the color to draw
     * @param rfile the relative file to draw the square on.
     * @param rrank the relative rank to draw the square on.
     */
    public void draw(GridPane checkerboard, int rfile, int rrank) {
        Rectangle rectangle = new Rectangle(origin.getX(), origin.getY(), size, size);
        rectangle.setFill(this.color);
        checkerboard.add(rectangle, rfile - 1, 8 - rrank);

        checkerboard.add(this.legalMoveHighlight, rfile - 1, 8 - rrank);
        GridPane.setHalignment(this.legalMoveHighlight, HPos.CENTER);
        GridPane.setValignment(this.legalMoveHighlight, VPos.CENTER);

        checkerboard.add(this.legalCaptureHighlight, rfile - 1, 8 - rrank);
        GridPane.setHalignment(this.legalCaptureHighlight, HPos.CENTER);
        GridPane.setValignment(this.legalCaptureHighlight, VPos.CENTER);

        checkerboard.add(this.imageView, rfile - 1, 8 - rrank);
    }

    public ChessPiece capture() {
        return removePiece();
    }

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
        if (piece != null) {
            setImage(piece.getImagePath());
        } else {
            removePiece();
        }
    }

    /**
     * Sets the value of this.piece equal to null. Not the same as capturing.
     */
    public ChessPiece removePiece() {
        ChessPiece piece = this.piece;
        this.piece = null;
        removeImage();
        return piece;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public void setImage(String imagePath) {
        this.image = new Image(imagePath);
        this.imageView.setImage(this.image);
    }

    /**
     * Deletes the image currently stored in this square.
     */
    public void removeImage() {
        this.imageView.setImage(null);
        this.image = null;
    }

    /**
     * Puts the Image temporarily stored in memory back into this square's ImageView. Useful for when
     * an illegal move is tried by the user.
     */
    public void reattachImage() {
        this.imageView.setImage(this.image);
    }

    /**
     * Removes ownership of the Image from the checkerboard but keeps the Image in
     * memory in case an illegal move is made.
     * @return the Image originally in this Square.
     */
    public Image detachImage() {
        this.imageView.setImage(null);
        return this.image;
    }

    public void legalMoveHighlight() {
        if (getPiece() != null) {
            this.legalCaptureHighlight.setVisible(true);
        } else {
            this.legalMoveHighlight.setVisible(true);
        }
    }

    public void legalMoveUnhighlight() {
        this.legalMoveHighlight.setVisible(false);
        this.legalCaptureHighlight.setVisible(false);
    }

    public double getSize() {
        return this.size;
    }

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }

    public static boolean isDarkSquare(int file, int rank) {
        return (file + rank) % 2 == 0;
    }
}
