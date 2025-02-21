package com.github.marcoseibert.ui;

import com.github.marcoseibert.util.Die;
import com.github.marcoseibert.util.RollButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DecathlonUI extends JFrame {
    protected static final Logger logger = LogManager.getLogger(DecathlonUI.class.getSimpleName());
    private static final String APP_TITLE = "Decathlon";
    private static final int WINDOW_WIDTH = 1024;
    private static final int WINDOW_HEIGHT = 1024;
    private static final int IMAGE_SIZE = 64;
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
    public final Cursor cursorDown;
    public final Cursor cursorUp;


    public DecathlonUI() {
        // Create window with GridBagLayout
        this.setTitle(APP_TITLE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLayout(new GridBagLayout());
        GridBagConstraints constr = new GridBagConstraints();
        // Center window on screen
        this.setLocationRelativeTo(null);
        // Set images for the cursor
        Image cursorImageUp;
        Image cursorImageDown;
        try {
            cursorImageUp = ImageIO.read(getClass().getResourceAsStream("/images/finger_up.png"));
            cursorImageDown = ImageIO.read(getClass().getResourceAsStream("/images/finger_down.png"));
        } catch (IOException e) {
            cursorImageUp = new BufferedImage(0, 0, 0);
            cursorImageDown = new BufferedImage(0, 0, 0);
        }

        this.cursorUp = getToolkit().createCustomCursor(cursorImageUp, new Point(12, 4), "Cursor up");
        this.cursorDown = getToolkit().createCustomCursor(cursorImageDown, new Point(12, 4), "Cursor down");
        this.setCursor(cursorUp);

        constr.gridx = 5;
        constr.gridy = 0;

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
        constr.gridwidth = 6;
        // Adding to window
        this.add(title, constr);

        // Create a map containing all the icons
        Map<Integer, JLabel> iconMap = new HashMap<>();
        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            BufferedImage disciplineImage = null;
            try {
                disciplineImage = ImageIO.read(getClass().getResourceAsStream(IMAGE_PATHS[i]));
            } catch (IOException e) {
                new BufferedImage(0, 0, 0);
            }
            ImageIcon disciplineIcon = new ImageIcon(disciplineImage);
            JLabel disciplineLabel = new JLabel(disciplineIcon);
            iconMap.put(i, disciplineLabel);
        }

        // Create a map containing all point fields
        Map<Integer, JTextField> pointsMap = getPointsTextMap();

        // Set spacing
        constr.insets = new Insets(10, 0, 0, 5);
        constr.gridwidth = 1;
        constr.gridheight = 1;

        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            // Add icons to the window
            JLabel icon = iconMap.get(i);
            constr.gridx = 0;
            constr.gridy = i + 2;
            constr.ipadx = 0;
            this.add(icon, constr);

            for (int j = 0; j < 40; j += 10) {
                // Add four columns of text to the window
                JTextField text = pointsMap.get(i + j);
                constr.gridx = j / 10 + 1;
                constr.fill = GridBagConstraints.VERTICAL;
                constr.ipadx = IMAGE_SIZE;
                this.add(text, constr);
            }
        }

        int nrOfDice = 8;
        Map<Integer, Die> diceMap = new HashMap<>();
        for (int i = 0; i < nrOfDice; i++) {
            Die die = new Die();
            constr.gridheight = 2;
            constr.gridx = i % 4 + 6;
            constr.gridy = (int) (2 + 2 * Math.floor((double) i / 4));
            constr.ipadx = 0;
            constr.insets = new Insets(0, 0, 0, 0);
            diceMap.put(i, die);
            this.add(die, constr);
        }

        List<Map<Integer, BufferedImage>> sprites = createSpriteMap();
        RollButton rollButton = new RollButton(this, diceMap, sprites);
        constr.gridheight = 1;
        constr.gridx = 6;
        constr.gridy = 1;
        this.add(rollButton, constr);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        logger.info("All set up!");
    }

    private static Map<Integer, JTextField> getPointsTextMap() {
        Map<Integer, JTextField> pointsMap = new HashMap<>();
        for (int i = 0; i < IMAGE_PATHS.length; i++) {
            // Create four player columns
            for (int j = 0; j < 40; j += 10) {
                JTextField a = new JTextField(String.valueOf(i));
                a.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
                a.setHorizontalAlignment(SwingConstants.CENTER);
                a.setEditable(false);
                a.setFocusable(false);
                pointsMap.put(i + j, a);
            }
        }
        return pointsMap;
    }


    public List<Map<Integer, BufferedImage>> createSpriteMap() {
        Map<Integer, BufferedImage> resultSprites = new HashMap<>();
        Map<Integer, BufferedImage> animSprites = new HashMap<>();
        try {
            for (int i = 0; i < 6; i++) {
                BufferedImage dieSprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/die" + (i + 1) + ".png"));
                resultSprites.put(i, dieSprite);
            }
            for (int i = 0; i < 8; i++) {
                BufferedImage dieSprite = ImageIO.read(getClass().getClassLoader().getResourceAsStream("images/ani" + (i + 1) + ".png"));
                animSprites.put(i, dieSprite);
            }
        } catch (Exception e) {
            logger.warn("Exception", e.fillInStackTrace());
        }
        List<Map<Integer, BufferedImage>> sprites = new ArrayList<>();
        sprites.add(resultSprites);
        sprites.add(animSprites);

        return sprites;

    }
}
