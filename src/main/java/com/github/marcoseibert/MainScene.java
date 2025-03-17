package com.github.marcoseibert;
import com.github.marcoseibert.controller.MainController;
import com.github.marcoseibert.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class MainScene {
    private static final Logger logger = LogManager.getLogger(MainScene.class.getSimpleName());
    private static int nrOfPlayers;
    private static List<Player> playersList;

    private static final Map<String, String> gameState = new HashMap<>();
    private static final HashMap<Integer, Map<Integer, TextField>> playerPointsMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> gamesParameterMap = new HashMap<>();
    private static Game game;
    private static Map<String, String> activeGameMap;
    private static int activeGameId = -1;

    private MainScene(){
    }

    public static void start(Stage stageMain, List<Player> playersListInput) throws IOException {
        logger.debug("Starting main screen");
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
        Cursor cursorUp = Functions.getCustomCursor(Constants.UP);
        sceneMain.setCursor(cursorUp);

        createPlayerPointsMap(controller);
        createGamesParameterMap();
        initGameState();
        gameState.put(Constants.GAMEID, "0");

        // Launch task
        Timeline timeLine = getTimeLine(controller);
        timeLine.play();
    }

    private static Timeline getTimeLine(MainController controller) {
        Timeline runningTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
            if (gameState.get(Constants.GAMEOVER).equals("true")) {
                activeGameId++;
                activeGameMap = gamesParameterMap.get(activeGameId);
                initGameState();
                if (logger.isInfoEnabled()){
                    logger.info("Starting new game: {}", activeGameMap.get(Constants.NAME));
                }
                // TODO switch für unterschiedliche categories
                String activeGameCategory = activeGameMap.get(Constants.CATEGORY);
                switch (activeGameCategory){
                    case Constants.RUNNING : game = new RunningGame(controller, activeGameMap); break;
                    case Constants.THROWING, Constants.HIGHJUMPING: throw new AssertionError();
                    default: game = new RestGame(controller, activeGameMap);
                }
                gameState.put(Constants.GAMEID, String.valueOf(activeGameId));
                gameState.put(Constants.GAMEOVER, "false");
            }
            doBackgroundTasks(controller);
            game.playGame(controller, activeGameMap, gameState);

        }));
        runningTasks.setCycleCount(Animation.INDEFINITE);
        return runningTasks;
    }

    private static void doBackgroundTasks(MainController controller){
        // Update the scores of the players
        for (int i = 0; i < nrOfPlayers; i++){
            int totalScore = 0;
            Player player = playersList.get(i);
            Map<Integer, Integer> playerPointMap = player.getPointsMap();
            for (int j = 2; j < 12; j++){
                if (playerPointMap.get(j-2) != null){
                    int score = playerPointMap.get(j-2);
                    playerPointsMap.get(i + 1).get(j).setText(String.valueOf(score));
                }
            }
            playerPointsMap.get(i+1).get(12).setText(String.valueOf(totalScore));
        }
        for (int i = 0; i < nrOfPlayers; i++){
            int totalScore = playersList.get(i).getTotalPoints();
            playerPointsMap.get(i+1).get(12).setText(String.valueOf(totalScore));
        }

        // Highlight on the active game
        GridPane.setConstraints(controller.highlightGame,0, Integer.parseInt(gameState.get(Constants.GAMEID)) + 2);

        updateDiceStatus(controller);

        // Only calculate score after the dice are done rolling
        for (Node child:controller.dicePane.getChildren()){
            if (child instanceof Die die && die.isRollingDone()){
                updateCurrentScore(controller);
                break;
            }
        }
    }

    private static void updateCurrentScore(MainController controller) {
        for (Node child:controller.dicePane.getChildren()){
            if (child.getId().equals("upperLabel")){
                ((Label) child).setText("Current Score: " + gameState.get("currentScore"));
            }
        }
    }

    public static void updateDiceStatus(MainController controller) {
        //update dice status
        for (Node child: controller.dicePane.getChildren()){
            if (child instanceof Die die){
                String dieStatus = die.getStatus();
                Rectangle2D viewport = die.getViewport();
                double viewportX = viewport.getMinX();
                double newViewportY;
                switch (dieStatus){
                    case Constants.ACTIVE: {
                        die.setVisible(true);
                        newViewportY = 0;
                        break;
                    }
                    case Constants.INACTIVE: {
                        die.setVisible(false);
                        newViewportY = 0;
                        break;
                    }
                    case Constants.FOUL: {
                        die.setVisible(true);
                        // display red die
                        newViewportY = 128;
                        break;
                    }
                    case Constants.GRAYED:{
                        die.setVisible(true);
                        // display gray die
                        newViewportY = 256;
                        break;
                    }
                    case Constants.FROZEN: {
                        die.setVisible(true);
                        // display blue die
                        newViewportY = 384;
                        break;
                    }
                    default:
                        die.setVisible(false);
                        newViewportY = 0;
                }
                die.setViewport(new Rectangle2D(viewportX, newViewportY, 128, 128));
            }
        }
    }

    private static void createPlayerPointsMap(MainController controller) {
        // Creating a map for every player and every game to access the points within
        logger.trace("Creating player points map");
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
        logger.trace("Create games parameter map");
        InputStream is = new FileInputStream("src/main/resources/GameParameters.json");
        String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(jsonTxt);
        JSONArray jsonGamesList = jsonObject.getJSONArray("games");

        for (int gameId = 0; gameId < jsonGamesList.length(); gameId++) {
            JSONObject gameObject = jsonGamesList.getJSONObject(gameId);
            Map<String, String> gameParameterMap = new HashMap<>();
            // General attributes for all games
            String gameCategory = gameObject.getString(Constants.CATEGORY);
            gameParameterMap.put(Constants.GAMEID, String.valueOf(gameId));
            gameParameterMap.put(Constants.NAME, gameObject.getString(Constants.NAME));
            gameParameterMap.put(Constants.CATEGORY, gameCategory);
            // Attributes for some games
            gameParameterMap.put("groups", "");
            gameParameterMap.put("dicePerGroup", "");
            gameParameterMap.put("minDice", "");
            gameParameterMap.put("maxDice", "");
            gameParameterMap.put("nrDice", "");
            gameParameterMap.put(Constants.PARITY, "");

            // Adjust the matching fields per game category
            switch (gameCategory) {
                case Constants.RUNNING:
                    String groupsOfDice = String.valueOf(gameObject.getInt("groups of dice"));
                    String dicePerGroup = String.valueOf(gameObject.getInt("dice per group"));
                    gameParameterMap.put("groups", groupsOfDice);
                    gameParameterMap.put("dicePerGroup", dicePerGroup);
                    break;

                case Constants.HIGHJUMPING:
                    String minDice = String.valueOf(gameObject.getInt("min number of dice"));
                    String maxDice = String.valueOf(gameObject.getInt("max number of dice"));
                    gameParameterMap.put("minDice", minDice);
                    gameParameterMap.put("maxDice", maxDice);
                    break;

                case Constants.THROWING:
                    String nrDice = String.valueOf(gameObject.getInt("number of dice"));
                    String parity = gameObject.getString(Constants.PARITY);
                    gameParameterMap.put("nrDice", nrDice);
                    gameParameterMap.put(Constants.PARITY, parity);
                    break;
            }
            gamesParameterMap.put(gameId, gameParameterMap);
        }
    }

    private static void initGameState() {
        // General attributes for game state
        logger.trace("initialize game state map");
        gameState.put(Constants.ACTIVEPLAYER, "0");
        gameState.put("currentScore", "0");
        gameState.put(Constants.GAMEOVER, "true");
        gameState.put("score", "0");
        gameState.put(Constants.LASTACHIEVED, "0"); //previousRoundScore for running, lastHeight for jumping, lastDistance for throwing
        gameState.put("nextTry", "false"); //nextRound for running, nextHeight for jumping
        gameState.put(Constants.CURRENTATTEMPT, "1"); // for jumping, throwing and rest
        // Attributes for running games
        gameState.put(Constants.ROUND, "1");
        gameState.put(Constants.THISROUNDSCORE, "0");
        gameState.put(Constants.REMAININGREROLLS, "5");
        // Attributes for jumping games
        gameState.put("currentHeight", "10");
        // Attributes for throwing games
        gameState.put(Constants.FROZENSUM, "0");
        gameState.put(Constants.FROZENAMOUNT, "0");
        // Attributes for the two rest games
        // TODO
    }

    public static Map<String, String> getGameState() {
        return gameState;
    }

    public static Map<String, String> getActiveGameMap() {
        return activeGameMap;
    }

    public static void setNrOfPlayers(int i){
        nrOfPlayers = i;
    }

    public static List<Player> getPlayersList() {
        return playersList;
    }

}
