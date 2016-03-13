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

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Log
public class App extends Application {
    private TextField fileNameField = new TextField();
    private Button patchButton = new Button();
    private Button backupButton = new Button();
    private Button browseButton = new Button();
    private String path = "";
    private Patcher patcher = new Patcher();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        System.out.println("uTorrent DHT patch.");
        try {
            initPatch();
            renderScene(primaryStage);
        } catch (Exception e) {
            log.severe("Finished with error");
            log.severe(e.getMessage());
        }

    }

    private void initPatch() {
        initFileNameField();
        checkIsFilePatched();
    }

    private void checkIsFilePatched() {
        if (Patcher.isFilePatched(Paths.get(path))) {
            patchButton.setDisable(true);
            backupButton.setDisable(false);
        } else {
            patchButton.setDisable(false);
            backupButton.setDisable(true);
        }
    }

    private void initFileNameField() {
        String currecntDirectory = "";
        try {
            currecntDirectory = FileUtils.returnCurrectDirectory();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (Files.exists(Paths.get(currecntDirectory, Constant.FILE_NAME))) {
            path = currecntDirectory.concat(Constant.FILE_NAME);
        } else if (Files.exists(Paths.get(Constant.DIRECTORY_NAME, Constant.FILE_NAME))) {
            path = Constant.DIRECTORY_NAME.concat(Constant.FILE_NAME);
        }

        if (!path.isEmpty()) {
            fileNameField.setText(path);
        } else {
            throw new RuntimeException("File " + Constant.FILE_NAME + " not found");
        }
    }

    private void renderScene(Stage primaryStage) {
        primaryStage.setTitle("DHT-patch");
        GridPane gridPane = getPane();
        primaryStage.setScene(new Scene(gridPane, 470, 150));
        addFileNameField(gridPane);
        addButtonBrowse(gridPane);
        addButtonPatch(gridPane);
        addButtonRevertFromBackup(gridPane);
        primaryStage.show();
    }

    private void addButtonRevertFromBackup(GridPane gridPane) {
        backupButton.setText("Revert from backup");
        backupButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                patcher.revertFromBackup(Paths.get(path));
                initPatch();
            }
        });
        GridPane.setConstraints(backupButton, 0, 6);
        gridPane.getChildren().add(backupButton);
    }

    private void addButtonBrowse(GridPane gridPane) {
        browseButton.setText("Browse");
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.printf("Browse");
            }
        });
        GridPane.setConstraints(browseButton, 1, 0);
        gridPane.getChildren().add(browseButton);
    }

    private void addFileNameField(GridPane gridPane) {
        fileNameField.setPrefWidth(370);
        GridPane.setConstraints(fileNameField, 0, 0);
        gridPane.getChildren().add(fileNameField);
    }

    private GridPane getPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        return gridPane;
    }

    private void addButtonPatch(GridPane gridPane) {
        patchButton.setText("Patch");
        patchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    patcher.makePatch(Paths.get(path));
                    initPatch();
                } catch (IOException e) {
                    throw new RuntimeException("Get problem with finding file " + path);
                }
            }
        });

        GridPane.setConstraints(patchButton, 0, 5);
        gridPane.getChildren().add(patchButton);
    }
}
