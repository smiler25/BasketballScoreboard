package com.smiler.basketball_scoreboard;

import java.text.SimpleDateFormat;

public class Constants {

    public static final long SECOND = 1000;
    public static final long SECONDS_60 = 60 * SECOND;
    public static final long MINUTES_2 = 120 * SECOND;
    public static final int LAYOUT_FULL= 0;
    public static final int LAYOUT_SIMPLE= 1;

    public static long mainTickInterval = SECOND;
    public static long shotTickInterval = SECOND;

    public static final String FORMAT_TWO_DIGITS = "%02d";
    public static final String TIME_FORMAT_SHORT = "%d.%d";
    public static final SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
    public static final SimpleDateFormat timeFormatMillis = new SimpleDateFormat("ss.S ");

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

    public static final String TAG_FRAGMENT_HELP = "HelpFragment";
    public static final String TAG_FRAGMENT_TIME = "TimeDialog";
    public static final String TAG_FRAGMENT_LIST = "ListDialog";
    public static final String TAG_FRAGMENT_CONFIRM = "ConfirmDialog";
    public static final String TAG_FRAGMENT_NAME_EDIT = "NameEditDialog";
    public static final String TAG_FRAGMENT_MAIN_TIME_PICKER = "MainTimePicker";
    public static final String TAG_FRAGMENT_SHOT_TIME_PICKER = "ShotTimePicker";

}