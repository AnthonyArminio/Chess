package chess.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;
import javafx.geometry.Point2D;

import chess.display.ChessBoard;

public class Chess extends Application {

    private final double boardSize = 500.0;
    private final String lightSquareColor = "#999999";
    private final String darkSquareColor = "#333333";
    private ChessBoard board;

    private Group root;
    private HBox layout;
    private Scene scene;

    public void init() {
        System.out.println("Test Init");

        this.root = new Group();
        this.layout = new HBox();
        this.board = new ChessBoard(this.root, Point2D.ZERO, boardSize, darkSquareColor, lightSquareColor);
        layout.getChildren().add(this.board.getCheckerboard());
        root.getChildren().add(layout);

        this.scene = new Scene(root);
    }
    
    public void start(Stage stage) {
        System.out.println("Test Start");
        
        stage.setTitle("Chess Application");
        stage.setScene(scene);

        stage.sizeToScene();
        stage.show();
    }
}
