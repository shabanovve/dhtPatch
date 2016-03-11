package ru.dz.dhtpatch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.java.Log;

@Log
public class App extends Application
{
    public static void main( String[] args ) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        renderScene(primaryStage);

        System.out.println( "uTorrent DHT patch.");
        try{
            new Patcher().start();
            log.info("successfully complete.");
        } catch (Exception e){
            log.severe("Finished with error");
            log.severe(e.getMessage());
        }

    }

    private void renderScene(Stage primaryStage) {
        primaryStage.setTitle("DHT-patch");

        StackPane root = new StackPane();
        primaryStage.setScene(new Scene(root, 600, 250));

        TextField textField = new TextField ();
        root.getChildren().add(textField);

        addButton(root);

        primaryStage.show();
    }

    private void addButton(StackPane root) {
        Button btn = new Button();
        btn.setText("Patch");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        root.getChildren().add(btn);
    }
}
