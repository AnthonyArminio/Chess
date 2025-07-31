package test;

import java.util.ArrayList;
import chess.intel.util.DataMath;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<Integer>();

        numbers.add(1);
        numbers.add(5);
        numbers.add(12);
        numbers.add(-11);
        numbers.add(-55);
        numbers.add(66);
        numbers.add(10);
        numbers.add(3);
        numbers.add(8);

        DataMath.<Integer>quickSort(numbers, false);

        for (int element : numbers) {
            System.out.println(element);
        }
    }
}
