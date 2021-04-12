import java.util.ArrayList;
import java.util.HashMap;

public class Game {

    // categories
    String animal;
    HashMap<Character, Integer> distinctAnimal; // hold distinct letters of the words
    String country;
    HashMap<Character, Integer> distinctCountry;
    String superhero;
    HashMap<Character, Integer> distinctSuperhero;

    ArrayList<String> usedWords;

    String currWord;
    HashMap<Character, Integer> distinctLetters;
    int numToGuess;

    int wordsCorrect;
    int chancesLeft; //
    int guessesLeft;

    boolean animalGuessed;
    boolean countryGuessed;
    boolean superheroGuessed;

    /*
     * The game is won if:
     * The player guesses a word correctly from each category
     * The game is lost if:
     * The player fails to guess a word from a category 3 times
     */

    boolean gameOver;
    boolean win;

    Game(String animalWord, String countryWord, String superheroWord) {
        animal = animalWord;
        country = countryWord;
        superhero = superheroWord;
        usedWords = new ArrayList<>();

        wordsCorrect = 0;
        chancesLeft = 3; // 3 chances per category to guess the word
        guessesLeft = 6;


        animalGuessed = false;
        countryGuessed = false;
        superheroGuessed = false;
        gameOver = false;
        win = false;
    }

    void newRound() {
        guessesLeft = 6;
    }

    void setCurrWord(String word) {
        usedWords.add(word);
        currWord = word;
    }

    void setDistinctLetters() {
        distinctLetters = new HashMap<>();
        for (int i = 0; i < currWord.length(); i++) {
            distinctLetters.putIfAbsent(currWord.charAt(i), 1);
        }
        numToGuess = distinctLetters.size();
    }

    String getAnimal() {
        return animal;
    }

    String getCountry() {
        return country;
    }

    String getSuperhero() {
        return superhero;
    }

    /* The server will NEVER send the word to the client, only the number of letters, respond
    to guesses and keep track of remaining guesses, keep track of categories and words
    guessed as well as winning and losing of the game.
     */
    boolean checkGuess(String guess) {
        if (currWord.contains(guess)) { // correct guess
            numToGuess--;
            if (numToGuess <= 0) { // the word was guessed
                if (currWord.equals(animal))
                    animalGuessed = true;
                if (currWord.equals(country))
                    countryGuessed = true;
                if (currWord.equals(superhero))
                    superheroGuessed = true;
                wordsCorrect++;
                if (wordsCorrect >= 3) {
                    gameOver = true;
                    win = true;
                }
            }
            return true;
        }
        else { // wrong guess
            guessesLeft--;
            if (guessesLeft <= 0) { // round lost
                chancesLeft--;
                if (chancesLeft <= 0) {
                    gameOver = true;
                    win = false;
                }
            }
        }
        return false;
    }


}