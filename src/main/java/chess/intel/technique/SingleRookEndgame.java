package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class SingleRookEndgame extends Technique {

    public static ChessPosition samplePosition() {
        int[] positionArray = {6, 0, 0, 0, 0, 0, 0, 0,
                               0, 4, 0, 0, 0, 0, 0, 0,
                               0, 0,-6, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0};

        int[] stateArray = {-1, 0, 0, 0, 0, 0};

        return new ChessPosition(positionArray, stateArray);
    }
    
    public SingleRookEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'R') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        int rookSquare = ChessLogic.findMatchingPiece(position, 'R');
        if (rookSquare < 0) {
            return Evaluation.DRAW;
        }
        char rookColor = position.getPieceAt(rookSquare).getColor();

        if (rookColor == position.colorToMove()) {
            return Evaluation.REJECTED;
        }

        int kingSquare = ChessLogic.findMatchingPiece(position, 'K', rookColor);
        int enemyKingSquare = ChessLogic.findMatchingPiece(position, 'K', ChessLogic.opponentOf(rookColor));

        if (!GridMath.isAdjacent(kingSquare, rookSquare)) {
            return Evaluation.INCONCLUSIVE;
        }

        int rookFile = GridMath.getFile(rookSquare);
        int rookRank = GridMath.getRank(rookSquare);
        int kingFile = GridMath.getFile(kingSquare);
        int kingRank = GridMath.getRank(kingSquare);
        int enemyKingFile = GridMath.getFile(enemyKingSquare);
        int enemyKingRank = GridMath.getRank(enemyKingSquare);

        if (sameLine(rookFile, rookRank, enemyKingFile, enemyKingRank)) {
            return Evaluation.INCONCLUSIVE;
        }

        String quadrant = quadrantOf(enemyKingFile, enemyKingRank, rookFile, rookRank);

        if (isInQuadrant(kingFile, kingRank, rookFile, rookRank, quadrant)) {
            return Evaluation.INCONCLUSIVE;
        }

        int borderFile = keyFile(quadrant, 1, 8);
        int borderRank = keyRank(quadrant, 1, 8);

        int boxSize = Math.abs(rookRank - borderRank) + Math.abs(rookFile - borderFile);
        int clock = 8 * (boxSize - 3) + 10;
        if (boxSize == 3) {
            int goalFile = Math.abs(rookFile - borderFile) == 1 ? keyFile(quadrant, 3, 6) : keyFile(quadrant, 2, 7);
            int goalRank = Math.abs(rookRank - borderRank) == 1 ? keyRank(quadrant, 3, 6) : keyRank(quadrant, 2, 7);
            clock = 6 + timeTo(kingFile, kingRank, goalFile, goalRank);
        }

        return Evaluation.mateIn(clock, rookColor);
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

    private static boolean sameLine(int rookFile, int rookRank, int kingFile, int kingRank) {
        return rookFile - kingFile == 0 || rookRank - kingRank == 0;
    }
}
