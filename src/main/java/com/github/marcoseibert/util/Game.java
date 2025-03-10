package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;

import java.util.Map;

public abstract class Game {

    protected Game(){
    }

    public abstract Map<String, String> playGame(Map<String, String> gameState, MainController controller);
}

