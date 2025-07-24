package chess.intel.training;

import chess.application.ChessGame;

public class TrainingGame extends ChessGame {

    private Trainee whitePlayer;
    private Trainee blackPlayer;

    public TrainingGame(Trainee whitePlayer, Trainee blackPlayer) {
        super(whitePlayer, blackPlayer);
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
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
