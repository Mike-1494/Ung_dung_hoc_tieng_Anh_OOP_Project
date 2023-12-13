package Base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class DictionaryCommandline {
    private final DictionaryManagement dictionaryManagement;
    private Scanner scanner;

    public DictionaryCommandline(DictionaryManagement dictionaryManagement) {
        this.dictionaryManagement = dictionaryManagement;
    }

    public void showAllWords() {
        ArrayList<Word> words = dictionaryManagement.getDictionary().getWordList();

        System.out.println("No  | English    | Vietnamese");
        System.out.println("----|------------|-----------------");

        for (int i = 0; i < words.size(); i++) {
            Word word = words.get(i);
            System.out.printf("%-4d| %-11s| %s%n", i + 1, word.getWord_target(), word.getWord_explain());
        }
    }

    public void dictionaryBasic() {
        this.dictionaryManagement.insertFromCommandline();
        this.showAllWords();
    }

}
