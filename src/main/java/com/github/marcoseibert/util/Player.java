package com.github.marcoseibert.util;

import java.util.HashMap;
import java.util.Map;

public class Player {
    String name;
    int totalPoints;
    Map<Integer, Integer> pointsMap;

    public Player(String name){
        this.name = name;
        this.totalPoints = 0;
        this.pointsMap = new HashMap<>();
    }

    public String getName() {
        return name;
    }


    public void setPointForGame(int game, int points){
        pointsMap.put(game, points);
    }

    public int getTotalPoints() {
        totalPoints = 0;
        for (var game : pointsMap.entrySet()){
            totalPoints += game.getValue();
        }
        return totalPoints;
    }
}
