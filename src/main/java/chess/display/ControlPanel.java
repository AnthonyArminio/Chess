package chess.display;

import java.util.ArrayList;

import chess.application.Chess;
import chess.application.ChessGame;
import chess.intel.Agent;
import chess.intel.Player;
import chess.intel.strategy.PositionalStrategy;
import chess.intel.strategy.Strategy;
import chess.intel.technique.SinglePawnEndgame;
import chess.intel.technique.SingleQueenEndgame;
import chess.intel.technique.SingleRookEndgame;
import chess.logic.ChessPosition;
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

    private final Chess app;
    private final VBox container;
    private final VBox choiceBoxContainer;
    private final VBox opponentTypeSelectContainer;
    private final VBox playerColorSelectContainer;
    private final Button newGameButton;
    private final Button testGameButton;
    private final ChoiceBox<String> opponentTypeSelect;
    private final ChoiceBox<String> playerColorSelect;

    public ControlPanel(Chess app){
        this.app = app;

        this.newGameButton = new Button("New Game");
        this.newGameButton.setOnAction(e -> onNewGameButtonPressed());

        this.container = new VBox();
        this.container.getChildren().add(new Text("Configure New Game\n"));
        
        this.choiceBoxContainer = new VBox();
        this.opponentTypeSelectContainer = new VBox();
        this.playerColorSelectContainer = new VBox();

        this.opponentTypeSelectContainer.getChildren().add(new Text("Play Against:"));
        ArrayList<String> opponentTypeOptions = new ArrayList<>();
        for (String option : OPPONENT_TYPE_OPTIONS){
            opponentTypeOptions.add(option);
        }
        this.opponentTypeSelect = new ChoiceBox<>(FXCollections.<String>observableList(opponentTypeOptions));
        this.opponentTypeSelect.setOnAction(e -> onChoiceBoxUpdated());
        this.opponentTypeSelectContainer.getChildren().add(this.opponentTypeSelect);

        this.playerColorSelectContainer.getChildren().add(new Text("Play As:"));
        ArrayList<String> playerColorOptions = new ArrayList<>();
        for (String option : PLAYER_COLOR_OPTIONS){
            playerColorOptions.add(option);
        }
        this.playerColorSelect = new ChoiceBox<>(FXCollections.<String>observableList(playerColorOptions));
        this.playerColorSelectContainer.getChildren().add(this.playerColorSelect);
        
        this.choiceBoxContainer.getChildren().add(this.opponentTypeSelectContainer);
        this.container.getChildren().add(this.choiceBoxContainer);
        this.container.getChildren().add(new Text(""));
        this.container.getChildren().add(this.newGameButton);

        this.testGameButton = new Button("Test Game");
        this.testGameButton.setOnAction(e -> onTestGameButtonPressed());
        this.container.getChildren().add(this.testGameButton);
    }

    public VBox getContainer(){
        return this.container;
    }

    private void onNewGameButtonPressed(){
        System.out.println("Button press read.");
        Player whitePlayer = new Player(true);
        Player blackPlayer = new Player(true);
        boolean flip = true;
        boolean flipAtStart = false;
        if ("Computer".equals(this.opponentTypeSelect.getValue())) {
            boolean userPlaysWhite;
            if ("White".equals(this.playerColorSelect.getValue())) {
                userPlaysWhite = true;
            } else if ("Black".equals(this.playerColorSelect.getValue())) {
                userPlaysWhite = false;
            } else {
                userPlaysWhite = Math.random() > 0.5;
            }

            whitePlayer = userPlaysWhite ? whitePlayer : getDefaultAgent();
            blackPlayer = userPlaysWhite ? getDefaultAgent() : blackPlayer;
            flip = false;
            flipAtStart = !userPlaysWhite;
        }

        this.app.getDisplayedGame().stop();
        
        BoardUISettings settings = configureSettings();
        createNewGame(whitePlayer, blackPlayer, new ChessBoard(Point2D.ZERO, flipAtStart, flip, settings), true);
    }

    private void onChoiceBoxUpdated() {
        if ("Computer".equals(this.opponentTypeSelect.getValue())) {
            if (!this.choiceBoxContainer.getChildren().contains(this.playerColorSelectContainer)) {
                this.choiceBoxContainer.getChildren().add(this.playerColorSelectContainer);
            }
        } else {
            if (this.choiceBoxContainer.getChildren().contains(this.playerColorSelectContainer)) {
                this.choiceBoxContainer.getChildren().remove(this.playerColorSelectContainer);
            }
        }
    }

    private void onTestGameButtonPressed() {
        this.app.getDisplayedGame().stop();

        BoardUISettings settings = configureSettings();
        createNewGame(getDefaultAgent(), getDefaultAgent(), SingleRookEndgame.randomSamplePosition(), new ChessBoard(Point2D.ZERO, false, false, settings), true);
    }

    public BoardUISettings configureSettings() {
        return new BoardUISettings();
    }

    public ChessGame createNewGame(Player whitePlayer, Player blackPlayer, ChessPosition startingPosition, ChessBoard board, boolean printMoves) {
        ChessGame game = new ChessGame(whitePlayer, blackPlayer, startingPosition, true);
        board.loadGame(game);
        this.app.loadGame(game);
        game.start();
        return game;
    }

    public ChessGame createNewGame(Player whitePlayer, Player blackPlayer, ChessBoard board, boolean printMoves) {
        ChessGame game = new ChessGame(whitePlayer, blackPlayer, true);
        board.loadGame(game);
        this.app.loadGame(game);
        game.start();
        return game;
    }

    public ChessGame createNewGame(Player whitePlayer, Player blackPlayer, boolean printMoves) {
        ChessGame game = new ChessGame(whitePlayer, blackPlayer, true);
        this.app.loadGame(game);
        game.start();
        return game;
    }

    public boolean doLegalMoveHighlights() {
        //
        return true;
        //
    }

    public static Player getDefaultAgent() {
        Strategy strategy = new PositionalStrategy();
        strategy.addTechnique(new SingleQueenEndgame());
        strategy.addTechnique(new SingleRookEndgame());
        strategy.addTechnique(new SinglePawnEndgame());
        return new Agent(strategy, 6, true);
    }

}
