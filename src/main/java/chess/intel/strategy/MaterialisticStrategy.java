package chess.intel.strategy;

import chess.logic.ChessPiece;
import chess.logic.ChessPosition;

public class MaterialisticStrategy extends Strategy {

    public double evaluate(ChessPosition position) {
        double sum = 0;
        for (int i = 0; i < 64; i++) {
            sum += materialValue(position.getPieceAt(i));
        }
        return sum;
    }

    private static double materialValue(ChessPiece piece) {

        if (piece == null) {
            return 0;
        }

        double value = 0;

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
