package chess.intel.util;

public class Matrix {
    
    protected double[][] data;
    private int rows;
    private int cols;

    public Matrix(int rows, int cols) {
        this.data = new double[cols][rows];
        this.rows = rows;
        this.cols = cols;

        clear();
    }

    /**
     * Sets every entry of this Matrix to 0.
     */
    public void clear() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[i][j] = 0;
            }
        }
    }

    /**
     * Returns the entry at a specifid row and column.
     * @param row
     * @param col
     * @return
     * @throws IllegalArgumentException if the specified row or column is out of bounds.
     */
    public double get(int row, int col) {
        if (row >= this.rows || col >= this.cols) {
            throw new IllegalArgumentException("get: specified row or column is out of bounds for this matrix.");
        }

        return this.data[col][row];
    }

    /**
     * Returns the number of rows of this Matrix.
     * @return
     */
    public int rows() {
        return this.rows;
    }

    /**
     * Returns the number of columns of this Matrix.
     * @return
     */
    public int cols() {
        return this.cols;
    }
}
