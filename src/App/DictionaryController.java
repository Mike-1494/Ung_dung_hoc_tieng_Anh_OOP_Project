package App;
import Base.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    @FXML
    private TabPane tab;
    @FXML
    private Button savenote;
    @FXML
    private TextArea note;
    @FXML
    private TableView<Task> todolist;
    @FXML
    private TableColumn<Task, Integer> priority;
    @FXML
    private TableColumn<Task, String> taskName; 
    @FXML 
    private TableColumn<Task, String> taskProgress;
    @FXML
    private TableView<Word> wordlist;
    @FXML
    private TableColumn<Word, String> word_tg;
    @FXML
    private TableColumn<Word, String> mean; 

    private ObservableList<Task> taskList;
    private ObservableList<Word> WordList;

    @FXML
    private TextField searchbar;
    @FXML
    private Button searchbutton;
    @FXML
    private Label word;
    @FXML 
    private Label phonetics;
    @FXML 
    private Label meaning;
    @FXML
    private Button saveword;

    private DictionaryAPI dictionaryAPI = new DictionaryAPI();
    private Note Note = new Note();
    private final int wordLength = 5;
    private final int numRow = 6;

    @FXML
    public void initialize() {
        searchbutton.setOnAction(e -> searchWord());
        saveword.setOnAction(e -> saveWord());
        savenote.setOnAction(e -> saveNoteApp());
        Note.loadNotesFromFile();

        taskList = FXCollections.observableArrayList();
        priority.setCellValueFactory(new PropertyValueFactory<Task, Integer>("priority"));
        taskName.setCellValueFactory(new PropertyValueFactory<Task, String>("taskName"));
        taskProgress.setCellValueFactory(new PropertyValueFactory<>("taskProgress"));
        taskProgress.setCellFactory(TextFieldTableCell.forTableColumn()); // Enable editing
        taskProgress.setOnEditCommit(event -> {
            Task task = event.getTableView().getItems().get(event.getTablePosition().getRow());
            task.setTaskProgress(event.getNewValue());
            updateTasktoMySQL(task);
        });    

        WordList = FXCollections.observableArrayList();
        word_tg.setCellValueFactory(new PropertyValueFactory<Word, String>("word_target"));
        mean.setCellValueFactory(new PropertyValueFactory<Word, String>("word_explain"));
        mean.setCellFactory(TextFieldTableCell.forTableColumn()); // Enable editing
        mean.setOnEditCommit(event -> {
            Word word = event.getTableView().getItems().get(event.getTablePosition().getRow());
            word.setWord_explain(event.getNewValue());
            updateWordtoMySQL(word);
        });

        wordlist.setItems(WordList);
        wordlist.setEditable(true);
        todolist.setEditable(true);
        todolist.setItems(taskList);
        loadTasksFromMySQL();
        note.setText(Note.getNote());
        loadWordsFromMySQL();
    }


    public void returnToMenu(ActionEvent event) throws Exception {
        resetScene(event);
        tab.setVisible(true);
        mainLabel.setText("How are you today?\nLet's start learning!");
        wordlist.getItems().clear();
        loadWordsFromMySQL();
        todolist.getItems().clear();
        loadTasksFromMySQL();
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
        tab.setVisible(false);
        multipleChoiceGrid.setVisible(false);
        multipleChoiceGrid.getChildren().clear();
        submitBtn.setText("Submit");
        submitBtn.setOnAction(null);
        submitBtn.setVisible(false);
    }
    /**
     * Wordle
     * @param event
     * @throws Exception
     */
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

    /**
     * Multiple choice
     * @param answer
     */
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
        tab.setVisible(false);
        mainLabel.setText("Multiple Choice");
        dataStore.wordInfo = dataStore.getWordInfo();
        dataStore.answer = dataStore.wordInfo.word;
        String example = dataStore.wordInfo.getExample();

        dataStore.answer = example.split(" ")[(int) (Math.random() * example.split(" ").length)];
        // remove special characters
        dataStore.answer = dataStore.answer.replaceAll("[^a-zA-Z0-9]", "");

        questionLabel.setText(example.replaceFirst(dataStore.answer, "______"));
        questionLabel.setWrapText(true);
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

    public void saveNoteApp() {
        String s = note.getText();
        File file = new File("task_descriptions.txt");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(s);
            System.out.println("Note saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving note to file: " + e.getMessage());
        }
    }

    public void switchtoTasklist(ActionEvent event) throws Exception {
        resetScene(event);
        mainLabel.setText("Notes");
        tab.setVisible(true);
    }

    private void insertTaskToMySQL(Task task) {
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";
    
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT INTO Tasklist (priority, task_name, progress) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setInt(1, task.getPriority());
                insertStmt.setString(2, task.getTaskName());
                insertStmt.setString(3, task.getTaskProgress());
                insertStmt.executeUpdate();
            }
    
            System.out.println("Task saved to MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving task to MySQL: " + e.getMessage());
        }
    }
    
    private void updateTasktoMySQL(Task task) {
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";
    
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "UPDATE Tasklist SET progress = ? WHERE task_name = ?";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, task.getTaskProgress());
                insertStmt.setString(2, task.getTaskName());

                insertStmt.executeUpdate();
            }
    
            System.out.println("Task saved to MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving task to MySQL: " + e.getMessage());
        }
    }

    @FXML
    public void addTask() {
        Task newTask = new Task(1, "New Task", "In Progress");
        taskList.add(newTask);
        insertTaskToMySQL(newTask);
    }

    private void loadTasksFromMySQL() {
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String selectQuery = "SELECT priority, task_name, progress FROM Tasklist";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                ResultSet resultSet = selectStmt.executeQuery()) {  
                while (resultSet.next()) {
                    int priority = resultSet.getInt("priority");
                    String taskName = resultSet.getString("task_name");
                    String taskProgress = resultSet.getString("progress");

                    Task task = new Task(priority, taskName, taskProgress);
                    taskList.add(task);
                }
            }

            System.out.println("Tasks loaded from MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Error loading tasks from MySQL: " + e.getMessage());
        }
    }

    private void searchWord() {
        String input = searchbar.getText().trim();
        if (!input.isEmpty()) {
            Word word = dictionaryAPI.getWordDetails(input);
            if (word != null) {
                this.word.setText(word.getWord_target());
                phonetics.setText(word.getPhonetics());
                meaning.setText(word.getWord_explain());
                meaning.setWrapText(true);
            } else {
                this.word.setText("Word not found");
                this.word.setWrapText(true);
                phonetics.setText("");
                meaning.setText("");
            }
        }
    }

    private void saveWord() {
        String word = this.word.getText();
        String phonetics = this.phonetics.getText();
        String meaning = this.meaning.getText();
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";
    
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "INSERT IGNORE INTO Words (phonetics, word_target, word_explain) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, phonetics);
                insertStmt.setString(2, word);
                insertStmt.setString(3, meaning);
                insertStmt.executeUpdate();
            }
    
            System.out.println("Word saved to MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Error saving word to MySQL: " + e.getMessage());
        }
        
        System.out.println("Word saved: " + word);
        System.out.println("Phonetics: " + phonetics);
        System.out.println("Meaning: " + meaning);
    }

    private void loadWordsFromMySQL() {
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String selectQuery = "SELECT phonetics, word_target, word_explain FROM Words";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                ResultSet resultSet = selectStmt.executeQuery()) {  
                while (resultSet.next()) {
                    String phonetics = resultSet.getString("phonetics");
                    String word_target = resultSet.getString("word_target");
                    String word_explain = resultSet.getString("word_explain");

                    Word word = new Word(word_target, word_explain);
                    word.setPhonetics(phonetics);
                    WordList.add(word);
                }
            }

            System.out.println("Words loaded from MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Error loading words from MySQL: " + e.getMessage());
        }
    }

    private void updateWordtoMySQL(Word word) {
        String url = "jdbc:mysql://localhost:3306/dictionary";
        String username = "root";
        String password = "140904";
    
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String insertQuery = "UPDATE Words SET word_explain = ? WHERE word_target = ?";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                insertStmt.setString(1, word.getWord_explain());
                insertStmt.setString(2, word.getWord_target());

                insertStmt.executeUpdate();
            }
    
            System.out.println("Word saved to MySQL successfully.");
        } catch (SQLException e) {
            System.out.println("Word saving task to MySQL: " + e.getMessage());
        }
    }
}