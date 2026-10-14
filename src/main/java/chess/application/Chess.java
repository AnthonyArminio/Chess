package chess.application;

import chess.display.ChessBoard;
import chess.display.ControlPanel;
import chess.intel.Player;
import chess.logic.ChessPosition;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Chess extends Application {

    private ChessGame displayedGame;

    private Group root;
    private HBox layout;
    private VBox gameView;
    private ControlPanel controlPanel;
    private Scene scene;

    @Override public void init() {
        System.out.println("Test Init");

        this.root = new Group();

        this.layout = new HBox();
        this.gameView = new VBox();
        this.controlPanel = new ControlPanel(this);

        this.root.getChildren().add(MouseActions.mouseImageView);

        this.layout.getChildren().add(this.gameView);
        this.layout.getChildren().add(this.controlPanel.getContainer());
        this.root.getChildren().add(this.layout);

        this.scene = new Scene(root);
    }
    
    @Override public void start(Stage stage) {
        System.out.println("Test Start");
        
        stage.setTitle("Chess Application");
        stage.setScene(this.scene);

        //this.controlPanel.createNewGame(getDefaultAgent(), new Player(true), SinglePawnEndgame.samplePosition(), new ChessBoard(Point2D.ZERO, false, false, new BoardUISettings()), false);
        this.controlPanel.createNewGame(new Player(), ControlPanel.getDefaultAgent(), new ChessPosition(), new ChessBoard(Point2D.ZERO, false, false, this.controlPanel.configureSettings()), true);

        stage.sizeToScene();
        stage.show();

        // FOR TESTING
        
    }

    public void loadGame(ChessGame game) {
        System.out.println("Loading game...");
        if (this.displayedGame != null) {
            //this.displayedGame.forceEnd();
            this.gameView.getChildren().remove(this.displayedGame.getBoard().getRoot());
        }
        this.gameView.getChildren().add(game.getBoard().getRoot());
        this.displayedGame = game;
        System.out.println("Done.");
    }

    public ChessGame getDisplayedGame() {
        return this.displayedGame;
    }
}
