package com.github.marcoseibert;
import com.github.marcoseibert.controller.MainController;
import com.github.marcoseibert.util.Functions;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.util.Duration;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainScene {
    private static final Logger logger = LogManager.getLogger(MainScene.class.getSimpleName());
    private static int nrOfPlayers;
    private static int activeGame = 1;

    private MainScene(){
        logger.debug("Should not be visible!");
    }

    public static void start(Stage stageMain, int nrOfPlayersInput, List<String> playerNames) throws IOException {
        nrOfPlayers = nrOfPlayersInput;
        FXMLLoader loaderMain = new FXMLLoader(MainScene.class.getClassLoader().getResource("Main.fxml"));
        Parent rootMain = loaderMain.load();
        Scene sceneMain = new Scene(rootMain);
        stageMain.setScene(sceneMain);
        stageMain.centerOnScreen();
        stageMain.show();
        MainController controller = loaderMain.getController();

        // Set custom cursor
        Cursor cursorUp = Functions.getCustomCursor("up");
        sceneMain.setCursor(cursorUp);

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
                if (gameNr == 1){
                    textField.setText(playerNames.get(playerNr-1));
                }
            }
        }

        // Launch background tasks
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
        highlightGame.setArcHeight(10);
        highlightGame.setArcWidth(10);
        scoreSheet.add(highlightGame, 0, 2);
        highlightGame.toBack();

        Timeline backgroundTasks = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
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

    public static int getNrOfPlayers(){
        return nrOfPlayers;
    }
    public static void setNrOfPlayers(int i){
        nrOfPlayers = i;
    }

}
