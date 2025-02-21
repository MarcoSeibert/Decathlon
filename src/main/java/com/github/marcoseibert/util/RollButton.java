package com.github.marcoseibert.util;

import com.github.marcoseibert.ui.DecathlonUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class RollButton extends MyButton {
    protected static final Logger logger = LogManager.getLogger(RollButton.class.getSimpleName());

    public RollButton(DecathlonUI frame, Map<Integer, Die> diceMap, List<Map<Integer, BufferedImage>> sprites){
        super(frame);
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                logger.info("Rolling the dice");
                diceMap.forEach( (_, d) -> d.rollDie(sprites));
            }
        });
        this.setText("Roll!");
    }
}
