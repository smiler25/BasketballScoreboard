package com.smiler.basketball_scoreboard.preferences;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;

import java.util.Arrays;
import java.util.List;

public class PrefActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        SetDefaultPreference.SetDefaultDialogListener,
        SeekBarPreference.SeekBarDialogListener,
        PrefFragment.OnSelectNestedScreenPreference {

    public static boolean prefChangedRestart = false;
    public static boolean prefChangedNoRestart = false;

    public static final String PREF_REGULAR_TIME = "regular_time_length";
    public static final String PREF_OVERTIME = "overtime_length";
    public static final String PREF_DIRECT_TIMER = "direct_timer";
    public static final String PREF_ENABLE_SHOT_TIME = "enable_shot_time";
    public static final String PREF_SHOT_TIME = "shot_time_length";
    public static final String PREF_ENABLE_SHORT_SHOT_TIME = "enable_short_shot_time";
    public static final String PREF_SHORT_SHOT_TIME = "short_shot_time_length";
    public static final String PREF_SHOT_TIME_RESTART = "shot_time_restart";
    public static final String PREF_ACTUAL_TIME = "list_actual_time";
    public static final String PREF_FRACTION_SECONDS_MAIN = "fraction_seconds_main";
    public static final String PREF_FRACTION_SECONDS_SHOT = "fraction_seconds_shot";

    public static final String PREF_NUM_REGULAR = "number_of_regular_periods";
    public static final String PREF_MAX_FOULS = "max_fouls";
    public static final String PREF_TIMEOUTS_RULES = "list_timeout_rules";
    public static final String PREF_HOME_NAME = "home_team_name";
    public static final String PREF_GUEST_NAME = "guest_team_name";
    private static final String PREF_OFFICIAL_RULES = "list_official_rules";

    public static final String PREF_ENABLE_SIDE_PANELS = "side_panels_activate";
    public static final String PREF_SIDE_PANELS_CLEAR = "side_panels_clear";
    public static final String PREF_SIDE_PANELS_CONNECTED = "side_panels_dependency";
    private static final String PREF_SIDE_PANELS_FOULS_RULES = "side_panels_player_fouls_rules";
    public static final String PREF_SIDE_PANELS_FOULS_MAX = "side_panels_player_max_fouls";

    public static final String PREF_LAYOUT = "list_layout";
    public static final String PREF_AUTO_SAVE_RESULTS = "auto_save_results";
    public static final String PREF_AUTO_BREAK = "auto_show_break";
    public static final String PREF_AUTO_TIMEOUT = "auto_show_timeout";
    public static final String PREF_AUTO_SWITCH_SIDES = "auto_switch_sides";
    public static final String PREF_VIBRATION = "vibration";
    public static final String PREF_SAVE_ON_EXIT = "save_on_exit";
    public static final String PREF_POSSESSION_ARROWS = "possession_arrows";

    public static final String PREF_AUTO_SOUND = "list_auto_sounds";
    public static final String PREF_PAUSE_ON_SOUND = "pause_on_sound";
    public static final String PREF_HORN_LENGTH = "horn_length";
    public static final String PREF_FIX_LANDSCAPE = "fix_landscape";
    public static final String PREF_PLAY_BY_PLAY = "play_by_play";

    private static final boolean DEFAULT_ENABLE_SHOT_TIME = true;
    private static final boolean DEFAULT_ENABLE_SHORT_SHOT_TIME = true;

    private static final String DEFAULT_FIBA_TIMEOUTS = "1";
    private static final String DEFAULT_NBA_TIMEOUTS = "2";

    private static final String SIDE_PANEL_FOULS_RULES_STRICT = "1";
//    private static final String DEFAULT_SIDE_PANEL_FOULS_RULES = "2";
    private static final String CUSTOM_SIDE_PANEL_FOULS_RULES = "3";
    private boolean playerRulesDefault = false;

    private SharedPreferences prefs;
    private Toolbar toolbar;
    private final List<String> restartPrefs = Arrays.asList(PREF_TIMEOUTS_RULES, PREF_LAYOUT, PREF_DIRECT_TIMER);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment())
                .commit();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initToolbar();
    }

    private void initToolbar() {
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_prefs, root, false);
        root.addView(toolbar, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    toolbar.setTitle(R.string.action_help);
                    toolbar.setTitle(R.string.title_activity_settings);
                } else {
                    finish();
                }
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (restartPrefs.contains(key)) {
            prefChangedRestart = true;
        } else if (key.equals(PREF_OFFICIAL_RULES)){
            setDefault(prefs.getInt(PREF_OFFICIAL_RULES, 0));
        } else {
            prefChangedNoRestart = true;
            if (key.equals(PREF_SIDE_PANELS_FOULS_MAX) && !playerRulesDefault) {
                setPlayerCustomFoulsRules();
            } else if (key.equals(PREF_SIDE_PANELS_FOULS_RULES)) {// && prefs.getString(PREF_SIDE_PANELS_FOULS_RULES, DEFAULT_SIDE_PANEL_FOULS_RULES).equals(DEFAULT_SIDE_PANEL_FOULS_RULES)) {
                setPlayerDefaultFouls();
            }
        }
    }

    private void setDefault(int type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_NUM_REGULAR, Constants.DEFAULT_NUM_REGULAR);
        editor.putInt(PREF_OVERTIME, Constants.DEFAULT_OVERTIME);
        editor.putInt(PREF_SHOT_TIME, Constants.DEFAULT_SHOT_TIME);
        editor.putInt(PREF_ENABLE_SHORT_SHOT_TIME, Constants.DEFAULT_SHORT_SHOT_TIME);
        editor.putInt(PREF_MAX_FOULS, Constants.DEFAULT_MAX_FOULS);
        editor.putBoolean(PREF_ENABLE_SHOT_TIME, DEFAULT_ENABLE_SHOT_TIME);
        editor.putBoolean(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_ENABLE_SHORT_SHOT_TIME);
        editor.putString(PREF_SIDE_PANELS_FOULS_RULES, SIDE_PANEL_FOULS_RULES_STRICT);
        if (type == 0) {
            editor.putInt(PREF_REGULAR_TIME, Constants.DEFAULT_FIBA_MAIN_TIME);
            editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, Constants.DEFAULT_FIBA_PLAYER_FOULS);
            editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_FIBA_TIMEOUTS);
        } else {
            editor.putInt(PREF_REGULAR_TIME, Constants.DEFAULT_NBA_MAIN_TIME);
            editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, Constants.DEFAULT_NBA_PLAYER_FOULS);
            editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_NBA_TIMEOUTS);
        }
        editor.apply();
        prefChangedRestart = true;
        restartActivity();
    }

    private void setPlayerCustomFoulsRules() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_SIDE_PANELS_FOULS_RULES, CUSTOM_SIDE_PANEL_FOULS_RULES);
        playerRulesDefault = false;
        editor.apply();
    }

    private void setPlayerDefaultFouls() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, (prefs.getInt(PREF_MAX_FOULS, Constants.DEFAULT_MAX_FOULS)));
        playerRulesDefault = true;
        editor.apply();
    }

    @Override
    public void onSetPositive() { setDefault(1); }

    @Override
    public void onSetNegative() { setDefault(0); }

    @Override
    public void onAcceptSeekBarValue(int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_HORN_LENGTH, value);
        editor.apply();
        prefChangedNoRestart = true;
    }

    @Override
    protected void onResume() {
         super.onResume();
         prefs.registerOnSharedPreferenceChangeListener(this);
     }

    @Override
    protected void onPause() {
         super.onPause();
         prefs.unregisterOnSharedPreferenceChangeListener(this);
     }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onSelectTimePreference() {
        openTimeSettings();
    }

    @Override
    public void onSelectSidePanelsPreference() {
        openSidePanelsSettings();
    }

    @Override
    public void onSelectSoundsPreference() {
        openSoundsSettings();
    }

    private void openTimeSettings() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentTime())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        toolbar.setTitle(R.string.time_screen);
    }

    private void openSidePanelsSettings() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentSidePanels())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        toolbar.setTitle(R.string.side_panels_screen);
    }

    private void openSoundsSettings() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentSounds())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        toolbar.setTitle(R.string.sounds_screen);
    }
}
