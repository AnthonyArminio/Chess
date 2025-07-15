package chess.logic;

/**
 * Position class to be used to detect repeated positions efficiently.
 */
public class CompressedPosition {


    public static final int BISHOP = 0;
    public static final int KNIGHT = 1;
    public static final int ROOK = 2;
    public static final int QUEEN = 3;
    public static final int KING = 4;

    public static final int WHITE = 0;
    public static final int BLACK = 1;

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
                this.piecePlacements[id - 2][WHITE] += 1l << i;
            } else if (id <= -2) {
                this.piecePlacements[-2 - id][BLACK] += 1l << i;
            }
        }

        // this is a pass by reference: no need to update this again when a move is made
        this.stateArray = position.getStateArray();
    }

    /**
     * Creates a new CompressedPosition where the stateArray is not stored by reference. As such, objects
     * created using this constructor should be final and should never call makeMove().
     * @param piecePlacements
     * @param stateArray
     */
    public CompressedPosition(long[][] piecePlacements, int[] stateArray) {

        this.piecePlacements = new long[5][2];
        for (int p = BISHOP; p < 5; p++) {
            for (int c = WHITE; c < 2; c++) {
                this.piecePlacements[p][c] = piecePlacements[p][c];
            }
        }

        this.stateArray = new int[5];
        for (int i = 0; i < 5; i++) {
            this.stateArray[i] = stateArray[i];
        }
    }

    private void makeAlteration(int start, int end, int id) {
        if (id >= 2) {
            this.piecePlacements[id - 2][WHITE] -= 1l << start;
            this.piecePlacements[id - 2][WHITE] += 1l << end;
        } else if (id <= -2) {
            this.piecePlacements[-2 - id][BLACK] -= 1l << start;
            this.piecePlacements[-2 - id][BLACK] += 1l << end;
        }
    }

    public void makeMove(ChessMove move) {
        int start = move.getStart();
        int end = move.getEnd();

        if (move.isCapture()) {
            for (int p = BISHOP; p < 5; p++) {
                for (int c = WHITE; c < 2; c++) {
                    if (((this.piecePlacements[p][c] >> end) & 1) != 0) {
                        this.piecePlacements[p][c] -= 1l << end;
                    }
                }
            }
        }

        int id = move.getPieceID();

        if (move.isPromotion()) {
            if (id >= 2) {
                this.piecePlacements[id - 2][WHITE] += 1l << end;
            } else if (id <= -2) {
                this.piecePlacements[-2 - id][BLACK] += 1l << end;
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

    public CompressedPosition copy() {
        return new CompressedPosition(this.piecePlacements, this.stateArray);
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

    public long[][] getPiecePlacements() {
        return this.piecePlacements;
    }
}
