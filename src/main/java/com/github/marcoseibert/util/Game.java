package com.github.marcoseibert.util;

import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.controller.MainController;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Game {
    private static final String RUNNING = "Running";
    private static final String THROWING = "Throwing";
    private static final String HIGHJUMPING = "HighJumping";
    private static final String ACTIVEPLAYER = "activePlayer";

    protected static final List<Die> allDiceList = new ArrayList<>();

    protected Game(MainController controller, Map<String, String> activeGameMap){
        int initDice = 0;
        String category = activeGameMap.get("category");
        switch (category){
            case RUNNING:
                initDice = 8;
                break;
            case THROWING:
                initDice = Integer.parseInt(activeGameMap.get("nrDice"));
                break;
            case HIGHJUMPING:
                initDice = Integer.parseInt(activeGameMap.get("minDice"));
                break;
            default:
                if (Objects.equals(activeGameMap.get("name"), "Shot Put")){
                    initDice = 1;
                } else if (Objects.equals(activeGameMap.get("name"), "Long jump")){
                    initDice = 5;
                }
        }
        for (Node child:controller.dicePane.getChildren()) {
            if (child instanceof Die die) {
                allDiceList.add(die);
            }
            if (allDiceList.size() == initDice){
                break;
            }
        }
    }

    public abstract AtomicReference<Map<String, String>> playGame(AtomicReference<Map<String, String>> gameState, MainController controller, Map<String, String> gamesParameterMap);

    public static AtomicReference<Map<String, String>> resetGameStateBetweenPlayers(AtomicReference<Map<String, String>> gameState){
        int lastPlayer = Integer.parseInt(gameState.get().get(ACTIVEPLAYER));
        gameState.get().put(ACTIVEPLAYER, String.valueOf(lastPlayer + 1));
        switch (gameState.get().get("category")){
            case RUNNING:
                gameState.get().put("round", "0");
                gameState.get().put("remainingRerolls", "5");
                gameState.get().put("previousRoundsScore", "0");
                gameState.get().put("thisRoundScore", "0");
        }

        return gameState;
    }

    public void writePointsInScoreSheet(AtomicReference<Map<String, String>> gameState) {
        List<Player> playerList = MainScene.getPlayersList();
        int activePlayerNr = Integer.parseInt(gameState.get().get(ACTIVEPLAYER));
        int activeGame = Integer.parseInt(gameState.get().get("gameId"));
        int score = Integer.parseInt(gameState.get().get("previousRoundsScore"));
        Map<Integer, Map<Integer, TextField>> playerPointsMap = MainScene.getPlayerPointsMap();
        // Set points in ScoreSheet
        playerPointsMap.get(activePlayerNr + 1).get(activeGame + 2).setText(String.valueOf(score));
        // Set points in Player
        Player activePlayer = playerList.get(activePlayerNr);
        activePlayer.setPointForGame(activePlayerNr, score);
    }

    public static List<Die> getAllDiceList() {
        return allDiceList;
    }
}

