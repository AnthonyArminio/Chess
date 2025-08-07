package chess.intel.training;

import chess.intel.Agent;
import chess.intel.strategy.NeuralNetwork;

import com.google.gson.annotations.Expose;

public class Trainee extends Agent implements Comparable<Trainee> {

    public static final Trainee EMPTY = new Trainee(new NeuralNetwork(new StandardInputStrategy()), 2);

    @Expose private final float WIN_REWARD = 10;
    @Expose private final float LOSS_PUNISHMENT = 10;

    @Expose private float fitness;

    public Trainee(NeuralNetwork nn, int depth) {
        super(nn, depth, false);
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
     * Rewards the Trainee for good performance in a game.
     * @param value The value to increase this Trainee's fitness by.
     */
    public void reward(float value) {
        this.fitness += value;
    }

    /**
     * Punishes the Trainee for losing a game.
     */
    public void punish() {
        this.fitness -= LOSS_PUNISHMENT;
    }

    /**
     * Punishes the Trainee for bad performance in a game.
     * @param value The value to decrease this Trainee's fitness by.
     */
    public void punish(float value) {
        this.fitness -= value;
    }

    public float getFitness() {
        return this.fitness;
    }

    @Override public NeuralNetwork getStrategy() {
        return (NeuralNetwork) super.getStrategy();
    }
}
