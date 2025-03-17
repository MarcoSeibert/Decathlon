package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.util.Constants;
import com.github.marcoseibert.util.Functions;
import com.github.marcoseibert.util.Player;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class StartController {
    private static final Logger logger = LogManager.getLogger(StartController.class.getSimpleName());

    @FXML
    public ChoiceBox<String> playerNumberChoioceBox;
    @FXML
    public GridPane playerGrid;
    @FXML
    public Button startButton;

    public void setPlayerNumber() {
       int nrOfPlayers = Integer.parseInt(playerNumberChoioceBox.getValue());
       MainScene.setNrOfPlayers(nrOfPlayers);
       for (int i=0; i<=3; i++){
           TextField playerName = (TextField) playerGrid.getChildren().get(i);
           if (i<nrOfPlayers) {
               playerName.setVisible(true);
           } else {
               playerName.setVisible(false);
               playerName.setText("");
               playerName.setPromptText("Player " + (i+1));
           }
       }
       logger.debug("Setting players to {}", nrOfPlayers);
    }

    public void startTheGame(MouseEvent mouseEvent) throws IOException {
        boolean start = false;
        List<Player> playersList = new ArrayList<>();
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            for (Node child:playerGrid.getChildren()){
                TextField playerName = (TextField) child;
                if (!playerName.isVisible()) {continue;}
                start = !playerName.getText().isEmpty();
                Player player = new Player(playerName.getText());
                playersList.add(player);
                }
            }
        if (start) {
            Stage stageStart = (Stage) startButton.getScene().getWindow();
            MainScene.start(stageStart, playersList);
            logger.debug("Starting with players:");
            for (Player player:playersList) {
                logger.debug("    {}", player.getName());
            }
        }
    }

    // Change cursor upon clicking the start button
    public void setCursorDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Cursor cursorDown = Functions.getCustomCursor(Constants.DOWN);
            startButton.setCursor(cursorDown);
        }
    }
    // Set custom cursor for text fields
    public void setCursorUp(MouseEvent mouseEvent) {
        GridPane gridPane = (GridPane) mouseEvent.getSource();
        Cursor cursorUp = Functions.getCustomCursor(Constants.UP);
        for (Node child:gridPane.getChildren()){
            if (child instanceof TextField textField){
                textField.setCursor(cursorUp);
            }
        }
    }
}

