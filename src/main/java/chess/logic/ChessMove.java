package chess.logic;

import chess.logic.util.GridMath;
import chess.logic.rules.ChessLogic;

public class ChessMove {
    private int start;
    private int end;
    private ChessPiece piece;
    private char pieceType;
    private char color;
    private boolean isLegal;
    private boolean isCapture;
    private boolean isEnPassant;
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
        this.pieceType = this.piece.getType();
        this.color = this.piece.getColor();

        this.isLegal = ChessLogic.isLegalMove(position, this);
        
        this.isEnPassant = ChessLogic.isEnPassant(position, this);
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

    public boolean isEnPassant() {
        return this.isEnPassant;
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
        return this.pieceType;
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
}
