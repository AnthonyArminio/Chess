package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class SingleQueenEndgame extends Technique {
    
    public SingleQueenEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'Q') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        int queenSquare = ChessLogic.findMatchingPiece(position, 'Q');
        char queenColor = position.getPieceAt(queenSquare).getColor();
        if (queenColor == position.colorToMove()) {
            return Evaluation.UNDECIDED;
        }

        int kingSquare = ChessLogic.findMatchingPiece(position, 'K', queenColor);
        int enemyKingSquare = ChessLogic.findMatchingPiece(position, 'K', ChessLogic.opponentOf(queenColor));

        if (GridMath.isAdjacent(queenSquare, enemyKingSquare)) {
            // In case the enemy king can capture the queen.
            return Evaluation.UNDECIDED;
        }

        int queenFile = GridMath.getFile(queenSquare);
        int queenRank = GridMath.getRank(queenSquare);
        int kingFile = GridMath.getFile(kingSquare);
        int kingRank = GridMath.getRank(kingSquare);
        int enemyKingFile = GridMath.getFile(enemyKingSquare);
        int enemyKingRank = GridMath.getRank(enemyKingSquare);

        String quadrant = quadrantOf(enemyKingFile, enemyKingRank, queenFile, queenRank);

        if (isInQuadrant(kingFile, kingRank, queenFile, queenRank, quadrant)) {
            // If the king is on the same side of the queen as the enemy king, the checkmate may not be possible.
            return Evaluation.UNDECIDED;
        }

        int borderFile = keyFile(quadrant, 1, 8);
        int borderRank = keyRank(quadrant, 1, 8);

        int timeToBorderFile = Math.abs(queenFile - borderFile) - 1;
        int timeToBorderRank = Math.abs(queenRank - borderRank) - 1;

        int clock = 2 * (Math.max(2 * timeToBorderFile + timeToBorderRank, 2 * timeToBorderRank + timeToBorderFile) + 7);
        if (timeToBorderFile == 0) {
            clock = kingAssistToFile(kingFile, kingRank, queenRank, enemyKingRank, quadrant);
        } else if (timeToBorderRank == 0) {
            clock = kingAssistToRank(kingFile, kingRank, queenFile, enemyKingFile, quadrant);
        }

        return Evaluation.mateIn(clock, queenColor);


    }

    /**
     * Returns the quadrant of a king's square relative to the queen.
     * Quandrants are: "top-right", "top-left", "bottom-left", and "bottom-right"
     */
    private static String quadrantOf(int kingFile, int kingRank, int queenFile, int queenRank) {

        if (kingFile < queenFile) {
            if (kingRank < queenRank) {
                return "bottom-left";
            } else {
                return "top-left";
            }
        } else {
            if (kingRank < queenRank) {
                return "bottom-right";
            } else {
                return "top-right";
            }
        }
    }

    /**
     * Returns true if the specified square is in (or bordering) a given quadrant.
     */
    private static boolean isInQuadrant(int squareFile, int squareRank, int queenFile, int queenRank, String quadrant) {

        if ("bottom-left".equals(quadrant)) {
            return squareRank <= queenRank && squareFile <= queenFile;
        } else if ("top-left".equals(quadrant)) {
            return squareRank >= queenRank && squareFile <= queenFile;
        } else if ("bottom-right".equals(quadrant)) {
            return squareRank <= queenRank && squareFile >= queenFile;
        } else if ("top-right".equals(quadrant)) {
            return squareRank >= queenRank && squareFile >= queenFile;
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

    private static int kingAssistToFile(int kingFile, int kingRank, int queenRank, int enemyKingRank, String quadrant) {
        int borderFile = keyFile(quadrant, 1, 8);
        int borderRank = keyRank(quadrant, 1, 8);
        int goalFile = keyFile(quadrant, 3, 6);
        int goalRank = keyRank(quadrant, 3, 6);

        if (kingRank == enemyKingRank) {
            return Math.abs(borderFile - kingFile) - 1;
        } else if (Math.abs(borderRank - kingRank) > Math.abs(borderRank - enemyKingRank)) {
            return timeTo(kingFile, kingRank, goalFile, goalRank) + 2;
        } else {
            if ("bottom-left".equals(quadrant) || "bottom-right".equals(quadrant)) {
                return timeTo(kingFile, kingRank, goalFile, Math.max(queenRank - 3, 1)) + 2;
            } else {
                return timeTo(kingFile, kingRank, goalFile, Math.min(queenRank + 3, 8)) + 2;
            }
        }
    }

    private static int kingAssistToRank(int kingFile, int kingRank, int queenFile, int enemyKingFile, String quadrant) {
        int borderFile = keyFile(quadrant, 1, 8);
        int borderRank = keyRank(quadrant, 1, 8);
        int goalFile = keyFile(quadrant, 3, 6);
        int goalRank = keyRank(quadrant, 3, 6);

        if (kingFile == enemyKingFile) {
            return Math.abs(borderRank - kingRank) - 1;
        } else if (Math.abs(borderFile - kingFile) > Math.abs(borderFile - enemyKingFile)) {
            return timeTo(kingFile, kingRank, goalFile, goalRank);
        } else {
            if ("bottom-left".equals(quadrant) || "top-left".equals(quadrant)) {
                return timeTo(kingFile, kingRank, Math.max(queenFile - 3, 1), goalRank) + 2;
            } else {
                return timeTo(kingFile, kingRank, Math.min(queenFile + 3, 8), goalRank) + 2;
            }
        }
    }
}
