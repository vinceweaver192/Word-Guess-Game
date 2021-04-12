import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class MyTest {
	private static Game A1;
	private static Game A2;
	private static Game A3;
	static String animalWord = "dog";
	static String countryWord = "unitedstates";
	static String superheroWord = "ironman";



	@BeforeAll
	static void setup() {
		A1 = new Game(animalWord, countryWord, superheroWord);
		A2 = new Game(animalWord, countryWord, superheroWord);
		A3 = new Game(animalWord, countryWord, superheroWord);

		A1.setCurrWord(animalWord);
		A2.setCurrWord(animalWord);
		A3.setCurrWord(animalWord);
	}

	@Test
	void setWordTest()
	{
		assertEquals("dog", A1.currWord, "Current word isnt dog");
	}

	@Test
	void checkGuessTest()
	{
		ArrayList<String> guesses = new ArrayList<String>();
		guesses.add("a");
		guesses.add("b");
		guesses.add("c");
		guesses.add("d");


		assertEquals(false, A2.checkGuess("a"), "'a' is not in the word 'dog'");
		assertEquals(false, A2.checkGuess("b"), "'b' is not in the word 'dog'");
		assertEquals(false, A2.checkGuess("c"), "'c' is not in the word 'dog'");
		assertEquals(true, A2.checkGuess("d"), "'d' is in the word 'dog'");

	}

	@Test
	void gameWonTest() {

		ArrayList<String> guesses = new ArrayList<String>();
		guesses.add("d");
		guesses.add("o");
		guesses.add("g");

		A3.checkGuess("d");
		assertEquals(false, A3.win, "the game is not over");

		A3.checkGuess("o");
		assertEquals(false, A3.win, "the game is not over");

		A3.checkGuess("g");
		assertEquals(true, A3.win, "the game is over");


	}


}
