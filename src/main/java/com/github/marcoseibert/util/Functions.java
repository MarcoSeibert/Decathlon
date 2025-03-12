package com.github.marcoseibert.util;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Functions {
    private Functions(){
            }
    public static void updateActiveDice(Map<String, String> gameState, List<Die> allDiceList, GridPane dicePane){
        int nrDice = Integer.parseInt(gameState.get("nrDice"));
        int round = Integer.parseInt(gameState.get("round"));
        for (Die die:allDiceList){die.setActive(false);}
        List<Die> activeDiceList = new ArrayList<>();
        for (int i = 0; i < nrDice; i++){
            activeDiceList.add(allDiceList.get(i + round * nrDice));
        }
        for (Die die:activeDiceList){
            die.setActive(true);
        }
        for (Node child:dicePane.getChildren()){
            if (Objects.equals(child.getId(), "continueButton")){
                child.setDisable(false);
            }
        }
    }
}

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
        if (Objects.equals(direction, "up")){
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
