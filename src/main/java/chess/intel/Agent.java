package chess.intel;

import chess.logic.ChessPosition;
import chess.logic.ChessMove;

/**
 * Class that represents a computer player.
 */
public class Agent {
    private char color;
    private Strategy strategy;

    public Agent(char color, Strategy s) {
        this.color = color;
        this.strategy = s;
    }

    public ChessMove findBestMove(ChessPosition position) {
        
    }

    private ChessMove minimax(ChessPosition position, char color, int depth) {
        if (color == this.color) {
            

            
        } else {

        }
    }
}
