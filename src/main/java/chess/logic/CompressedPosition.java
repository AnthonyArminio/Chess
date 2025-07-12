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

    // encodes the placements of each piece type (excluding pawns). The first dimension encodes
    // piece type, the second dimension encodes piece color, and the value of the long encodes
    // the placements of those pieces (each bit represents a square, starting from index 0).
    private long[][] piecePlacements;

    public CompressedPosition(ChessPosition position) {
        this.piecePlacements = new long[5][2];
    }
}
