package com.github.marcoseibert.util;

import com.github.marcoseibert.controller.MainController;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HighJumpingGame extends Game {
    protected static final Logger logger = LogManager.getLogger(HighJumpingGame.class.getSimpleName());

    public HighJumpingGame(Map<String, String> runningGameParametersMap, MainController controller) {
        super();
        String name = runningGameParametersMap.get("name");
        logger.debug("Starting running game: {}", name);
        int nrOfActiveDice = Integer.parseInt(runningGameParametersMap.get("dicePerGroup"));
        List<Die> dieList = new ArrayList<>();
        for (Node child:controller.dicePane.getChildren()){
            if (child instanceof Die die){
                dieList.add(die);
            }
        }
        for (int i=0; i<nrOfActiveDice; i++){
            dieList.get(i).setActive(true);
        }
    }

    @Override
    public void playGame() {
        logger.debug("jumping");
    }


}
