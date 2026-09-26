package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;

public class SingleRookEndgame extends Technique {
    
    public SingleRookEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'R') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {


        return Evaluation.UNDECIDED;
    }

    /**
     * Returns the quadrant of a king's square relative to the queen.
     * Quandrants are: "top-right", "top-left", "bottom-left", and "bottom-right"
     */
    private static String quadrantOf(int kingFile, int kingRank, int rookFile, int rookRank) {

        if (kingFile < rookFile) {
            if (kingRank < rookRank) {
                return "bottom-left";
            } else {
                return "top-left";
            }
        } else {
            if (kingRank < rookRank) {
                return "bottom-right";
            } else {
                return "top-right";
            }
        }
    }

    /**
     * Returns true if the specified square is in (or bordering) a given quadrant.
     */
    private static boolean isInQuadrant(int squareFile, int squareRank, int rookFile, int rookRank, String quadrant) {

        if ("bottom-left".equals(quadrant)) {
            return squareRank <= rookRank && squareFile <= rookFile;
        } else if ("top-left".equals(quadrant)) {
            return squareRank >= rookRank && squareFile <= rookFile;
        } else if ("bottom-right".equals(quadrant)) {
            return squareRank <= rookRank && squareFile >= rookFile;
        } else if ("top-right".equals(quadrant)) {
            return squareRank >= rookRank && squareFile >= rookFile;
        }
        return false;
    }

    private static int keyFile(String quadrant, int low, int high) {
        if ("bottom-left".equals(quadrant) || "top-left".equals(quadrant)) {
            return low;
        } else if ("bottom-right".equals(quadrant) || "top-right".equals(quadrant)) {
            return high;
        } else {
            throw new IllegalArgumentException("Error: invalid quadrant alias.");
        }
    }

    private static int keyRank(String quadrant, int low, int high) {
        if ("bottom-left".equals(quadrant) || "bottom-right".equals(quadrant)) {
            return low;
        } else if ("top-left".equals(quadrant) || "top-right".equals(quadrant)) {
            return high;
        } else {
            throw new IllegalArgumentException("Error: invalid quadrant alias.");
        }
    }

    private static int timeTo(int kingFile, int kingRank, int destinationFile, int destinationRank) {
        return 2 * Math.max(Math.abs(destinationFile - kingFile), Math.abs(destinationRank - kingRank));
    }
}
