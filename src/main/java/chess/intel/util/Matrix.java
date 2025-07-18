package chess.intel.util;

public class Matrix {
    
    protected float[][] data;
    private int rows;
    private int cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.cols][this.rows];

        clear();
    }

    public Matrix(float[][] data) {
        this.rows = data[0].length;
        this.cols = data.length;

        setAll(data);
    }

    /**
     * Sets every entry of this Matrix to 0.
     */
    public void clear() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[j][i] = 0;
            }
        }
    }

    public void set(int row, int col, float value) {
        this.data[col][row] = value;
    }

    public void setAll(float[][] data) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[j][i] = data[j][i];
            }
        }
    }

    public Matrix copy() {
        return new Matrix(this.data);
    }

    /**
     * Returns the entry at a specifid row and column.
     * @param row
     * @param col
     * @return
     * @throws IllegalArgumentException if the specified row or column is out of bounds.
     */
    public float get(int row, int col) {
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
