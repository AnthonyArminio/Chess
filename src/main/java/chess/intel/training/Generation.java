package chess.intel.training;

import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import chess.intel.Agent;

import chess.intel.util.DataMath;

import chess.intel.strategy.Strategy;
import chess.intel.util.json.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Class that represents a group of Trainees fit to play against each other to train.
 */
public class Generation {

    @Expose private int generationNumber;
    @Expose private int size;
    @Expose private ArrayList<Trainee> roster;

    /**
     * Creates the initial Generation based on the default size and default Trainee constructor.
     */
    public Generation(int size) {
        this.generationNumber = 0;
        this.size = size;
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
        final int BUFFER_SIZE = 500000;

        jsonFile.setReadable(true);
        FileReader reader = new FileReader(jsonFile);

        String jsonString = "";
        char[] buffer = new char[BUFFER_SIZE];
        int bytesRead = 0;
        do {
            bytesRead = reader.read(buffer);

            if (bytesRead > 0) {
                if (bytesRead < BUFFER_SIZE) {
                    char[] truncBuffer = new char[bytesRead];
                    for (int c = 0; c < bytesRead; c++) {
                        truncBuffer[c] = buffer[c];
                    }
                    jsonString += new String(truncBuffer);
                } else {
                    jsonString += new String(buffer);
                }
            }

        } while(bytesRead > 0);

        reader.close();

        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(Strategy.class, new JsonStrategyAdapter())
            .registerTypeAdapter(InputStrategy.class, new JsonInputStrategyAdapter())
            .create();
        return gson.fromJson(jsonString, Generation.class);

    }

    public void incrementGenerationNumber() {
        this.generationNumber++;
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

    public void sort() {
        DataMath.<Trainee>quickSort(this.roster, false);
    }

    /**
     * Writes the JSON String representing this Generation to a specified File.
     * @param file The file to write to
     * @throws IOException if an error occurs while writing the file.
     */
    public void write(File file) throws java.io.IOException {
        file.setWritable(true);
        FileWriter writer = new FileWriter(file);

        Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
        String jsonString = gson.toJson(this);

        writer.write(jsonString);
        writer.close();
    }
}
