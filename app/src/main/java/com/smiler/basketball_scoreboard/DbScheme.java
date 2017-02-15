package com.smiler.basketball_scoreboard;

import android.provider.BaseColumns;

class DbScheme {
    private static final String COMMA = ",";
    private static final String INT_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String TEXT_TYPE = " TEXT";

    public DbScheme() {}

    static abstract class ResultsTable implements BaseColumns {
        static final String TABLE_NAME = "results";
        static final String COLUMN_DATE = "date";
        static final String COLUMN_HOME_TEAM = "home_team";
        static final String COLUMN_GUEST_TEAM = "guest_team";
        static final String COLUMN_HOME_SCORE = "home_score";
        static final String COLUMN_GUEST_SCORE = "guest_score";
        static final String COLUMN_HOME_PERIODS = "home_periods";
        static final String COLUMN_GUEST_PERIODS = "guest_periods";
        static final String COLUMN_SHARE_STRING = "share_string";
        static final String COLUMN_REGULAR_PERIODS = "regular_periods";
        static final String COLUMN_COMPLETE = "complete";

        static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_DATE + LONG_TYPE + COMMA +
                        COLUMN_HOME_TEAM + TEXT_TYPE + COMMA +
                        COLUMN_GUEST_TEAM + TEXT_TYPE + COMMA +
                        COLUMN_HOME_SCORE + INT_TYPE + COMMA +
                        COLUMN_GUEST_SCORE + INT_TYPE + COMMA +
                        COLUMN_HOME_PERIODS + TEXT_TYPE + COMMA +
                        COLUMN_GUEST_PERIODS + TEXT_TYPE + COMMA +
                        COLUMN_COMPLETE + INT_TYPE + COMMA +
                        COLUMN_REGULAR_PERIODS + INT_TYPE + COMMA +
                        COLUMN_SHARE_STRING + TEXT_TYPE +
                        " )";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    static abstract class ResultsPlayersTable implements BaseColumns {
        static final String TABLE_NAME = "results_players";
        static final String COLUMN_GAME_ID = "game_id";
        static final String COLUMN_PLAYER_TEAM = "player_team";
        static final String COLUMN_PLAYER_NUMBER = "player_number";
        static final String COLUMN_PLAYER_NAME = "player_name";
        static final String COLUMN_PLAYER_POINTS = "player_points";
        static final String COLUMN_PLAYER_FOULS = "player_fouls";
        static final String COLUMN_PLAYER_CAPTAIN = "captain";

        static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_GAME_ID + INT_TYPE + COMMA +
                        COLUMN_PLAYER_TEAM + TEXT_TYPE + COMMA +
                        COLUMN_PLAYER_NUMBER + INT_TYPE + COMMA +
                        COLUMN_PLAYER_NAME + TEXT_TYPE + COMMA +
                        COLUMN_PLAYER_POINTS + INT_TYPE + COMMA +
                        COLUMN_PLAYER_FOULS + INT_TYPE + COMMA +
                        COLUMN_PLAYER_CAPTAIN + INT_TYPE + COMMA +
                        "PRIMARY KEY (" + COLUMN_GAME_ID + COMMA +
                        COLUMN_PLAYER_NUMBER + COMMA +
                        COLUMN_PLAYER_TEAM + "))";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    static abstract class GameDetailsTable implements BaseColumns {
        static final String TABLE_NAME = "results_detail";
        static final String COLUMN_GAME_ID = "game_id";
        static final String COLUMN_PLAY_BY_PLAY = "play_by_play";
        static final String COLUMN_LEADER_CHANGED = "lead_changed";
        static final String COLUMN_HOME_MAX_LEAD = "home_max_lead";
        static final String COLUMN_GUEST_MAX_LEAD = "guest_max_lead";
        static final String COLUMN_TIE = "tie";

        static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_GAME_ID + INT_TYPE + COMMA +
                        COLUMN_LEADER_CHANGED + INT_TYPE + COMMA +
                        COLUMN_TIE + INT_TYPE + COMMA +
                        COLUMN_HOME_MAX_LEAD + INT_TYPE + COMMA +
                        COLUMN_GUEST_MAX_LEAD + INT_TYPE + COMMA +
                        COLUMN_PLAY_BY_PLAY + TEXT_TYPE +
                        ")";

        static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}
