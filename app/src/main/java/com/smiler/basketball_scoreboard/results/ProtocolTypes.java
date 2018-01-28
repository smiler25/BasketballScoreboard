package com.smiler.basketball_scoreboard.results;

public enum ProtocolTypes {
    NONE,
    FROM_START_GAME,
    FROM_START_PERIOD;

    public static ProtocolTypes fromInteger(int x) {
        switch (x) {
            case 0:
                return NONE;
            case 1:
                return FROM_START_GAME;
            case 2:
                return FROM_START_PERIOD;
        }
        return null;
    }
}