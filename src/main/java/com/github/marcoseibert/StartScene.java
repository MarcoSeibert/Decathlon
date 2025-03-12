package com.github.marcoseibert;
import com.github.marcoseibert.util.Functions;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;



public class StartScene extends Application {

    @Override
    public void start(Stage stageStart) throws Exception {
        FXMLLoader loaderStart = new FXMLLoader(getClass().getClassLoader().getResource("Start.fxml"));
        Parent rootStart = loaderStart.load();
        Scene sceneStart = new Scene(rootStart);
        stageStart.setScene(sceneStart);
        stageStart.show();

        // Set custom cursor
        Cursor cursorUp = Functions.getCustomCursor("Up");
        sceneStart.setCursor(cursorUp);
    }

    public static void main(String[] args) {
            launch(args);
        }

}