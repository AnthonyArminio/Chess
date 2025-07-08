package chess.logic;

import chess.logic.util.GridMath;
import chess.logic.rules.ChessLogic;

public class ChessMove {
    private int start;
    private int end;
    private ChessPiece piece;
    private char color;
    private boolean isLegal;
    private boolean isCapture;
    private boolean isEnPassant;
    private boolean isPromotion;
    private boolean isKingsideCastle;
    private boolean isQueensideCastle;
    private boolean isCheck;

    /**
     * Creates a new ChessMove.
     * @param position the ChessPosition serving as the context of the move
     * @param start index of the piece that is moving
     * @param end index of the destination square
     */
    public ChessMove(ChessPosition position, int start, int end) {
        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
        this.color = this.piece.getColor();

        this.isKingsideCastle = ChessLogic.isKingsideCastle(position, this);
        this.isQueensideCastle = ChessLogic.isQueensideCastle(position, this);

        this.isLegal = ChessLogic.isLegalMove(position, this);
        
        this.isEnPassant = ChessLogic.isEnPassant(position, this);
        this.isPromotion = ChessLogic.isPromotion(this);

        this.isCapture = !position.isEmpty(end) || this.isEnPassant;
        this.isCheck = ChessLogic.isCheck(position.afterMove(this));
    }

    /**
     * Creates a new ChessMove.
     * @param position the ChessPosition serving as the context of the move
     * @param start index of the piece that is moving
     * @param end index of the destination square
     * @param promotionType the type of piece to promote to, if applicable.
     */
    public ChessMove(ChessPosition position, int start, int end, char promotionType) {
        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
        this.color = this.piece.getColor();

        this.isKingsideCastle = ChessLogic.isKingsideCastle(position, this);
        this.isQueensideCastle = ChessLogic.isQueensideCastle(position, this);

        this.isLegal = ChessLogic.isLegalMove(position, this);
        
        this.isEnPassant = ChessLogic.isEnPassant(position, this);
        this.isPromotion = ChessLogic.isPromotion(this);
        if (this.isPromotion) {
            promoteTo(promotionType);
        }

        this.isCapture = !position.isEmpty(end) || this.isEnPassant;
        this.isCheck = ChessLogic.isCheck(position.afterMove(this));
    }

    /**
     * Returns the location on the chess board that should be available for en passant after this move
     * is made.
     * @return the location's index as an integer, or -1 if no en passant opportunity should be made
     * available.
     */
    public int enPassantValue() {
        if (this.piece.getType() == 'P') {
            if (this.end - this.start == 16) {
                return this.start + 8;
            } else if (this.end - this.start == -16) {
                return this.start - 8;
            }
        }
        return -1;
    }

    public void promoteTo(char type) {
        this.piece = ChessPiece.getPiece(this.getColor(), type);
    }

    public boolean isEnPassant() {
        return this.isEnPassant;
    }

    public boolean isPromotion() {
        return this.isPromotion;
    }

    public boolean isKingsideCastle() {
        return this.isKingsideCastle;
    }

    public boolean isQueensideCastle() {
        return this.isQueensideCastle;
    }

    public boolean isLegal() {
        return this.isLegal;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public char getColor() {
        return this.color;
    }

    public char getPieceType() {
        return this.piece.getType();
    }

    public int getPieceID() {
        return this.piece.getID();
    }

    public boolean isCapture() {
        return this.isCapture;
    }

    public boolean isCheck() {
        return this.isCheck;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    /**
     * Returns the algebraic notation of this move in the context of a specified ChessPosition. If
     * the move is illegal, this method returns null.
     * @param position the context of the move
     * @return the notation, as a String, or null if the move is illegal.
     */
    public String getNotation(ChessPosition position) {
        if (this.isLegal) {
            String body;
            if (this.isKingsideCastle) {
                body = "O-O";
            } else if (this.isQueensideCastle) {
                body = "O-O-O";
            } else {
                body = "" + this.getPieceType();
                if (this.isCapture) {
                    body += 'x';
                }
            }
        } else {
            return null;
        }
    }
}
