package chess.intel.util;

import com.google.gson.annotations.Expose;

public class Matrix {
    
    @Expose protected float[][] data;
    @Expose private int rows;
    @Expose private int cols;

    public Matrix(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.cols][this.rows];

        clear();
    }

    public Matrix(int rows, int cols, float min, float max) {
        this.rows = rows;
        this.cols = cols;
        this.data = new float[this.cols][this.rows];

        randomize(min, max);
    }

    public Matrix(float[][] data) {
        this.rows = data[0].length;
        this.cols = data.length;
        this.data = new float[this.cols][this.rows];

        setAll(data);
    }

    public Matrix(Vector... vectors) {
        this.rows = vectors[0].dim();
        this.cols = vectors.length;
        this.data = new float[this.cols][this.rows];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[j][i] = vectors[j].get(i);
            }
        }
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

    private void randomize(float min, float max) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.data[j][i] = DataMath.random(min, max);
            }
        }
    }

    /**
     * Changes each entry in this Matrix to a random value between its current value and 0.
     */
    public void randomize() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                float min;
                float max;
                if (this.data[j][i] > 0) {
                    min = 0;
                    max = this.data[j][i];
                } else {
                    min = this.data[j][i];
                    max = 0;
                }
                this.data[j][i] = DataMath.random(min, max);
            }
        }
    }

    /**
     * Multiplies all of the entries in a given Matrix by a specified scalar value.
     * @param scalar The scalar to multiply by.
     * @return A reference to the altered Matrix.
     */
    public Matrix scalarMultiply(float scalar) {

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.set(i, j, scalar * this.get(i, j));
            }
        }

        return this;
    }

    /**
     * Adds another Matrix to this Matrix.
     * @param other The Matrix to add to this Matrix.
     * @return A reference to the altered Matrix.
     */
    public Matrix add(Matrix other) {

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                this.set(i, j, this.get(i, j) + other.get(i, j));
            }
        }

        return this;
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
