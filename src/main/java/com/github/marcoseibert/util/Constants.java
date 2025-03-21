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

    private Constants(){
        throw new AssertionError();
    }
}
