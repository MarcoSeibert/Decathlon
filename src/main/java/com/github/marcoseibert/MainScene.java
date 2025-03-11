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
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class MainScene {
    private static final String RUNNING = "Running";
    private static final String THROWING = "Throwing";
    private static final String HIGHJUMPING = "HighJumping";
    private static final String REST = "Rest";
    private static final String CATEGORY = "category";
    private static final String PARITY = "parity";
    private static int nrOfPlayers;
    private static List<Player> playersList;
    private static AtomicReference<Map<String, String>> gameState = new AtomicReference<>(new HashMap<>());
    private static final HashMap<Integer, Map<Integer, TextField>> playerPointsMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> gamesParameterMap = new HashMap<>();

    private static int activeGame = 0;

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
        Image cursorUp = new Image("/images/finger_up.png");
        sceneMain.setCursor(new ImageCursor(cursorUp));

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
        // Launch background tasks
        Timeline runningGames = getRunningGameTimeline(controller);
        runningGames.play();
        Timeline backgroundTasks = getBackgorundTasksTimeline(scoreSheet, controller.dicePane, playerPointsMap);
        backgroundTasks.play();
    }

    private static Timeline getRunningGameTimeline(MainController controller) {
        // General attributes for game state
        gameState.get().put("gameId", "0");
        gameState.get().put("activePlayer", "0");
        gameState.get().put("gameOver", "true");
        gameState.get().put("score", "0");
        gameState.get().put("lastAchieved", "0"); //previousRound for running, lastHeight for jumping, lastDistance for throwing
        gameState.get().put("nextTry", "false"); //nextRound for running, nextHeight for jumping
        gameState.get().put("currentAttempt", "0"); // for jumping, throwing and rest
        // Attributes for running games
        gameState.get().put("round", "0");
        gameState.get().put("thisRoundScore", "0");
        // Attributes for jumping games
        gameState.get().put("currentHeight", "10");
        // Attributes for throwing games
        // TODO
        // Attributes for the two rest games
        // TODO

        Timeline runningTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
            Map<String, String> activeGameMap = gamesParameterMap.get(activeGame);
            // TODO switch für unterschiedliche categories
            if (Objects.equals(gameState.get().get("gameOver"), "true")) {
                Game game = new RunningGame(controller, activeGameMap);
            }
//            gameState = game.playGame(gameState, controller, activeGameMap);

        }));
        runningTasks.setCycleCount(Animation.INDEFINITE);

        return runningTasks;

//        String activeGameCategory = gameCategoryMap.get(activeGame);
//        switch (activeGameCategory){
//            case RUNNING -> {
//                Map<String, String> activeGameMap = runningGamesParametersMap.get(activeGame);
//                String name = activeGameMap.get("name");
//                String nrDice = activeGameMap.get("dicePerGroup");
//                String nrRounds = activeGameMap.get("groups");
//                gameState.get().put("name", name);
//                gameState.get().put("nrDice", nrDice);
//                gameState.get().put("remainingRerolls", "5");
//                gameState.get().put("nrRounds", nrRounds);
//                if (!Objects.equals(name, "110 m hurdles")){
//                    gameState.get().put("foulValue", "6");
//                } else {
//                    gameState.get().put("foulValue", null);
//                }
//
//                game = new RunningGame(controller);
//            }
//            default -> game = new RestGame();
//        }

    }

    private static Timeline getBackgorundTasksTimeline(GridPane scoreSheet, GridPane dicePane, HashMap<Integer, Map<Integer, TextField>> playerPointsMap) {
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

//            // Highlight on the active game
//            GridPane.setConstraints(highlightGame,0,activeGame + 2);
//
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

    public static AtomicReference<Map<String, String>> getGameState() {
        return gameState;
    }

    public static void setGameState(AtomicReference<Map<String, String>> gameState) {
        MainScene.gameState = gameState;
    }

    public static int getNrOfPlayers(){
        return nrOfPlayers;
    }
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
