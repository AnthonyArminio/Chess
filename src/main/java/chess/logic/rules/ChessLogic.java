package chess.logic.rules;

import chess.logic.ChessPosition;
import chess.logic.ChessPiece;

import java.util.ArrayList;

/**
 * Class with static functions to help with chess logic such as checks, legal moves, and checkmate.
 */
public class ChessLogic {
    
    public static boolean isLegalMove(ChessPosition position, int start, int end) {
        
        // cannot move from an empty square
        if (position.isEmpty(start)) {
            return false;
        }

        ChessPiece piece = position.getPieceAt(start);

        // cannot move if it is not your turn
        if (piece.getColor() != position.colorToMove()) {
            return false;
        }

        // cannot move onto your own piece
        if (!position.isEmpty(end)) {
            if (piece.getColor() == position.getPieceAt(end).getColor()) {
                return false;
            }
        }

        // must move according to the capabilities of each piece
        boolean endInVision = false;
        for (int[] line : piece.getBaseMovement()) {
            if (!endInVision) {

                int previous = start;
                boolean pieceOnLine = false;
                boolean outOfBounds = false;

                for (int displacement : line) {
                    if (!endInVision && !outOfBounds && !pieceOnLine) {
                        if (isOutOfBounds(previous, start + displacement)) {
                            outOfBounds = true;
                        } else {
                            if (!position.isEmpty(start + displacement)) {
                                pieceOnLine = true;
                            }
                            if (start + displacement == end) {
                                endInVision = true;
                            }
                        }
                        previous = start + displacement;
                    }
                }
            }
        }
        
        if (piece.getType() == 'P') {
            // special pawn movement
        }

        if (piece.getType() == 'K') {
            // special king movement
        }

        if (!endInVision) {
            return false;
        }

        return true; // to do
    }

    public static boolean isCheck(ChessPosition position, int start, int end) {
        return false; // to do
    }

    /**
     * Determines if the step between the square indices 'previous' and 'next' implies a movement
     * out of the bounds of the chess board.
     * @param previous
     * @param next
     * @return true if the step is out of bounds, false otherwise.
     */
    private static boolean isOutOfBounds(int previous, int next) {
        
        // vertical bounds
        if (next < 0 || next >= 64) {
            return true;
        }

        // horizontal bounds
        if ((next % 8) - (previous % 8) < -2 || (next % 8) - (previous % 8) > 2) {
            return true;
        }

        return false;
    }
}
