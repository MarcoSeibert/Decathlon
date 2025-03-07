package com.github.marcoseibert.util.controller;
import com.github.marcoseibert.DecathlonApp;
import com.github.marcoseibert.MainScene;

import javafx.fxml.FXML;
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
import java.util.Objects;

public class StartController {
    protected static final Logger logger = LogManager.getLogger(DecathlonApp.class.getSimpleName());
    @FXML
    public ChoiceBox<String> playerNumberChoioceBox;
    @FXML
    public GridPane playerGrid;
    @FXML
    public Button startButton;
    private int nrOfPlayers = 1;

    public void setPlayerNumber() {
       nrOfPlayers = Integer.parseInt(playerNumberChoioceBox.getValue());
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
    }

    public void startTheGame(MouseEvent mouseEvent) throws IOException {
        boolean start = false;
        List<String> playerNames = new ArrayList<>();
        if (mouseEvent.getButton() == MouseButton.PRIMARY){
            for (Node child:playerGrid.getChildren()){
                TextField playerName = (TextField) child;
                if (!playerName.isVisible()) {continue;}
                start = !Objects.equals(playerName.getText(), "");
                playerNames.add(playerName.getText());
                }
            }
        if (start) {
            logger.debug("Starting the game");
            Stage stageStart = (Stage) startButton.getScene().getWindow();
            MainScene.start(stageStart, nrOfPlayers, playerNames);
        }

    }
}

