package chess.intel.training;

import chess.application.ChessGame;

public class TrainingGame extends ChessGame {

    private Trainee whitePlayer;
    private Trainee blackPlayer;
    private int maxMoves;

    public TrainingGame(Trainee whitePlayer, Trainee blackPlayer, int maxMoves) {
        super(whitePlayer, blackPlayer, false);
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
    }

    @Override protected boolean handleGameEnd() {
        if (this.getMoveNumber() > this.maxMoves) {
            onDraw(-1);
            return true;
        } else {
            return super.handleGameEnd();
        }
    }

    @Override protected void onWinForWhite() {
        this.whitePlayer.reward();
        this.blackPlayer.punish();
    }

    @Override protected void onWinForBlack() {
        this.blackPlayer.reward();
        this.whitePlayer.punish();
    }

    @Override protected void onDraw(int code) {

    }
}
