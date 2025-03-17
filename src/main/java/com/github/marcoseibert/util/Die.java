package com.github.marcoseibert.util;
import com.github.marcoseibert.MainScene;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;


public class Die extends ImageView {
    private final Random ran = new Random();
    private String status = Constants.INACTIVE;
    private boolean foul = false;
    private boolean locked = false;
    int value = 1;
    private static final double SPRITE_SIZE = 128;
    private static final int ANIM_SPRITES = 8;
    private static final int RESULT_SPRITES = 6;
    private static final Image diceSpriteSheet = new Image("images/diceSpriteSheet.png");

    public boolean isRollingDone() {
        return rollingDone;
    }

    private boolean rollingDone = false;


    public Die() {
        this.setImage(diceSpriteSheet);
        this.setViewport(new Rectangle2D(0,0, SPRITE_SIZE, SPRITE_SIZE));
        this.setFitHeight(128);
        this.setFitWidth(128);
        this.setVisible(false);
    }

    public void rollDie(){
        rollingDone = false;
        ObjectProperty<Integer> frameProperty = new SimpleObjectProperty<>(ran.nextInt(ANIM_SPRITES) + 1);

        int result = ran.nextInt(RESULT_SPRITES) + 1;
        this.value = result;
        String currentStatus = status;
        this.status = Constants.ACTIVE;

        //Set foul value according to category
        int foulValue;
        if (MainScene.getActiveGameMap().get(Constants.CATEGORY).equals(Constants.RUNNING) && !MainScene.getActiveGameMap().get(Constants.NAME).equals("110 m hurdles")){
            foulValue = 6;
        } else if (MainScene.getActiveGameMap().get(Constants.NAME).equals("Pole Vault")) {
            foulValue = 1;
        } else foulValue = 0;
        //Set foul status
        this.foul = value == foulValue;

        for (long i=1; i < 20; i++){
            delay(25 * i, ()->incrementImage(frameProperty));
        }
        delay(500, ()-> {
            this.status = currentStatus;
            this.setViewport(new Rectangle2D((result - 1) * SPRITE_SIZE, 0, SPRITE_SIZE, SPRITE_SIZE));

            //Set foul status
            if (value == foulValue){
                this.status = Constants.FOUL;
            } else {
                this.status = Constants.ACTIVE;
            }
            rollingDone = true;
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
        return this.status.equals(Constants.ACTIVE) || this.status.equals(Constants.FOUL);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getValue() {
        if (!this.foul) {
            return value;
        } else {
            return -value;
        }
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
