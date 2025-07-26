package chess.intel.training;

import chess.logic.ChessPosition;
import chess.logic.CompressedPosition;
import chess.intel.util.Vector;

public class StandardInputStrategy implements InputStrategy {

    private final int INPUT_SIZE = 6 * 128 + 5;
    
    @Override public Vector convertToInput(ChessPosition position) {

        long[][] piecePlacements = position.getCompressedPosition().getPiecePlacements();
        Vector input = new Vector(INPUT_SIZE); // 773-dimensional input vector

        for (int p = CompressedPosition.PAWN; p <= CompressedPosition.KING; p++) {
            for (int c = CompressedPosition.WHITE; c <= CompressedPosition.BLACK; c++) {
                int startIndex = (p << 7) + (c << 6);
                for (int i = 0; i < 64; i++) {
                    if (((piecePlacements[p][c] >> i) & 1) != 0) {
                        input.set(startIndex + i, 1);
                    }
                }
            }
        }
        for (int i = 1; i < 6; i++) {
            input.set(input.dim() - 6 + i, position.getStateArray()[i]);
        }

        return input;
    }

    @Override public int getInputSize() {
        return INPUT_SIZE;
    }
}
