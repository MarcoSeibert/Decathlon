package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public abstract class Game {

    protected Game(){
    }

    public abstract AtomicReference<Map<String, String>> playGame(AtomicReference<Map<String, String>> gameState, MainController controller);
}

