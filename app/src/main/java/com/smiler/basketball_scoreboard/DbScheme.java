package com.smiler.basketball_scoreboard;

import android.provider.BaseColumns;

public class DbScheme {
    public DbScheme() {}

    public static abstract class ResultsTable implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_HOME_TEAM = "home_team";
        public static final String COLUMN_NAME_GUEST_TEAM = "guest_team";
        public static final String COLUMN_NAME_HOME_SCORE = "home_score";
        public static final String COLUMN_NAME_GUEST_SCORE = "guest_score";
        public static final String COLUMN_NAME_HOME_PERIODS = "home_periods";
        public static final String COLUMN_NAME_GUEST_PERIODS = "guest_periods";
        public static final String COLUMN_NAME_SHARE_STRING = "share_string";
        public static final String COLUMN_NAME_REGULAR_PERIODS = "regular_periods";
        public static final String COLUMN_NAME_COMPLETE = "complete";
    }
}
