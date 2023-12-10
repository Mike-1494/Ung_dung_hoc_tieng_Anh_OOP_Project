public class Main {
    public static void main(String[] args) {
        Word word1 = new Word("hello", "Xinchao") ;
        Word word2 = new Word("I", "Toi");
        Word word3 = new Word("My", "cua toi");
        Dictionary dictionary = new Dictionary();
        dictionary.addWord(word3);
        dictionary.addWord(word2);
        dictionary.addWord(word1);
        dictionary.sortWords();
        DictionaryManagement dmng = new DictionaryManagement(dictionary);
        DictionaryCommandline dcml = new DictionaryCommandline(dmng);
        dcml.showAllWords();
        String res = dmng.getPhonetics("hi");
        System.out.println(res);
    }
}