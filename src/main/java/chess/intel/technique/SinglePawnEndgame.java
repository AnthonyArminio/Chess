package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class SinglePawnEndgame extends Technique {

    public SinglePawnEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'P') >= 0;
        };
    }

    @Override public Evaluation evaluate(ChessPosition position) {
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

        // runaway pawn
        if (!(blockingPawn(kingFile, kingRank, pawnFile, pawnRank, pawnColor) && isFlankPawn(pawnFile)) && !inPawnBox(enemyKingFile, enemyKingRank, pawnFile, adjustedPawnRank(pawnRank, pawnColor, position.colorToMove()), promotionRank)) {
            return new Evaluation(9.0f, pawnColor);
        }

        if (timeTo(kingFile, kingRank, pawnFile, pawnRank) - (pawnColor == position.colorToMove() ? 2 : 0) > timeTo(enemyKingFile, enemyKingRank, pawnFile, pawnRank)) {
            return Evaluation.INCONCLUSIVE;
        }

        if (pawnColor == position.colorToMove()) {
            return Evaluation.REJECTED;
        }

        if (isFlankPawn(pawnFile)) {

        } else {
            //if (aheadOfPawn(kingRank, pawnRank, pawnColor) && pawnColor == position.colorToMove() && ChessLogic.containsConfiguration(PieceConfiguration.OPPOSITION)) {
            //    return new Evaluation(9.0f, pawnColor);
            //}
        }


        return Evaluation.INCONCLUSIVE;
    }

    private static int timeTo(int kingFile, int kingRank, int destinationFile, int destinationRank) {
        return 2 * Math.max(Math.abs(destinationFile - kingFile), Math.abs(destinationRank - kingRank));
    }

    private static int adjustedPawnRank(int pawnRank, char pawnColor, char colorToMove) {
        int rank = pawnRank;
        if (pawnColor == 'w' && pawnRank == 2) {
            rank++;
        } else if (pawnColor == 'b' && pawnRank == 7) {
            rank--;
        }
        if (pawnColor != colorToMove) {
            if (pawnColor == 'w') {
                rank--;
            } else {
                rank++;
            }
        }
        return rank;
    }

    private static boolean blockingPawn(int kingFile, int kingRank, int pawnFile, int pawnRank, char pawnColor) {
        return pawnFile == kingFile && (pawnColor == 'w' ? kingRank > pawnRank : kingRank < pawnRank);
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
}
