package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;
import chess.intel.technique.util.PieceConfiguration;

public class SinglePawnEndgame extends Technique {

    private static final int QUEEN_ON_BOARD_CLOCK = 50;

    public static ChessPosition samplePosition() {
        int[] positionArray = {0, 0, 6, 0, 0, 0, 0, 0,
                               0, 1, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0,-6, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0};

        int[] stateArray = {-1, 0, 0, 0, 0, 0};

        return new ChessPosition(positionArray, stateArray);
    }

    public SinglePawnEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'P') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        int queenSquare = ChessLogic.findMatchingPiece(position, 'Q');
        if (queenSquare >= 0) {
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK, position.getPieceAt(queenSquare).getColor());
        }
        int pawnSquare = ChessLogic.findMatchingPiece(position, 'P');
        if (pawnSquare < 0) {
            return Evaluation.INCONCLUSIVE;
        }
        char pawnColor = position.getPieceAt(pawnSquare).getColor();

        int kingSquare = ChessLogic.findMatchingPiece(position, 'K', pawnColor);
        int enemyKingSquare = ChessLogic.findMatchingPiece(position, 'K', ChessLogic.opponentOf(pawnColor));

        int pawnFile = GridMath.getFile(pawnSquare);
        int pawnRank = GridMath.getRank(pawnSquare);
        int kingFile = GridMath.getFile(kingSquare);
        int kingRank = GridMath.getRank(kingSquare);
        int enemyKingFile = GridMath.getFile(enemyKingSquare);
        int enemyKingRank = GridMath.getRank(enemyKingSquare);

        int promotionRank = pawnColor == 'w' ? 8 : 1;
        int adjustedPawnRank = adjustedPawnRank(pawnRank, pawnColor);
        int kingRelativeLead = kingRelativeLead(kingRank, adjustedPawnRank, promotionRank);
        int enemyKingRelativeLead = kingRelativeLead(enemyKingRank, adjustedPawnRank, promotionRank);

        if (isFlankPawn(pawnFile)) {

        }

        if (position.containsConfiguration(PieceConfiguration.OPPOSITION)) {
            // drawn opposition
            if (kingRelativeLead == 1 && Math.abs(adjustedPawnRank - promotionRank) > 3 && position.colorToMove() == pawnColor) {
                return Evaluation.TECHNICAL_DRAW;
            }

            // winning opposition
            if (kingRelativeLead > 0 && position.colorToMove() != pawnColor) {
                return typicalWinningEvaluation(kingFile, kingRank, pawnFile, adjustedPawnRank, pawnColor);
            }
        }

        // runaway pawn
        if (position.colorToMove() == pawnColor && !inPawnBox(enemyKingFile, enemyKingRank, pawnFile, adjustedPawnRank, promotionRank)) {
            int halfclock = Math.abs(adjustedPawnRank - promotionRank) + (blockingPawn(kingFile, kingRank, pawnFile, pawnRank, pawnColor) ? 1 : 0);
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock - 1, pawnColor);
        }

        // flank position
        if (position.colorToMove() != pawnColor && kingRelativeLead > 0 && kingRelativeLead <= 3 && enemyKingRelativeLead > kingRelativeLead) {
            if ((kingFile - pawnFile == 1 && enemyKingFile < pawnFile) || (kingFile - pawnFile == -1 && enemyKingFile > pawnFile)) {
                return typicalWinningEvaluation(kingFile, kingRank, pawnFile, adjustedPawnRank, pawnColor);
            }
        }

        /**
        if (kingRelativeLead(enemyKingRank, pawnRank, pawnColor) <= 0 && GridMath.isAdjacent(kingSquare, pawnSquare) && position.colorToMove() == pawnColor) {
            int halfclock = Math.max(Math.abs(kingRank - promotionRank) - 1, 0) + Math.abs(adjustedPawnRank - promotionRank);
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock, pawnColor);
        }
            */

        // race to pawn
        if (position.colorToMove() != pawnColor && timeTo(enemyKingFile, enemyKingRank, pawnFile, pawnRank) < timeTo(kingFile, kingRank, pawnFile, pawnRank)) {
            return Evaluation.TECHNICAL_DRAW;
        }

        // all pieces on the same rank (assumes race to pawn is already evaluated)
        if (position.colorToMove() != pawnColor && kingRank == enemyKingRank && enemyKingRank == pawnRank) {
            return typicalWinningEvaluation(kingFile, kingRank, pawnFile, adjustedPawnRank, pawnColor);
        }


        return Evaluation.INCONCLUSIVE;
    }

    private static int timeTo(int kingFile, int kingRank, int destinationFile, int destinationRank) {
        return 2 * Math.max(Math.abs(destinationFile - kingFile), Math.abs(destinationRank - kingRank));
    }

    private static int adjustedPawnRank(int pawnRank, char pawnColor) {
        if (pawnColor == 'w') {
            if (pawnRank == 2) {
                return pawnRank + 1;
            }
        } else if (pawnRank == 7) {
            return pawnRank - 1;
        }
        return pawnRank;
    }

    private static boolean blockingPawn(int kingFile, int kingRank, int pawnFile, int pawnRank, char pawnColor) {
        return pawnFile == kingFile && (pawnColor == 'w' ? kingRank > pawnRank : kingRank < pawnRank);
    }

    private static int kingRelativeLead(int kingRank, int pawnRank, int promotionRank) {
        return Math.abs(pawnRank - promotionRank) - Math.abs(kingRank - promotionRank);
    }

    private static boolean isFlankPawn(int pawnFile) {
        return pawnFile == 1 || pawnFile == 8;
    }

    private static boolean inPawnBox(int kingFile, int kingRank, int pawnFile, int pawnRank, int promotionRank) {
        int squaresToPromotion = Math.abs(promotionRank - pawnRank);
        boolean withinFiles = kingFile >= pawnFile - squaresToPromotion && kingFile <= pawnFile + squaresToPromotion;
        boolean withinRanks = Math.abs(promotionRank - kingRank) <= Math.abs(promotionRank - pawnRank);
        return withinFiles && withinRanks;
    }

    private static Evaluation typicalWinningEvaluation(int kingFile, int kingRank, int pawnFile, int pawnRank, char pawnColor) {
        int halfclock = Math.abs(kingFile - pawnFile); // horizontal distance to pawn
        halfclock += 2 * (Math.abs(kingRank - (pawnColor == 'w' ? 8 : 1)) - 2) + 1; // time to secure promotion square
        halfclock += Math.abs(pawnRank - (pawnColor == 'w' ? 8 : 1)); // time to walk the pawn down the board.
        return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock, pawnColor);
    }
}
