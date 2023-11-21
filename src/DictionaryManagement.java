import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
class DictionaryManagement {
    private Dictionary dictionary;

    public DictionaryManagement(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public void insertFromCommandLine() {
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
        }
    }
}