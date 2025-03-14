package com.github.marcoseibert.util;
import com.github.marcoseibert.controller.MainController;

import java.util.*;


public class RunningGame extends Game {
    public RunningGame(MainController controller, Map<String, String> activeGameMap) {
        super(controller, activeGameMap);
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
        //Disable roll button if no more rerolls
        getRollButton().setDisable(Integer.parseInt(gameState.get("remainingRerolls")) == 0);
    }
}
