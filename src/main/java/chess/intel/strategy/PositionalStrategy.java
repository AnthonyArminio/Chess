package chess.intel.strategy;

import chess.intel.strategy.util.PositionalWeights;
import chess.logic.ChessPosition;

public class PositionalStrategy extends MaterialisticStrategy {

    private PositionalWeights weights;

    public PositionalStrategy() {
        this.weights = new PositionalWeights();
    }

    public PositionalStrategy(PositionalWeights weights) {
        this.weights = weights;
    }

    @Override public Evaluation subjectiveEvaluate(ChessPosition position) {

        return new Evaluation(position.getMaterialEvaluation() + weigh(position));
    }

    private float weigh(ChessPosition position) {
        return position.iterateOverPieces((v, pos, p, s) -> v + this.weights.getWeightMap(p)[s]);
    }

    public PositionalWeights getWeights() {
        return this.weights;
    }

    public void setWeights(PositionalWeights newWeights) {
        this.weights = newWeights;
    }
}
