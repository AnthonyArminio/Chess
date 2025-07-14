package chess.intel.strategy;

import chess.logic.util.ChessLogic;
import chess.logic.ChessPiece;
import chess.logic.ChessPosition;

public class MaterialisticStrategy extends Strategy {

    public DetailedEvaluation evaluate(ChessPosition position) {

        // standard evaluation
        if (ChessLogic.isCheckmate(position)) {
            if (position.colorToMove() == 'b') {
                return DetailedEvaluation.CHECKMATE_FOR_WHITE;
            } else {
                return DetailedEvaluation.CHECKMATE_FOR_BLACK;
            }
        } else if (ChessLogic.isStalemate(position)) {
            return DetailedEvaluation.DRAW;
        }

        float sum = 0;
        for (int i = 0; i < 64; i++) {
            sum += materialValue(position.getPieceAt(i));
        }
        return new DetailedEvaluation(sum);
    }

    private static float materialValue(ChessPiece piece) {

        if (piece == null) {
            return 0;
        }

        float value = 0;

        if (piece.getType() == 'P') {
            value = 1;
        } else if (piece.getType() == 'B') {
            value = 3;
        } else if (piece.getType() == 'N') {
            value = 3;
        } else if (piece.getType() == 'R') {
            value = 5;
        } else if (piece.getType() == 'Q') {
            value = 9;
        }

        if (piece.getColor() == 'b') {
            value *= -1;
        }

        return value;
    }
}
