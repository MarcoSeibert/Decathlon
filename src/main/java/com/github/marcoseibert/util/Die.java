package com.github.marcoseibert.util;
import com.github.marcoseibert.MainScene;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Random;


public class Die extends ImageView {
    protected static final Logger logger = LogManager.getLogger(Die.class.getSimpleName());
    private final Random ran = new Random();
    int value = 1;
    private boolean active = true;

    public Die() {
        Image dieSprite = new Image("/images/die1.png");
        this.setImage(dieSprite);
        this.setFitHeight(128);
        this.setFitWidth(128);
    }

    public void rollDie(){
        List<Map<Integer, Image>> sprites = MainScene.getSpriteMap();
        ObjectProperty<Integer> frameProperty = new SimpleObjectProperty<>(ran.nextInt(8) + 1);
        Map<Integer, Image> resultSprites = sprites.getFirst();
        Map<Integer, Image> animSprites = sprites.getLast();

        int result = ran.nextInt(6) + 1;
        this.value = result;
        Image resultImage = resultSprites.get(result - 1);

        for (long i=1; i < 20; i++){
            delay(25 * i, ()->incrementImage(frameProperty, animSprites));
        }
        logger.debug("Result of die is {}", result);
        delay(500, ()-> this.setImage(resultImage));
    }

    private void incrementImage(ObjectProperty<Integer> indexProperty, Map<Integer, Image> animSprites){
        int currentValue = indexProperty.get();
        indexProperty.set((currentValue < 8) ? currentValue + 1 : 1);
        indexProperty.addListener(_ -> {
            int frame = indexProperty.get();
            Image animImage = animSprites.get(frame - 1);
            this.setImage(animImage);
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean b) {
        active = b;
    }
}