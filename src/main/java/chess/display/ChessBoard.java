package chess.display;

import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Point2D;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import javafx.event.Event;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import chess.logic.ChessPosition;
import chess.logic.piece.ChessPiece;
import chess.logic.util.GridMath;

/**
 * Represents a grid of squares contained within a Pane object. The light square color and dark
 * square color are specified when passed to the constructor.
 */
public class ChessBoard {

    private Group root;
    private GridPane checkerboard;
    private ImageView mouseImageView;
    private Square[] squares;
    private Square selectedSquare;
    private ChessPosition chessPosition;

    private double squareSize;
    private Color[] squareColors;
    private Point2D origin;

    /**
     * Initializes a new chess board.
     * @param origin the top-left corner of the chess board.
     * @param boardSize the width/height of the chess board
     * @param lightSquareColor the color of the light squares of the chess board
     * @param darkSquareColor the color of the dark squares of the chess board
     */
    public ChessBoard(Group root, Point2D origin, double boardSize, String darkSquareColor, String lightSquareColor) {
        this.root = root;
        this.origin = origin;
        this.squareSize = boardSize / 8.0;
        checkerboard = new GridPane();
        checkerboard.setPrefSize(boardSize, boardSize);

        this.mouseImageView = null;
        this.selectedSquare = null;

        initializeColors(darkSquareColor, lightSquareColor);

        makeSquares();

        this.chessPosition = new ChessPosition();

        loadPosition();

        this.root.setOnMousePressed(e -> onMousePressed(e));
        this.root.setOnMouseDragged(e -> onMouseDragged(e));
        this.root.setOnMouseReleased(e -> onMouseReleased(e));
    }

    private void initializeColors(String darkSquareColor, String lightSquareColor) {
        this.squareColors = new Color[2];
        this.squareColors[0] = (Color) Paint.valueOf(darkSquareColor);
        this.squareColors[1] = (Color) Paint.valueOf(lightSquareColor);
    }

    /**
     * Draws the squares in the appropriate locations for the checkerboard.
     */
    private void makeSquares() {

        this.squares = new Square[64];

        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                Point2D squareOrigin = new Point2D(this.origin.getX() + this.squareSize * (file - 1), 
                                                   this.origin.getY() + this.squareSize * (8 - rank));
                this.squares[GridMath.index(file, rank)] = new Square(squareOrigin, this.squareSize, this.squareColors[(file + rank) % 2], 
                                                              this.checkerboard, file, rank);
            }
        }
    }

    /**
     * Loads the current position onto the board.
     */
    private void loadPosition() {
        for (int file = 1; file <= 8; file++) {
            for (int rank = 1; rank <= 8; rank++) {
                if (this.chessPosition.getPieceAt(file, rank) != null) {                
                    this.squares[GridMath.index(file, rank)].setPiece(this.chessPosition.getPieceAt(file, rank));
                }
            }
        }
    }

    /**
     * Attaches the image associated with a square at the location of the mouse onto the mouse
     * @param e
     */
    private void onMousePressed(MouseEvent e) {
        int selectedSquareIndex = GridMath.findSquareIndex(e.getX(), e.getY(), this.origin, this.squareSize);
        if (selectedSquareIndex >= 0) {
            this.selectedSquare = squares[selectedSquareIndex];

            System.out.println("Mouse pressed on square " + selectedSquare.getFile() + " " + selectedSquare.getRank());

            if (this.selectedSquare.getPiece() != null) {
                attachImage(this.selectedSquare.detachImage());
                System.out.println(e.getX() + " " + e.getY());
                moveImageToMouse(e);
            } else {
                System.out.println("No piece at that location.");
            }
        } else {
            System.out.println("Clicked out of bounds of the chess board.");
        }
    }

    private void onMouseDragged(MouseEvent e) {
        if (this.mouseImageView != null) {
            moveImageToMouse(e);
        }
    }

    /**
     * Returns ownership of the ImageView to the GridPane
     * @param e
     */
    private void onMouseReleased(MouseEvent e) {
        System.out.println("Mouse released");
        if (this.mouseImageView != null) {
            this.selectedSquare.reattachImage();
            detachImage();
        }
    }

    /**
     * Moves a specified ImageView to the root Node so it can be moved by the mouse.
     * @param imageView
     */
    private void attachImage(ImageView imageView) {
        if (imageView != null) {
            this.mouseImageView = imageView;
            this.root.getChildren().add(this.mouseImageView);
            System.out.println("ATTACHED IMAGE TO SCENE");
        }
    }

    private void detachImage() {
        if (this.mouseImageView != null) {
            this.root.getChildren().remove(this.mouseImageView);
            this.mouseImageView = null;
            System.out.println("REMOVED IMAGE FROM SCENE");
        }
    }

    private void moveImageToMouse(MouseEvent e) {
        double offset = this.squareSize / 2;
        this.mouseImageView.setX(e.getX() - offset);
        this.mouseImageView.setY(e.getY() - offset);
        this.mouseImageView.toFront();
    }

    public Point2D getOrigin() {
        return this.origin;
    }

    public ChessPosition getChessPosition() {
        return this.chessPosition;
    }

    public GridPane getCheckerboard() {
        return this.checkerboard;
    }
}
