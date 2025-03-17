package com.github.marcoseibert.util;


public final class Constants {
    //die status
    public static final String ACTIVE = "active";
    public static final String FOUL = "foul";
    public static final String FROZEN = "frozen";
    public static final String INACTIVE = "inactive";
    public static final String GRAYED = "grayed";

    //game categories
    public static final String RUNNING = "running";
    public static final String THROWING = "throwing";
    public static final String HIGHJUMPING = "highJumping";

    //game parameter map keys
    public static final String GAMEID = "gameId";
    public static final String NAME = "name";
    public static final String CATEGORY = "category";
    public static final String PARITY = "parity";

    //game state keys
    public static final String GAMEOVER = "gameOver";
    public static final String REMAININGREROLLS = "remainingRerolls";
    public static final String LASTACHIEVED = "lastAchieved";
    public static final String THISROUNDSCORE = "thisRoundScore";
    public static final String FROZENSUM = "frozenSum";
    public static final String FROZENAMOUNT = "frozenAmount";
    public static final String CURRENTATTEMPT = "currentAttempt";
    public static final String ACTIVEPLAYER = "activePlayer";
    public static final String ROUND = "round";

    //misc
    public static final String UP = "up";
    public static final String DOWN = "down";

    private Constants(){
        throw new AssertionError();
    }
}
