package com.github.marcoseibert.controller;
import com.github.marcoseibert.MainScene;

import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
    protected static final Logger logger = LogManager.getLogger(StartController.class.getSimpleName());
    private int nrOfPlayers = 1;

    @FXML
    public ChoiceBox<String> playerNumberChoioceBox;
    @FXML
    public GridPane playerGrid;
    @FXML
    public Button startButton;

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

    // Change cursor upon clicking the start button
    public void setCursorDown(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            Image cursorDown = new Image("/images/finger_down.png");
            startButton.setCursor(new ImageCursor(cursorDown));
        }
    }
    // Set custom curso for text fields
    public void setCursorUp(MouseEvent mouseEvent) {
        GridPane gridPane = (GridPane) mouseEvent.getSource();
        Image cursorUp = new Image("/images/finger_up.png");
        for (Node child:gridPane.getChildren()){
            if (child instanceof TextField textField){
                textField.setCursor(new ImageCursor(cursorUp));
            }
        }
    }
}

