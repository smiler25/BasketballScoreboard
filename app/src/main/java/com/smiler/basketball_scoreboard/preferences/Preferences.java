package com.smiler.basketball_scoreboard.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.smiler.basketball_scoreboard.results.PlayByPlayTypes;
import com.smiler.basketball_scoreboard.results.ProtocolTypes;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.Rules;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;

import java.text.SimpleDateFormat;

import lombok.Getter;

import static com.smiler.basketball_scoreboard.Constants.DEFAULT_HORN_LENGTH;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_FIBA_MAIN_TIME;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_FIBA_PLAYER_FOULS;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_MAX_FOULS;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_MAX_FOULS_WARN;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_NUM_REGULAR;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_OVERTIME;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_SHORT_SHOT_TIME;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_SHOT_TIME;
import static com.smiler.basketball_scoreboard.Rules.DEFAULT_TIMEOUTS;
import static com.smiler.basketball_scoreboard.Rules.MAX_PLAYERS;
import static com.smiler.basketball_scoreboard.Rules.MAX_PLAYERS_3X3;
import static com.smiler.basketball_scoreboard.Rules.MIN_PLAYERS;
import static com.smiler.basketball_scoreboard.Rules.MIN_PLAYERS_3X3;

public class Preferences {
    private final SharedPreferences prefs;
    private final Resources resources;

    public boolean autoSaveResults;
    public int autoSound, actualTime;
    public Rules.TimeoutRules timeoutRules;
    public BaseLayout.GameLayoutTypes layoutType;
    public PlayByPlayTypes playByPlay;
    public boolean fixLandscape, fixLandscapeChanged;
    public boolean layoutChanged, timeoutsRulesChanged;
    public boolean autoShowTimeout, autoShowBreak;
    public boolean autoSwitchSides;
    public boolean saveOnExit, pauseOnSound, vibrationOn;
    public boolean enableShotTime, shotTimePrefChanged, restartShotTimer;
    public boolean enableShortShotTime;
    public boolean useDirectTimer;
    public boolean fractionSecondsMain, fractionSecondsShot;
    public boolean spOn, spStateChanged, spConnected;
    public boolean spClearDelete;
    public boolean arrowsOn, arrowsStateChanged;
    public long mainTimePref, shotTimePref, shortShotTimePref, overTimePref;
    public short maxFouls, maxFoulsWarn;
    public short numRegularPeriods;
    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;
    public int whistleRepeats, hornRepeats, whistleLength, hornLength, hornUserRepeats;
    private int defaultScoreColor, defaultTimeColor;
    public String hName, gName;
    public int dontAskNewGame;
    @Getter
    private ProtocolTypes protocolType;

    public enum Elements {
        HSCORE,
        GSCORE,
        MAIN_TIME,
        SHOT_TIME,
        BACKGROUND
    }

    private static Preferences instance;

    public static Preferences getInstance(Context context){
        if (instance == null){
            instance = new Preferences(context);
        }
        return instance;
    }

    private Preferences(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        resources = context.getResources();
        whistleLength = 190;
        hornLength = 1000;
        defaultScoreColor = context.getResources().getColor(R.color.orange);
        defaultTimeColor = context.getResources().getColor(R.color.red);
    }

    public void read() {
        readNoRestart();
        readRestart();
    }

    public void readNoRestart() {
        boolean fixLandscape_ = prefs.getBoolean(PrefActivity.PREF_FIX_LANDSCAPE, true);
        fixLandscapeChanged = fixLandscape != fixLandscape_;
        fixLandscape = fixLandscape_;
        autoSound = Integer.parseInt(prefs.getString(PrefActivity.PREF_AUTO_SOUND, "0"));
        hornUserRepeats = prefs.getInt(PrefActivity.PREF_HORN_LENGTH, DEFAULT_HORN_LENGTH) * Math.round(hornLength / 1000f);
        autoSaveResults = prefs.getBoolean(PrefActivity.PREF_SAVE_GAME_RESULTS, true);
        autoShowTimeout = prefs.getBoolean(PrefActivity.PREF_AUTO_TIMEOUT, true);
        autoShowBreak = prefs.getBoolean(PrefActivity.PREF_AUTO_BREAK, true);
        autoSwitchSides = prefs.getBoolean(PrefActivity.PREF_AUTO_SWITCH_SIDES, false);
        pauseOnSound = prefs.getBoolean(PrefActivity.PREF_PAUSE_ON_SOUND, true);
        vibrationOn = prefs.getBoolean(PrefActivity.PREF_VIBRATION, false);
        saveOnExit = prefs.getBoolean(PrefActivity.PREF_SAVE_ON_EXIT, true);
        fractionSecondsMain = prefs.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_MAIN, true);
        fractionSecondsShot = prefs.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_SHOT, true);
