package chess.intel.util;

import java.util.ArrayList;

public class DataMath {

    /**
     * Returns the sum of two Vectors.
     * @param v1
     * @param v2
     * @return
     * @throws IllegalArgumentException If the dimensions of the two Vectors do not match.
     */
    public static Vector vectorAdd(Vector v1, Vector v2) {
        if (v1.dim() != v2.dim()) {
            throw new IllegalArgumentException("vectorAdd: both vectors do not have the same dimension.");
        }

        int dim = v1.dim();
        Vector result = new Vector(dim);

        for (int i = 0; i < dim; i++) {
            result.set(i, v1.get(i) + v2.get(i));
        }

        return result;
    }

    /**
     * Returns the sum of two Matrices.
     * @param m1
     * @param m2
     * @return
     * @throws IllegalArgumentException If the dimensions of the two Matrices do not match.
     */
    public static Matrix matrixAdd(Matrix m1, Matrix m2) {
        if (m1.rows() != m2.rows() || m1.cols() != m2.cols()) {
            throw new IllegalArgumentException("matrixAdd: both matrices do not have the same dimensions.");
        }

        int rows = m1.rows();
        int cols = m2.cols();
        Matrix result = new Matrix(rows, cols);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result.set(i, j, m1.get(i, j) + m2.get(i, j));
            }
        }

        return result;
    }

    public static Vector scalarMultiply(Vector v, float scalar) {
        return v.copy().scalarMultiply(scalar);
    }

    public static Matrix scalarMultiply(Matrix m, float scalar) {
        return m.copy().scalarMultiply(scalar);
    }

    /**
     * Multiplies a Matrix m by a Vector v.
     * @param m the Matrix to multiply.
     * @param v the Vector to multiply.
     * @return the product of m and v.
     * @throws IllegalArgumentException if the number of columns in m does not match the dimension of v.
     */
    public static Vector matrixMultiply(Matrix m, Vector v) {

        if (m.cols() != v.dim()) {
            throw new IllegalArgumentException("matrixMultiply: dimensions are not compatible.");
        }

        int dim = v.dim();
        int newDim = m.rows();
        Vector result = new Vector(newDim);

        for (int i = 0; i < newDim; i++) {
            float sum = 0;
            for (int j = 0; j < dim; j++) {
                sum += m.get(i, j) * v.get(j);
            }
            result.set(i, sum);
        }

        return result;
    }

    /**
     * Multiplies a Matrix m1 by another Matrix m2.
     * @param m1 The first Matrix to multiply.
     * @param m2 The second Matrix to multiply.
     * @return The product of m1 and m2.
     * @throws IllegalArgumentException if the number of columns in m1 does not match the number of rows in m2.
     */
    public static Matrix matrixMultiply(Matrix m1, Matrix m2) {
        if (m1.cols() != m2.rows()) {
            throw new IllegalArgumentException("matrixMultiply: dimensions are not compatible.");
        }

        int dim = m2.rows();
        int newRows = m1.rows();
        int newCols = m2.cols();
        Matrix result = new Matrix(newRows, newCols);

        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                float sum = 0;
                for (int k = 0; k < dim; k++) {
                    sum += m1.get(i, k) * m2.get(k, j);
                }
                result.set(i, j, sum);
            }
        }

        return result;
    }

    /**
     * Takes the transpose of a Matrix m.
     * @return The transpose of m.
     */
    public static Matrix matrixTranspose(Matrix m) {
        int newRows = m.cols();
        int newCols = m.rows();

        Matrix result = new Matrix(newRows, newCols);

        for (int i = 0; i < newRows; i++) {
            for (int j = 0; j < newCols; j++) {
                result.set(i, j, m.get(j, i));
            }
        }
        
        return result;
    }

    /**
     * Takes the inverse of a 2 by 2 Matrix m.
     * @return The inverse of m.
     * @throws IllegalArgumentException if m is not a 2 by 2 Matrix.
     */
    public static Matrix matrixInverse2D(Matrix m) {
        if (m.rows() != 2 || m.cols() != 2) {
            throw new IllegalArgumentException("matrixInverse2D: must pass 2 by 2 Matrix.");
        }

        float determinant = m.get(0, 0) * m.get(1, 1) - m.get(0, 1) * m.get(1, 0);

        if (determinant == 0) {
            determinant = 0.01f;
        }
        Matrix result = new Matrix(2, 2);

        result.set(0, 0, m.get(1, 1));
        result.set(0, 1, -1 * m.get(0, 1));
        result.set(1, 0, -1 * m.get(1, 0));
        result.set(1, 1, m.get(0, 0));

        return result.scalarMultiply(1f / determinant);
    }
    
    /**
     * Sorts a specified list using quick sort
     * @param <T> The list type, which must implement Comparable<T>
     * @param list The list to sort
     * @param reversed Whether the list should be sorted in reverse (greatest to least)
     */
    public static <T extends Comparable<T>> void quickSort(ArrayList<T> list, boolean reversed) {

        T basis = list.remove(0);
        ArrayList<T> minorList = new ArrayList<T>();
        ArrayList<T> majorList = new ArrayList<T>();

        for (T element : list) {
            if (element.compareTo(basis) < 0) {
                minorList.add(element);
            } else {
                majorList.add(element);
            }
        }

        if (minorList.size() > 1) {
            quickSort(minorList, reversed);
        }
        if (majorList.size() > 1) {
            quickSort(majorList, reversed);
        }

        ArrayList<T> sortedList;
        if (reversed) {
            majorList.add(basis);
            sortedList = combineLists(majorList, minorList);
        } else {
            minorList.add(basis);
            sortedList = combineLists(minorList, majorList);
        }

        list.clear();
        for (T element : sortedList) {
            list.add(element);
        }

    }

    /**
     * Returns a sigma distribution given an input x.
     * @param x
     * @return sigma(x)
     */
    public static float sigma(double x) {
        return 1 / (1 + (float) Math.pow(Math.E, -1 * x));
    }

    /**
     * Applies the sigma distribution to all entries of a given Vector.
     * @param v
     */
    public static void sigma(Vector v) {
        for (int i = 0; i < v.dim(); i++) {
            v.set(i, DataMath.sigma(v.get(i)));
        }
    }

    /**
     * Combines two lists into a single list and returns the result.
     * @param <T> The list type
     * @param list1 The first list
     * @param list2 The second list
     * @return The new list
     */
    private static <T> ArrayList<T> combineLists(ArrayList<T> list1, ArrayList<T> list2) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T element : list1) {
            newList.add(element);
        }
        for (T element : list2) {
            newList.add(element);
        }
        return newList;
    }

    /**
     * Returns a random integer between min and max, inclusive.
     * @throws IllegalArgumentException if max < min.
     */
    public static int random(int min, int max) {
        if (max < min) {
            throw new IllegalArgumentException("random: max cannot be less than min");
        }
        return (int) ((max - min + 1) * Math.random() + min);
    }

    /**
     * Returns a random value between min and max.
     * @throws IllegalArgumentException if max < min.
     */
    public static float random(float min, float max) {
        if (max < min) {
            throw new IllegalArgumentException("random: max cannot be less than min");
        }
        return (float) ((max - min) * Math.random() + min);
    }
}
