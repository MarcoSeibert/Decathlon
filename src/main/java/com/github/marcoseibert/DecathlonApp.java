package com.github.marcoseibert;

import com.github.marcoseibert.util.controller.MainFXMLController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.fxml.FXMLLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DecathlonApp extends Application {
    protected static final Logger logger = LogManager.getLogger(DecathlonApp.class.getSimpleName());
    public static int nrOfPlayers = 4;

    @Override
    public void start(Stage stage) throws Exception {
        logger.info("Starting!");
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MainFXML.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        MainFXMLController controller = loader.getController();

        // Creating a map for every player and every game to access the points within
        HashMap<Integer, Map<Integer, TextField>> playerPointsMap = new HashMap<>();
        for (int i = 0; i < nrOfPlayers; i++){
            HashMap<Integer, TextField> gameMap = new HashMap<>();
            playerPointsMap.put(i+1, gameMap);
        }
        GridPane scoreSheet = controller.scoreSheet;
        for (Node child:scoreSheet.getChildren()){
            if (child instanceof TextField textField) {
                int playerNr = GridPane.getColumnIndex(child);
                int gameNr = GridPane.getRowIndex(child);
                Map<Integer, TextField> gameMap = playerPointsMap.get(playerNr);
                gameMap.put(gameNr, textField);
            }
        }

        Timeline setSumOfPoints = getTimeline(playerPointsMap);
        setSumOfPoints.play();
    }

    private static Timeline getTimeline(HashMap<Integer, Map<Integer, TextField>> playerPointsMap) {
        Timeline setSumOfPoints = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            for (int i = 0; i < nrOfPlayers; i++){
                int totalScore = 0;
                for (int j = 2; j < 12; j++){
                    if (!Objects.equals(playerPointsMap.get(i + 1).get(j).getText(), "")) {
                        totalScore += Integer.parseInt(playerPointsMap.get(i + 1).get(j).getText());
                    }
                }
                playerPointsMap.get(i+1).get(12).setText(String.valueOf(totalScore));
            }
        }));
        setSumOfPoints.setCycleCount(Animation.INDEFINITE);
        return setSumOfPoints;
    }

    public static void main(String[] args) {
        launch(args);
    }

}