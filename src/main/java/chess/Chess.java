package chess;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Chess extends Application {
    public void init() {
        System.out.println("Test Init");
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
