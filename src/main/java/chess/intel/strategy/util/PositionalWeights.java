package chess.intel.strategy.util;

import chess.logic.ChessPiece;
import chess.logic.util.GridMath;

/**
 * Represents a collection of weights assigned to each square for each piece. Positive values indicate beneficial placements, while negative
 * values indicate harmful placements.
 */
public class PositionalWeights {
    private static final float[][] DEFAULT_WEIGHTS = {
        { // pawn
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, -0.1f, 0.30f, 0.30f, -0.1f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.50f, 0.60f, 0.60f, 0.50f, 0.00f, 0.00f,
            0.50f, 0.60f, 0.60f, 0.70f, 0.70f, 0.60f, 0.60f, 0.50f,
            0.80f, 0.80f, 0.80f, 0.90f, 0.90f, 0.80f, 0.80f, 0.80f,
            0.90f, 0.90f, 0.95f, 0.95f, 0.95f, 0.95f, 0.90f, 0.90f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
        },
        { // knight
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
        },
        { // bishop
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.50f, 0.00f, 0.00f, 0.00f, 0.00f, 0.50f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
        },
        { // rook
            0.00f, 0.00f, 0.30f, 0.50f, 0.50f, 0.30f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.20f, 0.40f, 0.40f, 0.20f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.70f, 0.70f, 0.70f, 0.70f, 0.70f, 0.70f, 0.70f, 0.70f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
        },
        { // queen
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.00f,
        },
        { // king
            0.50f, 0.50f, 0.40f, 0.20f, 0.30f, 0.30f, 0.50f, 0.50f,
            0.00f, 0.00f, 0.00f, 0.00f, 0.00f, 0.10f, 0.10f, 0.10f,
            0.00f, -0.3f, -0.5f, -0.8f, -0.8f, -0.5f, -0.3f, 0.00f,
            -0.6f, -0.7f, -0.9f, -1.0f, -1.0f, -0.9f, -0.7f, -0.6f,
            -0.7f, -0.8f, -0.9f, -1.0f, -1.0f, -0.9f, -0.8f, -0.7f,
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
        },
    };

    // A reference to the weights array used for this instance.
    private final float[][] weights;

    public PositionalWeights() {
        this.weights = copyWeights(DEFAULT_WEIGHTS, 1.0f);
    }

    public PositionalWeights(float multiplier) {
        this.weights = copyWeights(DEFAULT_WEIGHTS, multiplier);
    }

    public PositionalWeights(float[][] weights) {
        this.weights = copyWeights(weights, 1.0f);
    }

    public PositionalWeights(float[][] weights, float multiplier) {
        this.weights = copyWeights(weights, multiplier);
    }

    public float[][] getWeights() {
        return this.weights;
    }

    public float[] getWeightMap(ChessPiece piece) {
        int id = piece.getID();
        if (id < 0) {
            return this.weights[5-id];
        }
        return this.weights[id-1];
    }

    /**
     * Creates weights and reflected weights for the Black pieces.
     * @param weights input weights
     * @return a copy of the input weights, including the reflected weights.
     * @throws IllegalArgumentException if the input weights have the wrong shape (should be [6][64])
     */
    private float[][] copyWeights(float[][] weights, float multiplier) {
        if (weights.length != 6) {
            throw new IllegalArgumentException("Input weights have an invalid shape.");
        }

        float[][] weightsCopy = new float[12][64];
        for (int id = 0; id < 6; id++) {
            if (weights[id].length != 64) {
                throw new IllegalArgumentException("Input weights have an invalid shape.");
            }
            for (int s = 0; s < 64; s++) {
                weightsCopy[id][s] = weights[id][s] * multiplier;
            }
        }
        for (int id = 6; id < 12; id++) {
            for (int s = 0; s < 64; s++) {
                // use reflected, negative values for black pieces
                weightsCopy[id][s] = -weightsCopy[id-6][GridMath.getReflection(s)];
            }
        }

        return weightsCopy;
    }
}
