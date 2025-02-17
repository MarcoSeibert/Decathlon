package com.github.marcoseibert.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DecathlonUI {
    private static final String APP_TITLE = "Decathlon";
    private static final int WINDOW_WIDTH = 512;
    private static final int WINDOW_HEIGHT = 1024;
    private static final int MARGIN_LEFT = 20;
    private static final int MARGIN_TOP = 10;
    private static final int MARGIN_Y = 10;
    private static final int IMAGE_SIZE = 80;
    private static final String FONT_NAME = "Trebuchet MS";

    public DecathlonUI() throws IOException{
        JFrame window = new JFrame(APP_TITLE);
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setLocationRelativeTo(null);

        JLabel title = new JLabel(APP_TITLE, SwingConstants.CENTER);
        title.setBounds(MARGIN_LEFT, MARGIN_TOP, (WINDOW_WIDTH - 2 * MARGIN_LEFT), 24);
        title.setBackground(Color.WHITE);
        title.setFont(new Font(FONT_NAME, Font.BOLD, 20));
        window.add(title);

        JPanel icons = new JPanel();
        icons.setLayout(new BoxLayout(icons, BoxLayout.PAGE_AXIS));
        icons.setBounds(MARGIN_LEFT, MARGIN_TOP + title.getHeight() + MARGIN_Y, 50, WINDOW_HEIGHT - (MARGIN_TOP + title.getHeight() + MARGIN_Y));
        icons.setBorder(BorderFactory.createEmptyBorder(MARGIN_TOP + title.getHeight() + 2 * MARGIN_Y + IMAGE_SIZE / 2, MARGIN_LEFT,MARGIN_TOP,WINDOW_WIDTH - 2* MARGIN_LEFT - IMAGE_SIZE));

        final String pathToRunner = "/images/runner.png";

        BufferedImage imgRunning100 = ImageIO.read(getClass().getResourceAsStream(pathToRunner));
        BufferedImage imgRunning400 = ImageIO.read(getClass().getResourceAsStream(pathToRunner));
        BufferedImage imgRunning1500 = ImageIO.read(getClass().getResourceAsStream(pathToRunner));
        BufferedImage imgLongJump = ImageIO.read(getClass().getResourceAsStream("/images/long-jump.png"));
        BufferedImage imgShotPut = ImageIO.read(getClass().getResourceAsStream("/images/shot-put.png"));
        BufferedImage imgHighJump = ImageIO.read(getClass().getResourceAsStream("/images/high-jump.png"));
        BufferedImage imgHurdles = ImageIO.read(getClass().getResourceAsStream("/images/hurdles.png"));
        BufferedImage imgDiscus = ImageIO.read(getClass().getResourceAsStream("/images/discus.png"));
        BufferedImage imgPoleVault = ImageIO.read(getClass().getResourceAsStream("/images/pole-vault.png"));
        BufferedImage imgJavelin = ImageIO.read(getClass().getResourceAsStream("/images/javelin.png"));

        icons.add(new JLabel(new ImageIcon(imgRunning100)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgLongJump)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgShotPut)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgHighJump)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgRunning400)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgHurdles)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgDiscus)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgPoleVault)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgJavelin)));
        icons.add(Box.createVerticalStrut(5));
        icons.add(new JLabel(new ImageIcon(imgRunning1500)));

        window.add(icons);

        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

}
