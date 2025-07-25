package chess.intel.training;

import java.util.ArrayList;

/**
 * Class that represents a group of Trainees fit to play against each other to train.
 */
public class Generation {
    private final int DEFAULT_SIZE = 80;

    private int generationNumber;
    private int size;
    private ArrayList<Trainee> roster;

    /**
     * Creates the initial Generation based on the default size and default Trainee constructor.
     */
    public Generation() {
        this.generationNumber = 0;
        this.size = DEFAULT_SIZE;
        this.roster = new ArrayList<Trainee>();
        for (int i = 0; i < this.size; i++) {
            this.roster.add(new Trainee());
        }
    }

    public Generation(ArrayList<Trainee> roster, int generationNumber) {
        this.generationNumber = generationNumber;
        this.size = roster.size();
        this.roster = new ArrayList<Trainee>();
        for (Trainee trainee : roster) {
            this.roster.add(trainee);
        }
    }

    public int getGenerationNumber() {
        return this.generationNumber;
    }

    public int getSize() {
        return this.size;
    }

    public ArrayList<Trainee> getRoster() {
        return this.roster;
    }
}
