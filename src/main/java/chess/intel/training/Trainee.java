package chess.intel.training;

import chess.intel.Agent;
import chess.intel.strategy.NeuralNetwork;

public class Trainee extends Agent implements Comparable<Trainee> {

    private float fitness;

    public Trainee(char color, NeuralNetwork nn, int depth) {
        super(color, nn, depth);
        this.fitness = 0;
    }

    public int compareTo(Trainee other) {
        if (this.fitness > other.fitness) {
            return 1;
        } else if (this.fitness < other.fitness) {
            return -1;
        } else {
            return 0;
        }
    }
}
