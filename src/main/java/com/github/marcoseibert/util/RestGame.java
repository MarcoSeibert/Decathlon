package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;

import java.util.Map;


public class RestGame extends Game {
    public RestGame() {
        super();
    }

    @Override
    public Map<String, String> playGame(Map<String, String> gameState, MainController controller) {
        return gameState;
    }

}
