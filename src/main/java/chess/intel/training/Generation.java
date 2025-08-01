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

    private static final Gson GSON = new GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .registerTypeAdapter(Strategy.class, new JsonStrategyAdapter())
        .registerTypeAdapter(InputStrategy.class, new JsonInputStrategyAdapter())
        .create();

    @Expose private int generationNumber;
    @Expose private int size;
    private ArrayList<Trainee> roster;

    /**
     * Creates the initial Generation based on the default size and default Trainee constructor.
     */
    public Generation(int size) {
        this.generationNumber = 0;
        this.size = size;
        this.roster = new ArrayList<Trainee>();
        for (int i = 0; i < this.size; i++) {
            this.roster.add(new Trainee(2));
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
     * Returns a Generation object recovered from the contents of a directory of JSON-formatted files.
     * @param directoryPath The directory to parse
     * @return A Generation object
     * @throws java.io.IOException if an error occurs while reading the files.
     */
    public static Generation getFromJsonDirectory(String directoryPath) throws java.io.IOException {

        final int GEN_BUFFER_SIZE = 100;
        final int TRAINEE_BUFFER_SIZE = 50000;

        File genFile = new File(directoryPath + TrainingManager.GEN_METADATA_FILENAME);

        Generation empty = GSON.fromJson(readFile(genFile, GEN_BUFFER_SIZE), Generation.class);

        int size = empty.getSize();
        int generationNumber = empty.getGenerationNumber();

        ArrayList<Trainee> roster = new ArrayList<Trainee>();
        for (int t = 0; t < size; t++) {
            File tFile = new File(directoryPath + TrainingManager.TRAINEE_FILENAME + t);

            roster.add(GSON.fromJson(readFile(tFile, TRAINEE_BUFFER_SIZE), Trainee.class));
        }

        return new Generation(roster, generationNumber);

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
        DataMath.<Trainee>quickSort(this.roster, true);
    }

    /**
     * Returns a String representation of the contents of a specified File.
     * @param file The file to read
     * @param bufferSize The buffer size to use
     * @return The String representation of the File.
     * @throws java.io.IOException
     */
    private static String readFile(File file, int bufferSize) throws java.io.IOException {
        file.setReadable(true);
        FileReader reader = new FileReader(file);

        String string = "";
        char[] buffer = new char[bufferSize];
        int bytesRead = 0;
        do {
            bytesRead = reader.read(buffer);

            if (bytesRead > 0) {
                if (bytesRead < bufferSize) {
                    char[] truncBuffer = new char[bytesRead];
                    for (int c = 0; c < bytesRead; c++) {
                        truncBuffer[c] = buffer[c];
                    }
                    string += new String(truncBuffer);
                } else {
                    string += new String(buffer);
                }
            }

        } while(bytesRead > 0);

        reader.close();

        return string;
    }

    /**
     * Writes the JSON String representing this Generation as well as each Trainee to files in a specified directory.
     * @param directoryPath The directory to write to
     * @throws IOException if an error occurs while writing the file.
     */
    public void write(String directoryPath) throws java.io.IOException {

        // write this generation
        File genFile = new File(directoryPath + TrainingManager.GEN_METADATA_FILENAME);

        genFile.setWritable(true);
        FileWriter writer = new FileWriter(genFile);

        String jsonString = GSON.toJson(this);

        writer.write(jsonString);
        writer.close();

        // write trainees in separate files
        for (int t = 0; t < this.size; t++) {
            File tFile = new File(directoryPath + TrainingManager.TRAINEE_FILENAME + t);

            tFile.setWritable(true);
            writer = new FileWriter(tFile);

            jsonString = GSON.toJson(this.roster.get(t));

            writer.write(jsonString);
            writer.close();
        }
    }
}
