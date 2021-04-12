import java.util.HashMap;
import java.util.Optional;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class ClientGUI extends Application {

	private HashMap<String, Scene> sceneMap = new HashMap();
	//	VBox clientBox;
	TextField guesser;
	Button sendButton;
	Text category;
	Text length;
	ListView<String> listItems2;
	Client clientConnection;

	// category buttons
	Button c1 = new Button("Animals");
	Button c2 = new Button("Superheroes");
	Button c3 = new Button("Countries");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// login screen
		primaryStage.setScene(startScene(primaryStage));

		// get text from the text box into the message box
//		c1 = new TextField();
//		b1 = new Button("Send");
//

		category = new Text();
		length = new Text();
		listItems2 = new ListView();

		// create vbox to hold objects in the scene
//		clientBox = new VBox(10, c1,b1,listItems2);
//		clientBox.setStyle("-fx-background-color: blue");
//		Scene clientScene = new Scene(clientBox, 400, 400);
//		sceneMap.put("clientScene", clientScene);


		// set window
//		primaryStage.setScene(clientScene);
//		primaryStage.setTitle("Word Game Client");
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.show();

	}

	Scene startScene(Stage primaryStage) {
		Text portText = new Text("Enter port number (default 5555):");
		TextField port = new TextField();
		HBox portBox = new HBox(10, portText, port);
		portBox.setAlignment(Pos.CENTER);

		Text ipText = new Text("Enter IP Address:");
		TextField ipAddress = new TextField("127.0.0.1");
		HBox ipBox = new HBox(10, ipText, ipAddress);
		ipBox.setAlignment(Pos.CENTER);

		Button startButton = new Button("Connect");
		startButton.setAlignment(Pos.CENTER);

		VBox vbox = new VBox(10, portBox, ipBox, startButton);
		vbox.setAlignment(Pos.CENTER);
		Scene startScene = new Scene(vbox,400,400);
		sceneMap.put("startScene", startScene);

		startButton.setOnAction(e->{
			int portNum = Integer.parseInt(port.getText()); // convert text to int
			String ip = ipAddress.getText();
			clientConnection = new Client(ip, portNum, data->{
				Platform.runLater(()->{
					listItems2.getItems().add(clientConnection.message);
				});
			});
			clientConnection.start();

			sceneMap.put("gameScene", gameScene(primaryStage));
			sceneMap.put("categorySelect", categorySelect(primaryStage));

			primaryStage.setTitle("Play Word Guess!");
			primaryStage.setScene(sceneMap.get("categorySelect"));
//			primaryStage.setScene(sceneMap.get("clientScene"));
		});

		return startScene;
	}

	Scene gameScene(Stage primaryStage) {

		// menu bar
		HBox menu = new HBox(50); // sets spacing
		menu.setStyle("-fx-background-color: #add8e6;");
		menu.setAlignment(Pos.CENTER); // aligns items within

		//
//		VBox outputVBox = new VBox(50); // sets spacing
//		outputVBox.setStyle("-fx-background-color: #add8e6;");
//		outputVBox.setAlignment(Pos.CENTER); // aligns items within

		guesser = new TextField();
		guesser.setPromptText("Enter your guess here, one character at a time");
		sendButton = new Button("Send");
		sendButton.setOnAction(e->{
			// check for invalid input
			if (guesser.getText().length() != 1) { // if length of string

				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Invalid Input");
				alert.setHeaderText("Invalid Input");
				alert.setContentText("Must enter 1 letter at a time\n" +
						"Try again.\n");
				alert.getButtonTypes();

				ButtonType buttonExit = new ButtonType("Exit");

				Optional<ButtonType> result = alert.showAndWait();

				if (result.get() == buttonExit) {
					Platform.exit();
				}
			}
			else if (clientConnection.correctGuesses.containsKey(guesser.getText())) {
				// if you already put in a correct guess
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Already guessed this");
				alert.setHeaderText("Already guessed this");
				alert.setContentText("You already guessed this letter.\n" +
						"Try again.\n");
				alert.getButtonTypes();

				ButtonType buttonExit = new ButtonType("Exit");

				Optional<ButtonType> result = alert.showAndWait();

				if (result.get() == buttonExit) {
					Platform.exit();
				}
			}
			else {
				// move is valid
				GameInfo data = new GameInfo();
				data.guess = guesser.getText();
				data.currCategory = clientConnection.category;
				data.wordLength = clientConnection.wordLength;
				clientConnection.send(data);
				guesser.clear();

				if (clientConnection.gameOver) {
					if (clientConnection.win) {
						System.out.println("YOU WON!");
						primaryStage.setScene(endScene(primaryStage, "You won"));
					}
					else {
						System.out.println("YOU LOST!");
						primaryStage.setScene(endScene(primaryStage, "You lost"));
					}
				}
				else if (clientConnection.category.equals("Animals") && clientConnection.animalGuessed) {
					guesser.setEditable(false);
					PauseTransition pause = new PauseTransition(Duration.seconds(3));
					pause.play();
					c1.setDisable(true);
					primaryStage.setScene(sceneMap.get("categorySelect"));
				}
				else if (clientConnection.category.equals("Superheroes") && clientConnection.superheroGuessed) {
					guesser.setEditable(false);
					PauseTransition pause = new PauseTransition(Duration.seconds(3));
					pause.play();
					c2.setDisable(true);
					primaryStage.setScene(sceneMap.get("categorySelect"));
				}
				else if (clientConnection.category.equals("Countries") && clientConnection.countryGuessed) {
					guesser.setEditable(false);
					PauseTransition pause = new PauseTransition(Duration.seconds(3));
					pause.play();
					c3.setDisable(true);
					primaryStage.setScene(sceneMap.get("categorySelect"));
				}
				else if (clientConnection.roundLost) {
					primaryStage.setScene(sceneMap.get("categorySelect"));
				}

			}
		});

		VBox gameBox = new VBox(10, guesser, sendButton, category, length, listItems2); // sets spacing
		gameBox.setStyle("-fx-background-color: #ff99ff;");
		gameBox.setAlignment(Pos.CENTER); // aligns items within



//		// how to play text
//		TextArea howToPlayText = new TextArea("This is how to play");
//		howToPlayText.setMaxSize(200, 200);
//		howToPlayText.isDisable();



		// create empty menu dropdown bars
//		MenuBar menu1 = new MenuBar();
		MenuBar menu2 = new MenuBar();
		MenuBar menu3 = new MenuBar();

		// create and assign names for menu dropdown bars
//		Menu mOne = new Menu("Menu1");
		Menu mTwo = new Menu("Themes");
		Menu mThree = new Menu("Options");

		// create items for menu 1
		MenuItem reverseMove = new MenuItem("Reverse last move");



		// create items for menu 2
		MenuItem theme1 = new MenuItem("Theme 1");
		MenuItem theme2 = new MenuItem("Theme 2");


		VBox window = new VBox(10, menu, gameBox);
		window.setStyle("-fx-background-color: #ff99ff;");

		theme1.setOnAction(e-> {
			//change colors of stuff
			window.setStyle("-fx-background-color: #ff99ff;");

			menu.setStyle("-fx-background-color: #66ff66;");

//			outputVBox.setStyle("-fx-background-color: #66ff66;");

			gameBox.setStyle("-fx-background-color: #ffff99;");
		});

		theme2.setOnAction(e-> {
			//change colors of stuff
			window.setStyle("-fx-background-color: #ff99ff;");

			menu.setStyle("-fx-background-color: #ff944d;");

//			outputVBox.setStyle("-fx-background-color: #ff944d;");

			gameBox.setStyle("-fx-background-color: #990099;");
		});

		// create items for menu 3
		MenuItem howToPlay = new MenuItem("How to play");
//		MenuItem newGame = new MenuItem("New game");
		MenuItem exit = new MenuItem("Exit");

//		// actions for menu 3
		howToPlay.setOnAction(e->{
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("How to Play");
			alert.setHeaderText("How to play");
			alert.setContentText("Enter one character at a time to guess a word in a category.\n"+
					"You have 6 guesses per category and 3 chances to guess a word in every category.");
			alert.getButtonTypes();

			ButtonType buttonExit = new ButtonType("Exit");

			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == buttonExit) {
				Platform.exit();
			}
		});


		exit.setOnAction(e-> Platform.exit());

		// add menu option to menu 1
