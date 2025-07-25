package chess.intel.training;

import java.util.ArrayList;

/**
 * Class that represents a group of Trainees fit to play against each other to train.
 */
public class Generation {
    private int size;
    private ArrayList<Trainee> roster;

    public Generation(int size) {
        this.size = size;
        this.roster = new ArrayList<Trainee>();
    }

    public Generation(ArrayList<Trainee> roster) {
        this.size = roster.size();
        this.roster = roster;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<Trainee> getRoster() {
        return this.roster;
    }
}
