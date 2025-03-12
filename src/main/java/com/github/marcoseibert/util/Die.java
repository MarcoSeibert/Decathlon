package com.github.marcoseibert.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


import java.util.Random;


public class Die extends ImageView {
    private final Random ran = new Random();
    private boolean frozen = false;
    private boolean grayed = false;
    private boolean foul = false;
    int value = 1;
    private boolean active = true;
    private static final double SPRITE_SIZE = 128;
    private static final int ANIM_SPRITES = 8;
    private static final int RESULT_SPRITES = 6;
    private static final Image diceSpriteSheet = new Image("images/diceSpriteSheet.png");


    public Die() {
        this.setImage(diceSpriteSheet);
        this.setViewport(new Rectangle2D(0,0, SPRITE_SIZE, SPRITE_SIZE));
        this.setFitHeight(128);
        this.setFitWidth(128);
    }

    public void rollDie(){
        ObjectProperty<Integer> frameProperty = new SimpleObjectProperty<>(ran.nextInt(ANIM_SPRITES) + 1);

        int result = ran.nextInt(RESULT_SPRITES) + 1;
        this.value = result;

        for (long i=1; i < 20; i++){
            delay(25 * i, ()->incrementImage(frameProperty));
        }
        delay(500, ()-> {
            this.setViewport(new Rectangle2D((result - 1) * SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE));
            // TODO
            // rot einfärben, wenn foul value
        });
    }

    private void incrementImage(ObjectProperty<Integer> indexProperty){
        int currentValue = indexProperty.get();
        indexProperty.set((currentValue < ANIM_SPRITES) ? currentValue + 1 : 1);
        indexProperty.addListener(_ -> {
            int frame = indexProperty.get();
            this.setViewport(new Rectangle2D((frame - 1) * SPRITE_SIZE, 4 * SPRITE_SIZE,SPRITE_SIZE, SPRITE_SIZE));
        });
    }

    private static void delay(long millis, Runnable continuation) {
        Task<Void> sleeper = new Task<>() {
            @Override
            protected Void call() {
                try { Thread.sleep(millis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
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

    public int getValue() {
        return value;
    }
}