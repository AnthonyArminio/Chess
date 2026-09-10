package chess.intel.training;

import chess.application.ChessGame;

public class TrainingGame extends ChessGame {

    private Trainee whitePlayer;
    private Trainee blackPlayer;
    private int maxMoves;
    // Keeps track of the average material evaluation of the position to roughly determine the
    // better Trainee in the case of a draw.
    private float averageMaterial; // TO DO

    public TrainingGame(Trainee whitePlayer, Trainee blackPlayer, int maxMoves) {
        super(whitePlayer, blackPlayer, false);
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.maxMoves = maxMoves;
        this.averageMaterial = 0;
    }

    @Override protected void advanceGame() {
        this.averageMaterial += (this.getPosition().iterateOverPieces((v, pos, p, s) -> v + p.getValue()) - this.averageMaterial) / (float) this.getPlyNumber();
        super.advanceGame();
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
        if (code == -1) {
            if (this.averageMaterial > 0) {
                this.whitePlayer.reward(this.averageMaterial);
                this.blackPlayer.punish(this.averageMaterial);
            } else if (this.averageMaterial < 0) {
                this.blackPlayer.reward(-1 * this.averageMaterial);
                this.whitePlayer.punish(-1 * this.averageMaterial);
            }
        }
    }
}
