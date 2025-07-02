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
import chess.display.ChessBoard;

import chess.logic.util.GridMath;

public class PromotionUI {

    private static final String WHITE_PANEL_HEX = "#ffffff";
    private static final String BLACK_PANEL_HEX = "#000000";
    private static final Color WHITE_PANEL_COLOR = (Color) Paint.valueOf(WHITE_PANEL_HEX);
    private static final Color BLACK_PANEL_COLOR = (Color) Paint.valueOf(BLACK_PANEL_HEX);

    private static final ChessPiece[] WHITE_PROMOTION_CANDIDATES = 
        {ChessPiece.W_QUEEN, ChessPiece.W_KNIGHT, ChessPiece.W_ROOK, ChessPiece.W_BISHOP};
    private static final ChessPiece[] BLACK_PROMOTION_CANDIDATES = 
        {ChessPiece.B_BISHOP, ChessPiece.B_ROOK, ChessPiece.B_KNIGHT, ChessPiece.B_QUEEN};

    private Group panelGroup;
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

    public PromotionUI(ChessBoard chessBoard, ChessMove move) {
        this.panelGroup = new Group();
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

        for (int i = 0; i < this.numPromotionCandidates; i++) {
            this.imageViews[i] = new ImageView();
            this.images[i] = new Image(promotionCandidates[i].getImagePath());
        }

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

    private void draw() {
        drawRectangle(this.panelColor);

        for (int i = 0; i < this.imageViews.length; i++) {
            this.imageViews[i].setImage(this.images[i]);
            this.imageViews[i].setFitWidth(width);
            this.imageViews[i].setFitHeight(width);
            this.panel.getChildren().add(this.imageViews[i]);
        }

        this.panel.setLayoutX(origin.getX());
        this.panel.setLayoutY(origin.getY());

        this.panelGroup.getChildren().add(this.panel);
        this.panelGroup.toFront();
    }

    private void drawRectangle(Color color) {
        Rectangle rectangle = new Rectangle(origin.getX(), origin.getY(), width, height);
        rectangle.setFill(color);
        rectangle.setArcHeight(10);
        rectangle.setArcWidth(10);
        this.panelGroup.getChildren().add(rectangle);
    }

    public Group getPanel() {
        return this.panelGroup;
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
            System.out.println("promotion successful");

            this.chessBoard.makeMove(move);

            this.chessBoard.closePromotionUI();
        }
    }
}
