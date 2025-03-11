package com.github.marcoseibert.util;

import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.controller.MainController;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunningGame extends Game {

    public RunningGame() {
        super();
    }

    @Override
    public AtomicReference<Map<String, String>> playGame(AtomicReference<Map<String, String>> gameState, MainController controller, Map<String, String> gamesParameterMap) {
        //
        if (Objects.equals(gameState.get().get("nrRounds"), gameState.get().get("round"))){
            for (Node child : controller.dicePane.getChildren()) {
                if (Objects.equals(child.getId(), "rollButton")) {
                    ((Button) child).setText("Roll");
                    break;
                }
            }
            writePointsInScoreSheet(gameState);
            controller.setRolled(false);
            for (Node child : controller.dicePane.getChildren()) {
                if (Objects.equals(child.getId(), "continueButton")) {
                    child.setVisible(false);
                    break;
                }
            }
            int activePlayer = Integer.parseInt(gameState.get().get("activePlayer"));
            int nrOfPlayers = MainScene.getPlayersList().size();
            boolean gameOver = (activePlayer + 1 == nrOfPlayers);
            if (!gameOver) {
                gameState = resetGameStateBetweenPlayers(gameState);
            } else {
                System.out.println("GameOver");
            }
        } else {
            if (controller.isRolled()) {
                for (Node child : controller.dicePane.getChildren()) {
                    if (Objects.equals(child.getId(), "rollButton")) {
                        ((Button) child).setText("Reroll");
                        break;
                    }
                }
            }
            Functions.updateActiveDice(gameState, MainScene.getStartDiceList(), controller.dicePane);
        }
        return gameState;
    }


}
