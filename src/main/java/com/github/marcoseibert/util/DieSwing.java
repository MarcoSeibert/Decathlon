package com.github.marcoseibert.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;


public class DieSwing extends JLabel {
    protected static final Logger logger = LogManager.getLogger(DieSwing.class.getSimpleName());
    private final Random ran = new Random();
    int value = 1;

    public DieSwing() {
        BufferedImage dieSprite;
        try {
            dieSprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/die1.png"));
        } catch (IOException e) {
            dieSprite = new BufferedImage(128, 128, 2);
        }
        ImageIcon dieIcon = new ImageIcon(dieSprite);
        this.setIcon(dieIcon);
        this.setSize(128, 128);
    }

    public void rollDie(List<Map<Integer, BufferedImage>> sprites){
        ObjectProperty<Integer> frameProperty = new SimpleObjectProperty<>(ran.nextInt(8) + 1);
        Map<Integer, BufferedImage> resultSprites = sprites.getFirst();
        Map<Integer, BufferedImage> animSprites = sprites.getLast();

        int result = ran.nextInt(6) + 1;
        this.value = result;
        BufferedImage resultImage = resultSprites.get(result - 1);
        ImageIcon resultIcon = new ImageIcon(resultImage);

        for (long i=1; i < 20; i++){
            delay(25 * i, ()->incrementImage(frameProperty, animSprites));
        }
        logger.info("Result of die is {}", result);
        delay(500, ()-> this.setIcon(resultIcon));
    }

    private void incrementImage(ObjectProperty<Integer> indexProperty, Map<Integer, BufferedImage> animSprites){
        int currentValue = indexProperty.get();
        indexProperty.set((currentValue < 8) ? currentValue + 1 : 1);
        indexProperty.addListener(_ -> {
            int frame = indexProperty.get();
            BufferedImage animImage = animSprites.get(frame - 1);
            ImageIcon animIcon = new ImageIcon(animImage);
            this.setIcon(animIcon);
        });
    }

    private static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try { Thread.sleep(millis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Thread interrupted: ", e.fillInStackTrace());
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(_ -> continuation.run());
        new Thread(sleeper).start();
    }
}