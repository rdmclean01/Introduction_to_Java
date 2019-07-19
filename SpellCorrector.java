package spell;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by rdmclean on 1/26/18.
 */

public class SpellCorrector implements ISpellCorrector {

    private Trie myTrie = new Trie();
    /**
     * Tells this <code>SpellCorrector</code> to use the given file as its dictionary
     * for generating suggestions.
     * @param dictionaryFileName File containing the words to be used
     * @throws IOException If the file cannot be read
     */
    public void useDictionary(String dictionaryFileName) throws IOException{
        Scanner in = new Scanner(new File(dictionaryFileName));
        while(in.hasNext()){
            String word = in.nextLine();
            word = word.toLowerCase();  //Changes to lowercase
            if(word.length() != 0) {    //Remember that weird bug???
                myTrie.add(word);
            }
        }
        in.close();
    }

    /**
     * Suggest a word from the dictionary that most closely matches
     * <code>inputWord</code>
     * @param inputWord
     * @return The suggestion or null if there is no similar word in the dictionary
     */
    public String suggestSimilarWord(String inputWord){
        if(inputWord == ""){
            return null;    //Because that is what they want
        }
        inputWord = inputWord.toLowerCase();
        Trie.Node n = myTrie.find(inputWord);
        HashSet<String> List = new HashSet<>(); //Create a HashSet to keep track of words
        String bestWord = new String();
        int bestFrequency = 0;
        if(n!=null && n.getValue()>0){ //If it is not null, and it is a word
            bestWord = inputWord;
            return bestWord;            //Basically, if it is already in the dictionary
        }
        else{

            delete(inputWord,List);     //Build Edit Distance 1
            transpose(inputWord,List);
            alter(inputWord,List);
            insert(inputWord,List);
            for (String item: List) {
                n = myTrie.find(item);
                if(n != null){
                    if(n.getValue() > bestFrequency){
                        bestWord = item;
                        bestFrequency = n.getValue();
                    }
                }
            }
            if(bestWord.equals("")){         // Build Edit Distance 2
                HashSet<String> List2 = new HashSet<>();
                for (String item: List) {
                    delete(item,List2);
                    transpose(item,List2);
                    alter(item,List2);
                    insert(item,List2);
                }
                for (String item: List2) {
                    n = myTrie.find(item);
                    if(n != null){
                        if(n.getValue() > bestFrequency){
                            bestWord = item;
                            bestFrequency = n.getValue();
                        }
                    }
                } // If it is not done by now, give up
            }

        }
        if(bestWord.equals("")){
            return null;
        }
        return bestWord;
    }

    public void delete(String inputWord, HashSet<String> List){
        for(int i = 0; i< inputWord.length();i++){
            StringBuilder newWord = new StringBuilder(inputWord);
            List.add(newWord.deleteCharAt(i).toString());
        }
    }

    public void transpose(String inputWord, HashSet<String> List){
        for(int i = 0; i < inputWord.length() -1; i++){
            StringBuilder newWord = new StringBuilder(inputWord);
            String letter1 = new String(Character.toString(newWord.charAt(i)));
            String letter2 = new String(Character.toString(newWord.charAt(i+1)));
            newWord.replace(i,i+1,letter2);
            newWord.replace(i+1,i+2,letter1);
            List.add(newWord.toString());
        }
    }

    public void alter(String inputWord, HashSet<String> List){
        for (int i = 0; i < inputWord.length();i++){
            for(int j = 0; j < 26; j++){
                StringBuilder newWord = new StringBuilder(inputWord);
                String letter = Character.toString((char) (j+'a'));
                newWord.replace(i,i+1,letter);
                List.add(newWord.toString());
            }
        }
    }

    public void insert(String inputWord, HashSet<String> List){
        for (int i = 0; i < inputWord.length();i++){
            for(int j = 0; j < 26; j++){
                StringBuilder newWord = new StringBuilder(inputWord);
                String letter = Character.toString((char) (j+'a'));
                newWord.insert(i,letter);
                List.add(newWord.toString());
            }
        }
    }
}