//		mOne.getItems().add(reverseMove);

		// add menu option to menu 2
		mTwo.getItems().add(theme1);
		mTwo.getItems().add(theme2);

		// add menu option to menu 3
		mThree.getItems().add(howToPlay);
//		mThree.getItems().add(newGame);
		mThree.getItems().add(exit);

		// add updated menus to initial menu bar
//		menu1.getMenus().addAll(mOne);
		menu2.getMenus().addAll(mTwo);
		menu3.getMenus().addAll(mThree);



		//event handler is attached to each button in the GridPane



		// set VBox
		menu.getChildren().addAll(menu2, menu3);
//		outputVBox.getChildren().addAll(howToPlayText);

//		;


		//new scene with root node
		return new Scene(window, 500,500);

	} // end of game scene


	Scene categorySelect(Stage primaryStage) {
		Text categorySelect = new Text("Choose a category of word to guess!");

		c1.setOnAction(e->{
			guesser.setEditable(true);
			listItems2.getItems().clear();
			GameInfo data = new GameInfo();
			data.currCategory = "Animals";
			clientConnection.send(data);
			Platform.runLater(()->{
				category.setText("The category is: "+clientConnection.category);
				length.setText("The length of the word is: "+clientConnection.wordLength);
			});
			clientConnection.correctGuesses.clear();
			clientConnection.roundLost = false;
			primaryStage.setScene(sceneMap.get("gameScene"));
		});

		c2.setOnAction(e->{
			guesser.setEditable(true);
			listItems2.getItems().clear();
			GameInfo data = new GameInfo();
			data.currCategory = "Superheroes";
			clientConnection.send(data);
			Platform.runLater(()->{
				category.setText("The category is: "+clientConnection.category);
				length.setText("The length of the word is: "+clientConnection.wordLength);
			});
			clientConnection.correctGuesses.clear();
			clientConnection.roundLost = false;
			primaryStage.setScene(sceneMap.get("gameScene"));
		});

		c3.setOnAction(e->{
			guesser.setEditable(true);
			listItems2.getItems().clear();
			GameInfo data = new GameInfo();
			data.currCategory = "Countries";
			clientConnection.send(data);
			Platform.runLater(()->{
				category.setText("The category is: "+clientConnection.category);
				length.setText("The length of the word is: "+clientConnection.wordLength);
			});
			clientConnection.correctGuesses.clear();
			clientConnection.roundLost = false;
			primaryStage.setScene(sceneMap.get("gameScene"));
		});


		VBox root = new VBox(10, categorySelect,c1,c2,c3);

		Scene categories = new Scene(root, 500, 500);

		return categories;
	} // category select


	Scene endScene(Stage primaryStage, String result) {

		Text titleText = new Text(result);
		Button playAgain = new Button("Play Again");
		Button quitButton = new Button("Exit");

		playAgain.setOnAction(e->{
			// todo: reset game data

			// go back to pick category scene
			primaryStage.setTitle("Play Word Guess!");
			primaryStage.setScene(sceneMap.get("categorySelect"));
		});

		quitButton.setOnAction(e-> Platform.exit());


		VBox root = new VBox(10, titleText, playAgain, quitButton);
		root.setAlignment(Pos.CENTER);

		Scene theEnd = new Scene(root, 500, 500);

		return theEnd;
	}
}