package chess.display;

import javafx.scene.Group;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.geometry.Point2D;

import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;

import javafx.scene.input.MouseEvent;

import chess.logic.ChessPiece;
import chess.logic.ChessMove;

import chess.logic.util.GridMath;

public class PromotionUI {

    private static final String WHITE_PANEL_HEX = "#dddddd";
    private static final String BLACK_PANEL_HEX = "#222222";
    private static final Color WHITE_PANEL_COLOR = (Color) Paint.valueOf(WHITE_PANEL_HEX);
    private static final Color BLACK_PANEL_COLOR = (Color) Paint.valueOf(BLACK_PANEL_HEX);

    private static final ChessPiece[] WHITE_PROMOTION_CANDIDATES = 
        {ChessPiece.W_QUEEN, ChessPiece.W_KNIGHT, ChessPiece.W_ROOK, ChessPiece.W_BISHOP};
    private static final ChessPiece[] BLACK_PROMOTION_CANDIDATES = 
        {ChessPiece.B_BISHOP, ChessPiece.B_ROOK, ChessPiece.B_KNIGHT, ChessPiece.B_QUEEN};

    private Group display;
    private ChessBoard chessBoard;
    private ChessMove move;
    private Point2D origin;
    private char color;
    private Color panelColor;
    private VBox panel;
    private ImageView[] imageViews;
    private Image[] images;
    private ChessPiece[] promotionCandidates;
    private int numPromotionCandidates;
    private double width;
    private double height;

    /**
     * Creates a new PromotionUI that allows the user to select a piece to promote to.
     * @param chessBoard the board that this UI is part of
     * @param move the move that prompted this UI
     */
    public PromotionUI(ChessBoard chessBoard, ChessMove move) {
        this.display = new Group();
        this.panel = new VBox();

        this.chessBoard = chessBoard;
        this.move = move;

        this.color = move.getColor();

        int file = GridMath.getFile(move.getEnd());
        this.origin = PromotionUI.findOrigin(chessBoard, file, this.color);

        // set appropriate panel color and piece selection based on the player color
        if (this.color == 'w') {
            this.panelColor = WHITE_PANEL_COLOR;
            this.promotionCandidates = WHITE_PROMOTION_CANDIDATES;
        } else {
            this.panelColor = BLACK_PANEL_COLOR;
            this.promotionCandidates = BLACK_PROMOTION_CANDIDATES;
        }

        this.numPromotionCandidates = promotionCandidates.length;

        this.imageViews = new ImageView[this.numPromotionCandidates];
        this.images = new Image[this.numPromotionCandidates];

        this.width = this.chessBoard.getSquareSize();
        this.height = this.numPromotionCandidates * this.chessBoard.getSquareSize();

        draw();

        // event handling
        this.panel.setOnMouseClicked(e -> onMouseClicked(e));
        
    }

    /**
     * Returns a Point2D corresponding to the upper-left corner of the UI when it is drawn.
     * @param chessBoard the ChessBoard where the pawn will be promoted
     * @param file the file where a pawn will be promoted
     * @param color the player color that prompted the UI
     * @return a Point2D object
     */
    private static Point2D findOrigin(ChessBoard chessBoard, int file, char color) {
        Point2D boardOrigin = chessBoard.getOrigin();
        if (color == 'w') {
            return new Point2D(boardOrigin.getX() + (file - 1) * chessBoard.getSquareSize(), boardOrigin.getY());
        } else {
            return new Point2D(boardOrigin.getX() + (file - 1) * chessBoard.getSquareSize(), boardOrigin.getY() + 4 * chessBoard.getSquareSize());
        }
    }

    /**
     * Sets up the display for this UI
     */
    private void draw() {
        // draw background
        drawRectangle(this.panelColor);

        for (int i = 0; i < this.numPromotionCandidates; i++) {
            this.images[i] = new Image(promotionCandidates[i].getImagePath());

            ImageView iv = new ImageView();
            iv.setImage(this.images[i]);
            iv.setFitWidth(width);
            iv.setFitHeight(width);
            this.imageViews[i] = iv;
            this.panel.getChildren().add(iv);
        }

        this.panel.setLayoutX(origin.getX());
        this.panel.setLayoutY(origin.getY());

        this.display.getChildren().add(this.panel);
        this.display.toFront();
    }

    /**
     * Draws the background for this display
     * @param color the Color to use for the background
     */
    private void drawRectangle(Color color) {
        Rectangle rectangle = new Rectangle(origin.getX(), origin.getY(), width, height);
        rectangle.setFill(color);
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);
        this.display.getChildren().add(rectangle);
    }

    public Group getDisplay() {
        return this.display;
    }

    private void onMouseClicked(MouseEvent e) {

        if (this.chessBoard.isWaitingForPromotion()) {

            int candidateIndex = (int)(e.getY() * this.numPromotionCandidates / height);
            if (candidateIndex < 0) {
                candidateIndex = 0;
            } else if (candidateIndex >= this.numPromotionCandidates) {
                candidateIndex = this.numPromotionCandidates - 1;
            }
                
            move.promoteTo(this.promotionCandidates[candidateIndex].getType());

            this.chessBoard.makeMove(move);

            this.chessBoard.closePromotionUI();
        }
    }
}
