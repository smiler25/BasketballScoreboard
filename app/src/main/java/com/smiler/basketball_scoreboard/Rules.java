package com.smiler.basketball_scoreboard;

import android.content.SharedPreferences;

import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;

import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_ENABLE_SHORT_SHOT_TIME;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_ENABLE_SHOT_TIME;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_LAYOUT;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_MAX_FOULS;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_MAX_FOULS_WARN;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_NUM_REGULAR;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_OVERTIME;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_REGULAR_TIME;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_SHOT_TIME;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_SIDE_PANELS_FOULS_MAX;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_SIDE_PANELS_FOULS_RULES;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.PREF_TIMEOUTS_RULES;
import static com.smiler.basketball_scoreboard.preferences.PrefActivity.SIDE_PANEL_FOULS_RULES_STRICT;

public class Rules {

    public static final int RULES_FIBA = 0;
    public static final int RULES_NBA = 1;
    private static final int RULES_3X3 = 2;

    private static final String LAYOUT_COMMON = "0";
    private static final String LAYOUT_3X3 = "2";

    private static final boolean DEFAULT_ENABLE_SHOT_TIME = true;
    private static final boolean DEFAULT_ENABLE_SHORT_SHOT_TIME = true;
    private static final boolean DEFAULT_ENABLE_SHORT_SHOT_TIME_3X3 = false;
    public static final String DEFAULT_TIMEOUTS = "0";
    private static final String DEFAULT_TIMEOUTS_FIBA = "1";
    private static final String DEFAULT_TIMEOUTS_NBA = "2";
    private static final String DEFAULT_TIMEOUTS_3X3 = "3";

    public static final int MAX_PLAYERS = 12;
    public static final int MIN_PLAYERS = 5;
    public static final int DEFAULT_SHOT_TIME = 24;
    public static final int DEFAULT_SHORT_SHOT_TIME = 14;
    public static final int DEFAULT_NUM_REGULAR = 4;
    public static final int DEFAULT_OVERTIME = 5;
    public static final int DEFAULT_MAX_FOULS = 5;
    public static final int DEFAULT_MAX_FOULS_WARN = 5;

    public static final int DEFAULT_FIBA_MAIN_TIME = 10;
    public static final int DEFAULT_FIBA_PLAYER_FOULS = 5;

    private static final int DEFAULT_NBA_MAIN_TIME = 12;
    private static final int DEFAULT_NBA_PLAYER_FOULS = 6;

    private static final int DEFAULT_3X3_NUM_REGULAR = 1;
    private static final int DEFAULT_3X3_MAIN_TIME = 10;
    private static final int DEFAULT_3X3_SHOT_TIME = 12;
    private static final int DEFAULT_3X3_MAX_FOULS = 10;
    private static final int DEFAULT_3X3_MAX_FOULS_WARN = 7;
//    private static final int DEFAULT_3X3_TIMEOUTS = 1;
    private static final int DEFAULT_3X3_PLAYER_FOULS = 4;
    public static final int MAX_PLAYERS_3X3 = 4;
    public static final int MIN_PLAYERS_3X3 = 3;
    private static BaseLayout.GAME_LAYOUT prevLayout;


    public static void setDefaultRules(SharedPreferences prefs, int type) {
        SharedPreferences.Editor editor = prefs.edit();
        if (type == RULES_3X3) {
            set3X3Rules(editor);
        } else {
            prevLayout = BaseLayout.GAME_LAYOUT.fromInteger(Integer.parseInt(prefs.getString(PrefActivity.PREF_LAYOUT, "0")));
            setCommon(editor);
            if (type == RULES_FIBA) {
                setFIBA(editor);
            } else {
                setNBA(editor);
            }
        }
        editor.apply();
    }

    private static void setCommon(SharedPreferences.Editor editor) {
        if (prevLayout == BaseLayout.GAME_LAYOUT.STREETBALL) {
            editor.putString(PREF_LAYOUT, LAYOUT_COMMON);
        }
        editor.putInt(PREF_NUM_REGULAR, DEFAULT_NUM_REGULAR);
        editor.putInt(PREF_OVERTIME, DEFAULT_OVERTIME);
        editor.putInt(PREF_SHOT_TIME, DEFAULT_SHOT_TIME);
        editor.putInt(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_SHORT_SHOT_TIME);
        editor.putInt(PREF_MAX_FOULS, DEFAULT_MAX_FOULS);
        editor.putInt(PREF_MAX_FOULS_WARN, DEFAULT_MAX_FOULS_WARN);
        editor.putBoolean(PREF_ENABLE_SHOT_TIME, DEFAULT_ENABLE_SHOT_TIME);
        editor.putBoolean(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_ENABLE_SHORT_SHOT_TIME);
        editor.putString(PREF_SIDE_PANELS_FOULS_RULES, SIDE_PANEL_FOULS_RULES_STRICT);
    }

    private static void setNBA(SharedPreferences.Editor editor) {
        editor.putInt(PREF_REGULAR_TIME, DEFAULT_NBA_MAIN_TIME);
        editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, DEFAULT_NBA_PLAYER_FOULS);
        editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_TIMEOUTS_NBA);
    }

    private static void setFIBA(SharedPreferences.Editor editor) {
        editor.putInt(PREF_REGULAR_TIME, DEFAULT_FIBA_MAIN_TIME);
        editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, DEFAULT_FIBA_PLAYER_FOULS);
        editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_TIMEOUTS_FIBA);
    }

    private static void set3X3Rules(SharedPreferences.Editor editor) {
        editor.putString(PREF_LAYOUT, LAYOUT_3X3);
        editor.putInt(PREF_NUM_REGULAR, DEFAULT_3X3_NUM_REGULAR);
        editor.putInt(PREF_REGULAR_TIME, DEFAULT_3X3_MAIN_TIME);
        editor.putInt(PREF_SHOT_TIME, DEFAULT_3X3_SHOT_TIME);
        editor.putInt(PREF_MAX_FOULS, DEFAULT_3X3_MAX_FOULS);
        editor.putInt(PREF_MAX_FOULS_WARN, DEFAULT_3X3_MAX_FOULS_WARN);
        editor.putBoolean(PREF_ENABLE_SHOT_TIME, DEFAULT_ENABLE_SHOT_TIME);
        editor.putBoolean(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_ENABLE_SHORT_SHOT_TIME_3X3);
        editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_TIMEOUTS_3X3);
        editor.putString(PREF_SIDE_PANELS_FOULS_RULES, SIDE_PANEL_FOULS_RULES_STRICT);
        editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, DEFAULT_3X3_PLAYER_FOULS);
    }

    public enum TO_RULES {
        NONE,
        FIBA,
        NBA,
        STREETBALL;

        public static TO_RULES fromInteger(int x) {
            switch (x) {
                case 0:
                    return NONE;
                case 1:
                    return FIBA;
                case 2:
                    return NBA;
                case 3:
                    return STREETBALL;
            }
            return null;
        }
    }
}
