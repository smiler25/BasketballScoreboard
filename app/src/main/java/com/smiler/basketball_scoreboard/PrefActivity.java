package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.smiler.basketball_scoreboard.elements.SetDefaultPreference;

public class PrefActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener,
                                                      SetDefaultPreference.SetDefaultDialogListener,
                                                      PrefFragment.OnSelectTimePreference {

    static boolean gamePrefChanged = false;
    static boolean appPrefChanged = false;
    static final String PREF_TAG = "main_prefs";
    static final String PREF_REGULAR_TIME = "regular_time_length";
    static final String PREF_OVERTIME = "overtime_length";
    static final String PREF_DIRECT_TIMER = "direct_timer";
    static final String PREF_SHOT_TIME = "shot_time_length";
    static final String PREF_ENABLE_SHOT_TIME = "enable_shot_time";
    static final String PREF_ENABLE_SHORT_SHOT_TIME = "enable_short_shot_time";
    static final String PREF_SHORT_SHOT_TIME = "short_shot_time_length";
    static final String PREF_SHOT_TIME_RESTART = "short_shot_restart";
    static final String PREF_MAX_FOULS = "max_fouls";
    static final String PREF_NUM_REGULAR = "number_of_regular_periods";
    static final String PREF_HOME_NAME = "home_team_name";
    static final String PREF_GUEST_NAME = "guest_team_name";
    static final String PREF_ACTUAL_TIME = "list_actual_time";
    static final String PREF_TIMEOUTS_RULES = "list_timeout_rules";
    static final String PREF_OFFICIAL_RULES = "list_official_rules";

    static final String PREF_LAYOUT = "list_layout";
    static final String PREF_AUTO_SOUND = "list_auto_sounds";
    static final String PREF_AUTO_SAVE_RESULTS = "auto_save_results";
    static final String PREF_PAUSE_ON_SOUND = "pause_on_sound";
    static final String PREF_AUTO_BREAK = "auto_show_break";
    static final String PREF_AUTO_TIMEOUT = "auto_show_timeout";
    static final String PREF_SAVE_ON_EXIT = "save_on_exit";

    private static final int DEFAULT_SHOT_TIME = 24;
    private static final int DEFAULT_SHORT_SHOT_TIME = 14;
    private static final int DEFAULT_NUM_REGULAR = 4;
    private static final int DEFAULT_OVERTIME = 5;
    private static final boolean DEFAULT_ENABLE_SHOT_TIME = true;
    private static final boolean DEFAULT_ENABLE_SHORT_SHOT_TIME = true;
    private static final int DEFAULT_MAX_FOULS = 5;

    private static final int DEFAULT_FIBA_MAIN_TIME = 10;
    private static final String DEFAULT_FIBA_TIMEOUTS = "1";
    private static final int DEFAULT_NBA_MAIN_TIME = 12;
    private static final String DEFAULT_NBA_TIMEOUTS = "2";

    SharedPreferences prefs;

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
        Toolbar toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.pref_toolbar, root, false);
        root.addView(toolbar, 0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_AUTO_SOUND:
            case PREF_AUTO_SAVE_RESULTS:
            case PREF_PAUSE_ON_SOUND:
            case PREF_AUTO_BREAK:
            case PREF_AUTO_TIMEOUT:
            case PREF_SAVE_ON_EXIT:
                appPrefChanged = true;
                break;
            case PREF_OFFICIAL_RULES:
                setDefault(prefs.getInt(PREF_OFFICIAL_RULES, 0));
                break;
            default:
                gamePrefChanged = true;
                break;
        }
    }

    public void setDefault(int type) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PREF_NUM_REGULAR, DEFAULT_NUM_REGULAR);
        editor.putInt(PREF_OVERTIME, DEFAULT_OVERTIME);
        editor.putInt(PREF_SHOT_TIME, DEFAULT_SHOT_TIME);
        editor.putInt(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_SHORT_SHOT_TIME);
        editor.putInt(PREF_MAX_FOULS, DEFAULT_MAX_FOULS);
        editor.putBoolean(PREF_ENABLE_SHOT_TIME, DEFAULT_ENABLE_SHOT_TIME);
        editor.putBoolean(PREF_ENABLE_SHORT_SHOT_TIME, DEFAULT_ENABLE_SHORT_SHOT_TIME);
        if (type == 0) {
            editor.putInt(PREF_REGULAR_TIME, DEFAULT_FIBA_MAIN_TIME);
            editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_FIBA_TIMEOUTS);
        } else {
            editor.putInt(PREF_REGULAR_TIME, DEFAULT_NBA_MAIN_TIME);
            editor.putString(PREF_TIMEOUTS_RULES, DEFAULT_NBA_TIMEOUTS);
        }
        editor.apply();
        restartActivity();
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
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentTime())
                .addToBackStack(null)
                .commit();
    }
}