package ru.dz.dhtpatch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
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

        GridPane gridPane = getPane();

        primaryStage.setScene(new Scene(gridPane, 270, 100));

        addFileNameField(gridPane);

        addButtonBrowse(gridPane);

        addButtonPatch(gridPane);

        primaryStage.show();
    }

    private void addButtonBrowse(GridPane gridPane) {
        Button button = new Button();
        button.setText("Browse");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.printf("Browse");
            }
        });
        GridPane.setConstraints(button,1,0);
        gridPane.getChildren().add(button);
    }

    private void addFileNameField(GridPane gridPane) {
        TextField textField = new TextField ();
        GridPane.setConstraints(textField,0,0);
        gridPane.getChildren().add(textField);
    }

    private GridPane getPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        return gridPane;
    }

    private void addButtonPatch(GridPane gridPane) {
        Button btn = new Button();
        btn.setText("Patch");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        GridPane.setConstraints(btn,0,5);
        gridPane.getChildren().add(btn);
    }
}
