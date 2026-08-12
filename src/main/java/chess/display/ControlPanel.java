package chess.display;

import java.util.ArrayList;

import chess.application.Chess;
import chess.application.ChessGame;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Represents a control panel that the user may use to control game options.
 */
public class ControlPanel {

    private static final String[] OPPONENT_TYPE_OPTIONS = {"Human (Hot Seat)", "Computer"};
    private static final String[] PLAYER_COLOR_OPTIONS = {"White", "Black", "Random"};

    private Chess app;
    private VBox container;
    private Button newGameButton;
    private ChoiceBox<String> opponentTypeSelect;
    private ChoiceBox<String> playerColorSelect;

    public ControlPanel(Chess app){
        this.app = app;

        this.newGameButton = new Button("New Game");
        this.newGameButton.setOnAction(e -> onResetButtonPressed());

        this.container = new VBox();
        this.container.getChildren().add(new Text("Configure New Game\n"));
        this.container.getChildren().add(new Text("Play Against:"));

        ArrayList<String> opponentTypeOptions = new ArrayList<>();
        for (String option : OPPONENT_TYPE_OPTIONS){
            opponentTypeOptions.add(option);
        }
        this.opponentTypeSelect = new ChoiceBox<>(FXCollections.<String>observableList(opponentTypeOptions));
        this.container.getChildren().add(this.opponentTypeSelect);
        //this.container.getChildren().add(this.playerColorSelect);
        this.container.getChildren().add(this.newGameButton);
    }

    public VBox getContainer(){
        return this.container;
    }

    public void onResetButtonPressed(){
        System.out.println("Button press read.");
        ChessGame game = new ChessGame(this.app.getUser(), this.app.getDefaultAgent(), new ChessBoard(Point2D.ZERO, 504.0), false, true);
        this.app.loadGame(game);
        game.start();
    }

}
