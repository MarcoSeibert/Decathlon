package com.github.marcoseibert;

import com.github.marcoseibert.ui.DecathlonUI;
import javafx.application.Application;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DecathlonApp extends Application {
    protected static final Logger logger = LogManager.getLogger(DecathlonApp.class.getSimpleName());

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        logger.info("Starting!");
        new DecathlonUI();
    }



}