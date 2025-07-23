package chess.intel.training;

/**
 * Class that represents a group of Trainees fit to play against each other to train.
 */
public class Generation {
    private int size;
    private Trainee[] roster;

    public Generation(int size) {
        this.size = size;
        this.roster = new Trainee[size];
    }
}
