package com.smiler.basketball_scoreboard.results;

public enum PlayByPlayTypes {
    NONE,
    IN_GAME_ONLY,
    SAVE;

    public static PlayByPlayTypes fromInteger(int x) {
        switch (x) {
            case 0:
                return NONE;
            case 1:
                return IN_GAME_ONLY;
            case 2:
                return SAVE;
        }
        return null;
    }
}