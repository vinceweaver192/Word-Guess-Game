import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;

public class Server{

	int count = 1;	// keep track of number of clients in game
	int port;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>(); // list of clients
	GameServer server; // server for the game
	private Consumer<Serializable> callback; // allows communication between server/client

	// categories of words to guess
	String[] Superheroes = {"ironman", "captainamerica", "blackpanther", "hulk", "blackwidow", "vision"}; // will add more later
	String[] Animals = {"panda", "monkey", "spider", "dog", "cat", "seal", "lion", "elephant"}; // add more later
	String[] Countries = {"unitedstates", "canada", "mexico", "france", "unitedkingdom", "ireland"}; // add more later



	Server(int port, Consumer<Serializable> call){
		this.port = port;
		callback = call;
		server = new GameServer();
		server.start();
	}


	public class GameServer extends Thread {

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);) {
				System.out.println("Server is waiting for a client!");


				while(true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;

				}
			}//end of try

			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}


	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

//		public void updateClients(String message) {
//			for(int i = 0; i < clients.size(); i++) {
//				ClientThread t = clients.get(i);
//				try {
//					t.out.writeObject(message);
//				}
//				catch(Exception e) {}
//			}
//		}

		public void updateClients(String message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					GameInfo msg = new GameInfo();
					msg.guess = message;
					t.out.writeObject(msg);
				}
				catch(Exception e) { System.out.println("exception: "+e); }
			}
		}

		/* TODO: update one client only */
		public void updateOne(GameInfo data, ClientThread t) {
			try {
				t.out.writeObject(data);
			}
			catch(Exception e) { System.out.println("data not sent"); }
		}

		public void run() {

			try { // open server
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

//			updateClients("new client on server: client #"+count);

			// new game: choose random words from the pool and set all initial guess numbers
			Game game = new Game(Animals[(int)(Math.random() * (Animals.length + 1))],
					Superheroes[(int)(Math.random() * (Superheroes.length + 1))],
					Countries[(int)(Math.random() * (Countries.length + 1))]);

			// for now, choose specific words
//			Game game = new Game("dog", "unitedstates", "ironman");
//			game.setDistinctLetters(); // record distinct letters of each category's word
//			GameInfo data = new GameInfo();
//			data.newGame();
//			updateOne(data, this);

			while(true) {
				try {
					GameInfo incoming = (GameInfo) in.readObject(); // read in data sent from client
					if (incoming.guess == null) { // should only be null after selecting category
						incoming.newRound();
						game.newRound();

						if (incoming.currCategory.equals("Animals")) {
							incoming.wordLength = game.getAnimal().length();
							game.setCurrWord(game.getAnimal());
						}
						if (incoming.currCategory.equals("Countries")) {
							incoming.wordLength = game.getCountry().length();
							game.setCurrWord(game.getCountry());
						}
						if (incoming.currCategory.equals("Superheroes")) {
							incoming.wordLength = game.getSuperhero().length();
							game.setCurrWord(game.getSuperhero());
						}
						game.setDistinctLetters(); // record distinct letters of current word
						incoming.message = "Welcome to the Word Guessing game!";
						updateOne(incoming, this);
						callback.accept("client: " + count + " category: " + incoming.currCategory);
					}
					else {
						callback.accept("client: " + count + " guessed: " + incoming.guess);

						if (!game.checkGuess(incoming.guess)) { // wrong guess
							callback.accept("client: " + count + "'s guess was wrong");
							incoming.message = "your guess was wrong. "+game.guessesLeft+" guesses left";
							incoming.prevGuessCorrect = false;
							incoming.guessesLeft = game.guessesLeft;
							if (game.guessesLeft <= 0) { // used all guesses
								incoming.message = "your guess was wrong. you lost this round. the word was "+ game.currWord +
										". type '0' in the box and click 'send' to continue.";
							}
							incoming.chancesLeft = game.chancesLeft; // will change if all guesses used
							if (game.chancesLeft <= 0) {
								incoming.gameOver = true;
								incoming.win = false;
							}
						}

						else { // correct guess
							callback.accept("client: " + count + "'s guess was correct");
							if (game.numToGuess <= 0) {
								incoming.message = "great job! word guessed :) the word was "+ game.currWord +
										". type '0' in the box and click 'send' to continue.";
							}
							else {
								incoming.message = "your guess was correct! " + game.numToGuess + " letters left to guess";
							}
							incoming.prevGuessCorrect = true;
							incoming.guessesLeft = game.guessesLeft;
							incoming.wordsCorrect = game.wordsCorrect;
							incoming.animalGuessed = game.animalGuessed;
							incoming.countryGuessed = game.countryGuessed;
							incoming.superheroGuessed = game.superheroGuessed;
							incoming.gameOver = game.gameOver;
							incoming.win = game.win;
						}

						updateOne(incoming, this); // send the updated data back

					}

//					updateClients("client #"+count+" said: "+data.guess);




				}
				catch(Exception e) {
					System.out.println("exception: "+e);
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
//					updateClients("Client #"+count+" has left the server!");
					clients.remove(this);
					break;
				}
			}


		}//end of run


	}//end of client thread
}