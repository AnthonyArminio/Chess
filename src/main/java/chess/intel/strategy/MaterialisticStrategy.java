package chess.intel.strategy;

import chess.logic.ChessPiece;
import chess.logic.ChessPosition;

public class MaterialisticStrategy extends Strategy {

    @Override public Evaluation evaluate(ChessPosition position) {

        return new Evaluation(position.getMaterialEvaluation());
    }
}
