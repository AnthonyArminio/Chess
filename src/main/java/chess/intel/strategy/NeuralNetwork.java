package chess.intel.strategy;

import chess.logic.ChessPosition;

import chess.intel.training.InputStrategy;
import chess.intel.training.StandardInputStrategy;
import chess.intel.util.DataMath;
import chess.intel.util.Vector;
import chess.intel.util.Matrix;

public class NeuralNetwork extends Strategy {

    private final InputStrategy DEFAULT_INPUT_STRATEGY = new StandardInputStrategy();
    private final int[] DEFAULT_SHAPE = {DEFAULT_INPUT_STRATEGY.getInputSize(), 300, 300, 200, 1};
    private final int DEFAULT_NUM_LAYERS = DEFAULT_SHAPE.length - 1;
    private final float WEIGHT_RANDOMIZATION_MIN = -10;
    private final float WEIGHT_RANDOMIZATION_MAX = 10;
    private final float ACTIVATION_WEIGHT_RANDOMIZATION_MIN = -10;
    private final float ACTIVATION_WEIGHT_RANDOMIZATION_MAX = 10;

    // Number of neuron layers, not including the output layer.
    private int numLayers;

    private Matrix[] weights;
    private Vector[] activationWeights;

    private InputStrategy inputStrategy;

    /**
     * Creates the default NeuralNetwork.
     */
    public NeuralNetwork() {
        this.numLayers = DEFAULT_NUM_LAYERS;

        this.weights = new Matrix[this.numLayers];
        this.activationWeights = new Vector[this.numLayers - 1];
        this.inputStrategy = DEFAULT_INPUT_STRATEGY;

        for (int layer = 0; layer < this.numLayers; layer++) {
            this.weights[layer] = new Matrix(DEFAULT_SHAPE[layer + 1], DEFAULT_SHAPE[layer], 
                WEIGHT_RANDOMIZATION_MIN, WEIGHT_RANDOMIZATION_MAX);
        }

        for (int layer = 0; layer < this.numLayers - 1; layer++) {
            this.activationWeights[layer] = new Vector(DEFAULT_SHAPE[layer + 1],
                ACTIVATION_WEIGHT_RANDOMIZATION_MIN, ACTIVATION_WEIGHT_RANDOMIZATION_MAX);
        }
    }

    public NeuralNetwork(Matrix[] weights, Vector[] activationWeights, InputStrategy is) {
        this.numLayers = weights.length;
        this.weights = new Matrix[this.numLayers];
        this.activationWeights = new Vector[this.numLayers - 1];

        for (int layer = 0; layer < this.numLayers; layer++) {
            this.weights[layer] = weights[layer].copy();
        }

        for (int layer = 0; layer < this.numLayers - 1; layer++) {
            this.activationWeights[layer] = activationWeights[layer].copy();
        }

        this.inputStrategy = is;
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        Vector currentLayer = this.inputStrategy.convertToInput(position);
        for (int layer = 0; layer < this.numLayers - 1; layer++) {
            currentLayer = DataMath.vectorAdd(DataMath.matrixMultiply(weights[layer], currentLayer), this.activationWeights[layer]);
            DataMath.sigma(currentLayer);
        }
        // Do not handle activation weights or sigma distribution for the last layer since that should
        // represent a concrete evaluation of the position.
        currentLayer = DataMath.matrixMultiply(weights[this.numLayers - 1], currentLayer);
        return new Evaluation(currentLayer.get(0));
    }
}
