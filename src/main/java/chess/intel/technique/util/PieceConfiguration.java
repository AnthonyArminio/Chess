package chess.intel.technique.util;

import java.util.HashSet;
import java.util.Set;

import chess.logic.ChessPiece;
import chess.logic.ChessPosition;
import chess.logic.util.GridMath;

/**
 * Represents a configuration of pieces as a String. '/' represents a new rank, '?' represents any piece, '+' represents any white piece, '-' represents any black piece, '*' represents any piece or 
 * an empty square, and '0' represents an empty square. There must be exactly one character with an '@' symbol before it representing the focus piece.
 * PieceConfiguration
 */
public class PieceConfiguration {

    private final String configuration;
    private int windowWidth;
    private int windowHeight;
    private char focusPiece;
    // the following are 0-indexed
    private int relativeRow;
    private int relativeCol;
    
    public PieceConfiguration(String configurationString) {
        this.configuration = configurationString;
        initialize(configurationString);
        System.out.printf("Window Width/Height: %d/%d. Relative Row/Col: %d/%d\n", this.windowWidth, this.windowHeight, this.relativeRow, this.relativeCol);
    }

    /**
     * Initializes the accessible fields based on the given configuration string.
     * @param configurationString
     * @throws IllegalArgumentException if the configuration string is not formatted correctly.
     */
    private void initialize(String s) {
        boolean atFound = false;
        boolean focusPieceFound = false;
        int width = 0;
        int height = 1;
        int currentWidth = 0;
        for (int cIdx = 0; cIdx < s.length(); cIdx++) {
            if ("pbnrqkPBNRQK/*?+-0@".indexOf(s.charAt(cIdx)) < 0) {
                throw new IllegalArgumentException("PieceConfiguration: Invalid character found in configuration string.");
            }
            if (s.charAt(cIdx) == '/') {
                if (width <= 0) {
                    width = currentWidth;
                } else if (currentWidth != width) {
                    throw new IllegalArgumentException("PieceConfiguration: Window width must be consistent for each row.");
                }
                height++;
                currentWidth = 0;
            } else if (s.charAt(cIdx) == '@') {
                if (atFound) {
                    throw new IllegalArgumentException("PieceConfiguration: Misplaced at sign found in configuration string.");
                }
                atFound = true;
            } else {
                if (atFound && !focusPieceFound) {
                    this.focusPiece = s.charAt(cIdx);
                    this.relativeRow = height - 1;
                    this.relativeCol = currentWidth;
                    focusPieceFound = true;
                }
                currentWidth++;
            }

        }
        this.windowWidth = width;
        this.windowHeight = height;
        if (!focusPieceFound) {
            throw new IllegalArgumentException("PieceConfiguration: No valid focus piece found in the configuration string. Use an at sign '@' to denote the focus piece.");
        }
    }

    public boolean compare(ChessPosition position, int square) {
        ChessPiece piece = position.getPieceAt(square);
        char observedFocus = pieceToChar(piece);
        
        if (isValidPiece(observedFocus, this.focusPiece) && isInBounds(square)) {
            int homeFile = GridMath.getFile(square) - relativeCol;
            int file = homeFile;
            int rank = GridMath.getRank(square) + relativeRow;
            for (int cIdx = 0; cIdx < this.configuration.length(); cIdx++) {
                char c = this.configuration.charAt(cIdx);
                if (c == '/') {
                    file = homeFile;
                    rank--;
                } else if (c != '@') {
                    if (c != '*' && !isValidPiece(pieceToChar(position.getPieceAt(GridMath.index(file, rank))), c)) {
                        return false;
                    }
                    file++;
                }
            }
            return true;
        }
        return false;
    }

    private char pieceToChar(ChessPiece piece) {
        if (piece == null) {
            return '0';
        }
        if (piece.getColor() == 'w') {
            return Character.toUpperCase(piece.getType());
        }
        return Character.toLowerCase(piece.getType());
    }

    private boolean isValidPiece(char observedPiece, char expectedPiece) {
        if (expectedPiece == '*') {
            return true;
        }
        if (expectedPiece == '?') {
            return observedPiece != '0';
        }
        if (expectedPiece == '+') {
            return observedPiece >= 'A' && observedPiece <= 'Z';
        }
        if (expectedPiece == '-') {
            return observedPiece >= 'a' && observedPiece <= 'z';
        }
        return observedPiece == expectedPiece;
    }

    private boolean isInBounds(int square) {
        int file = GridMath.getFile(square);
        int rank = GridMath.getRank(square);
        if (file - this.relativeCol < 1 || file - this.relativeCol + this.windowWidth - 1 > 8) {
            return false;
        }
        if (rank + this.relativeRow > 8 || rank + this.relativeRow - this.windowHeight - 1 < 1) {
            return false;
        }
        return true;
    }

    public String getConfigurationString() {
        return this.configuration;
    }

    public char getFocusPiece() {
        return this.focusPiece;
    }

    public int getWindowWidth() {
        return this.windowWidth;
    }

    public int getWindowHeight() {
        return this.windowHeight;
    }

    public static Set<PieceConfiguration> buildSet(String... confStrings) {
        Set<PieceConfiguration> confSet = new HashSet<>();
        for (String s : confStrings) {
            confSet.add(new PieceConfiguration(s));
        }
        return confSet;
    }
}
