package chess.intel.strategy;

import chess.logic.ChessPosition;

import chess.intel.training.InputStrategy;
import chess.intel.training.StandardInputStrategy;
import chess.intel.util.DataMath;
import chess.intel.util.Vector;
import chess.intel.util.Matrix;

import com.google.gson.annotations.Expose;

public class NeuralNetwork extends Strategy {

    // Number of neuron layers, not including the output layer.
    @Expose private int numLayers;
    @Expose private int[] shape;

    @Expose private Matrix[] weights;
    @Expose private Vector[] activationWeights;

    @Expose private InputStrategy inputStrategy;

    /**
     * Creates an empty NeuralNetwork (all weights are 0)
     */
    public NeuralNetwork(InputStrategy is) {
        this.numLayers = 1;
        this.shape = new int[2];
        this.shape[0] = is.getInputSize();
        this.shape[this.numLayers] = 1;

        this.weights = new Matrix[1];
        this.weights[0] = new Matrix(this.shape[1], this.shape[0]);
        this.activationWeights = null;
    }

    /**
     * Creates a randomized Neural Network with the specified shape. The first element of shape
     * will be overridden by the size of the specified InputStrategy.
     */
    public NeuralNetwork(int[] shape, float min, float max, InputStrategy is) {
        this.numLayers = shape.length - 1;
        this.shape = DataMath.copy(shape);
        this.shape[0] = is.getInputSize();
        this.shape[this.numLayers] = 1;

        this.weights = new Matrix[this.numLayers];
        this.activationWeights = new Vector[this.numLayers - 1];
        this.inputStrategy = is;

        for (int layer = 0; layer < this.numLayers; layer++) {
            this.weights[layer] = new Matrix(shape[layer + 1], shape[layer], min, max);
        }

        for (int layer = 0; layer < this.numLayers - 1; layer++) {
            this.activationWeights[layer] = new Vector(shape[layer + 1], min, max);
        }
    }

    public NeuralNetwork(Matrix[] weights, Vector[] activationWeights, InputStrategy is) {
        this.numLayers = weights.length;
        this.shape = new int[this.numLayers + 1];
        this.shape[0] = is.getInputSize();
        this.shape[this.numLayers] = 1;

        this.weights = new Matrix[this.numLayers];
        this.activationWeights = new Vector[this.numLayers - 1];

        for (int layer = 0; layer < this.numLayers; layer++) {
            this.weights[layer] = weights[layer].copy();
        }

        for (int layer = 0; layer < this.numLayers - 1; layer++) {
            this.activationWeights[layer] = activationWeights[layer].copy();
            this.shape[layer + 1] = this.activationWeights[layer].dim();
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
        // Introduce hybrid strategy by adding the position's material evaluation.
        return new Evaluation(currentLayer.get(0) + 10 * position.getMaterialEvaluation());
    }

    public int getNumLayers() {
        return this.numLayers;
    }

    public int[] getShape() {
        return this.shape;
    }

    public Matrix[] getWeights() {
        return this.weights;
    }

    public Vector[] getActivationWeights() {
        return this.activationWeights;
    }
}
