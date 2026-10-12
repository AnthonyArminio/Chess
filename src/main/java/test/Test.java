package test;

import chess.intel.strategy.Evaluation;
import chess.intel.technique.SinglePawnEndgame;

public class Test {
    public static void main(String[] args) {
        Evaluation eval = new SinglePawnEndgame().evaluate(SinglePawnEndgame.samplePosition());
        System.out.printf("%s\n", eval.evalString());

    }
}
