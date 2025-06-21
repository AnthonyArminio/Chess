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

    public ChessMove(int start, int end, ChessPosition position) {
        this.start = start;
        this.end = end;
        this.piece = position.getPieceAt(start);

        // maybe don't do this, instead check to see if a move is legal before instantiating it.
        this.isLegal = ChessLogic.isLegalMove(position, start, end);
        
        if (this.isLegal) {
            this.isCapture = !position.isEmpty(end);
            this.isCheck = ChessLogic.isCheck(position, start, end);
        } else {
            this.isCapture = false;
            this.isCheck = false;
        }
    }
}
