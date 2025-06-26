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

    /**
     * Returns the index of the square at a given location in the scene based on the origin of its checkerboard
     * and the size of the squares of that board.
     * @param x
     * @param y
     * @param origin
     * @param squareSize
     * @return the index (0-63) of the square at pixel coordinates (x, y) in the scene, or -1 if the specified pixel
     * coordinates (x, y) are outside of the expected bounds of the board.
     */
    public static int findSquareIndex(double x, double y, Point2D origin, double squareSize) {
        // return -1 if the coordinates are out of bounds.
        if (x < origin.getX() || x >= origin.getX() + 8 * squareSize || 
            y < origin.getY() || y >= origin.getY() + 8 * squareSize) 
        {
            return -1;
        }
        
        int file = (int) ((x - origin.getX()) / squareSize) + 1;
        int rank = 8 - (int) ((y - origin.getY()) / squareSize);

        return GridMath.index(file, rank);
    }

    public static int getFile(int index) {
        return (index % 8) + 1;
    }

    public static int getRank(int index) {
        return (index / 8) + 1;
    }

    /**
     * Determines if the step between the square indices 'previous' and 'next' implies a movement
     * out of the bounds of the chess board.
     * @param previous
     * @param next
     * @return true if the step walks out of bounds, false otherwise.
     */
    public static boolean isOutOfBounds(int previous, int next) {
        
        // vertical bounds
        if (next < 0 || next >= 64) {
            return true;
        }

        // horizontal bounds
        if ((next % 8) - (previous % 8) < -2 || (next % 8) - (previous % 8) > 2) {
            return true;
        }

        return false;
    }
}
