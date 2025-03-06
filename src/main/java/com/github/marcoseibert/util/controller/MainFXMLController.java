package com.github.marcoseibert.util.controller;

import com.github.marcoseibert.DecathlonApp;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Objects;


public class MainFXMLController {

    @FXML
    public GridPane scoreSheet;

    public void initialize() {
        for (int i = 1; i <= DecathlonApp.nrOfPlayers; i++) {
            for (int j = 1; j <= 12; j++) {
                TextField text = new TextField();
                text.setAlignment(Pos.CENTER);
                text.setEditable(false);
                text.setFocusTraversable(false);
                text.setFont(Font.font("Trebuchet MS", 32));
                text.setPrefSize(128, 64);
                text.setMaxSize(128, 64);
                if (j==1){
                    text.setDisable(true);
                } else {
                    text.setPromptText("0");
                }
                scoreSheet.add(text, i, j);
            }
        }
        for (Node child : scoreSheet.getChildren()){
            if (Objects.equals(child.getId(), "title")){
                GridPane.setColumnSpan(child, DecathlonApp.nrOfPlayers + 1);
            }
        }

    }

}