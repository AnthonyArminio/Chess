package chess.application;

import chess.display.BoardUISettings;
import chess.display.ChessBoard;
import chess.display.ControlPanel;
import chess.display.Square;
import chess.intel.Agent;
import chess.intel.Player;
import chess.intel.strategy.PositionalStrategy;
import chess.intel.strategy.Strategy;
import chess.intel.technique.SingleQueenEndgame;
import chess.logic.ChessMove;
import chess.logic.ChessPiece;
import chess.logic.util.GridMath;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Chess extends Application {

    private ChessGame displayedGame;
    private ChessBoard selectedBoard;
    private ImageView mouseImageView;
    private boolean pieceInMouse;

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

        this.mouseImageView = new ImageView();
        this.pieceInMouse = false;
        this.root.getChildren().add(this.mouseImageView);

        this.layout.getChildren().add(this.gameView);
        this.layout.getChildren().add(this.controlPanel.getContainer());
        this.root.getChildren().add(this.layout);

        // set up event handlers
        this.root.setOnMousePressed(e -> onMousePressed(e));
        this.root.setOnMouseDragged(e -> onMouseDragged(e));
        this.root.setOnMouseReleased(e -> onMouseReleased(e));

        this.scene = new Scene(root);
    }
    
    @Override public void start(Stage stage) {
        System.out.println("Test Start");
        
        stage.setTitle("Chess Application");
        stage.setScene(this.scene);

        this.controlPanel.createNewGame(new Player(true), getDefaultAgent(), new ChessBoard(Point2D.ZERO, false, true, new BoardUISettings()), true);

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


    private void onMousePressed(MouseEvent e) {
        this.selectedBoard = this.displayedGame.getBoard();

        if (!this.displayedGame.isIdle() && !this.selectedBoard.isWaitingForPromotion()) {

            int selectedSquareIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                                    this.selectedBoard.getOrigin(), this.selectedBoard.getSquareSize(), this.selectedBoard.isFlipped());
            
            //System.out.println(e.getX() + " " + e.getY());

            if (selectedSquareIndex >= 0) {
                Square selectedSquare = this.selectedBoard.getSquareAt(selectedSquareIndex);

                this.mouseImageView.setFitWidth(selectedSquare.getSize());
                this.mouseImageView.setFitHeight(selectedSquare.getSize());

                //System.out.println("Mouse pressed on square " + selectedSquare.getFile() + " " + selectedSquare.getRank());

                ChessPiece piece = selectedSquare.getPiece();
                if (piece != null && piece.getColor() == this.selectedBoard.getPosition().colorToMove()) {
                    if (this.selectedBoard.getGame().getPlayer(piece.getColor()).isUser()) {
                        this.selectedBoard.setSelectedSquare(selectedSquareIndex);
                        attachImage(selectedSquare.detachImage());
                        moveImageToMouse(e);
                    }
                }
            } else {
                //System.out.println("Clicked out of bounds of the chess board.");
            }
        }
    }

    private void onMouseDragged(MouseEvent e) {
        if (this.pieceInMouse && !this.displayedGame.isIdle() && !this.selectedBoard.isWaitingForPromotion()) {
            moveImageToMouse(e);
        }
    }

    private void onMouseReleased(MouseEvent e) {
        //System.out.println("Mouse released");
        if (this.pieceInMouse && !this.displayedGame.isIdle() && !this.selectedBoard.isWaitingForPromotion()) {
            
            int releaseIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                               this.selectedBoard.getOrigin(), this.selectedBoard.getSquareSize(), this.selectedBoard.isFlipped());
            int startIndex = this.selectedBoard.getSelectedSquareIndex();

            detachImage();

            if (releaseIndex >= 0) {
                ChessMove move = new ChessMove(this.displayedGame.getPosition(), startIndex, releaseIndex);
                Player player = this.displayedGame.getPlayerToMove();

                //if (true && move.isLegal()) {
                if (player.isUser() && move.isLegal()) {
                    // move attempt succeeded; move the image and make the corresponsing move in the ChessPosition.
                    if (!move.isPromotion()) {
                        player.makeMove(move);
                    } else {
                        this.selectedBoard.openPromotionUI(move);
                        this.selectedBoard.waitForPromotion();
                    }
                    
                } else {
                    // move attempt failed; snap the image back.
                    this.selectedBoard.getSelectedSquare().reattachImage();
                }
            } else {
                // move attempt failed; snap the image back.
                this.selectedBoard.getSelectedSquare().reattachImage();
            }
            this.selectedBoard.deselectSquare();
            this.selectedBoard = null;
        }
    }

    private void moveImageToMouse(MouseEvent e) {
        double offset = this.selectedBoard.getSquareSize() / 2;
        this.mouseImageView.setX(e.getX() - offset);
        this.mouseImageView.setY(e.getY() - offset);
        this.mouseImageView.toFront();
    }

    public void attachImage(Image image) {
        this.mouseImageView.setImage(image);
        this.pieceInMouse = true;
    }

    public void detachImage() {
        this.mouseImageView.setImage(null);
        this.pieceInMouse = false;
    }

    public static Player getDefaultAgent() {
        Strategy strategy = new PositionalStrategy();
        strategy.addTechnique(new SingleQueenEndgame());
        return new Agent(strategy, 5, true);
    }
}
