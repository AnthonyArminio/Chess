package chess.intel.strategy;

import chess.logic.ChessMove;
import chess.logic.util.ChessLogic;

import java.util.ArrayList;

public class DetailedEvaluation extends Evaluation {

    public static final DetailedEvaluation CHECKMATE_FOR_WHITE = new DetailedEvaluation(Float.POSITIVE_INFINITY, 0);
    public static final DetailedEvaluation CHECKMATE_FOR_BLACK = new DetailedEvaluation(Float.NEGATIVE_INFINITY, 0);
    public static final DetailedEvaluation DRAW = new DetailedEvaluation(0);

    private ArrayList<ChessMove> movePath;
    
    public DetailedEvaluation(float value) {
        super(value);
        this.movePath = new ArrayList<ChessMove>();
    }

    public DetailedEvaluation(float value, int clock) {
        super(value, clock);
        this.movePath = new ArrayList<ChessMove>();
    }

    public DetailedEvaluation(float value, int clock, ArrayList<ChessMove> movePath) {
        super(value, clock);
        this.movePath = new ArrayList<ChessMove>();
        for (ChessMove move : movePath) {
            this.movePath.add(move);
        }
    }

    public DetailedEvaluation step(ChessMove move) {
        int newClock = this.clock;
        if (this.clock >= 0) {
            newClock++;
        }
        DetailedEvaluation newEval = new DetailedEvaluation(this.value, newClock, this.movePath);
        newEval.addMove(move);
        return newEval;
    }

    public void addMove(ChessMove move) {
        this.movePath.add(move);
    }

    public ArrayList<ChessMove> getMovePath() {
        return this.movePath;
    }

    public String pathString(int startingTurn, char startingColor) {

        int turnNum = startingTurn;
        char color = startingColor;

        String base = "";
        if (color == 'b') {
            base += "" + turnNum + ". ...";
        }

        for (ChessMove move : this.movePath.reversed()) {

            if (color == 'w') {
                base += " " + turnNum + ".";
            } else {
                turnNum++;
            }
            base += " " + move.getNotation();
            color = ChessLogic.opponentOf(color);
        }

        return base;
    }
}
