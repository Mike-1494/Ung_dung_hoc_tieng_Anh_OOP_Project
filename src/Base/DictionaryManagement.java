package Base;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.*;
import java.sql.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


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
        for (Word word : this.dictionary.getWordList()) {
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
        for (Word word : this.dictionary.getWordList()) {
            if (word.getWord_target().equals(word_target)) {
                wordToRemove = word;
                break;
            }
        }
        if (wordToRemove != null) {
            this.dictionary.removeWord(wordToRemove);

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
        Word res = dictionaryLookup(word_target);
        dictionary.updateWordExplain(res, new_word_explain);

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
    }

    public static int binarySearchFirstEqual(ArrayList<Word> res, String target) {
        int low = 0;
        int high = res.size() - 1;
        int result = -1;
        int len = target.length();

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String cur = res.get(mid).getWord_target();

            // Check if the current word's substring matches the target
            if (cur.length() >= len && cur.substring(0, len).equals(target)) {
                result = mid;
                high = mid - 1;  // Look for the first occurrence to the left
            } else if (cur.compareTo(target) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    /**
     * search word in dictionary.
     * @param target target to search
     * @return list of word
     */
    public ArrayList<Word> dictionarySearcher(String target) {
        ArrayList<Word> res = new ArrayList<Word>();
        int start = binarySearchFirstEqual(dictionary.getWordList(), target);
        if (start == -1) return res;

        while (start < dictionary.getWordList().size() &&
                dictionary.getWordList().get(start).getWord_target().startsWith(target)) {
            res.add(dictionary.getWordList().get(start));
            start++;
        }
        return res;
    }


    private String parse(String responseBody) {
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(responseBody);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            JSONArray phonetics = (JSONArray) jsonObject.get("phonetics");
            JSONObject phonetic = (JSONObject) phonetics.get(0);
            return (String) phonetic.get("audio");
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getPhonetics(String word) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.dictionaryapi.dev/api/v2/entries/en/" + word))
                .build();

        String responseBody = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        return parse(responseBody);
    }
}