package chess.display;

import chess.application.Chess;
import chess.application.ChessGame;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

/**
 * Represents a control panel that the user may use to control game options.
 */
public class ControlPanel {

    private Chess app;
    private VBox container;
    private Button resetButton;

    public ControlPanel(Chess app){
        this.app = app;

        this.resetButton = new Button("New Game");
        this.resetButton.setOnAction(e -> onResetButtonPressed());

        this.container = new VBox();
        this.container.getChildren().add(this.resetButton);
    }

    public VBox getContainer(){
        return this.container;
    }

    public void onResetButtonPressed(){
        System.out.println("Button press read.");
        ChessGame game = new ChessGame(this.app.getUser(), this.app.getDefaultAgent(), new ChessBoard(Point2D.ZERO, 504.0), true);
        this.app.loadGame(game);
        game.start();
    }

}
