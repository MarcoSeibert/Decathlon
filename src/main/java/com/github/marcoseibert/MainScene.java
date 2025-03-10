package com.github.marcoseibert;
import com.github.marcoseibert.controller.MainController;
import com.github.marcoseibert.util.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class MainScene {
    private static final String RUNNING = "Running";
    private static final String THROWING = "Throwing";
    private static final String HIGHJUMPING = "HighJumping";
    private static final String REST = "Rest";
    private static final Logger logger = LogManager.getLogger(MainScene.class.getSimpleName());
    private static int nrOfPlayers;
    private static List<Player> playersList;


    private static final Map<Integer, Map<String, String>> runningGamesParametersMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> highJumpingGamesParametersMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> throwingGamesParametersMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> restGamesParametersMap = new HashMap<>();
    private static final Map<Integer, String> gameCategoryMap = new HashMap<>();


    private static int activeGame = 0;

    private MainScene(){
        logger.debug("Should not be visible!");
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
        Image cursorUp = new Image("/images/finger_up.png");
        sceneMain.setCursor(new ImageCursor(cursorUp));

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
                    textField.setText(playersList.get(playerNr-1).getName());
                }
            }
        }

        // Import the game parameters from the json file and put them in a map
        InputStream is = new FileInputStream("src/main/resources/GameParameters.json");
        String jsonTxt = IOUtils.toString(is, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(jsonTxt);
        JSONArray jsonGamesList = jsonObject.getJSONArray("games");

        for (int gameId = 0; gameId < jsonGamesList.length(); gameId++) {
            JSONObject gameObject = jsonGamesList.getJSONObject(gameId);
            String gameName = gameObject.getString("name");
            String gameCategory = gameObject.getString("category");
            String foulValue;

            switch (gameCategory) {
                case RUNNING:
                    Map<String, String> runningGameParametersMap = new HashMap<>();
                    runningGameParametersMap.put("name", gameName);

                    String groupsOfDice = String.valueOf(gameObject.getInt("groups of dice"));
                    String dicePerGroup = String.valueOf(gameObject.getInt("dice per group"));
                    foulValue = String.valueOf(gameObject.getInt("foul value"));
                    runningGameParametersMap.put("groups", groupsOfDice);
                    runningGameParametersMap.put("dicePerGroup", dicePerGroup);
                    runningGameParametersMap.put("foulValue", foulValue);

                    runningGamesParametersMap.put(gameId, runningGameParametersMap);
                    gameCategoryMap.put(gameId, RUNNING);
                    break;

                case HIGHJUMPING:
                    Map<String, String> highJumpingGameParametersMap = new HashMap<>();
                    highJumpingGameParametersMap.put("name", gameName);

                    String minDice = String.valueOf(gameObject.getInt("min number of dice"));
                    String maxDice = String.valueOf(gameObject.getInt("max number of dice"));
                    foulValue = String.valueOf(gameObject.getInt("foul value"));
                    highJumpingGameParametersMap.put("minDice", minDice);
                    highJumpingGameParametersMap.put("maxDice", maxDice);
                    highJumpingGameParametersMap.put("foulValue", foulValue);

                    highJumpingGamesParametersMap.put(gameId, highJumpingGameParametersMap);
                    gameCategoryMap.put(gameId, HIGHJUMPING);
                    break;

                case THROWING:
                    Map<String, String> throwingGameParametersMap = new HashMap<>();
                    throwingGameParametersMap.put("name", gameName);

                    String nrDice = String.valueOf(gameObject.getInt("number of dice"));
                    String parity = gameObject.getString("parity");
                    throwingGameParametersMap.put("nrDice", nrDice);
                    throwingGameParametersMap.put("parity", parity);

                    throwingGamesParametersMap.put(gameId, throwingGameParametersMap);
                    gameCategoryMap.put(gameId, THROWING);
                    break;

                default:
                    Map<String, String> restGameParametersMap = new HashMap<>();
                    restGameParametersMap.put("name", gameName);
                    restGamesParametersMap.put(gameId, restGameParametersMap);
                    gameCategoryMap.put(gameId, REST);
            }
        }

        // Launch background tasks
        Timeline backgroundTasks = getBackgorundTasksTimeline(scoreSheet, playerPointsMap);
        Timeline runningGames = getRunningGameTimeline(controller);
        backgroundTasks.play();
        runningGames.play();
        activeGame += 1;
    }

    private static Timeline getRunningGameTimeline(MainController controller) {
        Game game;
        AtomicReference<Map<String, String>> gameState = new AtomicReference<>(new HashMap<>());
        gameState.get().put("round", "0");
        gameState.get().put("activePlayer", "0");
        gameState.get().put("currentScore", "0");
        String activeGameCategory = gameCategoryMap.get(activeGame);
        switch (activeGameCategory){
            case RUNNING -> {
                Map<String, String> activeGameMap = runningGamesParametersMap.get(activeGame);
                String name = activeGameMap.get("name");
                String nrDice = activeGameMap.get("dicePerGroup");
                gameState.get().put("name", name);
                gameState.get().put("nrDice", nrDice);
                gameState.get().put("remainingRerolls", "5");

                game = new RunningGame();
            }
            default -> game = new RestGame();
        }

        Timeline runningTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
                gameState.set(game.playGame(gameState.get(), controller));
            // run stuff
        }));
        runningTasks.setCycleCount(Animation.INDEFINITE);
        logger.debug("Starting the game: {}", game);
        return runningTasks;
    }

    private static Timeline getBackgorundTasksTimeline(GridPane scoreSheet, HashMap<Integer, Map<Integer, TextField>> playerPointsMap) {
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

        Timeline backgroundTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
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
            GridPane.setConstraints(highlightGame,0,activeGame + 2);


        }));
        backgroundTasks.setCycleCount(Animation.INDEFINITE);
        return backgroundTasks;
    }

    public static List<Map<Integer, Image>> getSpriteMap() {
        Map<Integer, Image> resultSprites = new HashMap<>();
        Map<Integer, Image> animSprites = new HashMap<>();

        for (int i = 0; i < 6; i++) {
            Image dieSprite = new Image("images/die" + (i + 1) + ".png");
            resultSprites.put(i, dieSprite);
        }
        for (int i = 0; i < 8; i++) {
            Image dieSprite = new Image("images/ani" + (i + 1) + ".png");
            animSprites.put(i, dieSprite);
        }

        List<Map<Integer, Image>> sprites = new ArrayList<>();
        sprites.add(resultSprites);
        sprites.add(animSprites);

        return sprites;
    }

    public static int getNrOfPlayers(){
        return nrOfPlayers;
    }
    public static void setNrOfPlayers(int i){
        nrOfPlayers = i;
    }

}
