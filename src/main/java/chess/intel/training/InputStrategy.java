package chess.intel.training;

import chess.logic.ChessPosition;
import chess.intel.util.Vector;

/**
 * Interface that can convert a ChessPosition into a series of inputs that can be provided to a NeuralNetwork.
 */
public interface InputStrategy {
    /**
     * Converts a ChessPosition into a Vector of useful data related to the position. The data will be
     * used by a NeuralNetwork to evaluate the position.
     * @param position The position to evaluate.
     * @return The determined Vector of inputs.
     */
    public Vector convertToInput(ChessPosition position);

    /**
     * Returns the dimensionality of the Vector typically returned by convertToInput.
     * @return The size of the input Vectors produced by this InputStrategy.
     */
    public int getInputSize();
}
