package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.util.Die;

import com.github.marcoseibert.util.Functions;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainController {

    private Map<String, String> gameState;
    private static final String THISROUNDSCORE = "thisRoundScore";

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
    public Button continueButton;
    @FXML
    public Label rerollLabel;
    @FXML
    public Label scoreLabel;

    public void initialize() {
        int nrOfPlayers = MainScene.getNrOfPlayers();
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
                Image cursorUp = new Image("/images/finger_up.png");
                text.setCursor(new ImageCursor(cursorUp));
                switch (j) {
                    case 1 -> text.setDisable(true);
                    case 12 -> text.setText("0");
                    default -> text.setPromptText("0");
                }
                scoreSheet.add(text, i, j);
            }
        }
        for (Node child : scoreSheet.getChildren()){
            if (Objects.equals(child, title)){
                GridPane.setColumnSpan(child, nrOfPlayers + 1);
            }
        }
        for (int i=0; i < 8; i++) {
            Die die = new Die();
            die.setVisible(false);
            dicePane.add(die, i%2, i/2 + 2);
        }
    }

    public void rollActiveDice(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            gameState = MainScene.getGameState();
            if (!rolled) {
                rolled = true;
                for (Node child:dicePane.getChildren()){
                    if (Objects.equals(child.getId(), "continueButton")){
                        child.setVisible(true);
                        break;
                    }
                }
            } else {
                int oldRerolls = Integer.parseInt(gameState.get("remainingRerolls"));
                gameState.put("remainingRerolls", String.valueOf(oldRerolls - 1));
            }
            int thisRoundScore = getThisRoundScore();
            gameState.put(THISROUNDSCORE, String.valueOf(thisRoundScore));
            MainScene.setGameState(gameState);
        }
    }

    private int getThisRoundScore() {
        int thisRoundScore = 0;
        for (Node child : dicePane.getChildren()) {
            if (child instanceof Die die && die.isActive()) {
                die.rollDie();
                int dieValue = die.getValue();
                if (String.valueOf(dieValue).equals(gameState.get("foulValue"))){
                    thisRoundScore -= die.getValue();
                } else {
                    thisRoundScore += die.getValue();
                }
            }
        }
        return thisRoundScore;
    }

    // Change cursor upon clicking the roll button
    public void setCursorDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Image cursorDown = new Image("/images/finger_down.png");
            Button clickedButton = getClickedButton(mouseEvent);
            clickedButton.setCursor(new ImageCursor(cursorDown));
        }
    }
    public void setCursorUp(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Image cursorUp = new Image("/images/finger_up.png");
            Button clickedButton = getClickedButton(mouseEvent);
            clickedButton.setCursor(new ImageCursor(cursorUp));
        }
    }

    private Button getClickedButton(MouseEvent mouseEvent){
        Node clickedNode = mouseEvent.getPickResult().getIntersectedNode();
        if (clickedNode instanceof Button button){
            return button;
        } else {
            return (Button) clickedNode.getParent();
        }
    }

    public boolean isRolled() {
        return rolled;
    }

    public void setRolled(boolean rolled) {
        this.rolled = rolled;
    }

    public void nextRound(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            gameState = MainScene.getGameState();
            int currentRound = Integer.parseInt(gameState.get("round"));
            int maxRounds = Integer.parseInt(gameState.get("nrRounds"));
            gameState.put("round", String.valueOf(currentRound + 1));
            gameState.put("nextRound", "true");
            int previousRoundsScore = Integer.parseInt(gameState.get("previousRoundsScore"));
            int thisRoundScore = Integer.parseInt(gameState.get(THISROUNDSCORE));
            gameState.put("previousRoundsScore", String.valueOf(previousRoundsScore + thisRoundScore));
            if (currentRound + 1 == maxRounds) {
                //TODO
            } else {
                List<Die> allDiceList = new ArrayList<>();
                for (Node child : dicePane.getChildren()) {
                    if (child instanceof Die die) {
                        allDiceList.add(die);
                        }
                    }
                Functions.updateActiveDice(gameState, allDiceList, dicePane);
                int thisRoundScoreNextRound = getThisRoundScore();
                gameState.put(THISROUNDSCORE, String.valueOf(thisRoundScoreNextRound));
                }
            MainScene.setGameState(gameState);
        }
    }
}