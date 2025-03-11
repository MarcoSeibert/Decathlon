package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class RunningGame extends Game {
    protected static final List<Die> allDiceList = new ArrayList<>();

    public RunningGame(MainController controller) {
        super();
        for (Node child:controller.dicePane.getChildren()) {
            if (child instanceof Die die) {
                allDiceList.add(die);
            }
        }
    }

    @Override
    public AtomicReference<Map<String, String>> playGame(AtomicReference<Map<String, String>> gameState, MainController controller) {
        // Regeln:
        // z.B. 100m
        // 4 Würfel (weiß) / 4 Würfel (grau)
        // RerollButton WeiterButton
        // max 5 Rerolls, dazu Label
        // auch Label für aktuellen Punkte Stand
        // Weiter -> 4 Würfel (blau) / 4 Würfel weiß
        // RerollButton FertigButton(WeiterButton?)
        // Fertig -> Punkte in Player + Sheet
        // Nächster Spieler
        // Spiel fertig
        // Nächstes Spiel (nicht hier)

        // Ideen:
        // 2 Runden
        // Weiter wechselt zwischen Runden
        // DicePane von hier ändern für active dice?
        // Fertig: Punkte in sheet (+Player) eintragen (nochmal GridPane, also controller wäre gut)

        // playGame wird alle 100 ms aufgerufen
        // status map, die jedes Mal übergegeben und angepasst wird?
        // "dice": 4 (fixed per game, also eher nicht in map?), "round": 1, "active_player": 0/name, "previous_rounds_score": 0, "this_round_score": 0, "remaining_rerolls": 5
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
            boolean gameOver = false;
            if (!gameOver) {
                gameState = resetGameStateBetweenPlayers(gameState);
            } else {
            // TODO
            // next game
            // // load new game
            // // new gamestate
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
            Functions.updateActiveDice(gameState, allDiceList, controller.dicePane);
        }
        return gameState;
    }


}
