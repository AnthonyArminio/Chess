package chess.logic;

import chess.logic.rules.ChessLogic;
import chess.logic.util.GridMath;

public class ChessPiece {

    private static final int[][] BASE_PAWN_MOVEMENT = {{0}, {0, 0}, {0}};
    private static final int[][] BASE_BISHOP_MOVEMENT = {{7, 14, 21, 28, 35, 42, 49}, 
                                                         {9, 18, 27, 36, 45, 54, 63},
                                                         {-7,-14,-21,-28,-35,-42,-49},
                                                         {-9,-18,-27,-36,-45,-54,-63}};
    private static final int[][] BASE_KNIGHT_MOVEMENT = {{6},{10},{15},{17},{-6},{-10},{-15},{-17}};
    private static final int[][] BASE_ROOK_MOVEMENT = {{1, 2, 3, 4, 5, 6, 7},
                                                       {8, 16, 24, 32, 40, 48, 56},
                                                       {-1,-2,-3,-4,-5,-6,-7},
                                                       {-8,-16,-24,-32,-40,-48,-56}};
    private static final int[][] BASE_QUEEN_MOVEMENT = {{7, 14, 21, 28, 35, 42, 49}, 
                                                        {9, 18, 27, 36, 45, 54, 63},
                                                        {-7,-14,-21,-28,-35,-42,-49},
                                                        {-9,-18,-27,-36,-45,-54,-63},
                                                        {1, 2, 3, 4, 5, 6, 7},
                                                        {8, 16, 24, 32, 40, 48, 56},
                                                        {-1,-2,-3,-4,-5,-6,-7},
                                                        {-8,-16,-24,-32,-40,-48,-56}};
    private static final int[][] BASE_KING_MOVEMENT = {{1, 0},{7},{8},{9},{-1, 0},{-7},{-8},{-9}};

    public static final ChessPiece W_PAWN = new ChessPiece("file:assets/sprites/White_Pawn.png", 'P', 'w', BASE_PAWN_MOVEMENT);
    public static final ChessPiece W_BISHOP = new ChessPiece("file:assets/sprites/White_Bishop.png", 'B', 'w', BASE_BISHOP_MOVEMENT);
    public static final ChessPiece W_KNIGHT = new ChessPiece("file:assets/sprites/White_Knight.png", 'N', 'w', BASE_KNIGHT_MOVEMENT);
    public static final ChessPiece W_ROOK = new ChessPiece("file:assets/sprites/White_Rook.png", 'R', 'w', BASE_ROOK_MOVEMENT);
    public static final ChessPiece W_QUEEN = new ChessPiece("file:assets/sprites/White_Queen.png", 'Q', 'w', BASE_QUEEN_MOVEMENT);
    public static final ChessPiece W_KING = new ChessPiece("file:assets/sprites/White_King.png", 'K', 'w', BASE_KING_MOVEMENT);

    public static final ChessPiece B_PAWN = new ChessPiece("file:assets/sprites/Black_Pawn.png", 'P', 'b', BASE_PAWN_MOVEMENT);
    public static final ChessPiece B_BISHOP = new ChessPiece("file:assets/sprites/Black_Bishop.png", 'B', 'b', BASE_BISHOP_MOVEMENT);
    public static final ChessPiece B_KNIGHT = new ChessPiece("file:assets/sprites/Black_Knight.png", 'N', 'b', BASE_KNIGHT_MOVEMENT);
    public static final ChessPiece B_ROOK = new ChessPiece("file:assets/sprites/Black_Rook.png", 'R', 'b', BASE_ROOK_MOVEMENT);
    public static final ChessPiece B_QUEEN = new ChessPiece("file:assets/sprites/Black_Queen.png", 'Q', 'b', BASE_QUEEN_MOVEMENT);
    public static final ChessPiece B_KING = new ChessPiece("file:assets/sprites/Black_King.png", 'K', 'b', BASE_KING_MOVEMENT);

    public static final ChessPiece[] WHITE_PIECES = {W_PAWN, W_BISHOP, W_KNIGHT, W_ROOK, W_QUEEN, W_KING};
    public static final ChessPiece[] BLACK_PIECES = {B_PAWN, B_BISHOP, B_KNIGHT, B_ROOK, B_QUEEN, B_KING};
    
    private String imagePath;
    private int[][] baseMovement;
    private char type;
    private char color;

    public ChessPiece(String imagePath, char type, char color, int[][] baseMovement) {
        this.imagePath = imagePath;
        this.type = type;
        this.color = color;
        this.baseMovement = baseMovement;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public char getType() {
        return this.type;
    }

    public char getColor() {
        return this.color;
    }

    public int[][] getBaseMovement() {
        return this.baseMovement;
    }

    public static ChessPiece[] getArmy(char color) {
        if (color == 'w') {
            return WHITE_PIECES;
        } else {
            return BLACK_PIECES;
        }
    }

    public boolean equals(ChessPiece other) {
        if (other == null) {
            return false;
        }
        return this.getType() == other.getType() && this.getColor() == other.getColor();
    }

    /**
     * Returns the movement pattern of this piece in context. For rooks, bishops, knights, and queens,
     * this method is equivalent to getBaseMovement, but for kings and pawns, this method is necessary
     * to account for special circumstances that alter the behaviour of the piece. This method does not
     * account for out-of-bounds movement and may not account for blocked paths.
     * @param position the chess position that is to be the context of this piece's movement.
     * @param location the index of the square that contains this piece.
     * @return the movement pattern of this piece in context.
     */
    public int[][] getMovement(ChessPosition position, int location) {
        
        int[][] movement = this.getBaseMovement();
        if (this.getType() == 'P') {
            if (this.getColor() == 'w') {
                if (position.isEmpty(location + 8)) {
                    movement[1][0] = 8;
                    if (GridMath.getRank(location) == 2 && position.isEmpty(location + 16)) {
                        movement[1][1] = 16;
                    }
                }
                if (!position.isEmpty(location + 7) || position.getEnPassantOpportunity() == location + 7) {
                    movement[0][0] = 7;
                }
                if (!position.isEmpty(location + 9) || position.getEnPassantOpportunity() == location + 9) {
                    movement[2][0] = 9;
                }
            } else {
                if (position.isEmpty(location - 8)) {
                    movement[1][0] = -8;
                    if (GridMath.getRank(location) == 7 && position.isEmpty(location - 16)) {
                        movement[1][1] = -16;
                    }
                }
                if (!position.isEmpty(location - 7) || position.getEnPassantOpportunity() == location - 7) {
                    movement[0][0] = -7;
                }
                if (!position.isEmpty(location - 9) || position.getEnPassantOpportunity() == location - 9) {
                    movement[2][0] = -9;
                }
            }
        
        } else if (this.getType() == 'K') {
            if (position.hasKingsideCastlingRights(this.getColor())) {
                if (true) {
                    movement[0][1] = 2;
                }
            }
            if (position.hasQueensideCastlingRights(this.getColor())) {
                if (position.isEmpty(location - 3)) {
                    movement[4][1] = -2;
                }
            }
        }

        return movement;
    }
}
