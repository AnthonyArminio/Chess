package chess.logic.util;

import javafx.geometry.Point2D;

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

    public static int findSquareIndex(double x, double y, Point2D origin, double squareSize) {
        int file = (int) ((x - origin.getX()) / squareSize) + 1;
        int rank = 8 - (int) ((y - origin.getY()) / squareSize);

        return GridMath.index(file, rank);
    }
}
