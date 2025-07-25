package chess.intel.training;

import java.util.ArrayList;
import java.io.File;

public class TrainingManager {

    private static final String TEMPFILE_PATH = "file:output/training/gen";
    private static final int NUM_ROUNDS = 100;
    private static final int MAX_MOVES = 100;

    /**
     * Trains a specified number of generations starting from the Generation found by parsing the tempfile.
     * When the training is complete, this method writes the final Generation to that same tempfile.
     * @param numGenerations The number of Generations to train.
     */
    public static void startTraining(int numGenerations) {
        File genFile = new File(TEMPFILE_PATH);

        Generation gen;
        if (genFile.exists()) {
            gen = new Generation(genFile);
        } else {
            gen = new Generation();
        }

        int startingGenNumber = gen.getGenerationNumber();

        for (int genNumber = startingGenNumber; genNumber < startingGenNumber + numGenerations; genNumber++) {
            gen = train(gen);
        }

        gen.write(genFile);
    }

    /**
     * Returns a new Generation created by training the previous Generation
     * @param gen The previous Generation to train
     * @return The next improved Generation
     */
    public static Generation train(Generation gen) {
        ArrayList<Trainee> roster = gen.getRoster();

        for (Trainee t1 : roster) {
            for (Trainee t2 : roster) {
                if (t1 != t2) {
                    for (int round = 0; round < NUM_ROUNDS; round++) {
                        match(t1, t2);
                        match(t2, t1);
                    }
                }
            }
        }

        return breed(gen);
    }

    /**
     * Creates a new TrainingGame between two Trainees. The result of the game is used to update the
     * fitness value of each Trainee.
     */
    public static TrainingGame match(Trainee white, Trainee black) {
        return new TrainingGame(white, black);
    }
}
