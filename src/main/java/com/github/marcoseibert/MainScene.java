package com.github.marcoseibert;
import com.github.marcoseibert.controller.MainController;
import com.github.marcoseibert.util.*;

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

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.util.Duration;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class MainScene {
    private static final String RUNNING = "running";
    private static final String THROWING = "throwing";
    private static final String HIGHJUMPING = "highJumping";
    private static final String REST = "rest";
    private static final String CATEGORY = "category";
    private static final String PARITY = "parity";
    private static int nrOfPlayers;
    private static List<Player> playersList;
    private static Map<String, String> gameState = new HashMap<>();
    private static final HashMap<Integer, Map<Integer, TextField>> playerPointsMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> gamesParameterMap = new HashMap<>();
    private static Game game;
    private static Map<String, String> activeGameMap;

    private static int activeGameId = 0;

    private MainScene(){
    }

    public static void start(Stage stageMain, List<Player> playersListInput) throws IOException {
        playersList = playersListInput;
        nrOfPlayers = playersList.size();
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

        createPlayerPointsMap(controller);
        createGamesParameterMap();
        initGameState();

        // Launch task
        Timeline timeLine = getTimeLine(controller);
        timeLine.play();
    }

    private static Timeline getTimeLine(MainController controller) {
        Timeline runningTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
            if (Objects.equals(gameState.get("gameOver"), "true")) {
                activeGameMap = gamesParameterMap.get(activeGameId);
                // TODO switch für unterschiedliche categories
                game = new RunningGame(controller, activeGameMap);
                gameState.put("gameOver", "false");
            }
            doBackgroundTasks(controller);
            game.playGame();

        }));
        runningTasks.setCycleCount(Animation.INDEFINITE);
        return runningTasks;
    }

    private static void doBackgroundTasks(MainController controller){
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
        for (int i = 0; i < nrOfPlayers; i++){
            int totalScore = playersList.get(i).getTotalPoints();
            playerPointsMap.get(i+1).get(12).setText(String.valueOf(totalScore));
        }

        // Highlight on the active game
        GridPane.setConstraints(controller.highlightGame,0, Integer.parseInt(gameState.get("gameId")) + 2);
    }

    private static void createPlayerPointsMap(MainController controller) {
        // Creating a map for every player and every game to access the points within
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
    }

    private static void createGamesParameterMap() throws IOException {
        // Import the game parameters from the json file and put them in a map
        InputStream is = new FileInputStream("src/main/resources/GameParameters.json");
        String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(jsonTxt);
        JSONArray jsonGamesList = jsonObject.getJSONArray("games");

        for (int gameId = 0; gameId < jsonGamesList.length(); gameId++) {
            JSONObject gameObject = jsonGamesList.getJSONObject(gameId);
            Map<String, String> gameParameterMap = new HashMap<>();
            // General attributes for all games
            String gameCategory = gameObject.getString(CATEGORY);
            gameParameterMap.put("gameId", String.valueOf(gameId));
            gameParameterMap.put("name", gameObject.getString("name"));
            gameParameterMap.put(CATEGORY, gameCategory);
            // Attributes for some games
            gameParameterMap.put("groups", "");
            gameParameterMap.put("dicePerGroup", "");
            gameParameterMap.put("minDice", "");
            gameParameterMap.put("maxDice", "");
            gameParameterMap.put("nrDice", "");
            gameParameterMap.put(PARITY, "");

            // Adjust the matching fields per game category
            switch (gameCategory) {
                case RUNNING:
                    String groupsOfDice = String.valueOf(gameObject.getInt("groups of dice"));
                    String dicePerGroup = String.valueOf(gameObject.getInt("dice per group"));
                    gameParameterMap.put("groups", groupsOfDice);
                    gameParameterMap.put("dicePerGroup", dicePerGroup);
                    break;

                case HIGHJUMPING:
                    String minDice = String.valueOf(gameObject.getInt("min number of dice"));
                    String maxDice = String.valueOf(gameObject.getInt("max number of dice"));
                    gameParameterMap.put("minDice", minDice);
                    gameParameterMap.put("maxDice", maxDice);
                    break;

                case THROWING:
                    String nrDice = String.valueOf(gameObject.getInt("number of dice"));
                    String parity = gameObject.getString(PARITY);
                    gameParameterMap.put("nrDice", nrDice);
                    gameParameterMap.put(PARITY, parity);
                    break;
            }
            gamesParameterMap.put(gameId, gameParameterMap);
        }
    }

    private static void initGameState() {
        // General attributes for game state
        gameState.put("gameId", "0");
        gameState.put("activePlayer", "0");
        gameState.put("gameOver", "true");
        gameState.put("score", "0");
        gameState.put("lastAchieved", "0"); //previousRoundScore for running, lastHeight for jumping, lastDistance for throwing
        gameState.put("nextTry", "false"); //nextRound for running, nextHeight for jumping
        gameState.put("currentAttempt", "0"); // for jumping, throwing and rest
        // Attributes for running games
        gameState.put("round", "0");
        gameState.put("thisRoundScore", "0");
        // Attributes for jumping games
        gameState.put("currentHeight", "10");
        // Attributes for throwing games
        // TODO
        // Attributes for the two rest games
        // TODO
    }

//            // Set the remaining rerolls
//            String remainingRerolls = getGameState().get().get("remainingRerolls");
//            for (Node child:dicePane.getChildren()){
//                if (Objects.equals(child.getId(), "rerollLabel")){
//                    ((Label) child).setText("Rerolls: " + remainingRerolls);
//                    break;
//                }
//            }
//            if (Objects.equals(remainingRerolls, "0")){
//                for (Node child:dicePane.getChildren()){
//                    if (Objects.equals(child.getId(), "rollButton")){
//                        child.setDisable(true);
//                        break;
//                    }
//                }
//            }
//
//            // Show the current score
//            int previousRoundsScore = Integer.parseInt(getGameState().get().get("previousRoundsScore"));
//            int thisRoundScore = Integer.parseInt(getGameState().get().get("thisRoundScore"));
//            String currentScore = String.valueOf(previousRoundsScore + thisRoundScore);
//            for (Node child:dicePane.getChildren()){
//                if (Objects.equals(child.getId(), "scoreLabel")){
//                    ((Label) child).setText("Current score: " + currentScore);
//                    break;
//                }
//            }

    public static void setNrOfPlayers(int i){
        nrOfPlayers = i;
    }

    public static Map<Integer, Map<Integer, TextField>> getPlayerPointsMap() {
        return playerPointsMap;
    }

    public static List<Player> getPlayersList() {
        return playersList;
    }

}
