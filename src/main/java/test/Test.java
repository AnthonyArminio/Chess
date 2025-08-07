package test;

import java.util.ArrayList;

import chess.logic.ChessPosition;

import chess.intel.strategy.*;

import chess.intel.training.StandardInputStrategy;
import chess.intel.util.DataMath;

public class Test {
    public static void main(String[] args) {
        NeuralNetwork nn = new NeuralNetwork(new StandardInputStrategy());

        System.out.println(nn.evaluate(new ChessPosition()).evalString());

    }
}
