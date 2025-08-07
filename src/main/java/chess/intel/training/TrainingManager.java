package chess.intel.training;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

import chess.intel.util.DataMath;
import chess.intel.util.Matrix;
import chess.intel.util.Vector;

import chess.intel.Agent;
import chess.intel.strategy.NeuralNetwork;

public class TrainingManager {

    // Number of Trainees per Generation
    private static final int BATCH_SIZE = 200;

    // Default NeuralNetwork specifications
    private static final int[] NETWORK_SHAPE = {new StandardInputStrategy().getInputSize(), 300, 300, 200, 1};
    private static final int WEIGHT_RANDOMIZATION_MIN = -10;
    private static final int WEIGHT_RANDOMIZATION_MAX = 10;

    private static final String GEN_DIRECTORY_PATH = "output/training/gen_stream_3/";
    public static final String GEN_METADATA_FILENAME = "gen";
    public static final String TRAINEE_FILENAME = "roster/t";
    private static final int THINKING_DEPTH = 2;
    private static final int MAX_MOVES = 40;
    private static final int NUM_THREADS = 10;

    private static int currentGeneration;
    private static int gamesFinished;

    /**
     * Trains a specified number of generations starting from the Generation found by parsing the tempfile.
     * When the training is complete, this method writes the final Generation to that same tempfile.
     * @param numGenerations The number of Generations to train.
     */
    public static void startTraining(int numGenerations, boolean fromBeginning) {
        File genFile = new File(GEN_DIRECTORY_PATH + GEN_METADATA_FILENAME);

        Runnable r = () -> {
            try {
                Generation gen;
                if (fromBeginning || !genFile.exists()) {
                    gen = new Generation(BATCH_SIZE, NETWORK_SHAPE, WEIGHT_RANDOMIZATION_MIN, WEIGHT_RANDOMIZATION_MAX);
                } else {
                    System.out.println("Getting generation from JSON");
                    gen = Generation.getFromJsonDirectory(GEN_DIRECTORY_PATH);
                }

                int startingGenNumber = gen.getGenerationNumber();

                for (int genNumber = startingGenNumber; genNumber < startingGenNumber + numGenerations; genNumber++) {
                    currentGeneration = genNumber;
                    gen = train(gen);
                    System.out.printf("Finished training generation %d.\n", genNumber);
                    try {
                        gen.sort();
                        gen.write(GEN_DIRECTORY_PATH);
                    } catch (java.io.IOException ex) {
                        System.out.println(ex.getMessage());
                        //System.out.println("Error: Failed to write to JSON file.");
                    }
                }

            } catch (java.io.IOException ex) {
                System.out.println("Error: Failed to read from JSON file.");
            }
        };

        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();

        try {
            t.join();
        } catch (InterruptedException ex) {
            System.out.println("Thread interrupted.");
        }
    }

    /**
     * Returns a new Generation created by training the previous Generation
     * @param gen The previous Generation to train
     * @return The next improved Generation
     */
    private static Generation train(Generation gen) {
        ArrayList<Trainee> roster = gen.getRoster();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        //ArrayList<TrainingGame> games = matchStrategy1(roster);
        ArrayList<TrainingGame> games = matchStrategy2(roster);

        int totalGames = games.size();
        System.out.printf("Training generation %d with %d threads.\n", gen.getGenerationNumber(), NUM_THREADS);
        System.out.printf("Total games scheduled: %d\n", totalGames);
        gamesFinished = 0;

        for (int i = 0; i < NUM_THREADS; i++) {
            final int threadNumber = i;
            Thread t = new Thread(() -> simulateGames(games, NUM_THREADS, threadNumber));
            t.setDaemon(true);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.println("Thread interrupted.");
            }
        }

