package com.github.marcoseibert.util;
import com.github.marcoseibert.controller.MainController;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.*;


public class RunningGame extends Game {
    public RunningGame(MainController controller, Map<String, String> activeGameMap) {
        super(controller, activeGameMap);
        controller.topLabel.setVisible(false);
    }

    @Override
    public void playGame(MainController controller, Map<String, String> activeGameMap, Map<String, String> gameState) {
        //Show next button after rolling
        getNextButton().setVisible(controller.isRolled());
        //Change text on roll button after rolling
        if (controller.isRolled()){
            getRollButton().setText("Reroll");
        } else {
            getRollButton().setText("Roll");
        }
        updateRerollCount(controller.dicePane, gameState);
        //Disable roll button if no more rerolls
        getRollButton().setDisable(Integer.parseInt(gameState.get(Constants.REMAININGREROLLS)) == 0);

        int thisRoundScore = Integer.parseInt(gameState.get(Constants.THISROUNDSCORE));
        int previousRoundsScore = Integer.parseInt(gameState.get(Constants.LASTACHIEVED));
        String currentScore = String.valueOf(thisRoundScore + previousRoundsScore);
        gameState.put("currentScore", currentScore);
    }


    private static void updateRerollCount(GridPane dicePane, Map<String, String> gameState) {
        for (Node child:dicePane.getChildren()){
            if (child.getId().equals("lowerLabel")){
                ((Label) child).setText("Rerolls: " + gameState.get(Constants.REMAININGREROLLS));
            }
        }
    }

}
