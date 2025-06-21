package chess.logic;

import chess.logic.util.GridMath;
import chess.logic.rules.ChessLogic;

public class ChessMove {
    private int start;
    private int end;
    private ChessPiece piece;
    private boolean isLegal;
    private boolean isCapture;
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

        this.isLegal = ChessLogic.isLegalMove(position, this);
        
        this.isCapture = !position.isEmpty(end);
        this.isCheck = ChessLogic.isCheck(position.afterMove(this));
    }

    public boolean isLegal() {
        return this.isLegal;
    }

    public ChessPiece getPiece() {
        return this.piece;
    }

    public boolean isCapture() {
        return this.isCapture;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }
}