        return breedStrategy1(gen);
    }

    /**
     * Returns a list of games from a specified roster in which each Trainee plays each other Trainee
     * exactly once.
     * @param roster
     * @return The list of scheduled games
     */
    private static ArrayList<TrainingGame> matchStrategy1(ArrayList<Trainee> roster) {
        ArrayList<TrainingGame> games = new ArrayList<TrainingGame>();

        for (Trainee t1 : roster) {
            for (Trainee t2 : roster) {
                if (t1 != t2) {
                    games.add(new TrainingGame(t1, t2, MAX_MOVES));
                }
            }
        }

        return games;
    }

    /**
     * Pairs each Trainee against the same materialistic Trainee, which uses an empty NeuralNetwork
     * @param roster
     * @return
     */
    private static ArrayList<TrainingGame> matchStrategy2(ArrayList<Trainee> roster) {
        ArrayList<TrainingGame> games = new ArrayList<TrainingGame>();

        for (Trainee t : roster) {
            games.add(new TrainingGame(t, Trainee.EMPTY, MAX_MOVES));
        }

        return games;
    }

    /**
     * Randomly selects a subset of numGames games from a list of TrainingGames and returns the resulting list.
     * If numGames is greater than the size of the list, a copy of the list is returned.
     * @param games
     * @param numGames
     * @return A new list created by a random subset of games from the specified list.
     */
    private static ArrayList<TrainingGame> subset(ArrayList<TrainingGame> games, int numGames) {
        ArrayList<TrainingGame> subset = new ArrayList<TrainingGame>();

        for (TrainingGame g : games) {
            subset.add(g);
        }

        for (int i = games.size(); i > numGames; i--) {
            subset.remove(DataMath.random(0, i - 1));
        }

        return subset;
    }

    /**
     * Starts a set of games, allowing for multiple threads of excution.
     * @param games
     * @param totalThreads
     * @param threadNumber
     */
    private static void simulateGames(ArrayList<TrainingGame> games, int totalThreads, int threadNumber) {
        int totalGames = games.size();
        for (int g = threadNumber; g < totalGames; g += totalThreads) {
            games.get(g).start();
            System.out.printf("====GEN %d==== Finished training game. %.2f%% complete.\n", 
                currentGeneration, 100.0 * (++gamesFinished) / totalGames);
        }
    }

    /**
     * Determines the next Generation based on the best-performing Trainees from a specified Generation.
     * This method should introduce some random noise/mutations to encourage new strategies. All Trainees
     * are expected to use NeuralNetworks of the same shape.
     * @param prevGen The previous Generation.
     * @return The next Generation, breeded to hopefully be better at chess than the previous.
     */
    private static Generation breedStrategy1(Generation prevGen) {

        int generationNumber = prevGen.getGenerationNumber();

        ArrayList<Trainee> roster = prevGen.getRoster();
        int genSize = prevGen.getSize();
        
        prevGen.sort();
        int numLayers = roster.get(0).getStrategy().getNumLayers();
        int[] shape = roster.get(0).getStrategy().getShape();

        // print fitnesses for debugging
        for (Trainee t : roster) {
            System.out.println("DEBUG (fitness): " + t.getFitness());
        }

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
        float maxFitness = roster.get(0).getFitness();
        for (Trainee t : roster) {
            Matrix[] weightStep = new Matrix[numLayers];
            Vector[] activationWeightStep = new Vector[numLayers - 1];

            Matrix[] newWeights = new Matrix[numLayers];
            Vector[] newActivationWeights = new Vector[numLayers - 1];

            for (int layer = 0; layer < numLayers; layer++) {
                weightStep[layer] = DataMath.scalarMultiply(weightImportance[layer], (maxFitness - t.getFitness()) * DataMath.sigma(-1 * t.getFitness()));
                weightStep[layer].randomize();
                newWeights[layer] = DataMath.matrixAdd(t.getStrategy().getWeights()[layer], weightStep[layer]);
            }

            for (int layer = 0; layer < numLayers - 1; layer++) {
                activationWeightStep[layer] = DataMath.scalarMultiply(activationWeightImportance[layer], (maxFitness - t.getFitness()) * DataMath.sigma(-1 * t.getFitness()));
                activationWeightStep[layer].randomize();
                newActivationWeights[layer] = DataMath.vectorAdd(t.getStrategy().getActivationWeights()[layer], activationWeightStep[layer]);
            }

            newRoster.add(new Trainee(new NeuralNetwork(newWeights, newActivationWeights, new StandardInputStrategy()), THINKING_DEPTH));
        }
        return new Generation(newRoster, generationNumber + 1);
    }

    /**
     * Determines the next Generation based on the best-performing Trainees from a specified Generation.
     * This method should introduce some random noise/mutations to encourage new strategies. All Trainees
     * are expected to use NeuralNetworks of the same shape.
     * 
     * This strategy is simpler than breedStrategy1, simply introducing purely random noise in varying amounts
     * for each Trainee. The fittest Trainee from the previous Generation will remain unchanged this Generation.
     * 
     * @param prevGen The previous Generation.
     * @return The next Generation, breeded to hopefully be better at chess than the previous.
     */
    private static Generation breedStrategy2(Generation prevGen) {

        System.out.printf("Creating the next generation...\n");

        int generationNumber = prevGen.getGenerationNumber();
        int genSize = prevGen.getSize();

        prevGen.sort();
        ArrayList<Trainee> roster = prevGen.getRoster();
        ArrayList<Trainee> newRoster = new ArrayList<Trainee>();

        int numLayers = roster.get(0).getStrategy().getNumLayers();
        int[] shape = roster.get(0).getStrategy().getShape();

        // print fitnesses for debugging
        for (Trainee t : roster) {
            System.out.println("DEBUG (fitness): " + t.getFitness());
        }

        final float MAX_STEP = 10f;
        float step = 0;

        for (Trainee t : roster) {

            Matrix[] weights = new Matrix[numLayers];
            Vector[] activationWeights = new Vector[numLayers - 1];

            // weights
            for (int layer = 0; layer < numLayers; layer++) {
                weights[layer] = new Matrix(shape[layer + 1], shape[layer], -step, step);
                weights[layer].add(t.getStrategy().getWeights()[layer]);
            }

            // activation weights
            for (int layer = 0; layer < numLayers - 1; layer++) {
                activationWeights[layer] = new Vector(shape[layer + 1], -step, step);
                activationWeights[layer].add(t.getStrategy().getActivationWeights()[layer]);
            }

            newRoster.add(new Trainee(new NeuralNetwork(weights, activationWeights, new StandardInputStrategy()), THINKING_DEPTH));
            
            step += MAX_STEP / (genSize - 1);
        }

        return new Generation(newRoster, generationNumber + 1);
    }

    public static Agent getBestAgent(int depth) {
        try {
            Generation bestGen = Generation.getFromJsonDirectory(GEN_DIRECTORY_PATH);
            return new Agent(bestGen.getRoster().get(0).getStrategy(), depth, true);
        } catch (java.io.IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
}
