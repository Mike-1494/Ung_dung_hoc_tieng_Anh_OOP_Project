package App;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
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
    @FXML
    private GridPane wordleGrid;
    @FXML
    public Text text;
    @FXML
    private Label messagLabel;
    @FXML
    private Button submitButton;

    private final int wordLength = 5;
    private final int numRow = 6;

    public void returnToMenu(ActionEvent event) throws Exception {
        resetScene(event);
        mainLabel.setText("How are you today?\nLet's start learning!");
    }

    public void resetScene(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        dataStore.currentColumn = 0;
        dataStore.currentRow = 0;
        dataStore.state = 0;
        dataStore.answer = "";

        wordleGrid.getStyleClass().clear();
        wordleGrid.setGridLinesVisible(false);
        wordleGrid.getChildren().clear();
    }

    public void submitAnswer(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        String answer = dataStore.answer;
        int count = 0;
        for (int i = 0; i < wordLength; i++) {
            Node node = wordleGrid.getChildren().get(i * numRow + dataStore.currentRow);

            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;
                Text text = (Text) stackPane.getChildren().get(0);
                int color = 0;
                for (int j = 0; j < answer.length(); j++)
                    if (String.valueOf(answer.charAt(j)).toLowerCase()
                            .equals(text.getText().toLowerCase())) {
                        if (i == j) {
                            color = 2;
                        } else {
                            color = Math.max(color, 1);
                        }
                        break;
                    }
                if (color == 0)
                    stackPane.setStyle("-fx-background-color: grey;");
                if (color == 1) {
                    stackPane.setStyle("-fx-background-color: yellow;");
                    answer = answer.replaceFirst(text.getText().toLowerCase(), "_");
                }
                if (color == 2) {
                    count++;
                    stackPane.setStyle("-fx-background-color: green;");
                    answer = answer.replaceFirst(text.getText().toLowerCase(), "_");
                }
            }
        }
        if (count == wordLength) {
            mainLabel.setText("Correct!");
        } else {
            mainLabel.setText("Wrong!");
        }
        dataStore.currentColumn = 0;
        dataStore.currentRow++;
    }

    public void handleKeyboardInput(KeyEvent event) throws Exception {
        String key = event.getCode().toString();
        DataStore dataStore = DataStore.getInstance();
        
        if (key.length() != 1) {
            if (key == "BACK_SPACE" && dataStore.state == 2 && dataStore.currentColumn > 0) {
                Node node = wordleGrid.getChildren().get((dataStore.currentColumn - 1) * numRow + dataStore.currentRow);

                if (node instanceof StackPane) {
                    StackPane stackPane = (StackPane) node;
                    Text text = (Text) stackPane.getChildren().get(0);
                    text.setText("_");
                }
                dataStore.currentColumn--;
            }
            return;
        }
        Character ch = key.charAt(0);
        if (Character.isLetter(ch) && dataStore.currentColumn < wordLength && dataStore.currentRow < numRow
                && dataStore.state == 2) {

            Text text = (Text) ((StackPane) wordleGrid.getChildren()
                    .get(dataStore.currentColumn * numRow + dataStore.currentRow)).getChildren().get(0);
            text.setText(ch.toString());
            dataStore.currentColumn++;
        }
    }

    public void switchToGameMultipleChoice(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        resetScene(event);
        mainLabel.setText("Multiple Choice");
        dataStore.state = 1;
    }

    public void switchToGameWordle(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        resetScene(event);
        dataStore.state = 2;
        try {
            String randomWord = DataStore.getRandomWord(wordLength);
            dataStore.wordInfo = DataStore.getWordInfo(randomWord);
            dataStore.answer = dataStore.wordInfo.word;
        } catch (Exception e) {
            dataStore.wordInfo = DataStore.getWordInfo("hello");
            dataStore.answer = "hello";
        }
        mainLabel.setText("Wordle");
        for (int col = 0; col < wordLength; col++)
            for (int row = 0; row < numRow; row++) {
                StackPane stackPane = new StackPane();
                Text text = new Text();
                text.setText("_");
                stackPane.getChildren().add(text);
                wordleGrid.add(stackPane, col, row);
            }
        // show grid border
        wordleGrid.setGridLinesVisible(true);
        // show grid
    }

}