package chess.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import chess.display.ChessBoard;
import chess.display.Square;
import chess.display.PromotionUI;
import chess.logic.rules.ChessLogic;
import chess.logic.ChessMove;
import chess.logic.util.GridMath;

public class Chess extends Application {

    private final double boardSize = 504.0;
    private final String lightSquareColor = "#999999";
    private final String darkSquareColor = "#333333";
    private ChessBoard board;
    private ChessBoard selectedBoard;
    private ImageView mouseImageView;
    private boolean pieceInMouse;

    private Group root;
    private HBox layout;
    private Scene scene;

    public void init() {
        System.out.println("Test Init");

        this.root = new Group();
        this.layout = new HBox();
        this.board = new ChessBoard(Point2D.ZERO, boardSize, darkSquareColor, lightSquareColor);
        this.selectedBoard = null;

        this.mouseImageView = new ImageView();
        this.pieceInMouse = false;
        this.root.getChildren().add(this.mouseImageView);

        layout.getChildren().add(this.board.getBoard());
        root.getChildren().add(layout);

        // set up event handlers
        this.root.setOnMousePressed(e -> onMousePressed(e));
        this.root.setOnMouseDragged(e -> onMouseDragged(e));
        this.root.setOnMouseReleased(e -> onMouseReleased(e));

        this.scene = new Scene(root);
    }
    
    public void start(Stage stage) {
        System.out.println("Test Start");
        
        stage.setTitle("Chess Application");
        stage.setScene(this.scene);

        stage.sizeToScene();
        stage.show();
    }


    private void onMousePressed(MouseEvent e) {
        this.selectedBoard = this.board; // change this later when multiple boards can be displayed

        if (!this.selectedBoard.isWaitingForPromotion()) {

            int selectedSquareIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                                    this.selectedBoard.getOrigin(), this.selectedBoard.getSquareSize());
            
            //System.out.println(e.getX() + " " + e.getY());

            if (selectedSquareIndex >= 0) {
                this.selectedBoard.setSelectedSquare(selectedSquareIndex);
                Square selectedSquare = this.selectedBoard.getSelectedSquare();

                //System.out.println("Mouse pressed on square " + selectedSquare.getFile() + " " + selectedSquare.getRank());

                if (selectedSquare.getPiece() != null) {
                    attachImage(selectedSquare.detachImage());
                    moveImageToMouse(e);
                } else {
                    System.out.println("No piece at that location.");
                }
            } else {
                //System.out.println("Clicked out of bounds of the chess board.");
            }
        }
    }

    private void onMouseDragged(MouseEvent e) {
        if (this.pieceInMouse && !this.selectedBoard.isWaitingForPromotion()) {
            moveImageToMouse(e);
        }
    }

    private void onMouseReleased(MouseEvent e) {
        //System.out.println("Mouse released");
        if (this.pieceInMouse && !this.selectedBoard.isWaitingForPromotion()) {
            
            int releaseIndex = GridMath.findSquareIndex(e.getX(), e.getY(), 
                               this.selectedBoard.getOrigin(), this.selectedBoard.getSquareSize());
            int startIndex = this.selectedBoard.getSelectedSquareIndex();

            if (releaseIndex >= 0) {
                ChessMove move = new ChessMove(this.selectedBoard.getChessPosition(), startIndex, releaseIndex);

                if (move.isLegal()) {
                    // move attempt succeeded; move the image and make the corresponsing move in the ChessPosition.
                    if (!move.isPromotion()) {
                        this.selectedBoard.makeMove(move);
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
            detachImage();
            //this.selectedBoard.deselectSquare();
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
}
