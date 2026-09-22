package chess.logic;

import java.util.ArrayList;

import chess.logic.util.GridMath;
import chess.logic.util.condition.Accumulator;

/**
 * Represents a chess position. Contains information about where each piece is in a compact form.
 */
public class ChessPosition {

    protected static final int EN_PASSANT = 0;
    protected static final int W_K_CASTLING_RIGHTS = 1;
    protected static final int W_Q_CASTLING_RIGHTS = 2;
    protected static final int B_K_CASTLING_RIGHTS = 3;
    protected static final int B_Q_CASTLING_RIGHTS = 4;
    protected static final int TO_MOVE = 5;

    // The current state of the board represented as a list of 64 integers.
    private int[] positionArray;

    // The metadata associated with the position is stored here. stateArray[0] represents the index
    // where en passant is available, or -1 otherwise. stateArray[1-4] represent castling rights
    // (white kingside, white queenside, black kingside, and black queenside, respectively.) stateArray[5]
    // represents whose turn it is (0 for white, 1 for black).
    private int[] stateArray;

    // The compressed version of this position.
    private CompressedPosition compressedPosition;

    // A list of all the positions reached for the purpose of detecting threefold repetition.
    private ArrayList<CompressedPosition> reachedPositions;

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
                                  -4,-3,-2,-5,-6,-2,-3,-4};

        int[] startingState = {-1, 1, 1, 1, 1, 0};

        this.positionArray = startingPosition;
        this.stateArray = startingState;

        this.compressedPosition = new CompressedPosition(this);
        this.reachedPositions = new ArrayList<>();
        this.reachedPositions.add(this.compressedPosition.copy());
    }

    public ChessPosition(int[] positionArray, int[] stateArray) {
        this.positionArray = new int[positionArray.length];
        this.stateArray = new int[stateArray.length];
        for (int i = 0; i < positionArray.length; i++) {
            this.positionArray[i] = positionArray[i];
        }
        for (int i = 0; i < stateArray.length; i++) {
            this.stateArray[i] = stateArray[i];
        }

        this.compressedPosition = new CompressedPosition(this);
        this.reachedPositions = new ArrayList<>();
        this.reachedPositions.add(this.compressedPosition.copy());
        
    }

    public ChessPosition(int[] positionArray, int[] stateArray, ArrayList<CompressedPosition> reachedPositions) {
        this.positionArray = new int[positionArray.length];
        this.stateArray = new int[stateArray.length];
        for (int i = 0; i < positionArray.length; i++) {
            this.positionArray[i] = positionArray[i];
        }
        for (int i = 0; i < stateArray.length; i++) {
            this.stateArray[i] = stateArray[i];
        }

        this.compressedPosition = new CompressedPosition(this);
        this.reachedPositions = new ArrayList<>();
        for (CompressedPosition position : reachedPositions) {
            this.reachedPositions.add(position);
        }
        
    }

    public ChessPosition copy() {
        return new ChessPosition(this.positionArray, this.stateArray, this.reachedPositions);
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

        if (move.isIrreversible()) {
            this.reachedPositions.clear();
        } else {
            this.reachedPositions.add(this.compressedPosition.copy());
        }
        this.compressedPosition.makeMove(move);

        makeAlteration(move.getStart(), move.getEnd());
        if (move.isPromotion()) {
            this.positionArray[move.getEnd()] = move.getPieceID();
        }

        else if (move.isEnPassant()) {
            if (move.getColor() == 'w') {
                this.positionArray[move.getEnd() - 8] = 0;
            } else {
                this.positionArray[move.getEnd() + 8] = 0;
            }
        }

        // move the rook after castling
        else if (move.isKingsideCastle()) {
            if (move.getColor() == 'w') {
                makeAlteration(7, 5);
            } else {
                makeAlteration(63, 61);
            }
        } else if (move.isQueensideCastle()) {
            if (move.getColor() == 'w') {
                makeAlteration(0, 3);
            } else {
                makeAlteration(56, 59);
            }
        }

        handleEnPassantOpportunity(move);

        // update castling rights
        if (castlingRightsExist()) {
            handleCastlingRights(move);
        }

        advanceGame();
    }

    private void handleEnPassantOpportunity(ChessMove move) {
        int enPassantValue = move.enPassantValue();
        this.stateArray[EN_PASSANT] = enPassantValue;
        if (enPassantValue >= 0) {
            ChessPosition afterPass = this.copy().passTurn();
            if (!(!this.isEmpty(move.getEnd() + 1) && 
                this.getPieceAt(move.getEnd() + 1).getType() == 'P' && 
                new ChessMove(afterPass, move.getEnd() + 1, enPassantValue).isLegal()) && 
                !(!this.isEmpty(move.getEnd() - 1) && 
                this.getPieceAt(move.getEnd() - 1).getType() == 'P' && 
                new ChessMove(afterPass, move.getEnd() - 1, enPassantValue).isLegal())) {

                this.stateArray[EN_PASSANT] = -1;
            }
        }
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

    public CompressedPosition getCompressedPosition() {
        return this.compressedPosition;
    }

    public ArrayList<CompressedPosition> getReachedPositions() {
        return this.reachedPositions;
    }

    /**
     * Returns a copy of this position after a specified alteration is made.
     */
    public ChessPosition afterAlteration(int start, int end) {
        ChessPosition position = this.copy();
        position.makeAlteration(start, end);
        return position;
    }

    /**
     * Returns a copy of this position after a specified move is made. The move is assumed to be legal.
     * @param move the move to be made
     * @return a ChessPosition representing the position after the move is made
     */
    public ChessPosition afterMove(ChessMove move) {
        ChessPosition position = this.copy();
        position.makeMove(move);
        return position;
    }

    /**
     * Passes the turn and advances the game.
     */
    public void advanceGame() {
        passTurn();
        if (this.stateArray[TO_MOVE] == 0) {
            // to do: increment move counter here.
        }
    }

    /**
     * Changes the color to move without advancing the game.
     * @return the new position after the turn has been passed
     */
    public ChessPosition passTurn() {
        this.stateArray[TO_MOVE] = -1 * (this.stateArray[TO_MOVE] - 1);
        return this;
    }

    /**
     * Returns a copy of this position where the opposite color is to move without changing
     * the state of the original position.
     * @return the new position after the turn has been passed
     */
    /*
    public ChessPosition afterPass() {
        ChessPosition position = this.copy();
        return position.passTurn();
    }
    */

    /**
     * Iterates over all pieces in the position and returns a value which should be quantifying some kind of total.
     */
    public float iterateOverPieces(Accumulator acc) {
        float v = 0;
        for (int i = 0; i < this.positionArray.length; i++) {
            ChessPiece piece = this.getPieceAt(i);
            if (piece != null)
            v = acc.update(v, this, piece, i);
        }
        return v;
    }

    public int countPieces() {
        return (int) iterateOverPieces((v, pos, p, s) -> v + 1);
    }

    public int getMaterialEvaluation() {
        return (int) iterateOverPieces((v, pos, p, s) -> v + p.getValue());
    }

    /**
     * Returns a reference to this position's positionArray.
     */
    public int[] getPositionArray() {
        return this.positionArray;
    }

    /**
     * Returns a reference to this position's stateArray.
     */
    public int[] getStateArray() {
        return this.stateArray;
    }

    public char colorToMove() {
        if (this.stateArray[TO_MOVE] == 0) {
            return 'w';
        } else {
            return 'b';
        }
    }

    public int getEnPassantOpportunity() {
        return this.stateArray[EN_PASSANT];
    }

    public boolean hasCastlingRights(char color, char side) {
        if (color == 'w') {
            if (side == 'K') {
                return this.stateArray[W_K_CASTLING_RIGHTS] == 1;
            } else {
                return this.stateArray[W_Q_CASTLING_RIGHTS] == 1;
            }
        } else {
            if (side == 'K') {
                return this.stateArray[B_K_CASTLING_RIGHTS] == 1;
            } else {
                return this.stateArray[B_Q_CASTLING_RIGHTS] == 1;
            }
        }
    }

    public boolean castlingRightsExist() {
        return this.stateArray[W_K_CASTLING_RIGHTS] == 1 ||
               this.stateArray[W_Q_CASTLING_RIGHTS] == 1 ||
               this.stateArray[B_K_CASTLING_RIGHTS] == 1 ||
               this.stateArray[B_Q_CASTLING_RIGHTS] == 1;
    }

    public void removeCastlingRights(char color, char side) {
        if (color == 'w') {
            if (side == 'K') {
                this.stateArray[W_K_CASTLING_RIGHTS] = 0;
            } else {
                this.stateArray[W_Q_CASTLING_RIGHTS] = 0;
            }
        } else {
            if (side == 'K') {
                this.stateArray[B_K_CASTLING_RIGHTS] = 0;
            } else {
                this.stateArray[B_Q_CASTLING_RIGHTS] = 0;
            }
        }
    }

    public void removeCastlingRights(char color) {
        if (color == 'w') {
            this.stateArray[W_K_CASTLING_RIGHTS] = 0;
            this.stateArray[W_Q_CASTLING_RIGHTS] = 0;
        } else {
            this.stateArray[B_K_CASTLING_RIGHTS] = 0;
            this.stateArray[B_Q_CASTLING_RIGHTS] = 0;
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
