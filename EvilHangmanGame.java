package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by rdmclean on 1/29/18.
 */

public class EvilHangmanGame implements IEvilHangmanGame {
    boolean gameWon;
    HashSet<String> myDictionary;
    String partialWord;

    public void setNumGuesses(int numGuesses) { // this is called in main during the setUp process
        this.numGuesses = numGuesses;
    }

    int numGuesses;
    int wordLength;
    TreeSet<String> guessedChars;

    @SuppressWarnings("serial")
    public static class GuessAlreadyMadeException extends Exception {
        public GuessAlreadyMadeException() {
            super();
        }
    }

    public EvilHangmanGame() {
        super();
    }

    public void beginPrint(){
        System.out.println("Number of remaining guesses is " + numGuesses);
        System.out.print("Guessed letters: ");
        for (String item: guessedChars) {
            System.out.print(item + " ");
        }
        System.out.println("\nYour word so far is " + partialWord);
    }

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength){
        gameWon = false;
        StringBuilder build = new StringBuilder();
        for(int i = 0; i < wordLength; i++){
            build.append("_");
        }
        partialWord = build.toString();
        //Remember to set the numGuesses in main
        this.wordLength = wordLength;
        guessedChars = new TreeSet<>();

        try {
            Scanner in = new Scanner(dictionary);
            while(in.hasNext()){
                String input = in.nextLine();
                input = input.toLowerCase();
                if(input.length() == wordLength){
                    myDictionary.add(input);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException{
        //Assume input validation happens in main
        for (String check: guessedChars) {                  //Does nothing
            if(check.equals(Character.toString(guess))){
                throw new IEvilHangmanGame.GuessAlreadyMadeException();
            }
        }
        this.numGuesses--;
        this.guessedChars.add(Character.toString(guess));

        HashMap<String, Set<String>> partitions = new HashMap<>();
        for (String word: myDictionary) {
            String key = partialWord;   // To put into the map
            HashSet<String> partitionSet = new HashSet<>();
            for(int i = 0; i < word.length(); i++){
                if(Character.toString(word.charAt(i)).equals(Character.toString(guess))){
                    key = word.substring(0,i) + Character.toString(guess) + word.substring(i+1,word.length());
                }
            }
            if(!partitions.containsKey(key)){
                partitions.put(key,new HashSet<String>());
            }
            partitions.get(key).add(word);
        }

        int largestSize = 0;
        String largestKey = partialWord;
        for (String it: partitions.keySet()) {
            int size = partitions.get(it).size();
            int numLargest = 0;
            int numIt = 0;
            for(int i = 0; i < it.length();i++){
                if(it.charAt(i) == guess){
                    numIt++;
                }
                if(largestKey.charAt(i) == guess){
                    numLargest++;
                }
            }
            if(size > largestSize){
                largestSize = size;
                largestKey = it;
            }
            else if(size == largestSize){
                if(it.equals(partialWord)){
                    largestKey = it;
                }
                else if(largestKey.equals(partialWord)){
                    //largestKey =largestKey;
                }
                else if(numIt < numLargest){
                    largestKey = it;
                }
                else if(numLargest < numIt){
                    //largestKey =largestKey;
                }
                else{
                    for(int i = it.length() - 1; i >= 0; i--){
                        if(it.charAt(i) == guess && largestKey.charAt(i) != guess){
                            largestKey = it;
                            break;
                        }
                        else if(it.charAt(i) != guess && largestKey.charAt(i) == guess){
                            //largestKey =largestKey;
                            break;
                        }
                    }
                }
            }
        }
        myDictionary.clear();
        myDictionary.addAll(partitions.get(largestKey));
        partialWord = largestKey;

        if((myDictionary.size() == 1 && myDictionary.contains(partialWord))){
            this.gameWon = true;
        }
        return myDictionary;
    }
}
