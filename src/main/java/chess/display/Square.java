package chess.display;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

public class Square {
    private ImageView imageView;
    private Image image;
    private Point2D origin;
    private double size;
    private Color color;
    private Rectangle rectangle;

    public Square(Point2D origin, double size, Color color) {
        this.origin = origin;
        this.size = size;
        this.color = color;
        this.imageView = null;
        this.image = null;

        this.rectangle = new Rectangle(origin.getX(), origin.getY(), size, size);
        this.rectangle.setFill(color);
    }

    public void setImage(Image image) {

    }
}
