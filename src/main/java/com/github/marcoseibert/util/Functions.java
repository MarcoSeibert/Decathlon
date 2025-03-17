package com.github.marcoseibert.util;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.Objects;


public class Functions {
    private Functions(){
            }

    public static Cursor getCustomCursor(String direction) {
        Image cursorSprites = new Image("/images/cursorSpriteSheet.png");
        ImageView cursorView = new ImageView(cursorSprites);
        if (Objects.equals(direction, Constants.UP)){
            cursorView.setViewport(new Rectangle2D(0, 0, 32,32));
        } else {
            cursorView.setViewport(new Rectangle2D(32, 0, 32,32));
        }
        SnapshotParameters cursorParams = new SnapshotParameters();
        cursorParams.setFill(Color.TRANSPARENT);
        WritableImage cursorImage = cursorView.snapshot(cursorParams, null);
        return new ImageCursor(cursorImage);
    }
}
