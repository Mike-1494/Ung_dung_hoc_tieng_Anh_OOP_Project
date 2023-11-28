import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.*;
import java.sql.*;

public class DictionaryManagement {
    private final Dictionary dictionary;

    public DictionaryManagement(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void insertFromCommandline() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of words: ");
        int numWords = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < numWords; i++) {
            System.out.print("Enter the English word: ");
            String wordTarget = scanner.nextLine();
            System.out.print("Enter the Vietnamese explanation: ");
            String wordExplain = scanner.nextLine();

            Word word = new Word(wordTarget, wordExplain);
            dictionary.addWord(word);
            dictionary.sortWords();
        }
        scanner.close();
    }

    public void insertFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "140904");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM words");

            while (resultSet.next()) {
                String wordTarget = resultSet.getString("word_target");
                String wordExplain = resultSet.getString("word_explain");
                Word word = new Word(wordTarget, wordExplain);
                this.dictionary.addWord(word);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Word dictionaryLookup(String word_target) {
        for (Word word : this.dictionary.getWords()) {
            if (word.getWord_target().equals(word_target)) {
                return word;
            }
        }

        return null;
    }

    public void addWord(Word word) {
        this.dictionary.addWord(word);

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "140904");
            PreparedStatement statement = connection.prepareStatement("INSERT INTO words (word_target, word_explain) VALUES (?, ?)");
            statement.setString(1, word.getWord_target());
            statement.setString(2, word.getWord_explain());
            statement.executeUpdate();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeWord(String word_target) {
        Word wordToRemove = null;
        for (Word word : this.dictionary.getWords()) {
            if (word.getWord_target().equals(word_target)) {
                wordToRemove = word;
                break;
            }
        }

        if (wordToRemove != null) {
            this.dictionary.getWords().remove(wordToRemove);

            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "140904");
                PreparedStatement statement = connection.prepareStatement("DELETE FROM words WHERE word_target = ?");
                statement.setString(1, word_target);
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateWord(String word_target, String new_word_explain) {
        for (Word word : this.dictionary.getWords()) {
            if (word.getWord_target().equals(word_target)) {
                word.setWord_explain(new_word_explain);

                try {
                    Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dictionary", "root", "140904");
                    PreparedStatement statement = connection.prepareStatement("UPDATE words SET word_explain = ? WHERE word_target = ?");
                    statement.setString(1, new_word_explain);
                    statement.setString(2, word_target);
                    statement.executeUpdate();
                    statement.close();
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    public List<Word> dictionarySearcher(String prefix) {
        List<Word> result = new ArrayList<>();
        for (Word word : this.dictionary.getWords()) {
            if (word.getWord_target().startsWith(prefix)) {
                result.add(word);
            }
        }

        return result;
    }

}