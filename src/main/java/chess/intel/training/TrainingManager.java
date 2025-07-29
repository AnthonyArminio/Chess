package chess.intel.training;

import java.util.ArrayList;
import java.io.File;

import chess.intel.util.DataMath;
import chess.intel.util.Matrix;
import chess.intel.util.Vector;

import chess.intel.Agent;
import chess.intel.strategy.NeuralNetwork;

public class TrainingManager {

    // Number of Trainees per Generation
    private static final int BATCH_SIZE = 8; //80;

    private static final String TEMPFILE_PATH = "output/training/gen";
    private static final int THINKING_DEPTH = 2;
    private static final int NUM_ROUNDS = 5; //100;
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
                    System.out.println("Getting generation from JSON");
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
                    gen.sort();
                    gen.write(genFile);
                } catch (java.io.IOException ex) {
                    System.out.println(ex.getMessage());
                    //System.out.println("Error: Failed to write to JSON file.");
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
        ArrayList<Thread> threads = new ArrayList<Thread>();

        for (Trainee t1 : roster) {
            for (Trainee t2 : roster) {
                if (t1 != t2) {
                    Runnable r = () -> {
                        for (int round = 0; round < NUM_ROUNDS; round++) {
                            match(t1, t2);
                        }
                    };
                    Thread t = new Thread(r);
                    threads.add(t);
                    t.setDaemon(true);
                    t.start();
                }
            }
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted.");
            }
        }

        return breed(gen);
    }

    /**
     * Creates a new TrainingGame between two Trainees. The result of the game is used to update the
     * fitness value of each Trainee.
     */
    private static TrainingGame match(Trainee white, Trainee black) {
        TrainingGame newGame = new TrainingGame(white, black);
        newGame.start();
        return newGame;
    }

    /**
     * Determines the next Generation based on the best-performing Trainees from a specified Generation.
     * This method should introduce some random noise/mutations to encourage new strategies. All Trainees
     * are expected to use NeuralNetworks of the same shape.
     * @param prevGen The previous Generation.
     * @return The next Generation, breeded to hopefully be better at chess than the previous.
     */
    private static Generation breed(Generation prevGen) {

        int generationNumber = prevGen.getGenerationNumber();

        ArrayList<Trainee> roster = prevGen.getRoster();
        int genSize = prevGen.getSize();
        
        int numLayers = roster.get(0).getStrategy().getNumLayers();
        int[] shape = roster.get(0).getStrategy().getShape();

        int totalWeights = 0;
        for (int layer = 0; layer < numLayers; layer++) { // weights
            totalWeights += shape[layer] * shape[layer + 1];
        }
        for (int layer = 0; layer < numLayers - 1; layer++) { // activation weights
            totalWeights += shape[layer + 1];
        }
        int weightsAnalyzed = 0;
        System.out.printf("Breeding generation %d. Total weights to analyze: %d\n", generationNumber, totalWeights);

        // Find linear regression matrix (((XTX)^-1)XT)
        Vector corner = Vector.corner(genSize); // All 1s Vector
        float f[][] = new float[1][genSize]; // Fitness Vector
        for (int t = 0; t < genSize; t++) {
            f[0][t] = roster.get(t).getFitness();
        }
        Vector fitness = new Vector(f);
        Matrix X = new Matrix(corner, fitness);
        Matrix XT = DataMath.matrixTranspose(X);
        Matrix linearRegressionMatrix = DataMath.matrixMultiply(DataMath.matrixInverse2D(DataMath.matrixMultiply(XT, X)), XT);

        Matrix[] weightImportance = new Matrix[numLayers];
        Vector[] activationWeightImportance = new Vector[numLayers - 1];

        // Matrix weight importance
        System.out.printf("Analyzing matrix weights...\n");
        for (int layer = 0; layer < numLayers; layer++) {

            int rows = shape[layer + 1];
            int cols = shape[layer];
            weightImportance[layer] = new Matrix(rows, cols);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    float[][] w = new float[1][genSize];
                    for (int t = 0; t < genSize; t++) {
                        w[0][t] = roster.get(t).getStrategy().getWeights()[layer].get(row, col);
                    }
                    Vector wVector = new Vector(w);

                    weightImportance[layer].set(row, col, DataMath.matrixMultiply(linearRegressionMatrix, wVector).get(1));
                    weightsAnalyzed++;
                }
                System.out.printf("Analyzing weights... (%.2f%%)\n", 100 * (float) weightsAnalyzed / totalWeights);
            }
        }

        // Activation weight importance
        System.out.printf("Analyzing activation weights...\n");
        for (int layer = 0; layer < numLayers - 1; layer++) {

            int dim = shape[layer + 1];
            activationWeightImportance[layer] = new Vector(dim);

            for (int i = 0; i < dim; i++) {
                float[][] w = new float[1][genSize];
                for (int t = 0; t < genSize; t++) {
                    w[0][t] = roster.get(t).getStrategy().getActivationWeights()[layer].get(i);
                }
                Vector wVector = new Vector(w);

                activationWeightImportance[layer].set(i, DataMath.matrixMultiply(linearRegressionMatrix, wVector).get(1));
                weightsAnalyzed++;
            }
            System.out.printf("Analyzing weights... (%.2f%%)\n", 100 * (float) weightsAnalyzed / totalWeights);
        }

        System.out.printf("All weights analyzed. Creating the next generation...\n");

        ArrayList<Trainee> newRoster = new ArrayList<Trainee>();
        // Find step Matrices for each Trainee and apply steps in random amounts.
        for (Trainee t : roster) {
            Matrix[] weightStep = new Matrix[numLayers];
            Vector[] activationWeightStep = new Vector[numLayers - 1];

            Matrix[] newWeights = new Matrix[numLayers];
            Vector[] newActivationWeights = new Vector[numLayers - 1];

            for (int layer = 0; layer < numLayers; layer++) {
                weightStep[layer] = weightImportance[layer].scalarMultiply(1f / DataMath.sigma(t.getFitness()));
                weightStep[layer].randomize();
                newWeights[layer] = DataMath.matrixAdd(t.getStrategy().getWeights()[layer], weightStep[layer]);
            }

            for (int layer = 0; layer < numLayers - 1; layer++) {
                activationWeightStep[layer] = activationWeightImportance[layer].scalarMultiply(1f / DataMath.sigma(t.getFitness()));
                activationWeightStep[layer].randomize();
                newActivationWeights[layer] = DataMath.vectorAdd(t.getStrategy().getActivationWeights()[layer], activationWeightStep[layer]);
            }

            newRoster.add(new Trainee(new NeuralNetwork(newWeights, newActivationWeights, new StandardInputStrategy()), THINKING_DEPTH));
        }
        return new Generation(newRoster, generationNumber + 1);
    }

    public static Agent getBestAgent(int depth) {
        try {
            Generation bestGen = Generation.getFromJson(new File(TEMPFILE_PATH));
            return new Agent(bestGen.getRoster().get(0).getStrategy(), depth);
        } catch (java.io.IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    /**
     * Deletes the tempfile holding the latest Generation JSON.
     */
    private static void purge() {
        
    }
}
