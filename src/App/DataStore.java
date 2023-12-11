package App;

import java.io.BufferedReader;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataStore {
    private static DataStore instance = new DataStore();
    public WordInfo wordInfo;
    public int currentColumn;
    public int currentRow;
    public List<String> words = new ArrayList<String>();
    public String answer;
    public int state;

    private DataStore() {

    }

    public void Init() {
        try {
            File myObj = new File("./src/App/dictionaries.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                words.add(data);
            }
            myReader.close();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public class WordInfo {
        public class Meaning {
            public class Definition {
                public String definition;
                public String example;
                public List<String> synonyms;
                public List<String> asyonyms;

            }

            public String partOfSpeech;
            public List<Definition> definitions;
        }

        public List<Meaning> meanings;
        public String word;

        public String getExample() {
            String example = meanings.get(0).definitions.get(0).example;
            if (example == null) {
                return "Failed to get example";
            }
            return example;
        }
    }

    public static WordInfo getWordInfo(String word) {
        try {
            URL url = new URL("https://api.dictionaryapi.dev/api/v2/entries/en/" + word);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(
                        new java.io.InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                JsonArray json = JsonParser.parseString(content.toString()).getAsJsonArray();
                JsonObject jsonObject = json.get(0).getAsJsonObject();
                WordInfo newWordInfo = new Gson().fromJson(jsonObject, WordInfo.class);
                if (newWordInfo.getExample().equals("Failed to get example")) {
                    return null;
                }
                return newWordInfo;
            } else {
                System.out.println("Failed to get word info " + word);
                return null;
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public static WordInfo getWordInfo() {
        try {
            int getWordTryCount = 0;
            while (getWordTryCount < 5) {
                String word = getRandomWord();
                if (getWordTryCount == 5)
                    word = "hello";
                WordInfo neWordInfo = getWordInfo(word);
                if (neWordInfo != null) {
                    return neWordInfo;
                } else {
                    getWordTryCount++;
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static String getRandomWord() {
        while (true) {
            DataStore dataStore = DataStore.getInstance();
            int randomIndex = (int) (Math.random() * dataStore.words.size());
            String randomWord = dataStore.words.get(randomIndex);
            if (randomWord.length() >= 4 && randomWord.length() <= 5) {
                return randomWord;
            }
        }
    }

    public static String getRandomWord(int length) {
        List<String> words = new ArrayList<String>();
        for (String word : words) {
            if (word.length() == length) {
                words.add(word);
            }
        }
        int randomIndex = (int) (Math.random() * words.size());
        String randomWord = words.get(randomIndex);
        return randomWord;
    }

    public static DataStore getInstance() {
        return instance;
    }

}
