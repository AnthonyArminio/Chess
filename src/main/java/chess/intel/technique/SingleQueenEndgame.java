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

        String quadrant = quadrantOf(enemyKingSquare, queenSquare);

        if (isInQuadrant(kingSquare, queenSquare, quadrant)) {
            // If the king is on the same side of the queen as the enemy king, the checkmate may not be possible.
            return Evaluation.UNDECIDED;
        }

        int borderFile = borderFile(quadrant);
        int borderRank = borderRank(quadrant);

        int timeToBorderFile = Math.abs(enemyKingFile - borderFile);
        int timeToBorderRank = Math.abs(enemyKingRank - borderRank);

        int clock = Math.max(timeToBorderFile, timeToBorderRank) + 7;
        if (timeToBorderFile == 0) {
            
        } else if (timeToBorderRank == 0) {

        }

        return Evaluation.mateIn(clock, queenColor);


    }

    /**
     * Returns the quadrant of a king's square relative to the queen.
     * Quandrants are: "top-right", "top-left", "bottom-left", and "bottom-right"
     */
    private static String quadrantOf(int kingSquare, int queenSquare) {
        int kingFile = GridMath.getFile(kingSquare);
        int kingRank = GridMath.getRank(kingSquare);
        int queenFile = GridMath.getFile(queenSquare);
        int queenRank = GridMath.getRank(queenSquare);

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
    private static boolean isInQuadrant(int square, int queenSquare, String quadrant) {
        int squareFile = GridMath.getFile(square);
        int squareRank = GridMath.getRank(square);
        int queenFile = GridMath.getFile(queenSquare);
        int queenRank = GridMath.getRank(queenSquare);

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
}
