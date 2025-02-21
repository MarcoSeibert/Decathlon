package com.github.marcoseibert.util;

import com.github.marcoseibert.ui.DecathlonUI;

import javax.swing.*;
import java.awt.event.*;

public class MyButton extends JButton {

    public MyButton(DecathlonUI frame){
        super.setText("");
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (e.getButton() == MouseEvent.BUTTON1){
                    frame.setCursor(frame.cursorDown);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                frame.setCursor(frame.cursorUp);
            }
        });
    }

}

