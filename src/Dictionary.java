import java.util.ArrayList;
import java.util.Comparator;

public class Dictionary {
    private final ArrayList<Word> words;

    public Dictionary() {
        this.words = new ArrayList<>();
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void sortWords() {
        this.words.sort(new Comparator<Word>() {
            @Override
            public int compare(Word word1, Word word2) {
                return word1.getWord_target().compareToIgnoreCase(word2.getWord_target());
            }
        });
    }

    public ArrayList<Word> getWords() {
        return words;
    }
}
