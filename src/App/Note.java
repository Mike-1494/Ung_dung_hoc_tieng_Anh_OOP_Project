package App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Note {
    private String note;

    public String getNote() {
        return this.note;
    }

    public void setNote(String s) {
        this.note = s;
    }

    public void loadNotesFromFile() {
        File file = new File("task_descriptions.txt");

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(System.lineSeparator());
                }
                System.out.println("Notes loaded from file: " + file.getAbsolutePath());
                this.note = stringBuilder.toString();
            } catch (IOException e) {
                System.out.println("Error loading notes from file: " + e.getMessage());
            }
        }
    }
}
