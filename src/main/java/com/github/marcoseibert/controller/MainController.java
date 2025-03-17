package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.util.Constants;
import com.github.marcoseibert.util.Die;
import com.github.marcoseibert.util.Functions;
import com.github.marcoseibert.util.Player;

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
import java.util.Objects;


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
            if (Objects.equals(child, title)){
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
            dicePane.add(die, i%2, i/2 + 2);
        }
    }

    public void clickRollButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            logger.debug("rollButton pressed");
            if (!rolled) {
                rolled = true;
            }
            rollActiveDice();
            Map<String, String> gameState = MainScene.getGameState();
            Map<String, String> activeGameMap = MainScene.getActiveGameMap();
            String activeGameCategory = activeGameMap.get(Constants.CATEGORY);
            switch (activeGameCategory){
                case Constants.RUNNING: {
                    if (Objects.equals(rollButton.getText(), "Reroll")){
                        int newRemainingRerolls = Integer.parseInt(gameState.get(Constants.REMAININGREROLLS)) - 1;
                        gameState.put(Constants.REMAININGREROLLS, String.valueOf(newRemainingRerolls));
                    }
                    break;
                }
                default:

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

            if (Objects.equals(nextButton.getText(), "Finish")){
                Player activePlayer = MainScene.getPlayersList().get(Integer.parseInt(gameState.get("activePlayer")));
                activePlayer.setPointForGame(Integer.parseInt(activeGameMap.get(Constants.GAMEID)), Integer.parseInt(gameState.get(Constants.LASTACHIEVED)) + Integer.parseInt(gameState.get(Constants.THISROUNDSCORE)));
            } else {
                switch (activeGameCategory) {
                    case Constants.RUNNING: {
                        for (Node child : dicePane.getChildren()) {
                            if (child instanceof Die die && die.isActive()) {
                                int prevScore = Integer.parseInt(gameState.get(Constants.LASTACHIEVED));
                                gameState.put(Constants.LASTACHIEVED, String.valueOf(prevScore + die.getValue()));
                                die.setStatus(Constants.GRAYED);
                            } else if (child instanceof Die die && Objects.equals(die.getStatus(), Constants.INACTIVE)) {
                                die.setStatus(Constants.ACTIVE);
                            }
                        }
                        rollActiveDice();
                        int maxRounds = Integer.parseInt(activeGameMap.get("groups"));
                        int currentRound = Integer.parseInt(gameState.get("round"));
                        gameState.put("round", String.valueOf(currentRound + 1));
                        if (currentRound + 1  == maxRounds) {
                            nextButton.setText("Finish");
                        }
                        break;
                    }
                    default:
                }
            }
        }
    }

    //test method
    public void clickOnDie(MouseEvent mouseEvent){
        Node clickedNode = mouseEvent.getPickResult().getIntersectedNode();
        if (clickedNode instanceof Die die){
            die.setStatus(Constants.FOUL);
        }
    }

    public boolean isRolled() {
        return rolled;
    }
}