package chess.logic;

import chess.logic.util.GridMath;

/**
 * Represents a chess position. Contains information about where each piece is in a compact form.
 */
public class ChessPosition {

    private static final int EN_PASSANT = 64;
    private static final int W_CASTLING_RIGHTS = 65;
    private static final int B_CASTLING_RIGHTS = 66;
    private static final int TO_MOVE = 67;

    // The current state of the board represented as a list of 68 integers. The first 64
    // represent the pieces at each square starting from the bottom-left. positionArray[64]
    // represents the index where en passant is available, or -1 otherwise.
    // positionArray[65 & 66] represent castling rights for white and black, respectively (starts
    // at 6, kingside/queenside castling is encoded as divisibility by 2 and 3, respectively).
    // positionArray[67] represents whose turn it is (0 for black, 1 for white).
    private int[] positionArray;

    /**
     * The default constructor. Creates the default starting position.
     */
    public ChessPosition() {
        
        int[] startingPosition = {4, 3, 2, 5, 6, 2, 3, 4,
                                  1, 1, 1, 1, 1, 1, 1, 1,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  0, 0, 0, 0, 0, 0, 0, 0,
                                  -1,-1,-1,-1,-1,-1,-1,-1,
                                  -4,-3,-2,-5,-6,-2,-3,-4,
                                  -1, 6, 6, 1};

        this.positionArray = startingPosition;
    }

    public ChessPosition(int[] positionArray) {
        this.positionArray = new int[positionArray.length];
        for (int i = 0; i < positionArray.length; i++) {
            this.positionArray[i] = positionArray[i];
        }
    }

    /**
     * Updates the positionArray based on a specified move.
     * @param move the move to make
     */
    public void makeMove(ChessMove move) {
        this.positionArray[move.getEnd()] = this.positionArray[move.getStart()];
        this.positionArray[move.getStart()] = 0;

        // to do: update castling rights here.

        advanceGame();
    }

    public int findKing(char color) {
        int targetID;
        if (color == 'w') {
            targetID = 6;
        } else {
            targetID = -6;
        }

        for (int i = 0; i < 64; i++) {
            if (this.positionArray[i] == targetID) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns a copy of this position after a specified move is made.
     * @param move the move to be made
     * @return a ChessPosition representing the position after the move is made
     */
    public ChessPosition afterMove(ChessMove move) {
        ChessPosition position = new ChessPosition(this.positionArray);
        position.makeMove(move);
        return position;
    }

    /**
     * Passes the turn and advances the game.
     */
    public void advanceGame() {
        passTurn();
        if (this.positionArray[TO_MOVE] == 0) {
            // to do: increment move counter here.
        }
    }

    /**
     * Changes the color to move without advancing the game.
     * @return the new position after the turn has been passed
     */
    public ChessPosition passTurn() {
        this.positionArray[TO_MOVE] = -1 * (this.positionArray[TO_MOVE] - 1);
        return this;
    }

    public char colorToMove() {
        if (this.positionArray[TO_MOVE] == 1) {
            return 'w';
        } else {
            return 'b';
        }
    }

    public int getEnPassantOpportunity() {
        return this.positionArray[EN_PASSANT];
    }

    /**
     * Returns true if the square at a specified index is empty, and false otherwise. Returns true
     * if the specified index is out out bounds.
     */
    public boolean isEmpty(int index) {
        if (index < 0 || index >= 64) {
            return true;
        }

        return this.positionArray[index] == 0;
    }

    /**
     * Returns true if the square at the specified file and rank is empty, and false otherwise. Returns
     * true if the specified coordinates are out of bounds.
     */
    public boolean isEmpty(int file, int rank) {
        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            return true;
        }

        return this.positionArray[GridMath.index(file, rank)] == 0;
    }

    public int getPieceIDAt(int index) {
        if (index < 0 || index >= 64) {
            throw new IllegalArgumentException("getPieceIDAt: index must be between 0 and 63, inclusive.");
        }

        return this.positionArray[index];
    }

    public int getPieceIDAt(int file, int rank) {
        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("getPieceIDAt: file and rank must be between 1 and 8, inclusive.");
        }

        return this.positionArray[GridMath.index(file, rank)];
    }

    public ChessPiece getPieceAt(int index) {
        if (index < 0 || index >= 64) {
            throw new IllegalArgumentException("getPieceAt: index must be between 0 and 63, inclusive.");
        }

        int pieceID = this.positionArray[index];
        if (pieceID > 0) {
            return ChessPiece.WHITE_PIECES[pieceID - 1];
        } else if (pieceID < 0) {
            return ChessPiece.BLACK_PIECES[-1 * pieceID - 1];
        } else {
            return null;
        }
    }

    public ChessPiece getPieceAt(int file, int rank) {
        if (file < 1 || file > 8 || rank < 1 || rank > 8) {
            throw new IllegalArgumentException("getPieceAt: file and rank must be between 1 and 8, inclusive.");
        }

        int pieceID = this.positionArray[GridMath.index(file, rank)];
        if (pieceID > 0) {
            return ChessPiece.WHITE_PIECES[pieceID - 1];
        } else if (pieceID < 0) {
            return ChessPiece.BLACK_PIECES[-1 * pieceID - 1];
        } else {
            return null;
        }
    }
}
