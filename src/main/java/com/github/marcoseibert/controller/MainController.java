package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.util.Constants;
import com.github.marcoseibert.util.Die;
import com.github.marcoseibert.util.Functions;
import com.github.marcoseibert.util.Player;
import com.github.marcoseibert.util.Game;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;


public class MainController {
    private static final Logger logger = LogManager.getLogger(MainController.class.getSimpleName());
    private static final double SPRITE_SIZE = 64;
    private boolean rolled = false;

    @FXML
    public GridPane scoreSheet;
    @FXML
    public GridPane dicePane;
    @FXML
    public Label title;
    @FXML
    public Button rollButton;
    @FXML
    public Button nextButton;
    @FXML
    public Label lowerLabel;
    @FXML
    public Label upperLabel;
    @FXML
    public Label topLabel;
    @FXML
    public ImageView running100;
    @FXML
    public ImageView running400;
    @FXML
    public ImageView running1500;
    @FXML
    public ImageView longJump;
    @FXML
    public ImageView shotPut;
    @FXML
    public ImageView highJump;
    @FXML
    public ImageView hurdles;
    @FXML
    public ImageView discus;
    @FXML
    public ImageView poleVault;
    @FXML
    public ImageView javelin;
    @FXML
    public Rectangle highlightGame;

    public void initialize() {
        // for every player add the text fields to hold the name and the points of the single games
        logger.trace("Initialize text fields");
        List<Player> playerList = MainScene.getPlayersList();
        int nrOfPlayers = playerList.size();
        for (int i = 1; i <= nrOfPlayers; i++) {
            for (int j = 1; j <= 12; j++) {
                TextField text = new TextField();
                text.setAlignment(Pos.CENTER);
                text.setEditable(false);
                text.setFocusTraversable(false);
                text.setFont(Font.font("Trebuchet MS", 32));
                int width = 512 / nrOfPlayers;
                text.setPrefSize(width, 64);
                text.setMaxSize(width, 64);
                Cursor cursorUp = Functions.getCustomCursor(Constants.UP);
                text.setCursor(cursorUp);
                switch (j) {
                    case 1 -> text.setText(playerList.get(i-1).getName());
                    case 12 -> text.setText("0");
                    default -> text.setPromptText("0");
                }
                scoreSheet.add(text, i, j);
            }
        }

        // Dynamical spanning of the title
        logger.trace("Span title");
        for (Node child : scoreSheet.getChildren()){
            if (child.equals(title)){
                GridPane.setColumnSpan(child, nrOfPlayers + 1);
            }
        }

        // Restrict the game icon to only one sport
        logger.trace("Setting viewports for icons");
        Rectangle2D runningRect = new Rectangle2D(2 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
        running100.setViewport(runningRect);
        longJump.setViewport(new Rectangle2D(0, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE));
        shotPut.setViewport(new Rectangle2D(3 * SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE));
        highJump.setViewport(new Rectangle2D(SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE));
        running400.setViewport(runningRect);
        hurdles.setViewport(new Rectangle2D(2 * SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE));
        discus.setViewport(new Rectangle2D(0,0, SPRITE_SIZE, SPRITE_SIZE));
        poleVault.setViewport(new Rectangle2D(SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE));
        javelin.setViewport(new Rectangle2D(3 * SPRITE_SIZE,0, SPRITE_SIZE, SPRITE_SIZE));
        running1500.setViewport(runningRect);

        // Create 8 dice
        logger.trace("Creating dice");
        for (int i=0; i < 8; i++) {
            Die die = new Die();
            die.setOnMouseClicked(this::clickOnDie);
            dicePane.add(die, i%2, i/2 + 3);
        }
    }

    public void clickRollButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            logger.debug("rollButton pressed");
            if (!rolled) {
                rolled = true;
            }
            Map<String, String> gameState = MainScene.getGameState();
            Map<String, String> activeGameMap = MainScene.getActiveGameMap();
            String activeGameCategory = activeGameMap.get(Constants.CATEGORY);
            switch (activeGameCategory){
                case Constants.RUNNING: {
                    if (rollButton.getText().equals("Reroll")){
                        int newRemainingRerolls = Integer.parseInt(gameState.get(Constants.REMAININGREROLLS)) - 1;
                        gameState.put(Constants.REMAININGREROLLS, String.valueOf(newRemainingRerolls));
                    }
                    break;
                }
                case Constants.THROWING, Constants.HIGHJUMPING: throw new AssertionError();
                default: {
                    if (activeGameMap.get(Constants.NAME).equals(Constants.LONGJUMP)) {
                        rerollLongJump();
                    }
                }
            }
            rollActiveDice();
        }
    }

    private void rerollLongJump() {
        // lock the frozen dice to prevent from unfreezing
        for (Node child:dicePane.getChildren()){
            if (child instanceof Die die && die.getStatus().equals(Constants.FROZEN)){
                die.setLocked(true);
            }
        }
    }

    private void rollActiveDice() {
        Map<String, String> gameState = MainScene.getGameState();
        gameState.put(Constants.THISROUNDSCORE, "0");
        for (Node child:dicePane.getChildren()){
            if (child instanceof Die die && die.isActive()){
                die.rollDie();
                int thisRoundScore = Integer.parseInt(gameState.get(Constants.THISROUNDSCORE));
                gameState.put(Constants.THISROUNDSCORE, String.valueOf(thisRoundScore + die.getValue()));
            }
        }
    }

    // Change cursor upon clicking the roll button
    public void setCursorDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Cursor cursorDown = Functions.getCustomCursor(Constants.DOWN);
            rollButton.setCursor(cursorDown);
        }
    }

    public void setCursorUp(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Cursor cursorUp = Functions.getCustomCursor(Constants.UP);
            rollButton.setCursor(cursorUp);
        }
    }

    public void clickNextButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            logger.debug("nextButton pressed");
            Map<String, String> gameState = MainScene.getGameState();
            Map<String, String> activeGameMap = MainScene.getActiveGameMap();
            String activeGameCategory = activeGameMap.get(Constants.CATEGORY);
            String textOnButton = nextButton.getText();
            switch (textOnButton) {
                case "Finish" -> clickOnFinishButton(gameState, activeGameMap);
                case "Foul" -> clickOnFoulButton(gameState, activeGameMap);
                case "Jump" -> clickOnJumpButton(gameState, activeGameMap);
                default -> {
                    switch (activeGameCategory) {
                        case Constants.RUNNING: {
                            nextRoundRunning(gameState, activeGameMap);
                            break;
                        }
                        case Constants.THROWING, Constants.HIGHJUMPING: {
                            throw new AssertionError();
                        }
                        default:
                    }
                }
            }
        }
    }

    private void clickOnJumpButton(Map<String, String> gameState, Map<String, String> activeGameMap) {
        if (activeGameMap.get(Constants.NAME).equals(Constants.LONGJUMP)){
            int nrOfDiceForJumping = Integer.parseInt(gameState.get(Constants.FROZENAMOUNT));
            int nrOfActivatedDice = 0;
            for (Node child:dicePane.getChildren()){
                if (child instanceof Die die && nrOfActivatedDice < nrOfDiceForJumping){
                    die.setStatus(Constants.ACTIVE);
                    nrOfActivatedDice++;
                    die.setLocked(false);
                    die.setStatus(Constants.ACTIVE);
                } else if (child instanceof Die die) {
                    die.setLocked(false);
                    die.setStatus(Constants.INACTIVE);
                }
            }
            rolled = false;
            gameState.put(Constants.ROUND, "2");
        }
    }

    private void nextRoundRunning(Map<String, String> gameState, Map<String, String> activeGameMap) {
        for (Node child : dicePane.getChildren()) {
            if (child instanceof Die die && die.isActive()) {
                int prevScore = Integer.parseInt(gameState.get(Constants.LASTACHIEVED));
                gameState.put(Constants.LASTACHIEVED, String.valueOf(prevScore + die.getValue()));
                die.setStatus(Constants.GRAYED);
            } else if (child instanceof Die die && die.getStatus().equals(Constants.INACTIVE)) {
                die.setStatus(Constants.ACTIVE);
            }
        }
        rollActiveDice();
        int maxRounds = Integer.parseInt(activeGameMap.get("groups"));
        int currentRound = Integer.parseInt(gameState.get(Constants.ROUND));
        gameState.put(Constants.ROUND, String.valueOf(currentRound + 1));
        if (currentRound + 1  == maxRounds) {
            nextButton.setText("Finish");
        }
    }

    private void clickOnFoulButton(Map<String, String> gameState, Map<String, String> activeGameMap) {
        int currentAttempt = Integer.parseInt(gameState.get(Constants.CURRENTATTEMPT));
        gameState.put(Constants.CURRENTATTEMPT, String.valueOf(currentAttempt + 1));
        rolled = false;
        for (Node child : dicePane.getChildren()) {
            if (child instanceof Die die) {
                die.setValue(0);
                die.setStatus("inactive");
                die.setViewport(new Rectangle2D(0, 0, SPRITE_SIZE, SPRITE_SIZE));
            }
        }
        Game.createStartingDice(this, activeGameMap);
        gameState.put(Constants.FROZENAMOUNT, "0");
        gameState.put(Constants.FROZENSUM, "0");
    }

    private void clickOnFinishButton(Map<String, String> gameState, Map<String, String> activeGameMap) {
        List<Player> playersList = MainScene.getPlayersList();
        int nrOfPlayers = playersList.size();
        Player activePlayer = playersList.get(Integer.parseInt(gameState.get(Constants.ACTIVEPLAYER)));
        activePlayer.setPointForGame(Integer.parseInt(activeGameMap.get(Constants.GAMEID)), Integer.parseInt(gameState.get(Constants.LASTACHIEVED)) + Integer.parseInt(gameState.get(Constants.THISROUNDSCORE)));
        rolled = false;
        nextButton.setText("Next");
        for (Node child : dicePane.getChildren()) {
            if (child instanceof Die die) {
                die.setValue(0);
                die.setStatus("inactive");
                die.setViewport(new Rectangle2D(0, 0, SPRITE_SIZE, SPRITE_SIZE));
            }
        }
        if (playersList.indexOf(activePlayer) + 1 == nrOfPlayers) {
            gameState.put(Constants.GAMEOVER, "true");
        } else {
            resetGameStateForNextPlayer();
            Game.createStartingDice(this, activeGameMap);
        }
    }

    private void resetGameStateForNextPlayer() {
        Map<String, String> gameState = MainScene.getGameState();
        int activePlayer = Integer.parseInt(gameState.get(Constants.ACTIVEPLAYER));
        gameState.put(Constants.ACTIVEPLAYER, String.valueOf(activePlayer + 1));
        gameState.put(Constants.LASTACHIEVED, "0");
        gameState.put(Constants.CURRENTATTEMPT, "0");
        gameState.put(Constants.ROUND, "1");
        gameState.put(Constants.THISROUNDSCORE, "0");
        gameState.put(Constants.REMAININGREROLLS, "5");
        gameState.put("currentHeight", "10");
    }

    //test method
    public void clickOnDie(MouseEvent mouseEvent){
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Map<String, String> activeGameMap = MainScene.getActiveGameMap();
            Node clickedNode = mouseEvent.getPickResult().getIntersectedNode();
            if (isRolled() && clickedNode instanceof Die die) {
                switch (activeGameMap.get(Constants.NAME)){
                    case Constants.LONGJUMP:{
                        if (die.getStatus().equals(Constants.ACTIVE)) {
                            die.setStatus(Constants.FROZEN);
                        } else if (die.getStatus().equals(Constants.FROZEN) && !die.isLocked()){
                            die.setStatus(Constants.ACTIVE);
                        }
                        break;
                    }
                    case "discus", "javelin":{
                        throw new AssertionError();
                    }
                }

            }
        }
    }

    public boolean isRolled() {
        return rolled;
    }
}