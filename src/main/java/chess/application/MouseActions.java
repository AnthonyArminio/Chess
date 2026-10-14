package chess.application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MouseActions {

    public static ImageView mouseImageView = new ImageView();
    public static boolean pieceInMouse = false;

    public static void attachImage(Image image) {
        mouseImageView.setImage(image);
        pieceInMouse = true;
    }

    public static void detachImage() {
        mouseImageView.setImage(null);
        pieceInMouse = false;
    }

    public static void move(double x, double y) {
        mouseImageView.setX(x);
        mouseImageView.setY(y);
        mouseImageView.toFront();
    }

    public static void adjustDimensions(double width, double height) {
        mouseImageView.setFitWidth(width);
        mouseImageView.setFitHeight(height);
    }
}
