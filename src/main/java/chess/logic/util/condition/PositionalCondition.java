package chess.logic.util.condition;

import chess.logic.ChessPosition;

public interface PositionalCondition {
    public abstract boolean test(ChessPosition position);
}
