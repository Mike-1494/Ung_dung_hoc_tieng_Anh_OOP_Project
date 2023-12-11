package App;

import java.util.Arrays;
import java.util.Collections;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private Label messageLabel;
    @FXML
    private Button submitBtn;
    @FXML
    private Label questionLabel;
    @FXML
    private GridPane multipleChoiceGrid;

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
        submitBtn.setVisible(false);
        messageLabel.setVisible(false);
        questionLabel.setVisible(false);
        multipleChoiceGrid.setVisible(false);
        multipleChoiceGrid.getChildren().clear();
        submitBtn.setText("Submit");
        submitBtn.setOnAction(null);
        submitBtn.setVisible(false);
    }

    public void submitAnswer(ActionEvent event) throws Exception {
        if (submitBtn.getText() == "Try again") {
            switchToGameWordle(event);
            return;
        }
        DataStore dataStore = DataStore.getInstance();
        String answer = dataStore.answer;
        int count = 0;
        String word = "";
        for (int i = 0; i < wordLength; i++) {
            Node node = wordleGrid.getChildren().get(i * numRow + dataStore.currentRow);

            if (node instanceof StackPane) {
                StackPane stackPane = (StackPane) node;
                Text text = (Text) stackPane.getChildren().get(0);
                word += text.getText();
            }
        }

        if (dataStore.findWord(word.toLowerCase()) == false) {
            messageLabel.setVisible(true);
            messageLabel.setText(word + " is not in word list! Try again.");
            return;
        }

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
        dataStore.currentColumn = 0;
        dataStore.currentRow++;
        if (count == wordLength) {
            messageLabel.setVisible(true);
            messageLabel.setText("Correct!");
            submitBtn.setText("Try again");
        } else {
            if (dataStore.currentRow == numRow) {
                messageLabel.setVisible(true);
                messageLabel.setText("The answer is " + dataStore.answer + "! Try again.");
                submitBtn.setText("Try again");
            }
        }
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

    private void addAnswer(String answer) {
        StackPane stackPane = new StackPane();
        Button button = new Button();
        int nChildren = multipleChoiceGrid.getChildren().size();
        char ch = (char) ('A' + nChildren);
        button.setText(ch + ". " + answer);
        stackPane.getChildren().add(button);
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DataStore dataStore = DataStore.getInstance();
                String word = button.getText().split(" ")[1];
                String answer = dataStore.answer;

                if (word.toLowerCase().equals(answer.toLowerCase())) {
                    messageLabel.setVisible(true);
                    messageLabel.setText("Correct!");
                    submitBtn.setText("Next question -->");
                    submitBtn.setVisible(true);
                    submitBtn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                switchToGameMultipleChoice(event);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    messageLabel.setVisible(true);
                    messageLabel.setText("Wrong answer! The answer is " + dataStore.answer);
                }
            }
        });
        multipleChoiceGrid.add(stackPane, 0, nChildren);
    }

    public void switchToGameMultipleChoice(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        resetScene(event);
        mainLabel.setText("Multiple Choice");
        dataStore.wordInfo = dataStore.getWordInfo();
        dataStore.answer = dataStore.wordInfo.word;
        String example = dataStore.wordInfo.getExample();

        dataStore.answer = example.split(" ")[(int) (Math.random() * example.split(" ").length)];
        // remove special characters
        dataStore.answer = dataStore.answer.replaceAll("[^a-zA-Z0-9]", "");

        questionLabel.setText(example.replaceFirst(dataStore.answer, "______"));
        questionLabel.setVisible(true);
        String[] answers = { dataStore.getRandomWord(),
                dataStore.answer,
                dataStore.getRandomWord(),
                dataStore.getRandomWord() };

        Collections.shuffle(Arrays.asList(answers));

        for (String answer : answers) {
            addAnswer(answer);
        }
        multipleChoiceGrid.setVisible(true);

        dataStore.state = 1;
    }

    public void switchToGameWordle(ActionEvent event) throws Exception {
        DataStore dataStore = DataStore.getInstance();
        resetScene(event);
        dataStore.state = 2;
        dataStore.wordInfo = dataStore.getWordInfo(wordLength);
        dataStore.answer = dataStore.wordInfo.word;
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
        submitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    submitAnswer(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        submitBtn.setVisible(true);
        // show grid
    }

}