//        mainTimeFormat = fractionSecondsMain && 0 < mainTime && mainTime < SECONDS_60 ? TIME_FORMAT_MILLIS : TIME_FORMAT;

        shotTimePref = prefs.getInt(PrefActivity.PREF_SHOT_TIME, DEFAULT_SHOT_TIME) * 1000;
        boolean enableShotTime_ = prefs.getBoolean(PrefActivity.PREF_ENABLE_SHOT_TIME, true);
        shotTimePrefChanged = enableShotTime != enableShotTime_;
        enableShotTime = enableShotTime_;

        boolean enableShortShotTime_ = prefs.getBoolean(PrefActivity.PREF_ENABLE_SHORT_SHOT_TIME, true);
        long shortShotTimePref_ = enableShortShotTime_ ? prefs.getInt(PrefActivity.PREF_SHORT_SHOT_TIME, DEFAULT_SHORT_SHOT_TIME) * 1000 : shotTimePref;
        if (!shotTimePrefChanged && (enableShortShotTime != enableShortShotTime_ || shortShotTimePref != shortShotTimePref_)) {
            shotTimePrefChanged = true;
        }
        enableShortShotTime = enableShortShotTime_;
        shortShotTimePref = shortShotTimePref_;

        mainTimePref = prefs.getInt(PrefActivity.PREF_REGULAR_TIME, DEFAULT_FIBA_MAIN_TIME) * SECONDS_60;
        overTimePref = prefs.getInt(PrefActivity.PREF_OVERTIME, DEFAULT_OVERTIME) * SECONDS_60;
        numRegularPeriods = (short) prefs.getInt(PrefActivity.PREF_NUM_REGULAR, DEFAULT_NUM_REGULAR);
        hName = prefs.getString(PrefActivity.PREF_HOME_NAME, resources.getString(R.string.home_team_name_default));
        gName = prefs.getString(PrefActivity.PREF_GUEST_NAME, resources.getString(R.string.guest_team_name_default));
        actualTime = Integer.parseInt(prefs.getString(PrefActivity.PREF_ACTUAL_TIME, "1"));
        maxFouls = (short) prefs.getInt(PrefActivity.PREF_MAX_FOULS, DEFAULT_MAX_FOULS);
        maxFoulsWarn = (short) prefs.getInt(PrefActivity.PREF_MAX_FOULS_WARN, DEFAULT_MAX_FOULS_WARN);
        boolean sidePanelsOn_ = prefs.getBoolean(PrefActivity.PREF_ENABLE_SIDE_PANELS, false);
        if (sidePanelsOn_ != spOn) {
            spOn = sidePanelsOn_;
            spStateChanged = true;
        }
        spClearDelete = prefs.getString(PrefActivity.PREF_SIDE_PANELS_CLEAR, "0").equals("0");
        spConnected = prefs.getBoolean(PrefActivity.PREF_SIDE_PANELS_CONNECTED, false);
        SidePanelRow.setMaxFouls(prefs.getInt(PrefActivity.PREF_SIDE_PANELS_FOULS_MAX, DEFAULT_FIBA_PLAYER_FOULS));

        restartShotTimer = prefs.getBoolean(PrefActivity.PREF_SHOT_TIME_RESTART, true);
        playByPlay = PlayByPlayTypes.fromInteger(Integer.parseInt(prefs.getString(PrefActivity.PREF_PLAY_BY_PLAY, "0")));
        protocolType = ProtocolTypes.fromInteger(Integer.parseInt(prefs.getString(PrefActivity.PREF_PROTOCOL, "0")));

        boolean arrowsOn_ = prefs.getBoolean(PrefActivity.PREF_POSSESSION_ARROWS, false);
        if (arrowsOn != arrowsOn_) {
            arrowsStateChanged = true;
            arrowsOn = arrowsOn_;
        }

        BaseLayout.GameLayoutTypes temp = BaseLayout.GameLayoutTypes.fromInteger(Integer.parseInt(prefs.getString(PrefActivity.PREF_LAYOUT, "0")));
        if (temp != layoutType) {
            layoutChanged = true;
            layoutType = temp;
        }
        Rules.TimeoutRules temp_rules = Rules.TimeoutRules.fromInteger(Integer.parseInt(prefs.getString(PrefActivity.PREF_TIMEOUTS_RULES, DEFAULT_TIMEOUTS)));
        if (temp_rules != timeoutRules) {
            timeoutsRulesChanged = true;
            timeoutRules = temp_rules;
            if (temp_rules == Rules.TimeoutRules.STREETBALL) {
                SidePanelFragment.setMaxPlayers(MAX_PLAYERS_3X3);
                SidePanelFragment.setMinPlayers(MIN_PLAYERS_3X3);
            }
            else {
                SidePanelFragment.setMaxPlayers(MAX_PLAYERS);
                SidePanelFragment.setMinPlayers(MIN_PLAYERS);
            }
        }
        PrefActivity.prefChangedNoRestart = false;
        if (PrefActivity.prefColorChanged) {
            PrefActivity.prefColorChanged = false;
        }
    }

    private void readRestart() {
        useDirectTimer = prefs.getBoolean(PrefActivity.PREF_DIRECT_TIMER, false);
        PrefActivity.prefChangedRestart = false;
    }

    public void setDontAskNewGame(int value) {
        dontAskNewGame = value;
    }

    public int getColor(Elements element) {
        switch (element) {
            case HSCORE:
                return prefs.getInt(ColorPickerPreference.getKeyName(0), defaultScoreColor);
            case GSCORE:
                return prefs.getInt(ColorPickerPreference.getKeyName(1), defaultScoreColor);
            case MAIN_TIME:
                return prefs.getInt(ColorPickerPreference.getKeyName(2), defaultTimeColor);
            case SHOT_TIME:
                return prefs.getInt(ColorPickerPreference.getKeyName(3), defaultTimeColor);
        }
        return Color.RED;
    }

    public void setTeamName(int team, String value) {
        SharedPreferences.Editor editor = prefs.edit();
        if (team == HOME) {
            hName = value;
            editor.putString(PrefActivity.PREF_HOME_NAME, value);
        } else {
            gName = value;
            editor.putString(PrefActivity.PREF_GUEST_NAME, value);
        }
        editor.apply();
    }

    public void resetChangeStates() {
        arrowsStateChanged = false;
        fixLandscapeChanged = false;
        layoutChanged = false;
        shotTimePrefChanged = false;
        spStateChanged = false;
        timeoutsRulesChanged = false;
    }
}