package com.smiler.basketball_scoreboard.preferences;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewParent;
import android.widget.LinearLayout;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.Rules;

import static com.smiler.basketball_scoreboard.Rules.RULES_FIBA;

public class PrefActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        SetDefaultPreference.SetDefaultDialogListener,
        SeekBarPreference.SeekBarDialogListener,
        ColorPickerPreference.ColorPickerListener,
        PrefFragment.OnSelectNestedScreenPreference {

    private static final String TAG = "BS-PrefActivity";
    public static boolean prefChangedNoRestart = false;
    public static boolean prefChangedRestart = false;
    public static boolean prefColorChanged = false;

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
    public static final String PREF_MAX_FOULS_WARN = "max_fouls_warn";
    public static final String PREF_TIMEOUTS_RULES = "list_timeout_rules";
    public static final String PREF_HOME_NAME = "home_team_name";
    public static final String PREF_GUEST_NAME = "guest_team_name";
    private static final String PREF_OFFICIAL_RULES = "list_official_rules";

    public static final String PREF_ENABLE_SIDE_PANELS = "side_panels_activate";
    public static final String PREF_SIDE_PANELS_CLEAR = "side_panels_clear";
    public static final String PREF_SIDE_PANELS_CONNECTED = "side_panels_dependency";
    public static final String PREF_SIDE_PANELS_FOULS_RULES = "side_panels_player_fouls_rules";
    public static final String PREF_SIDE_PANELS_FOULS_MAX = "side_panels_player_max_fouls";

    public static final String PREF_LAYOUT = "list_layout";
    public static final String PREF_AUTO_BREAK = "auto_show_break";
    public static final String PREF_AUTO_TIMEOUT = "auto_show_timeout";
    public static final String PREF_AUTO_SWITCH_SIDES = "auto_switch_sides";
    public static final String PREF_VIBRATION = "vibration";
    public static final String PREF_SAVE_GAME_RESULTS = "save_game_results";
    public static final String PREF_SAVE_ON_EXIT = "save_on_exit";
    public static final String PREF_POSSESSION_ARROWS = "possession_arrows";

    public static final String PREF_AUTO_SOUND = "list_auto_sounds";
    public static final String PREF_PAUSE_ON_SOUND = "pause_on_sound";
    public static final String PREF_HORN_LENGTH = "horn_length";
    public static final String PREF_FIX_LANDSCAPE = "fix_landscape";
    public static final String PREF_PLAY_BY_PLAY = "play_by_play";
    public static final String PREF_PROTOCOL = "protocol";

    public static final String SIDE_PANEL_FOULS_RULES_STRICT = "1";
//    private static final String DEFAULT_SIDE_PANEL_FOULS_RULES = "2";
    private static final String CUSTOM_SIDE_PANEL_FOULS_RULES = "3";
    private boolean playerRulesDefault = false;

    private SharedPreferences prefs;
    private Toolbar toolbar;

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

    private LinearLayout getRoot(ViewParent view) {
        ViewParent parent = view.getParent();
        if (parent instanceof LinearLayout) {
            return (LinearLayout) parent;
        }
        return getRoot(parent);
    }

    private void initToolbar() {
        try {
            LinearLayout root = getRoot(findViewById(android.R.id.list).getParent());
            toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar_prefs, root, false);
            root.addView(toolbar, 0);
            toolbar.setNavigationOnClickListener(v -> {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                    toolbar.setTitle(R.string.action_help);
                    toolbar.setTitle(R.string.title_activity_settings);
                } else {
                    finish();
                }
            });

        } catch (RuntimeException e) {
            Log.e(TAG, "Error init toolbar");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case PREF_DIRECT_TIMER:
                prefChangedRestart = true;
                break;
            case PREF_OFFICIAL_RULES:
                setDefault(Integer.parseInt(prefs.getString(PREF_OFFICIAL_RULES, Integer.toString(RULES_FIBA))));
                break;
            default:
                prefChangedNoRestart = true;
                if (key.equals(PREF_SIDE_PANELS_FOULS_MAX) && !playerRulesDefault) {
                    setPlayerCustomFoulsRules();
                } else if (key.equals(PREF_SIDE_PANELS_FOULS_RULES)) {
                    // && prefs.getString(PREF_SIDE_PANELS_FOULS_RULES, DEFAULT_SIDE_PANEL_FOULS_RULES).equals(DEFAULT_SIDE_PANEL_FOULS_RULES)) {
                    setPlayerDefaultFouls();
                }
                break;
        }
    }

    private void setDefault(int type) {
        Rules.setDefaultRules(prefs, type);
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
        editor.putInt(PREF_SIDE_PANELS_FOULS_MAX, prefs.getInt(PREF_MAX_FOULS, Rules.DEFAULT_MAX_FOULS));
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
        toolbar.setTitle(R.string.screen_time);
    }

    private void openSidePanelsSettings() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentSidePanels())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        toolbar.setTitle(R.string.screen_sp);
    }

    private void openSoundsSettings() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragmentSounds())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
        toolbar.setTitle(R.string.screen_sounds);
    }

    @Override
    public void onAcceptColor() {
        prefColorChanged = true;
    }
}
