package chess.logic;

import chess.logic.util.GridMath;
import chess.logic.rules.ChessLogic;

public class ChessMove {
    private int start;
    private int end;
    private ChessPiece piece;
    private boolean isCapture;
    private boolean isCheck;

    /**
     * Creates a new ChessMove. It is assumed that all ChessMoves instantiated using this constructor are legal.
     * @param position the ChessPosition serving as the context of the move
     * @param start index of the piece that is moving
     * @param end index of the destination square
     */
    public ChessMove(ChessPosition position, int start, int end) {
        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);
        
        this.isCapture = !position.isEmpty(end);
        this.isCheck = ChessLogic.isCheck(position, start, end);
    }
}
