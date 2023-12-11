package App;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class DictionaryController {

    @FXML
    private Label label;
    @FXML
    private Stage stage;
    @FXML
    private Scene scene;
    @FXML
    private Parent root;
    @FXML
    private Label mainLabel;

    public Text text;

    public void getWordInfo() {
        try {
            String word = getRandomWord();
            URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            System.out.println(status);
            if (status == 200) {
                BufferedReader in = new BufferedReader(
                        new java.io.InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                    System.out.println(inputLine);
                }
                in.close();
                con.disconnect();
                System.out.println("Response: " + content.toString());
                // parse JSON
                
                mainLabel.setText(String.valueOf(status));
            } else {
                System.out.println("Error");
            }
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL");
        } catch (IOException e) {
            // mainLabel.setText(e.getMessage());
            System.out.println(e);
        }
    }

    public String getRandomWord() {
        try {
            File myObj = new File("./src/App/dictionaries.txt");
            Scanner myReader = new Scanner(myObj);
            List<String> words = new ArrayList<String>();
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                words.add(data);
            }
            int randomIndex = (int) (Math.random() * words.size());
            String randomWord = words.get(randomIndex);
            myReader.close();
            return randomWord;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return "hello";
    }

    public void returnToMenu(ActionEvent event) throws Exception {
        mainLabel.setText("How are you today?\nLet's start learning!");
    }

    public void switchToGameWordle(ActionEvent event) throws Exception {
        getWordInfo();
    }

    public void switchToGameMultipleChoice(ActionEvent event) throws Exception {
        getWordInfo();
    }
}