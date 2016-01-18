package com.smiler.basketball_scoreboard;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {

    public static final long SECOND = 1000;
    public static final long SECONDS_60 = 60 * SECOND;
    public static final long MINUTES_2 = 120 * SECOND;
    public static final int LAYOUT_FULL= 0;
    public static final int LAYOUT_SIMPLE= 1;

    public static final int SIDE_PANELS_LEFT = 0;
    public static final int SIDE_PANELS_RIGHT = 1;

    public static long mainTickInterval = SECOND;
    public static long shotTickInterval = SECOND;

    public static final String FORMAT_TWO_DIGITS = "%02d";
    public static final String TIME_FORMAT_SHORT = "%d.%d";
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    public static final SimpleDateFormat timeFormatMillis = new SimpleDateFormat("ss.S", Locale.ENGLISH);
    public static String API16_TIME_REGEX = "(\\d{2}\\.\\d)(\\d{1,3})";

    public static final String STATE_MAIN_TIME = "mainTime";
    public static final String STATE_SHOT_TIME = "shotTime";
    public static final String STATE_HOME_SCORE = "homeScore";
    public static final String STATE_GUEST_SCORE = "guestScore";
    public static final String STATE_HOME_NAME = "homeName";
    public static final String STATE_GUEST_NAME = "guestName";
    public static final String STATE_HOME_FOULS = "homeFouls";
    public static final String STATE_HOME_TIMEOUTS = "homeTimeouts";
    public static final String STATE_HOME_TIMEOUTS_NBA = "homeTimeoutsNBA";
    public static final String STATE_HOME_TIMEOUTS20 = "homeTimeouts20";
    public static final String STATE_GUEST_FOULS = "homeFouls";
    public static final String STATE_GUEST_TIMEOUTS = "guestTimeouts";
    public static final String STATE_GUEST_TIMEOUTS_NBA = "guestTimeoutsNBA";
    public static final String STATE_GUEST_TIMEOUTS20 = "guestTimeouts20";
    public static final String STATE_PERIOD = "period";
    public static final String STATE_HOME_ACTIVE_PLAYERS = "home_active_players";
    public static final String STATE_GUEST_ACTIVE_PLAYERS = "guest_active_players";

    public static final String TAG_FRAGMENT_APP_UPDATES = "AppUpdatesFragment";
    public static final String TAG_FRAGMENT_TIME = "TimeDialog";
    public static final String TAG_FRAGMENT_CONFIRM = "ConfirmDialog";
    public static final String TAG_FRAGMENT_NAME_EDIT = "NameEditDialog";
    public static final String TAG_FRAGMENT_MAIN_TIME_PICKER = "MainTimePicker";
    public static final String TAG_FRAGMENT_SHOT_TIME_PICKER = "ShotTimePicker";

    public static final int MAX_PLAYERS = 12;
    public static final int DEFAULT_SHOT_TIME = 24;
    public static final int DEFAULT_SHORT_SHOT_TIME = 14;
    public static final int DEFAULT_NUM_REGULAR = 4;
    public static final int DEFAULT_OVERTIME = 5;
    public static final int DEFAULT_MAX_FOULS = 5;
    public static final int DEFAULT_FIBA_MAIN_TIME = 10;
    public static final int DEFAULT_NBA_MAIN_TIME = 12;
    public static final int DEFAULT_FIBA_PLAYER_FOULS = 5;
    public static final int DEFAULT_NBA_PLAYER_FOULS = 6;

}