package chess.display;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.scene.Group;
import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import chess.logic.piece.ChessPiece;

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

    public void setPiece(ChessPiece piece) {
        this.piece = piece;
        setImage(piece.getImagePath());
    }

    /**
     * Sets the value of this.piece equal to null. Not the same as capturing.
     */
    public void removePiece() {
        this.piece = null;
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
        this.checkerboard.getChildren().remove(this.imageView);
        this.image = null;
        this.imageView = null;
    }

    /**
     * Snaps the ImageView temporarily stored in memory back into position. Useful for when
     * an illegal move is tried by the user.
     */
    public void reattachImage() {
        this.imageView.setX(this.origin.getX());
        this.imageView.setY(this.origin.getY());
        this.checkerboard.add(this.imageView, file - 1, 8 - rank);
    }

    /**
     * Removes ownership of the ImageView from the checkerboard but keeps the ImageView in
     * memory in case an illegal move is made.
     * @return
     */
    public ImageView detachImage() {
        this.checkerboard.getChildren().remove(this.imageView);
        return this.imageView;
    }

    public int getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }
}
