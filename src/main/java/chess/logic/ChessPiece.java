package chess.logic.piece;

public class ChessPiece {

    public static final ChessPiece W_PAWN = new ChessPiece("file:assets/sprites/White_Pawn.png", 'P', 'w');
    public static final ChessPiece W_BISHOP = new ChessPiece("file:assets/sprites/White_Bishop.png", 'B', 'w');
    public static final ChessPiece W_KNIGHT = new ChessPiece("file:assets/sprites/White_Knight.png", 'N', 'w');
    public static final ChessPiece W_ROOK = new ChessPiece("file:assets/sprites/White_Rook.png", 'R', 'w');
    public static final ChessPiece W_QUEEN = new ChessPiece("file:assets/sprites/White_Queen.png", 'Q', 'w');
    public static final ChessPiece W_KING = new ChessPiece("file:assets/sprites/White_King.png", 'K', 'w');

    public static final ChessPiece B_PAWN = new ChessPiece("file:assets/sprites/Black_Pawn.png", 'P', 'b');
    public static final ChessPiece B_BISHOP = new ChessPiece("file:assets/sprites/Black_Bishop.png", 'B', 'b');
    public static final ChessPiece B_KNIGHT = new ChessPiece("file:assets/sprites/Black_Knight.png", 'N', 'b');
    public static final ChessPiece B_ROOK = new ChessPiece("file:assets/sprites/Black_Rook.png", 'R', 'b');
    public static final ChessPiece B_QUEEN = new ChessPiece("file:assets/sprites/Black_Queen.png", 'Q', 'b');
    public static final ChessPiece B_KING = new ChessPiece("file:assets/sprites/Black_King.png", 'K', 'b');

    public static final ChessPiece[] WHITE_PIECES = {W_PAWN, W_BISHOP, W_KNIGHT, W_ROOK, W_QUEEN, W_KING};
    public static final ChessPiece[] BLACK_PIECES = {B_PAWN, B_BISHOP, B_KNIGHT, B_ROOK, B_QUEEN, B_KING};
    
    private String imagePath;
    private char type;
    private char color;

    public ChessPiece(String imagePath, char type, char color) {
        this.imagePath = imagePath;
        this.type = type;
        this.color = color;
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
}
