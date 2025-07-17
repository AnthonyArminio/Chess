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

    private InputStrategy inputStrategy;

    public NeuralNetwork(Matrix[] weights, InputStrategy is) {
        this.numLayers = weights.length;
        this.weights = new Matrix[numLayers];
        for (int layer = 0; layer < numLayers; layer++) {
            this.weights[layer] = weights[layer].copy();
        }
        this.inputStrategy = is;
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        Vector inputLayer = this.inputStrategy.convertToInput(position);
        for (int layer = 0; layer < this.numLayers; layer++) {
            inputLayer = DataMath.matrixMultiply(weights[layer], inputLayer);
            DataMath.sigma(inputLayer);
        }
        return new Evaluation(inputLayer.get(0));
    }
}
