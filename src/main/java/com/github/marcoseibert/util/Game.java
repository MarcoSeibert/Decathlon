package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public abstract class Game {
    protected static final List<Die> START_DICE_LIST = new ArrayList<>();

    protected Game(MainController controller, Map<String, String> activeGameMap){
        createStartingDice(controller, activeGameMap);
    }

    private void createStartingDice(MainController controller, Map<String, String> activeGameMap){
        int initDice = 0;
        String category = activeGameMap.get("category");
        // find initDice the number of dice at the beginning of the game
        switch (category){
            case "running":
                initDice = Integer.parseInt(activeGameMap.get("dicePerGroup"));
                break;
            case "throwing":
                initDice = Integer.parseInt(activeGameMap.get("nrDice"));
                break;
            case "highJumping":
                initDice = Integer.parseInt(activeGameMap.get("minDice"));
                break;
            default:
                if (Objects.equals(activeGameMap.get("name"), "Shot Put")){
                    initDice = 1;
                } else if (Objects.equals(activeGameMap.get("name"), "Long jump")){
                    initDice = 5;
                }
        }
        // set starting dice to visible
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

    public abstract void playGame();
}


