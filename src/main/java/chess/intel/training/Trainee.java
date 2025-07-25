package chess.intel.training;

import chess.intel.Agent;
import chess.intel.strategy.NeuralNetwork;

public class Trainee extends Agent implements Comparable<Trainee> {

    private final int[] DEFAULT_NEURAL_NETWORK_SHAPE = {};

    private final float WIN_REWARD = 10;
    private final float LOSS_PUNISHMENT = -10;

    private float fitness;

    /**
     * Creates a default Trainee with a random Matrix using the default NeuralNetwork specifications.
     */
    public Trainee() {

    }

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

    /**
     * Rewards the Trainee for winning a game.
     */
    public void reward() {
        this.fitness += WIN_REWARD;
    }

    /**
     * Punishes the Trainee for losing a game.
     */
    public void punish() {
        this.fitness += LOSS_PUNISHMENT;
    }
}
