package chess.intel.technique;

import java.util.Set;

import chess.intel.strategy.Evaluation;
import chess.intel.technique.util.PieceConfiguration;
import chess.intel.technique.util.TechniqueLogic;
import chess.intel.util.DataMath;
import chess.logic.ChessPiece;
import chess.logic.ChessPosition;
import chess.logic.util.ChessLogic;
import chess.logic.util.GridMath;

public class SinglePawnEndgame extends Technique {

    private static final int QUEEN_ON_BOARD_CLOCK = 50;
    private static final Set<PieceConfiguration> VERTICAL_OPPOSITION = PieceConfiguration.buildSet("k/*/@K", "K/*/@k");
    private static final Set<PieceConfiguration> HORIZONTAL_OPPOSITION = PieceConfiguration.buildSet("@K*k", "@k*K");

    public static ChessPosition samplePosition() {
        int[] positionArray = {0, 0, 0, 0, 0, 0, 0, 0,
                               1, 6, 0, 0, 0, 0, 0, 0,
                               0, 0, 0,-6, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0};

        int[] stateArray = {-1, 0, 0, 0, 0, 0};

        return new ChessPosition(positionArray, stateArray);
    }

    public static ChessPosition randomSamplePosition() {
        int firstKing = DataMath.randomInt(0, 63);
        Set<Integer> exclusion = ChessLogic.coverage(firstKing, ChessPiece.BASE_KING_MOVEMENT);
        exclusion.add(firstKing);
        int secondKing = DataMath.randomInt(0, 63, exclusion);

        int pawnColor = DataMath.randomInt(0, 1); // 0 == 'w'; 1 == 'b'
        int colorToMove = DataMath.randomInt(0, 1);
        exclusion.clear();
        if (pawnColor == colorToMove) {
            if (pawnColor == 0) {
                int[][] pawnCheckExclusion = {{-9}, {-7}};
                exclusion.addAll(ChessLogic.coverage(secondKing, pawnCheckExclusion));
            } else {
                int[][] pawnCheckExclusion = {{7}, {9}};
                exclusion.addAll(ChessLogic.coverage(secondKing, pawnCheckExclusion));
            }
        }
        exclusion.add(firstKing);
        exclusion.add(secondKing);

        int pawn = DataMath.randomInt(8, 55, exclusion);

        int[] positionArray = {0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0,
                               0, 0, 0, 0, 0, 0, 0, 0};

        positionArray[firstKing] = colorToMove == 0 ? 6 : -6;
        positionArray[secondKing] = colorToMove == 0 ? -6 : 6;
        positionArray[pawn] = pawnColor == 0 ? 1 : -1;

        int[] stateArray = {-1, 0, 0, 0, 0, 0};

        stateArray[5] = colorToMove;

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
            char queenColor = position.getPieceAt(queenSquare).getColor();
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK - (position.colorToMove() == queenColor ? 1 : 0), queenColor);
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

        // runaway pawn
        if (position.colorToMove() == pawnColor && !inPawnBox(enemyKingFile, enemyKingRank, pawnFile, adjustedPawnRank, promotionRank)) {
            int halfclock = Math.abs(adjustedPawnRank - promotionRank) + (blockingPawn(kingFile, kingRank, pawnFile, pawnRank, pawnColor) ? 1 : 0);
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock - 1, pawnColor);
        }

        // race to pawn
        if (position.colorToMove() == pawnColor && TechniqueLogic.timeTo(enemyKingFile, enemyKingRank, pawnFile, pawnRank, false) < TechniqueLogic.timeTo(kingFile, kingRank, pawnFile, pawnRank, true) - 1) {
            return Evaluation.TECHNICAL_DRAW;
        }

        if (isFlankPawn(pawnFile)) {
            int keyFile = pawnFile == 1 ? 2 : 7;
            int keyRank = pawnColor == 'w' ? 7 : 2;
            int kingAssistTime = TechniqueLogic.timeTo(kingFile, kingRank, keyFile, keyRank, position.colorToMove() == pawnColor);
            int enemyKingChallengeTime = TechniqueLogic.timeTo(enemyKingFile, enemyKingRank, keyFile, keyRank, position.colorToMove() != pawnColor);
            if (kingAssistTime < enemyKingChallengeTime - 1 || (Math.abs(kingFile - keyFile) < Math.abs(enemyKingFile - keyFile) && position.containsConfiguration(HORIZONTAL_OPPOSITION))) {
                int clock = kingAssistTime + 2 * Math.abs(adjustedPawnRank - promotionRank) - (kingAssistTime == 0 && position.colorToMove() == pawnColor ? 1 : 0);
                return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + clock, pawnColor);
            }

            return Evaluation.INCONCLUSIVE;
        }

        if (position.containsConfiguration(VERTICAL_OPPOSITION)) {
            // drawn opposition
            if (kingRelativeLead == 1 && Math.abs(adjustedPawnRank - promotionRank) > 3 && position.colorToMove() == pawnColor) {
                return Evaluation.TECHNICAL_DRAW;
            }
            // winning opposition
            if (position.colorToMove() != pawnColor && kingRelativeLead > 0) {
                int halfclock = Math.abs(kingFile - pawnFile); // horizontal distance to pawn
                halfclock += 2 * (Math.abs(kingRank - (pawnColor == 'w' ? 8 : 1)) - 2) + 1; // time to secure promotion square
                halfclock += Math.abs(adjustedPawnRank - (pawnColor == 'w' ? 8 : 1)); // time to walk the pawn down the board.
                return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock, pawnColor);
            }
        }

        // flank position
        if (position.colorToMove() == pawnColor && kingRelativeLead > 0 && kingRelativeLead <= 2) {
            int halfclock = Math.abs(kingFile - pawnFile); // horizontal distance to pawn
            halfclock += 2 * (Math.abs(kingRank - (pawnColor == 'w' ? 8 : 1)) - 2) + 1; // time to secure promotion square
            halfclock += Math.abs(adjustedPawnRank - (pawnColor == 'w' ? 8 : 1)); // time to walk the pawn down the board.
            return Evaluation.mateIn(QUEEN_ON_BOARD_CLOCK + 2 * halfclock - 1, pawnColor);
        }


        return Evaluation.INCONCLUSIVE;
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
}
