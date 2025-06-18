package chess.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import chess.display.ChessBoard;

public class Chess extends Application {
    private final String lightSquareColor = "#999999";
    private final String darkSquareColor = "#333333";
    private ChessBoard board;

    
    public void init() {
        System.out.println("Test Init");
        this.board = new ChessBoard(lightSquareColor, darkSquareColor);
    }
    
    public void start(Stage stage) {
        HBox root = new HBox();
        Scene scene = new Scene(root);

        System.out.println("Test Start");
        
        stage.setTitle("Chess Application");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
}
