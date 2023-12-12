package Base;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DictionaryAPI {
    private HttpClient client;

    public DictionaryAPI() {
        this.client = HttpClient.newHttpClient();
    }

    public Word getWordDetails(String word) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.dictionaryapi.dev/api/v2/entries/en/" + word))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return parse(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Word parse(String responseBody) {
        Word word = new Word();
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(responseBody);
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);
            String w = (String) jsonObject.get("word");
            word.setWord_target(w);
            JSONArray phoneticsArray = (JSONArray) jsonObject.get("phonetics");
            JSONObject phonetic = (JSONObject) phoneticsArray.get(0);
            String phonetics = (String) phonetic.get("text");
            word.setPhonetics(phonetics);
            JSONArray meaningsArray = (JSONArray) jsonObject.get("meanings");
            JSONObject meaningObject = (JSONObject) meaningsArray.get(0);
            JSONArray definitionsArray = (JSONArray) meaningObject.get("definitions");
            JSONObject definitionObject = (JSONObject) definitionsArray.get(0);
            String definition = (String) definitionObject.get("definition");
            word.setWord_explain(definition);
            return word;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
