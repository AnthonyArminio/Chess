package chess.display;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.geometry.Point2D;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import javafx.event.Event;
import javafx.event.EventHandler;

public class Square {
    private Rectangle hitbox;
    private ImageView imageView;
    private Image image;
    private Point2D origin;
    private double size;
    private int file;
    private int rank;

    public Square(Point2D origin, double size, Color color, GridPane checkerboard, int file, int rank) {
        this.file = file;
        this.rank = rank;
        
        this.origin = origin;
        this.size = size;

        this.imageView = new ImageView();
        this.imageView.setX(origin.getX());
        this.imageView.setY(origin.getY());
        this.imageView.setFitHeight(size);
        this.imageView.setFitWidth(size);
        this.image = null;

        drawRectangle(checkerboard, color);
        checkerboard.add(this.imageView, file - 1, 8 - rank);

        // make hitbox for hearing mouse events
        this.hitbox = new Rectangle(origin.getX(), origin.getY(), size, size);
        this.hitbox.setOpacity(0);
        checkerboard.add(this.hitbox, this.file - 1, 8 - this.rank);

        EventHandler<Event> eventHandler = (Event e) -> onMousePressed(e);
        this.hitbox.setOnMousePressed(eventHandler);
    }

    private void drawRectangle(GridPane checkerboard, Color color) {
        Rectangle rectangle = new Rectangle(origin.getX(), origin.getY(), size, size);
        rectangle.setFill(color);
        checkerboard.add(rectangle, this.file - 1, 8 - this.rank);
    }

    public void setImage(String imagePath) {
        this.image = new Image(imagePath);
        this.imageView.setImage(this.image);
    }

    private void onMousePressed(Event e) {
        System.out.println("Mouse pressed on file " + this.file + " and rank " + this.rank + ".");
    }
}
