package chess.logic.util;

public class GridMath {
    /**
     * Returns the index (0-63) of the square referenced by a specified file and rank.
     * @param file integer between 1 and 8, inclusive.
     * @param rank integer between 1 and 8, inclusive.
     * @return square index corresponding to the specified coordinates.
     */
    public static int index(int file, int rank) {
        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("squareIndexOf: file and rank must be between 1 and 8, inclusive.");
        }

        return 8 * (rank - 1) + file - 1;
    }
}
