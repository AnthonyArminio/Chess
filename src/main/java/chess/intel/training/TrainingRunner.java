package chess.intel.training;

/**
 * Entry point for training the AI.
 */
public class TrainingRunner {
    public static void main(String[] args) {
        //TrainingManager.purge(); DO NOT USE THIS UNLESS NECESSARY
        TrainingManager.startTraining(10, true);
    }
}
