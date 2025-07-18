package chess.intel.strategy;

import chess.logic.ChessPosition;

import chess.intel.training.InputStrategy;

import chess.intel.util.DataMath;
import chess.intel.util.Vector;
import chess.intel.util.Matrix;

public class NeuralNetwork extends Strategy {

    // Number of neuron layers, not including the output layer.
    private int numLayers;

    private Matrix[] weights;
    private Vector[] activationWeights;

    private InputStrategy inputStrategy;

    public NeuralNetwork(Matrix[] weights, Vector[] activationWeights, InputStrategy is) {
        this.numLayers = weights.length;
        this.weights = new Matrix[numLayers];
        this.activationWeights = new Vector[numLayers];
        for (int layer = 0; layer < numLayers; layer++) {
            this.weights[layer] = weights[layer].copy();
            this.activationWeights[layer] = activationWeights[layer].copy();
        }
        this.inputStrategy = is;
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        Vector inputLayer = this.inputStrategy.convertToInput(position);
        for (int layer = 0; layer < this.numLayers; layer++) {
            inputLayer = DataMath.vectorAdd(DataMath.matrixMultiply(weights[layer], inputLayer), this.activationWeights[layer]);
            DataMath.sigma(inputLayer);
        }
        return new Evaluation(inputLayer.get(0));
    }
}
