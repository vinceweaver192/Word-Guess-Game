import java.io.Serializable;

public class GameInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	// client fills this out
	String guess; // char to be sent and checked
	String currCategory; // category that the client chooses

	// server edits these
	String message;

	int wordLength;

	int guessesLeft; // 6
	int wordsCorrect;  // up to 3
	int chancesLeft; // 3

	boolean prevGuessCorrect;
	boolean animalGuessed;
	boolean countryGuessed;
	boolean superheroGuessed;

	boolean gameOver;
	boolean win;

	void newGame() { // for the server to send to client
		guessesLeft = 6;
		wordsCorrect = 0;
		chancesLeft = 3;
		gameOver = false;
		win = false;
	}

	void newRound() {
		guessesLeft = 6;
	}


}