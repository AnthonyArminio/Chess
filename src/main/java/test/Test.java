package test;

import java.util.ArrayList;

import chess.intel.util.DataMath;

public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(15);
        list.add(3);
        list.add(12);
        list.add(-4);
        list.add(7);
        DataMath.quickSort(list, false);
        for (int element : list) {
            System.out.println(element);
        }
        //NeuralNetwork nn = new NeuralNetwork(new StandardInputStrategy());

        //System.out.println(nn.evaluate(new ChessPosition()).evalString());

    }
}
