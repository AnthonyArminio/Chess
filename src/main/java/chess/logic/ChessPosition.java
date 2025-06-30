package chess.logic;

import chess.logic.util.GridMath;

/**
 * Represents a chess position. Contains information about where each piece is in a compact form.
 */
public class ChessPosition {

    private static final int EN_PASSANT = 64;
    private static final int W_K_CASTLING_RIGHTS = 65;
    private static final int W_Q_CASTLING_RIGHTS = 66;
    private static final int B_K_CASTLING_RIGHTS = 67;
    private static final int B_Q_CASTLING_RIGHTS = 68;
    private static final int TO_MOVE = 69;

    // The current state of the board represented as a list of 70 integers. The first 64
    // represent the pieces at each square starting from the bottom-left. positionArray[64]
    // represents the index where en passant is available, or -1 otherwise.
    // positionArray[65-68] represent castling rights (white kingside, white queenside, black
    // kingside, and black queenside, respectively). positionArray[67] represents whose turn it
    // is (0 for black, 1 for white).
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
                                  -1, 1, 1, 1, 1, 1};

        this.positionArray = startingPosition;
    }

    public ChessPosition(int[] positionArray) {
        this.positionArray = new int[positionArray.length];
        for (int i = 0; i < positionArray.length; i++) {
            this.positionArray[i] = positionArray[i];
        }
    }

    /**
     * Returns the index corresponding to the castling destination (where the king lands) for a given
     * color and side. ('K' for kingside castling, 'Q' for queenside castling)
     */
    public static int getCastlingDestination(char color, char side) {
        if (color == 'w') {
            if (side == 'K') {
                return 6;
            } else {
                return 2;
            }
        } else {
            if (side == 'K') {
                return 62;
            } else {
                return 58;
            }
        }
    }

    /**
     * Moves a piece from one location to another but otherwise does nothing; does not advance the game.
     */
    public void makeAlteration(int start, int end) {
        this.positionArray[end] = this.positionArray[start];
        this.positionArray[start] = 0;
    }

    /**
     * Updates the positionArray based on a specified move.
     * @param move the move to make
     */
    public void makeMove(ChessMove move) {
        makeAlteration(move.getStart(), move.getEnd());

        if (move.isEnPassant()) {
            if (move.getColor() == 'w') {
                this.positionArray[move.getEnd() - 8] = 0;
            } else {
                this.positionArray[move.getEnd() + 8] = 0;
            }
        }

        this.positionArray[EN_PASSANT] = move.enPassantValue();
        System.out.println("EPO: " + this.getEnPassantOpportunity());

        // move the rook after castling
        if (move.isKingsideCastle()) {
            makeAlteration(7, 5);
        } else if (move.isQueensideCastle()) {
            makeAlteration(0, 3);
        }

        // update castling rights
        if (castlingRightsExist()) {
            handleCastlingRights(move);
        }

        advanceGame();
    }

    private void handleCastlingRights(ChessMove move) {
        int start = move.getStart();
        int end = move.getEnd();

        if (move.getPieceType() == 'K') {
            removeCastlingRights(move.getColor());
        } else {
            if (hasCastlingRights('w', 'K') && (start == 7 || end == 7)) {
                removeCastlingRights('w', 'K');
            }
            else if (hasCastlingRights('w', 'Q') && (start == 0 || end == 0)) {
                removeCastlingRights('w', 'Q');
            }
            if (hasCastlingRights('b', 'K') && (start == 63 || end == 63)) {
                removeCastlingRights('b', 'K');
            }
            else if (hasCastlingRights('b', 'Q') && (start == 56 || end == 56)) {
                removeCastlingRights('b', 'Q');
            }
        }
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
     * Returns a copy of this position after a specified alteration is made.
     */
    public ChessPosition afterAlteration(int start, int end) {
        ChessPosition position = new ChessPosition(this.positionArray);
        position.makeAlteration(start, end);
        return position;
    }

    /**
     * Returns a copy of this position after a specified move is made. The move is assumed to be legal.
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

    public boolean hasCastlingRights(char color, char side) {
        if (color == 'w') {
            if (side == 'K') {
                return this.positionArray[W_K_CASTLING_RIGHTS] == 1;
            } else {
                return this.positionArray[W_Q_CASTLING_RIGHTS] == 1;
            }
        } else {
            if (side == 'K') {
                return this.positionArray[B_K_CASTLING_RIGHTS] == 1;
            } else {
                return this.positionArray[B_Q_CASTLING_RIGHTS] == 1;
            }
        }
    }

    public boolean castlingRightsExist() {
        return this.positionArray[W_K_CASTLING_RIGHTS] == 1 ||
               this.positionArray[W_Q_CASTLING_RIGHTS] == 1 ||
               this.positionArray[B_K_CASTLING_RIGHTS] == 1 ||
               this.positionArray[B_Q_CASTLING_RIGHTS] == 1;
    }

    public void removeCastlingRights(char color, char side) {
        if (color == 'w') {
            if (side == 'K') {
                this.positionArray[W_K_CASTLING_RIGHTS] = 0;
            } else {
                this.positionArray[W_Q_CASTLING_RIGHTS] = 0;
            }
        } else {
            if (side == 'K') {
                this.positionArray[B_K_CASTLING_RIGHTS] = 0;
            } else {
                this.positionArray[B_Q_CASTLING_RIGHTS] = 0;
            }
        }
    }

    public void removeCastlingRights(char color) {
        if (color == 'w') {
            this.positionArray[W_K_CASTLING_RIGHTS] = 0;
            this.positionArray[W_Q_CASTLING_RIGHTS] = 0;
        } else {
            this.positionArray[B_K_CASTLING_RIGHTS] = 0;
            this.positionArray[B_Q_CASTLING_RIGHTS] = 0;
        }
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
            // out of bounds
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
