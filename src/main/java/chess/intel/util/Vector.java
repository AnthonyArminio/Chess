package chess.intel.util;

public class Vector extends Matrix {

    private int dim;

    public Vector(int dimension) {
        super(dimension, 1);
        this.dim = dimension;
    }

    public Vector(float[][] data) {
        super(data);
        this.dim = data[0].length;
    }

    public void set(int index, float value) {
        this.getData()[index] = value;
    }

    @Override public Vector copy() {
        return new Vector(this.data);
    }

    /**
     * Returns the entry at a specified index
     * @param index
     * @return
     * @throws IllegalArgumentException if the specified index exceeds or equals the number of dimensions.
     */
    public float get(int index) {
        if (index >= this.dim) {
            throw new IllegalArgumentException("get: specified index exceeds the dimension of this vector.");
        }

        return this.getData()[index];
    }

    /**
     * Returns the dimension of this Vector.
     * @return
     */
    public int dim() {
        return this.dim;
    }

    public float[] getData() {
        return this.data[0];
    }
}
