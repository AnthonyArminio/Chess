package chess.intel.training;

import java.util.ArrayList;

public class TrainingManager {

    private static final String TEMPFILE_PATH = "file:output/training/gen";
    private static final int NUM_ROUNDS = 100;

    /**
     * 
     * @param numGenerations
     */
    public static void startTraining(int numGenerations) {

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

        return gen.breed();
    }

    /**
     * Creates a new TrainingGame between two Trainees. The result of the game is used to update the
     * fitness value of each Trainee.
     */
    public static TrainingGame match(Trainee white, Trainee black) {
        return new TrainingGame(white, black);
    }
}
