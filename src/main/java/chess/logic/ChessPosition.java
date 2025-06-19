package chess.logic;

/**
 * Represents a chess position. Contains information about where each piece is in a compact form.
 */
public class ChessPosition {

    // The current state of the board represented as a list of 68 integers. The first 64
    // represent the pieces at each square starting from the bottom-left. positionArray[64]
    // represents the index where en passant is available, or -1 otherwise.
    // positionArray[65 & 66] represent castling rights for white and black, respectively. 
    // positionArray[67] represents whose turn it is (-1 for black, 1 for white).
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
                                  -1, 1, 1, 1};

        this.positionArray = startingPosition;
    }
}
