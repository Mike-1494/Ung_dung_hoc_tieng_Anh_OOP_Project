import java.util.ArrayList;
import java.util.Collections;
class Dictionary {
    private ArrayList<Word> words;

    public Dictionary() {
        this.words = new ArrayList<>();
    }

    public void addWord(Word word) {
        words.add(word);
    }

    public void sortWords() {
        Collections.sort(words, (w1, w2) -> w1.getWord_target().compareToIgnoreCase(w2.getWord_target()));
    }

    public ArrayList<Word> getWords() {
        return words;
    }
}
