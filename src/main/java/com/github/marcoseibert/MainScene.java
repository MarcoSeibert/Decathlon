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


public class MainScene {
    private static final String RUNNING = "Running";
    private static final String THROWING = "Throwing";
    private static final String HIGHJUMPING = "HighJumping";
    private static final String REST = "Rest";
    private static final String CATEGORY = "category";
    private static final String PARITY = "parity";
    private static int nrOfPlayers;
    private static List<Player> playersList;
    private static Map<String, String> gameState = new HashMap<>();
    private static final HashMap<Integer, Map<Integer, TextField>> playerPointsMap = new HashMap<>();
    private static final Map<Integer, Map<String, String>> gamesParameterMap = new HashMap<>();
    protected static final List<Die> START_DICE_LIST = new ArrayList<>();
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
        Image cursorUp = new Image("/images/finger_up.png");
        sceneMain.setCursor(new ImageCursor(cursorUp));

        createPlayerPointsMap(controller);
        createGamesParameterMap();
        initGameState();

        // Launch task
        Timeline timeLine = getTimeLine(controller);
        timeLine.play();
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
                if (gameNr == 1){
                    textField.setText(playersList.get(playerNr-1).getName());
                }
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

    private static Timeline getTimeLine(MainController controller) {
        Timeline runningTasks = new Timeline(new KeyFrame(Duration.millis(100), _ -> {
            if (Objects.equals(gameState.get("gameOver"), "true")) {
                activeGameMap = gamesParameterMap.get(activeGameId);
                createStartingDice(controller, activeGameMap);
                // TODO switch für unterschiedliche categories
                game = new RunningGame();
            }
            game.playGame(gameState, controller, activeGameMap);

        }));
        runningTasks.setCycleCount(Animation.INDEFINITE);
        return runningTasks;
    }

    private static void createStartingDice(MainController controller, Map<String, String> activeGameMap) {
        int initDice = 0;
        String category = activeGameMap.get(CATEGORY);
        switch (category){
            case RUNNING:
                initDice = Integer.parseInt(activeGameMap.get("dicePerGroup"));
                break;
            case THROWING:
                initDice = Integer.parseInt(activeGameMap.get("nrDice"));
                break;
            case HIGHJUMPING:
                initDice = Integer.parseInt(activeGameMap.get("minDice"));
                break;
            default:
                if (Objects.equals(activeGameMap.get("name"), "Shot Put")){
                    initDice = 1;
                } else if (Objects.equals(activeGameMap.get("name"), "Long jump")){
                    initDice = 5;
                }
        }
        for (Node child: controller.dicePane.getChildren()) {
            if (child instanceof Die die) {
                START_DICE_LIST.add(die);
                die.setVisible(true);
            }
            if (START_DICE_LIST.size() == initDice){
                break;
            }
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

    public static Map<String, String> getGameState() {
        return gameState;
    }

    public static void setGameState(Map<String, String> gameState) {
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

    public static List<Die> getStartDiceList() {
        return START_DICE_LIST;
    }
}
