package chess.intel.training;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import com.google.gson.Gson;

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

    /**
     * Returns a Generation object recovered from the contents of a JSON-formatted file.
     * @param jsonFile The file to parse
     * @return A Generation object
     * @throws java.io.IOException if an error occurs while reading the file.
     */
    public static Generation getFromJson(File jsonFile) throws java.io.IOException {
        jsonFile.setReadable(true);
        FileReader reader = new FileReader(jsonFile);

        String jsonString = "";
        char[] buffer = new char[50];
        while(reader.read(buffer) > 0) {
            jsonString += new String(buffer);
        }

        reader.close();

        Gson gson = new Gson();
        return gson.fromJson(jsonString, Generation.class);

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

    /**
     * Writes the JSON String representing this Generation to a specified File.
     * @param file The file to write to
     * @throws IOException if an error occurs while writing the file.
     */
    public void write(File file) throws java.io.IOException {
        file.setWritable(true);
        FileWriter writer = new FileWriter(file);

        Gson gson = new Gson();
        String jsonString = gson.toJson(this);

        writer.write(jsonString);
        writer.close();
    }
}
