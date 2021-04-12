import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerGUI extends Application{


    TextField s1,s2,s3,s4, c1;
    Button serverChoice,clientChoice,b1;
    HashMap<String, Scene> sceneMap;
    HBox buttonBox;
    Scene startScene;
    BorderPane startPane;
    Server serverConnection;

    ListView<String> listItems, listItems2;


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // TODO Auto-generated method stub
        primaryStage.setTitle("Word Game Server");

        listItems = new ListView<String>();
        listItems2 = new ListView<String>();

        sceneMap = new HashMap<String, Scene>();
        sceneMap.put("server",  createServerGui());

        startScene(primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });


        primaryStage.setScene(sceneMap.get("startScene"));
        primaryStage.show();

    }

    public Scene createServerGui() {

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(70));
        pane.setStyle("-fx-background-color: coral");

        pane.setCenter(listItems);

        return new Scene(pane, 500, 400);


    }

    public Scene startScene(Stage primaryStage) {
        Text port = new Text("Enter port number (default 5555):");
        TextField enterPort = new TextField("5555");
        Button button = new Button("Connect");
        VBox root = new VBox(10, port, enterPort, button);
        Scene scene = new Scene(root, 500, 400);
        sceneMap.put("startScene", scene);
        button.setOnAction(e->{
            int portNum = Integer.parseInt(enterPort.getText());
            serverConnection = new Server(portNum, data -> {
                Platform.runLater(()->{
                    listItems.getItems().add(data.toString());
                });
            });
            primaryStage.setScene(sceneMap.get("server"));
        });

        return scene;
    }

}