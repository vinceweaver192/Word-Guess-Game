import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.function.Consumer;



public class Client extends Thread{


	Socket socketClient;

	ObjectOutputStream out;
	ObjectInputStream in;

	String ip;
	int port;
	private Consumer<Serializable> callback;

	int wordLength;
	String category;
	String message;
	HashMap<String, Integer> correctGuesses = new HashMap<>();
	int guessesLeft;

	boolean animalGuessed;
	boolean countryGuessed;
	boolean superheroGuessed;

	boolean roundLost;
	boolean gameOver;
	boolean win;

//	Client(Consumer<Serializable> call){
//
//		callback = call;
//	}

	Client(String inIPAddress, int inPort, Consumer<Serializable> call){
		ip = inIPAddress;
		port = inPort;
		callback = call;
//		exitCallBack = exit;
	}

	public void run() {

		try {
			System.out.println("Connecting...");
			socketClient = new Socket(ip, port);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);
			System.out.println("Connected to Server on port "+port);
		}
		catch(Exception e) {
			System.out.println("connection exception: "+ e);
		}

		while(true) {

			try {
//				String message = in.readObject().toString();
				GameInfo data = (GameInfo) in.readObject(); // accept data from Server
				callback.accept(data);

				System.out.println("currCategory: "+data.currCategory);
				category = data.currCategory;

				System.out.println("wordLength: "+data.wordLength);
				wordLength = data.wordLength;

				System.out.println("message: "+data.message);
				message = data.message;

				guessesLeft = data.guessesLeft;
				if (guessesLeft <= 0) {
					roundLost = true;
				}
				System.out.println(guessesLeft);
				System.out.println("roundLost: "+roundLost);

				if (data.prevGuessCorrect) {
					correctGuesses.put(data.guess, 1);
				}

				animalGuessed = data.animalGuessed;
				System.out.println("animalGuessed: "+animalGuessed);
				countryGuessed = data.countryGuessed;
				System.out.println("countryGuessed: "+countryGuessed);
				superheroGuessed = data.superheroGuessed;
				System.out.println("superheroGuessed: "+superheroGuessed);

				gameOver = data.gameOver;
				System.out.println("gameOver: "+gameOver);
				win = data.gameOver;
				System.out.println("win: "+win);
			}
			catch(Exception e) { System.out.println("data not accepted exception: " + e); }
		}

	}

	public void send(GameInfo data) {

		try {
			out.writeObject(data);
			System.out.println("sent guess: "+data.guess);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}