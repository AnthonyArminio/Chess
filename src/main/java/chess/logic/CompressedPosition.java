package chess.logic;

/**
 * Position class to be used to detect repeated positions efficiently.
 */
public class CompressedPosition {

    private static final int BISHOP = 0;
    private static final int KNIGHT = 1;
    private static final int ROOK = 2;
    private static final int QUEEN = 3;
    private static final int KING = 4;

    private static final int WHITE = 0;
    private static final int BLACK = 1;

    // Encodes the placements of each piece type (excluding pawns). The first dimension encodes
    // piece type, the second dimension encodes piece color, and the value of the long encodes
    // the placements of those pieces (each bit represents a square, starting from index 0).
    private long[][] piecePlacements;

    // The stateArray (metadata) of the encoded position
    private int[] stateArray;

    public CompressedPosition(ChessPosition position) {
        this.piecePlacements = new long[5][2];

        for (int p = BISHOP; p < 5; p++) {
            for (int c = WHITE; c < 2; c++) {
                this.piecePlacements[p][c] = 0;
            }
        }

        for (int i = 0; i < 64; i++) {
            int id = position.getPieceIDAt(i);
            if (id >= 2) {
                this.piecePlacements[id - 2][WHITE] += 1 << i;
            } else if (id <= -2) {
                this.piecePlacements[-2 - id][BLACK] += 1 << i;
            }
        }

        // no need to update this again when a move is made: this is a reference.
        this.stateArray = position.getStateArray();
    }

    private void makeAlteration(int start, int end, int id) {
        if (id >= 2) {
            this.piecePlacements[id - 2][WHITE] -= 1 << start;
            this.piecePlacements[id - 2][WHITE] += 1 << end;
        } else if (id <= -2) {
            this.piecePlacements[-2 - id][BLACK] -= 1 << start;
            this.piecePlacements[-2 - id][BLACK] += 1 << end;
        }
    }

    public void makeMove(ChessMove move) {
        int start = move.getStart();
        int end = move.getEnd();

        if (move.isCapture()) {
            for (int p = BISHOP; p < 5; p++) {
                for (int c = WHITE; c < 2; c++) {
                    if (((this.piecePlacements[p][c] << end) & 1) != 0) {
                        this.piecePlacements[p][c] -= 1 << end;
                    }
                }
            }
        }

        int id = move.getPieceID();

        if (move.isPromotion()) {
            if (id >= 2) {
                this.piecePlacements[id - 2][WHITE] += 1 << end;
            } else if (id <= -2) {
                this.piecePlacements[-2 - id][BLACK] += 1 << end;
            }
        } else {
            makeAlteration(start, end, id);
            if (move.isKingsideCastle()) {
                if (move.getColor() == 'w') {
                    makeAlteration(7, 5, ChessPiece.W_ROOK.getID());
                } else {
                    makeAlteration(63, 61, ChessPiece.B_ROOK.getID());
                }
            } else if (move.isQueensideCastle()) {
                if (move.getColor() == 'w') {
                    makeAlteration(0, 3, ChessPiece.W_ROOK.getID());
                } else {
                    makeAlteration(56, 59, ChessPiece.B_ROOK.getID());
                }
            }
        }
    }

    /**
     * Returns true if this position is the same as another position, and false otherwise.
     */
    public boolean equals(CompressedPosition other) {

        for (int p = BISHOP; p < 5; p++) {
            for (int c = WHITE; c < 2; c++) {
                if (this.piecePlacements[p][c] != other.piecePlacements[p][c]) {
                    return false;
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            if (this.stateArray[i] != other.stateArray[i]) {
                return false;
            }
        }

        return true;
    }
}
