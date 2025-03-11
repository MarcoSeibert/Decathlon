package com.github.marcoseibert.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Functions {
    private Functions(){
            }
    public static void updateActiveDice(AtomicReference<Map<String, String>> gameState, List<Die> allDiceList){
        int nrDice = Integer.parseInt(gameState.get().get("nrDice"));
        int round = Integer.parseInt(gameState.get().get("round"));
        for (Die die:allDiceList){die.setActive(false);}
        List<Die> activeDiceList = new ArrayList<>();
        for (int i = 0; i < nrDice; i++){
            activeDiceList.add(allDiceList.get(i + round * nrDice));
        }
        for (Die die:activeDiceList){
            die.setActive(true);
        }
    }
}
