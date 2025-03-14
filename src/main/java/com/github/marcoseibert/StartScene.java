package com.github.marcoseibert;
import com.github.marcoseibert.util.Functions;

import javafx.application.Application;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class StartScene extends Application {
    private static final Logger logger = LogManager.getLogger(StartScene.class.getSimpleName());

    @Override
    public void start(Stage stageStart) throws Exception {
        FXMLLoader loaderStart = new FXMLLoader(getClass().getClassLoader().getResource("Start.fxml"));
        Parent rootStart = loaderStart.load();
        Scene sceneStart = new Scene(rootStart);
        stageStart.setScene(sceneStart);
        stageStart.show();

        // Set custom cursor
        Cursor cursorUp = Functions.getCustomCursor("up");
        sceneStart.setCursor(cursorUp);
    }

    public static void main(String[] args) {
            logger.info("Starting application");
            launch(args);
        }

}