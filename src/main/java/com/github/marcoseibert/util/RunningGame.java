package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class RunningGame extends Game {
    protected static final Logger logger = LogManager.getLogger(RunningGame.class.getSimpleName());

    public RunningGame() {
        super();
    }

    @Override
    public Map<String, String> playGame(Map<String, String> gameState, MainController controller) {
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
        // "dice": 4 (fixed per game, also eher nicht in map?), "round": 1, "active_player": 0/name, "current_score": 0, "remaining_rerolls": 5
        logger.debug(controller.dicePane.getChildren().getFirst().getId());
        return gameState;
    }

}
