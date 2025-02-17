package com.github.marcoseibert.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DecathlonUI {
    private static final String APP_TITLE = "Decathlon";
    private static final int WINDOW_WIDTH = 512;
    private static final int WINDOW_HEIGHT = 1024;
    private static final int IMAGE_SIZE = 80;
    private static final String FONT_NAME = "Trebuchet MS";

    private static final String PATH_TO_RUNNER = "/images/runner.png";
    private static final String[] IMAGE_PATHS = {PATH_TO_RUNNER,
                                                "/images/long-jump.png",
                                                "/images/shot-put.png",
                                                "/images/high-jump.png",
                                                PATH_TO_RUNNER,
                                                "/images/hurdles.png",
                                                "/images/discus.png",
                                                "/images/pole-vault.png",
                                                "/images/javelin.png",
                                                PATH_TO_RUNNER};

    public DecathlonUI() throws IOException{
        // Create window with GridBagLayout
        JFrame window = new JFrame(APP_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();
        // Center window on screen
        window.setLocationRelativeTo(null);

        // Create and add a title
        // Create title
        JLabel title = new JLabel(APP_TITLE);
        title.setBackground(Color.WHITE);
        title.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        // Position on the grid
        constr.gridx = 0;
        constr.gridy = 0;
        // Spanning over all columns
        constr.gridwidth = 5;
        // Adding to window
        window.add(title, constr);


        // Create a map containing all the icons
        Map<Integer, JLabel> iconMap = new HashMap<>();
        for (int i = 0; i < IMAGE_PATHS.length; i++){
            BufferedImage a = ImageIO.read(getClass().getResourceAsStream(IMAGE_PATHS[i]));
            ImageIcon b = new ImageIcon(a);
            JLabel c = new JLabel(b);
            iconMap.put(i, c);
        }

        // Create a map containing all point fields
        Map<Integer, JTextField> pointsMap = new HashMap<>();
        for (int i = 0; i < IMAGE_PATHS.length; i++){
            // Create four player columns
            for (int j = 0; j < 40; j += 10){
                JTextField a = new JTextField(String.valueOf(i));
                a.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
                a.setHorizontalAlignment(SwingConstants.CENTER);
                a.setEditable(false);
                pointsMap.put(i + j, a);
            }
        }

        // Set spacing
        constr.insets = new Insets(10, 0, 0, 5);
        constr.gridwidth = 1;

        for (int i = 0; i < IMAGE_PATHS.length; i++){
            // Add icons to the window
            JLabel icon = iconMap.get(i);
            constr.gridx = 0;
            constr.gridy = i + 2;
            constr.ipadx = 0;
            window.add(icon, constr);

            for (int j = 0; j < 40; j += 10){
                // Add four columns of text to the window
                JTextField text = pointsMap.get(i + j);
                constr.gridx = j / 10 + 1;
                constr.fill = GridBagConstraints.VERTICAL;
                constr.ipadx = IMAGE_SIZE;
                window.add(text, constr);
            }
        }

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

}
