package chess.display;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import chess.logic.ChessPiece;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Square {
    private GridPane checkerboard;
    private ChessPiece piece;
    private ImageView imageView;
    private Image image;
    private Point2D origin;
    private double size;
    private int file;
    private int rank;

    public Square(Point2D origin, double size, Color color, GridPane checkerboard, int file, int rank) {
        this.checkerboard = checkerboard;

        this.file = file;
        this.rank = rank;
        
        this.origin = origin;
        this.size = size;

        this.piece = null;

        this.imageView = new ImageView();
        this.imageView.setX(origin.getX());
        this.imageView.setY(origin.getY());
        this.imageView.setFitHeight(size);
        this.imageView.setFitWidth(size);
        this.image = null;

        drawRectangle(color);
        checkerboard.add(this.imageView, file - 1, 8 - rank);

    }

    /**
     * Draw the Rectangle that gives color to the square.
     * @param color the color to draw
     */
    private void drawRectangle(Color color) {
        Rectangle rectangle = new Rectangle(origin.getX(), origin.getY(), size, size);
        rectangle.setFill(color);
        this.checkerboard.add(rectangle, this.file - 1, 8 - this.rank);
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

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }
}
