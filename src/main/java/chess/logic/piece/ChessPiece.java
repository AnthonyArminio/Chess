package chess.logic.piece;

public class ChessPiece {

    public static ChessPiece W_PAWN = new ChessPiece("assets/sprites/White_Pawn.png", 'w');
    public static ChessPiece W_BISHOP = new ChessPiece("assets/sprites/White_Bishop.png", 'w');
    
    private String imagePath;
    private char color;

    public ChessPiece(String imagePath, char color) {
        this.imagePath = imagePath;
        this.color = color;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public char getColor() {
        return this.color;
    }
}
