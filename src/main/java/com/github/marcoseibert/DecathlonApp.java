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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.fxml.FXMLLoader;

import java.util.HashMap;
import java.util.Map;

public class DecathlonApp extends Application {
    protected static final Logger logger = LogManager.getLogger(DecathlonApp.class.getSimpleName());
    public static int nrOfPlayers = 4;
    public static int activeGame = 1;

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

        Timeline backgroundTasks = getTimeline(scoreSheet, playerPointsMap);
        backgroundTasks.play();
    }

    private static Timeline getTimeline(GridPane scoreSheet, HashMap<Integer, Map<Integer, TextField>> playerPointsMap) {
        // Highlight on the first game
        Rectangle highlightGame = new Rectangle();
        highlightGame.setFill(Color.BLUEVIOLET);
        highlightGame.setOpacity(0.25);
        highlightGame.setHeight(64);
        highlightGame.setWidth(64);
        scoreSheet.add(highlightGame, 0, 2);
        highlightGame.toBack();

        Timeline backgroundTasks = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> {
            // Update the total score of the players
            for (int i = 0; i < nrOfPlayers; i++){
                int totalScore = 0;
                for (int j = 2; j < 12; j++){
                    try {
                        totalScore += Integer.parseInt(playerPointsMap.get(i + 1).get(j).getText());
                    } catch (NumberFormatException e) {
                        totalScore += 0;
                    }
                }
                playerPointsMap.get(i+1).get(12).setText(String.valueOf(totalScore));
            }

            // Highlight on the active game
            GridPane.setConstraints(highlightGame,0,activeGame + 1);

        }));
        backgroundTasks.setCycleCount(Animation.INDEFINITE);
        return backgroundTasks;
    }

    public static void main(String[] args) {
        launch(args);
    }

}