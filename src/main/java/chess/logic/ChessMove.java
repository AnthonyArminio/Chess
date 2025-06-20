package chess.logic;

import chess.logic.util.GridMath;

public class ChessMove {
    private int start;
    private int end;
    private ChessPiece piece;
    private boolean isLegal;
    private boolean isCapture;

    public ChessMove(int start, int end, ChessPosition position) {
        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
    }
}
