package chess.intel.util;

import java.util.ArrayList;

public class DataMath {

    /**
     * Returns the sum of two vectors.
     * @param v1
     * @param v2
     * @return
     * @throws IllegalArgumentException If the dimensions of the two vectors do not match.
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
     * Multiplies a Matrix m by a Vector v.
     * @param m the Matrix to multiply.
     * @param v the Vector to multiply.
     * @return the product of m and v.
     * @throws IllegalArgumentException if the number of columns of m does not match the dimension of v.
     */
    public static Vector matrixMultiply(Matrix m, Vector v) {

        if (m.cols() != v.dim()) {
            throw new IllegalArgumentException("matrixMultiply: dimensions do not match.");
        }

        int dim = v.dim();
        int newDim = m.rows();
        Vector result = new Vector(newDim);

        for (int i = 0; i < newDim; i++) {
            for (int j = 0; j < dim; j++) {
                result.set(i, result.get(i) + (m.get(i, j) * v.get(j)));
            }
        }

        return result;
    }
    
    /**
     * Sorts a specified list using quick sort
     * @param <T> The list type, which must implement Comparable<T>
     * @param list The list to sort
     * @param reversed Whether the list should be sorted in reverse
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

        minorList.add(basis);
        ArrayList<T> sortedList;
        if (reversed) {
            sortedList = combineLists(minorList, majorList);
        } else {
            sortedList = combineLists(majorList, minorList);
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
        return 1 / (1 + (float) Math.pow(Math.E, -x));
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
}
