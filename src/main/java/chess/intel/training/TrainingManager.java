package chess.intel.training;

import java.util.ArrayList;
import java.io.File;

public class TrainingManager {

    // Number of Trainees per Generation
    private static final int BATCH_SIZE = 8; //80;

    private static final String TEMPFILE_PATH = "file:output/training/gen";
    private static final int NUM_ROUNDS = 1; //100;
    private static final int MAX_MOVES = 100;

    /**
     * Trains a specified number of generations starting from the Generation found by parsing the tempfile.
     * When the training is complete, this method writes the final Generation to that same tempfile.
     * @param numGenerations The number of Generations to train.
     */
    public static void startTraining(int numGenerations) {
        File genFile = new File(TEMPFILE_PATH);

        Runnable r = () -> {
            try {
                Generation gen;
                if (genFile.exists()) {
                    gen = Generation.getFromJson(genFile);
                } else {
                    gen = new Generation(BATCH_SIZE);
                }

                int startingGenNumber = gen.getGenerationNumber();

                for (int genNumber = startingGenNumber; genNumber < startingGenNumber + numGenerations; genNumber++) {
                    System.out.println("DEBUG: Training generation " + genNumber);
                    gen = train(gen);
                    System.out.println("DEBUG: Finished training generation " + genNumber);
                }

                // PROBLEM TO FIX: ALL GAMES HAPPEN ON A SEPARATE THREAD, SO THIS LINE IS REACHED BEFORE ALL
                // THE GAMES FINISH.

                try {
                    gen.write(genFile);
                } catch (java.io.IOException ex) {
                    System.out.println("Error: Failed to write to JSON file.");
                }

            } catch (java.io.IOException ex) {
                System.out.println("Error: Failed to read from JSON file.");
            }
        };

        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    /**
     * Returns a new Generation created by training the previous Generation
     * @param gen The previous Generation to train
     * @return The next improved Generation
     */
    private static Generation train(Generation gen) {
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
    private static TrainingGame match(Trainee white, Trainee black) {
        return new TrainingGame(white, black);
    }

    /**
     * Determines the next Generation based on the best-performing Trainees from a specified Generation.
     * This method should introduce some random noise/mutations to encourage new strategies.
     * @param prevGen The previous Generation.
     * @return The next Generation, breeded to hopefully be better at chess than the previous.
     */
    private static Generation breed(Generation prevGen) {
        return prevGen; // FOR TESTING
    }
}
