package com.github.marcoseibert.util;
import com.github.marcoseibert.controller.MainController;

import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.*;

public class RunningGame extends Game {

    public RunningGame() {
        super();
    }

    @Override
    public void playGame(Map<String, String> gameState, MainController controller, Map<String, String> gamesParameterMap) {
        // happens after last round finished, cleanup,
        //      Reroll->Roll auf RollButton
        //      WritePoints
        //      setRolled Flag -> False
        //      continueButton visible = false
        //      check if current player was last player
        // => nicht hier, passiert nach jedem Spiel, nicht nur running

        if (controller.isRolled()) {
            for (Node child : controller.dicePane.getChildren()) {
                if (Objects.equals(child.getId(), "rollButton")) {
                    ((Button) child).setText("Reroll");
                    break;
                }
            }
//            }
//            Functions.updateActiveDice(gameState, MainScene.getStartDiceList(), controller.dicePane);
//
        }


    }
}
