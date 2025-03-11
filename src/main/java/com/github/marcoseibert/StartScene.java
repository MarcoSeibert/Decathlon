package com.github.marcoseibert;

import javafx.application.Application;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
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
        Image cursorUp = new Image("/images/finger_up.png");
        sceneStart.setCursor(new ImageCursor(cursorUp));
    }

    public static void main(String[] args) {
            launch(args);
        }

}