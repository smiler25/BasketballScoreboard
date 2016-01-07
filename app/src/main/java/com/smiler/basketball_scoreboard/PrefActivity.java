package com.smiler.basketball_scoreboard;

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

import com.smiler.basketball_scoreboard.elements.SetDefaultPreference;

import java.util.Arrays;
import java.util.List;

public class PrefActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        SetDefaultPreference.SetDefaultDialogListener,
        PrefFragment.OnSelectNestedScreenPreference {

    static boolean prefChangedRestart = false;
    static boolean prefChangedNoRestart = false;

    static final String PREF_REGULAR_TIME = "regular_time_length";
    static final String PREF_OVERTIME = "overtime_length";
    static final String PREF_DIRECT_TIMER = "direct_timer";
    static final String PREF_ENABLE_SHOT_TIME = "enable_shot_time";
    static final String PREF_SHOT_TIME = "shot_time_length";
    static final String PREF_ENABLE_SHORT_SHOT_TIME = "enable_short_shot_time";
    static final String PREF_SHORT_SHOT_TIME = "short_shot_time_length";
    static final String PREF_SHOT_TIME_RESTART = "shot_time_restart";
    static final String PREF_ACTUAL_TIME = "list_actual_time";
    static final String PREF_FRACTION_SECONDS_MAIN = "fraction_seconds_main";
    static final String PREF_FRACTION_SECONDS_SHOT = "fraction_seconds_shot";

    static final String PREF_NUM_REGULAR = "number_of_regular_periods";
    static final String PREF_MAX_FOULS = "max_fouls";
    static final String PREF_TIMEOUTS_RULES = "list_timeout_rules";
    static final String PREF_HOME_NAME = "home_team_name";
    static final String PREF_GUEST_NAME = "guest_team_name";
    static final String PREF_OFFICIAL_RULES = "list_official_rules";

    static final String PREF_ENABLE_SIDE_PANELS = "side_panels_activate";
    static final String PREF_SIDE_PANELS_INTERACTION = "side_panels_interaction";
    static final String PREF_SIDE_PANELS_CONNECTED = "side_panels_dependency";
    static final String PREF_SIDE_PANELS_FOULS_RULES = "side_panels_player_fouls_rules";
    static final String PREF_SIDE_PANELS_FOULS_MAX = "side_panels_player_max_fouls";

    static final String PREF_LAYOUT = "list_layout";
    static final String PREF_AUTO_SOUND = "list_auto_sounds";
    static final String PREF_AUTO_SAVE_RESULTS = "auto_save_results";
    static final String PREF_PAUSE_ON_SOUND = "pause_on_sound";
    static final String PREF_AUTO_BREAK = "auto_show_break";
    static final String PREF_AUTO_TIMEOUT = "auto_show_timeout";
    static final String PREF_VIBRATION = "vibration";
    static final String PREF_SAVE_ON_EXIT = "save_on_exit";

    private static final boolean DEFAULT_ENABLE_SHOT_TIME = true;
    private static final boolean DEFAULT_ENABLE_SHORT_SHOT_TIME = true;

    private static final String DEFAULT_FIBA_TIMEOUTS = "1";
    private static final String DEFAULT_NBA_TIMEOUTS = "2";

    private static final String SIDE_PANEL_FOULS_RULES_STRICT = "1";
    private static final String DEFAULT_SIDE_PANEL_FOULS_RULES = "2";
    private static final String CUSTOM_SIDE_PANEL_FOULS_RULES = "3";
    private boolean playerRulesDefault = false;

    SharedPreferences prefs;
    private Toolbar toolbar;
    private final List<String> noRestartPrefs = Arrays.asList(PREF_AUTO_SOUND, PREF_AUTO_SAVE_RESULTS,
            PREF_PAUSE_ON_SOUND, PREF_AUTO_BREAK, PREF_AUTO_TIMEOUT, PREF_SAVE_ON_EXIT, PREF_VIBRATION,
            PREF_FRACTION_SECONDS_MAIN, PREF_FRACTION_SECONDS_SHOT, PREF_ENABLE_SIDE_PANELS,
            PREF_SIDE_PANELS_INTERACTION, PREF_SIDE_PANELS_CONNECTED, PREF_SIDE_PANELS_FOULS_RULES,
            PREF_SIDE_PANELS_FOULS_MAX);

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
        toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.pref_toolbar, root, false);
        root.addView(toolbar, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    toolbar.setTitle(R.string.action_help);
//                    onBackPressed();
                    toolbar.setTitle(R.string.title_activity_settings);
                } else {
                    finish();
                }
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (noRestartPrefs.contains(key)) {
            prefChangedNoRestart = true;
            if (key.equals(PREF_SIDE_PANELS_FOULS_MAX) && !playerRulesDefault) {
                setPlayerCustomFoulsRules();
            } else if (key.equals(PREF_SIDE_PANELS_FOULS_RULES)) {// && prefs.getString(PREF_SIDE_PANELS_FOULS_RULES, DEFAULT_SIDE_PANEL_FOULS_RULES).equals(DEFAULT_SIDE_PANEL_FOULS_RULES)) {
                setPlayerDefaultFouls();
            }
        } else if (key.equals(PREF_OFFICIAL_RULES)){
            setDefault(prefs.getInt(PREF_OFFICIAL_RULES, 0));
        } else {
            prefChangedRestart = true;
        }
    }

    public void setDefault(int type) {
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
}