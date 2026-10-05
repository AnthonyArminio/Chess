package chess.intel.technique;

import chess.intel.strategy.Evaluation;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class SinglePawnEndgame extends Technique {

    private static final int QUEEN_ON_BOARD_CLOCK = 50;

    public SinglePawnEndgame() {
        this.activateCondition = (pos) -> {
            return pos.countPieces() == 3 && ChessLogic.findMatchingPiece(pos, 'P') >= 0;
        };
    }

    @Override public Evaluation objectiveEvaluate(ChessPosition position) {
        return Evaluation.INCONCLUSIVE;
    }

    @Override public Evaluation evaluate(ChessPosition position) {
        int queenSquare = ChessLogic.findMatchingPiece(position, 'Q');
        if (queenSquare >= 0) {
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK, position.getPieceAt(queenSquare).getColor());
        }
        int pawnSquare = ChessLogic.findMatchingPiece(position, 'P');
        if (pawnSquare < 0) {
            return Evaluation.REJECTED;
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

        
        

        if (isFlankPawn(pawnFile)) {

        } else {
            
        }


        return Evaluation.REJECTED;
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
