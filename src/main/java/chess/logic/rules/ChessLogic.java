package chess.logic.rules;

import chess.logic.ChessPosition;
import chess.logic.ChessPiece;
import chess.logic.ChessMove;
import chess.logic.util.GridMath;

/**
 * Class with static functions to help with chess logic such as checks, legal moves, and checkmate.
 */
public class ChessLogic {
    
    public static boolean isLegalMove(ChessPosition position, ChessMove move) {
        
        int start = move.getStart();
        int end = move.getEnd();

        // cannot move from an empty square
        if (position.isEmpty(start)) {
            return false;
        }

        ChessPiece piece = move.getPiece();

        // cannot move if it is not your turn
        if (piece.getColor() != position.colorToMove()) {
            System.out.println("piece does not match color to move.");
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
                        if (GridMath.isOutOfBounds(previous, start + displacement)) {
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

        else if (piece.getType() == 'K') {
            // special king movement
        }

        if (!endInVision) {
            return false;
        }

        // cannot put own king in check
        if (ChessLogic.isCheck(position.afterMove(move).passTurn())) {
            System.out.println("Cannot put own king in check.");
            return false;
        }

        return true; // to do
    }

    /**
     * Returns true if the king of the color to move is under threat of being captured, and false otherwise.
     */
    public static boolean isCheck(ChessPosition position) {
        int kingLocation = position.findKing(position.colorToMove());

        if (kingLocation == -1) {
            return false;
        }

        ChessPiece[] opponentArmy = ChessPiece.getArmy(opponentOf(position.colorToMove()));

        // go through all possible piece types that could be checking the king and check to see if
        // a piece of that type can see the king.
        boolean foundThreat = false;
        for(ChessPiece piece : opponentArmy) {
            if (!foundThreat) {
                if (piece.getType() == 'P') {
                    // pawn movement is not symmetrical
                } else {
                    // similar code to that used in isLegalMove. Is it possible to create a new method to reduce reused code?
                    for (int[] line : piece.getBaseMovement()) {
                        if (!foundThreat) {

                            int previous = kingLocation;
                            boolean pieceOnLine = false;
                            boolean outOfBounds = false;

                            for (int displacement : line) {
                                if (!foundThreat && !outOfBounds && !pieceOnLine) {
                                    if (GridMath.isOutOfBounds(previous, kingLocation + displacement)) {
                                        outOfBounds = true;
                                    } else {
                                        if (!position.isEmpty(kingLocation + displacement)) {
                                            pieceOnLine = true;
                                            ChessPiece potentialThreat = position.getPieceAt(kingLocation + displacement);
                                            if (potentialThreat.getType() == piece.getType() && potentialThreat.getColor() == piece.getColor()) {
                                                foundThreat = true;
                                            }
                                        }
                                    }
                                    previous = kingLocation + displacement;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (foundThreat) {
            return true;
        }

        return false;
    }

    private static char opponentOf(char color) {
        if (color == 'w') {
            return 'b';
        } else {
            return 'w';
        }
    }
}
