package chess.logic.util;

import chess.logic.ChessPosition;
import chess.logic.ChessPiece;

import java.util.ArrayList;

import chess.logic.ChessMove;

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

        // cannot move if it is not your turn
        if (move.getColor() != position.colorToMove()) {
            System.out.println("Piece does not match color to move.");
            return false;
        }

        // cannot move onto your own piece
        if (!position.isEmpty(end)) {
            if (move.getColor() == position.getPieceAt(end).getColor()) {
                return false;
            }
        }

        // must move according to the capabilities of each piece
        chess.logic.util.ChessCondition condition = (p, s, e) -> e == end;
        if (!searchVision(position, start, move.getPiece(), condition)) {
            return false;
        }

        // cannot put own king in check
        if (ChessLogic.isCheck(position.afterMove(move).passTurn())) {
            System.out.println("Cannot put own king in check.");
            return false;
        }

        // cannot castle through or out of check
        if (move.isKingsideCastle() && 
            (ChessLogic.isCheck(position) || ChessLogic.isCheck(position.afterAlteration(move.getStart(), move.getStart() + 1)))) {
            return false;
        }
        if (move.isQueensideCastle() && 
            (ChessLogic.isCheck(position) || ChessLogic.isCheck(position.afterAlteration(move.getStart(), move.getStart() - 1)))) {
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
        for (ChessPiece piece : opponentArmy) {
            if (piece.getType() == 'P') {
                // pawn movement is not symmetrical
                if (piece.getColor() == 'w') {
                    if (!GridMath.isOutOfBounds(kingLocation, kingLocation - 7) && piece.equals(position.getPieceAt(kingLocation - 7))) {
                        return true;
                    }
                    if (!GridMath.isOutOfBounds(kingLocation, kingLocation - 9) && piece.equals(position.getPieceAt(kingLocation - 9))) {
                        return true;
                    }
                } else {
                    if (!GridMath.isOutOfBounds(kingLocation, kingLocation + 7) && piece.equals(position.getPieceAt(kingLocation + 7))) {
                        return true;
                    }
                    if (!GridMath.isOutOfBounds(kingLocation, kingLocation + 9) && piece.equals(position.getPieceAt(kingLocation + 9))) {
                        return true;
                    }
                }
            } else {
                
                chess.logic.util.ChessCondition condition = (p, s, e) -> piece.equals(p.getPieceAt(e));
                
                if (searchVision(position, kingLocation, piece, condition)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isCheckmate(ChessPosition position) {
        return ChessLogic.isCheck(position) && ChessLogic.generateLegalMoves(position).size() == 0;
    }

    public static boolean isEnPassant(ChessPosition position, ChessMove move) {
        return move.getPieceType() == 'P' && move.getEnd() == position.getEnPassantOpportunity();
    }

    public static boolean isPromotion(ChessMove move) {
        return move.getPieceType() == 'P' && (GridMath.getRank(move.getEnd()) == 8 || GridMath.getRank(move.getEnd()) == 1);
    }

    public static boolean isKingsideCastle(ChessPosition position, ChessMove move) {
        return move.getPieceType() == 'K' && position.hasCastlingRights(move.getColor(), 'K') && 
               move.getEnd() == ChessPosition.getCastlingDestination(move.getColor(), 'K');
    }

    public static boolean isQueensideCastle(ChessPosition position, ChessMove move) {
        return move.getPieceType() == 'K' && position.hasCastlingRights(move.getColor(), 'Q') &&
               move.getEnd() == ChessPosition.getCastlingDestination(move.getColor(), 'Q');
    }

    public static char opponentOf(char color) {
        if (color == 'w') {
            return 'b';
        } else {
            return 'w';
        }
    }

    /**
     * Searches the vision of a piece at a given location in a given context for a visible square that matches a specified
     * condition.
     * @param origin
     * @param piece
     * @param condition implements chess.logic.util.ChessCondition.
     * @return true if a square matching the condition was found, false otherwise.
     */
    private static boolean searchVision(ChessPosition position, int origin, ChessPiece piece, chess.logic.util.ChessCondition condition) {
        
        boolean success = false;
        
        for (int[] line : piece.getMovement(position, origin)) {
            if (!success) {

                int previous = origin;
                boolean pieceOnLine = false;
                boolean outOfBounds = false;

                for (int displacement : line) {
                    if (displacement != 0 && !success && !outOfBounds && !pieceOnLine) {
                        if (GridMath.isOutOfBounds(previous, origin + displacement)) {
                            outOfBounds = true;
                        } else {
                            if (condition.test(position, origin, origin + displacement)) {
                                success = true;
                            }
                            if (!position.isEmpty(origin + displacement)) {
                                pieceOnLine = true;
                            }
                        }
                        previous = origin + displacement;
                    }
                }
            }
        }

        return success;
    }

    /**
     * Determines all of the legal moves in a given position and returns an ArrayList of ChessMoves
     * representing those moves.
     * @param position the context of the moves
     * @return An ArrayList of all of the legal moves in the position.
     */
    public static ArrayList<ChessMove> generateLegalMoves(ChessPosition position) {
        System.out.println("Generating legal moves...");

        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        char colorToMove = position.colorToMove();

        for (int i = 0; i < 64; i++) {
            ChessPiece piece = position.getPieceAt(i);
            if (piece != null && piece.getColor() == colorToMove) {

                for (int[] line : piece.getMovement(position, i)) {

                    int previous = i;
                    boolean pieceOnLine = false;
                    boolean outOfBounds = false;

                    for (int displacement : line) {

                        if (displacement != 0 && !outOfBounds && !pieceOnLine) {
                            if (GridMath.isOutOfBounds(previous, i + displacement)) {
                                outOfBounds = true;
                            } else {
                                ChessMove move = new ChessMove(position, i, i + displacement);
                                if (move.isLegal()) {
                                    if (move.isPromotion()) {
                                        moves.add(new ChessMove(position, i, i + displacement, 'Q'));
                                        moves.add(new ChessMove(position, i, i + displacement, 'N'));
                                        moves.add(new ChessMove(position, i, i + displacement, 'R'));
                                        moves.add(new ChessMove(position, i, i + displacement, 'B'));
                                    } else {
                                        moves.add(move);
                                    }
                                }
                                if (!position.isEmpty(i + displacement)) {
                                    pieceOnLine = true;
                                }
                            }
                            previous = i + displacement;
                        }
                    }
                }
            }
        }

        System.out.println("Found " + moves.size() + " legal moves.");

        return moves;
    }
}
