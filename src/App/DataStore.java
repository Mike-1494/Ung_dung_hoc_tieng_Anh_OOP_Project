package App;

import Base.*;
import java.io.BufferedReader;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javazoom.jl.player.Player;

public class DataStore {
    private static DataStore instance = new DataStore();
    public WordInfo wordInfo;
    public volatile int currentColumn;
    public int currentRow;
    public List<String> words = new ArrayList<String>();
    public String answer;
    public int state;
    public int numberOfWordleDone = 0;
    public int numberOfMultipleChoiceDone = 0;

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

        public void playAudio() throws Exception {
            String url = DictionaryManagement.getPhonetics(word);
            if (url == null) {
                return;
            }
            try {
                URLConnection urlConnection = new URL(url).openConnection();
                urlConnection.connect();
                Player player = new Player(urlConnection.getInputStream());
                player.play(300);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        public String getExample() {
            String example = meanings.get(0).definitions.get(0).example;
            if (example == null) {
                return "Failed to get example";
            }
            return example;
        }

        public String getDefinition() {
            String definition = meanings.get(0).definitions.get(0).definition;
            if (definition == null) {
                return "Failed to get definition";
            }
            return definition;
        }

        public static WordInfo defaultWordInfo() {
            WordInfo wordInfo = instance.new WordInfo();
            wordInfo.word = "hello";
            wordInfo.meanings = new ArrayList<Meaning>();
            Meaning meaning = wordInfo.new Meaning();
            meaning.partOfSpeech = "noun";
            meaning.definitions = new ArrayList<Meaning.Definition>();
            Meaning.Definition definition = meaning.new Definition();
            definition.definition = "used as a greeting or to begin a telephone conversation.";
            definition.example = "Hello? How may I help you?";
            definition.synonyms = new ArrayList<String>();
            definition.asyonyms = new ArrayList<String>();
            meaning.definitions.add(definition);
            wordInfo.meanings.add(meaning);
            return wordInfo;
        }
    }

    public WordInfo getWordInfo(String word) {
        wordInfo = null;
        try {
            Runnable r = new Runnable() {
                public void run() {
                    if (wordInfo != null) {
                        return;
                    }
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
                                return;
                            }

                            wordInfo = newWordInfo;
                            return;
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }
                }
            };
            ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(3);
            for (int i = 0; i < 3; i++)
                executor.submit(r);

            shutdownAndAwaitTermination(executor);

            if (wordInfo != null) {
                return wordInfo;
            }
            return WordInfo.defaultWordInfo();
        } catch (Exception e) {
            System.out.println(e);
            return WordInfo.defaultWordInfo();
        }
    }

    void shutdownAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ie) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public WordInfo getWordInfo() {
        wordInfo = null;
        try {
            ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(20);
            Runnable r = new Runnable() {
                public void run() {
                    if (wordInfo != null) {
                        return;
                    }
                    try {
                        String word = getRandomWord();
                        WordInfo newWordInfo = getWordInfo(word);
                        if (newWordInfo.getExample().equals("Failed to get example")) {
                            return;
                        }
                        if (newWordInfo.word == "hello") {
                            return;
                        }
                        wordInfo = newWordInfo;
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }
                }
            };
            for (int i = 0; i < 20; i++)
                executor.submit(r);
            shutdownAndAwaitTermination(executor);

            if (wordInfo != null) {
                return wordInfo;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return WordInfo.defaultWordInfo();
    }

    public WordInfo getWordInfo(int wordLength) {
        wordInfo = null;
        try {
            ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(10);
            Runnable r = new Runnable() {
                public void run() {
                    if (wordInfo != null) {
                        return;
                    }
                    try {
                        String word = getRandomWord(wordLength);
                        WordInfo newWordInfo = getWordInfo(word);
                        if (newWordInfo.getExample().equals("Failed to get example")) {
                            return;
                        }
                        if (newWordInfo.word == "hello") {
                            return;
                        }
                        wordInfo = newWordInfo;
                    } catch (Exception e) {
                        System.out.println(e);
                        return;
                    }
                }
            };
            for (int i = 0; i < 10; i++)
                executor.submit(r);
            shutdownAndAwaitTermination(executor);

            if (wordInfo != null) {
                return wordInfo;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return WordInfo.defaultWordInfo();
    }

    public String getRandomWord() {
        while (true) {
            DataStore dataStore = DataStore.getInstance();
            int randomIndex = (int) (Math.random() * dataStore.words.size());
            String randomWord = dataStore.words.get(randomIndex);
            if (randomWord.length() >= 4 && randomWord.length() <= 5) {
                return randomWord;
            }
        }
    }

    public String getRandomWord(int length) {
        List<String> satisfiedWords = new ArrayList<String>();
        for (String word : words) {
            if (word.length() == length) {
                satisfiedWords.add(word);
            }
        }
        int randomIndex = (int) (Math.random() * satisfiedWords.size());
        String randomWord = satisfiedWords.get(randomIndex);
        return randomWord;
    }

    public static DataStore getInstance() {
        return instance;
    }

    public boolean findWord(String word) {
        for (String w : words) {
            if (w.equals(word)) {
                return true;
            }
        }
        return false;
    }
}
