package com.github.marcoseibert.util;
import com.github.marcoseibert.controller.MainController;

import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.Map;
import java.util.Objects;


public abstract class Game {
    private Button rollButton = new Button();
    private Button nextButton = new Button();

    protected Game(MainController controller, Map<String, String> activeGameMap){
        createStartingDice(controller, activeGameMap);
        for (Node child:controller.dicePane.getChildren()) {
            if (child instanceof Button button){
                if (Objects.equals(button.getId(), "rollButton")){
                    this.rollButton = button;
                } else if (Objects.equals(button.getId(), "nextButton")) {
                    this.nextButton = button;
                }
            }
        }
    }

    private void createStartingDice(MainController controller, Map<String, String> activeGameMap){
        int initDice = 0;
        String category = activeGameMap.get(Constants.CATEGORY);
        // find initDice the number of dice at the beginning of the game
        switch (category){
            case Constants.RUNNING:
                initDice = Integer.parseInt(activeGameMap.get("dicePerGroup"));
                break;
            case Constants.THROWING:
                initDice = Integer.parseInt(activeGameMap.get("nrDice"));
                break;
            case Constants.HIGHJUMPING:
                initDice = Integer.parseInt(activeGameMap.get("minDice"));
                break;
            default:
                if (Objects.equals(activeGameMap.get(Constants.NAME), "Shot Put")){
                    initDice = 1;
                } else if (Objects.equals(activeGameMap.get(Constants.NAME), "Long jump")){
                    initDice = 5;
                }
        }
        // set starting dice to active
        int nrOfActiveDice = 0;
        for (Node child: controller.dicePane.getChildren()) {
            if (child instanceof Die die) {
                nrOfActiveDice += 1;
                die.setStatus(Constants.ACTIVE);
            }
            if (nrOfActiveDice == initDice){
                break;
            }
        }
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getRollButton() {
        return rollButton;
    }

    public abstract void playGame(MainController controller, Map<String, String> activeGameMap, Map<String, String> gameState);
}


