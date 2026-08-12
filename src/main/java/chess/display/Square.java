package chess.display;

import chess.logic.ChessPiece;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Square {
    private ChessPiece piece;
    private ImageView imageView;
    private Image image;
    private Point2D origin;
    private Color color;
    private double size;
    private int file;
    private int rank;

    public Square(Point2D origin, double size, Color color, int file, int rank) {

        this.file = file;
        this.rank = rank;
        
        this.origin = origin;
        this.size = size;
        this.color = color;

        this.piece = null;

        this.imageView = new ImageView();
        this.imageView.setX(origin.getX());
        this.imageView.setY(origin.getY());
        this.imageView.setFitHeight(size);
        this.imageView.setFitWidth(size);
        this.image = null;
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

    public double getSize() {
        return this.size;
    }

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }
}
