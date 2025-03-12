package com.github.marcoseibert.util;

import com.github.marcoseibert.MainScene;
import com.github.marcoseibert.controller.MainController;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Map;

public abstract class Game {
    private static final String RUNNING = "Running";
    private static final String THROWING = "Throwing";
    private static final String HIGHJUMPING = "HighJumping";
    private static final String ACTIVEPLAYER = "activePlayer";


    protected Game(){
    }

    public abstract void playGame(Map<String, String> gameState, MainController controller, Map<String, String> gamesParameterMap);

    public static void resetGameStateBetweenPlayers(Map<String, String> gameState){
        int lastPlayer = Integer.parseInt(gameState.get(ACTIVEPLAYER));
        gameState.put(ACTIVEPLAYER, String.valueOf(lastPlayer + 1));
        switch (gameState.get("category")){
            case RUNNING:
                gameState.put("round", "0");
                gameState.put("remainingRerolls", "5");
                gameState.put("previousRoundsScore", "0");
                gameState.put("thisRoundScore", "0");
        }
    }

    public void writePointsInScoreSheet(Map<String, String> gameState) {
        List<Player> playerList = MainScene.getPlayersList();
        int activePlayerNr = Integer.parseInt(gameState.get(ACTIVEPLAYER));
        int activeGame = Integer.parseInt(gameState.get("gameId"));
        int score = Integer.parseInt(gameState.get("previousRoundsScore"));
        Map<Integer, Map<Integer, TextField>> playerPointsMap = MainScene.getPlayerPointsMap();
        // Set points in ScoreSheet
        playerPointsMap.get(activePlayerNr + 1).get(activeGame + 2).setText(String.valueOf(score));
        // Set points in Player
        Player activePlayer = playerList.get(activePlayerNr);
        activePlayer.setPointForGame(activePlayerNr, score);
    }
}

