package com.smiler.basketball_scoreboard;

import android.provider.BaseColumns;

public class DbScheme {
    private static final String COMMA = ",";
    private static final String INT_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String TEXT_TYPE = " TEXT";

    public DbScheme() {}

    public static abstract class ResultsTable implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_HOME_TEAM = "home_team";
        public static final String COLUMN_GUEST_TEAM = "guest_team";
        public static final String COLUMN_HOME_SCORE = "home_score";
        public static final String COLUMN_GUEST_SCORE = "guest_score";
        public static final String COLUMN_HOME_PERIODS = "home_periods";
        public static final String COLUMN_GUEST_PERIODS = "guest_periods";
        public static final String COLUMN_SHARE_STRING = "share_string";
        public static final String COLUMN_REGULAR_PERIODS = "regular_periods";
        public static final String COLUMN_COMPLETE = "complete";

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

    public static abstract class ResultsPlayersTable implements BaseColumns {
        public static final String TABLE_NAME = "results_players";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_PLAYER_TEAM = "player_team";
        public static final String COLUMN_PLAYER_NUMBER = "player_number";
        public static final String COLUMN_PLAYER_NAME = "player_name";
        public static final String COLUMN_PLAYER_POINTS = "player_points";
        public static final String COLUMN_PLAYER_FOULS = "player_fouls";
        public static final String COLUMN_PLAYER_CAPTAIN = "captain";

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

    public static abstract class GameDetailsTable implements BaseColumns {
        public static final String TABLE_NAME = "results_detail";
        public static final String COLUMN_GAME_ID = "game_id";
        public static final String COLUMN_PLAY_BY_PLAY = "play_by_play";
        public static final String COLUMN_LEADER_CHANGED = "lead_changed";
        public static final String COLUMN_HOME_MAX_LEAD = "home_max_lead";
        public static final String COLUMN_GUEST_MAX_LEAD = "guest_max_lead";
        public static final String COLUMN_TIE = "tie";

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
