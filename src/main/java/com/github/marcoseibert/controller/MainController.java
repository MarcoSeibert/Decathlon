package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.List;
import java.util.Objects;


public class MainController {
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
    public Label rerollLabel;
    @FXML
    public Label scoreLabel;
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
                Cursor cursorUp = Functions.getCustomCursor("up");
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
        for (Node child : scoreSheet.getChildren()){
            if (Objects.equals(child, title)){
                GridPane.setColumnSpan(child, nrOfPlayers + 1);
            }
        }

        // Restrict the game icon to only one port
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

        // Create 8 dice invisible for now
        for (int i=0; i < 8; i++) {
            Die die = new Die();
            die.setVisible(false);
            dicePane.add(die, i%2, i/2 + 2);
        }
    }

    public void clickRollButton(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            if (!rolled) {
                rolled = true;
            }
            for (Node child:dicePane.getChildren()){
                if (child instanceof Die die && die.isActive()){
                    die.rollDie();
                }
            }
        }
    }

    // Change cursor upon clicking the roll button
    public void setCursorDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Cursor cursorDown = Functions.getCustomCursor("down");
            rollButton.setCursor(cursorDown);
        }
    }
    public void setCursorUp(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Cursor cursorUp = Functions.getCustomCursor("up");
            rollButton.setCursor(cursorUp);
        }
    }

    public void clickNextButton(MouseEvent mouseEvent) {

    }
}