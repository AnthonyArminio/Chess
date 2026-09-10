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
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
        { // knight
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
        { // bishop
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
        { // rook
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
        { // queen
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
        { // king
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0,
        },
    };

    // A reference to the weights array used for this instance.
    private final float[][] weights;

    public PositionalWeights() {
        this.weights = copyWeights(DEFAULT_WEIGHTS);
    }

    public PositionalWeights(float[][] weights) {
        this.weights = copyWeights(weights);
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
    private float[][] copyWeights(float[][] weights) {
        if (weights.length != 6) {
            throw new IllegalArgumentException("Input weights have an invalid shape.");
        }

        float[][] weightsCopy = new float[12][64];
        for (int id = 0; id < 6; id++) {
            if (weights[id].length != 64) {
                throw new IllegalArgumentException("Input weights have an invalid shape.");
            }
            for (int s = 0; s < 64; s++) {
                weightsCopy[id][s] = weights[id][s];
            }
        }
        for (int id = 6; id < 12; id++) {
            for (int s = 0; s < 64; s++) {
                weightsCopy[id][s] = weights[id-6][GridMath.getReflection(s)];
            }
        }

        return weightsCopy;
    }
}
