package test;

import java.util.ArrayList;
import chess.intel.util.DataMath;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        int[] list = {6, 2, 8, 1, -6, -8, 12, 0, 4};

        for (int element : list) {
            numbers.add(element);
        }

        DataMath.<Integer>quickSort(numbers, false);

        for (int element : numbers) {
            System.out.println(element);
        }
    }
}
