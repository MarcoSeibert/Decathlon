package com.github.marcoseibert.util;
import com.github.marcoseibert.controller.MainController;

import javafx.scene.Node;

import java.util.Map;


public class RestGame extends Game {
    public RestGame(MainController controller, Map<String, String> activeGameMap) {
        super(controller, activeGameMap);
        controller.topLabel.setVisible(true);
        if (activeGameMap.get(Constants.NAME).equals(Constants.LONGJUMP)) {
            controller.lowerLabel.setText("Frozen Total: 0");
            controller.nextButton.setText("Jump");
        }
    }

    @Override
    public void playGame(MainController controller, Map<String, String> activeGameMap, Map<String, String> gameState) {
        getNextButton().setVisible(controller.isRolled());
        controller.topLabel.setText("Current Attempt: " + gameState.get(Constants.CURRENTATTEMPT));
        if (activeGameMap.get(Constants.NAME).equals(Constants.LONGJUMP)) {
            updateFreezeCount(controller, gameState);
            if (Integer.parseInt(gameState.get(Constants.FROZENSUM)) >= 9) {
                controller.nextButton.setText("Foul");
                controller.rollButton.setText("Reroll");
                controller.rollButton.setDisable(true);
            } else {
                controller.nextButton.setText("Jump");
                boolean thisRoundFrozen = false;
                for (Node child:controller.dicePane.getChildren()){
                    if (child instanceof Die die && die.getStatus().equals(Constants.FROZEN) && !die.isLocked()){
                        thisRoundFrozen = true;
                    }
                }
                if (controller.isRolled()) {
                    if (!thisRoundFrozen) {
                        controller.rollButton.setText("Freeze");
                        controller.rollButton.setDisable(true);
                        controller.nextButton.setDisable(true);
                    } else {
                        controller.rollButton.setText("Reroll");
                        controller.rollButton.setDisable(false);
                        controller.nextButton.setDisable(false);
                    }
                } else {
                    controller.rollButton.setDisable(false);
                    getRollButton().setText("Roll");
                }
            }
        }
    }

    private static void updateFreezeCount(MainController controller, Map<String, String> gameState) {
        int freezeSum = 0;
        int freezeAmount = 0;
        for (Node child:controller.dicePane.getChildren()){
            if (child instanceof Die die && (die.getStatus().equals(Constants.FROZEN) || die.isLocked())){
                freezeSum += die.getValue();
                freezeAmount += 1;
            }
        }
        controller.lowerLabel.setText("Frozen Total: " + freezeSum);
        gameState.put(Constants.FROZENSUM, String.valueOf(freezeSum));
        gameState.put(Constants.FROZENAMOUNT, String.valueOf(freezeAmount));
    }
}
