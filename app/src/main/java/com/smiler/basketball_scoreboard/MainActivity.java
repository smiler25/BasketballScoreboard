package com.smiler.basketball_scoreboard;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.ConfirmDialog;
import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.elements.ListDialog;
import com.smiler.basketball_scoreboard.elements.NameEditDialog;
import com.smiler.basketball_scoreboard.elements.TimePickerFragment;
import com.smiler.basketball_scoreboard.help.HelpActivity;
import com.smiler.basketball_scoreboard.layout.StandardLayout;
import com.smiler.basketball_scoreboard.models.Game;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;
import com.smiler.basketball_scoreboard.preferences.Preferences;
import com.smiler.basketball_scoreboard.results.ResultsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.realm.Realm;

import static com.smiler.basketball_scoreboard.Constants.OVERLAY_PANELS;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;
import static com.smiler.basketball_scoreboard.Constants.SIDE_PANELS_LEFT;
import static com.smiler.basketball_scoreboard.Constants.SIDE_PANELS_RIGHT;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_APP_UPDATES;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_MAIN_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_NAME_EDIT;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_SHOT_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_TIME;


public class MainActivity extends AppCompatActivity implements
        StandardLayout.ClickListener,
        StandardLayout.LongClickListener,
        ConfirmDialog.ConfirmDialogListener,
        Drawer.OnDrawerItemClickListener,
        EditPlayerDialog.OnEditPlayerListener,
        NameEditDialog.OnChangeNameListener,
        OverlayFragment.OverlayFragmentListener,
        SidePanelFragment.SidePanelListener,
        SoundPool.OnLoadCompleteListener,
        ListDialog.NewTimeoutDialogListener,
        TimePickerFragment.OnChangeTimeListener {

    public static final String TAG = "BS-MainActivity";
    private SharedPreferences statePref;
    private ViewGroup leftPlayersButtonsGroup, rightPlayersButtonsGroup;
    private Drawer.Result drawer;
    private Preferences preferences;
    private Game game;
    private Realm realm;

    private boolean doubleBackPressedFirst;
    private FloatingCountdownTimerDialog floatingDialog;
    private SidePanelFragment leftPanel, rightPanel;
    private OverlayFragment overlayPanels, overlaySwitch;
    private ArrayList<View> leftPlayersButtons = new ArrayList<>();
    private ArrayList<View> rightPlayersButtons = new ArrayList<>();

    private Animation shotTimeBlinkAnimation = new AlphaAnimation(1, 0);
    private int soundWhistleId, soundHornId, soundWhistleStreamId, soundHornStreamId;
    private int whistleRepeats, hornRepeats, whistleLength, hornLength;
    private boolean whistlePressed, hornPressed;

    private SoundPool soundPool;
    private Vibrator vibrator;
    private long[] longClickVibrationPattern = {0, 50, 50, 50};
    private TreeMap<Integer, SidePanelRow> inactivePlayers;
    private static Context mainActivityContext;

    public static Context getContext() {
        return mainActivityContext;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (preferences.saveOnExit) {
            saveCurrentState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        handleScoreViewSize();
        // System.out.println("res_type = " + getResources().getString(R.string.res_type));

        if (PrefActivity.prefChangedRestart) {
            showConfirmDialog("new_game", false);
        } else if (PrefActivity.prefChangedNoRestart) {
            preferences.readRestart();
            if (preferences.fixLandscapeChanged) {
                handleOrientation();
                preferences.fixLandscapeChanged = false;
            }

            if (preferences.spStateChanged) {
                if (!preferences.spOn) {
                    leftPlayersButtonsGroup.setVisibility(View.GONE);
                    rightPlayersButtonsGroup.setVisibility(View.GONE);
                } else {
                    if (leftPlayersButtonsGroup == null){
                        initSidePanels();
                    } else {
                        leftPlayersButtonsGroup.setVisibility(View.VISIBLE);
                        rightPlayersButtonsGroup.setVisibility(View.VISIBLE);
                    }
                }
            }
//            if (preferences.enableShotTimeChanged && preferences.layoutType == GAME_TYPE.COMMON) {
//                try {
//                    if (!preferences.enableShotTime) {
//                        shotTimeView.setVisibility(View.GONE);
//                        shotTimeSwitchView.setVisibility(View.GONE);
//                    } else {
//                        shotTimeView.setVisibility(View.VISIBLE);
//                        if (preferences.shortShotTimePref != preferences.shotTimePref) {
//                            shotTimeSwitchView.setVisibility(View.VISIBLE);
//                        }
//                    }
//                } catch (NullPointerException e) {
//                    Log.d(TAG, e.getMessage() + (shotTimeView != null ? shotTimeView.toString() : "shotTimeView == null"));
//                }
//            }
////            if (preferences.arrowsStateChanged) {
////                handleArrowsVisibility();
////            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityContext = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        initSounds();

        preferences = Preferences.getInstance(getApplicationContext());
        preferences.read();
//        if (hName == null || hName.equals("")) {
//            hName = preferences.hName;
//        }
//        if (gName == null || gName.equals("")) {
//            gName = preferences.gName;
//        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getInt("app_version", 1) < BuildConfig.VERSION_CODE) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("app_version", BuildConfig.VERSION_CODE);
            editor.apply();

            new AppUpdatesFragment().show(getFragmentManager(), TAG_FRAGMENT_APP_UPDATES);
            migrateToRealm();
            migrateActivityPreferences();
        }

        handleOrientation();
        initGame();
        initLayout();

        if (sharedPref.getBoolean("first_launch", true)) {
            drawer.openDrawer();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first_launch", false);
            editor.apply();
        }

        shotTimeBlinkAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        floatingDialog = new FloatingCountdownTimerDialog();
        floatingDialog.setCancelable(false);
//        if (preferences.saveOnExit) {
//            getSavedState();
//            setSavedState();
//        } else {
//            newGame();
//        }
    }

    private void migrateToRealm() {
        String parent = getFilesDir().getParent();
        File path = new File(parent + "/databases");
        if (path.exists()) {
            File dbPath = new File(parent + "/databases/" + DbHelper.DATABASE_NAME);
            File realmPath = new File(parent + "/files/" + RealmController.realmName);
            if (dbPath.exists() && !realmPath.exists()) {
                Log.i(TAG, "Migrate sql to realm");
                DbHelper helper = DbHelper.getInstance(this);
                helper.open();
            }
        }
    }

    private void migrateActivityPreferences() {
        getSavedState();
        SharedPreferences prefs = getSharedPreferences(Constants.STATE_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(STATE_HOME_NAME, hName);
//        editor.putString(STATE_GUEST_NAME, gName);
//        editor.putLong(STATE_SHOT_TIME, shotTime);
//        editor.putLong(STATE_MAIN_TIME, mainTime);
//        editor.putInt(STATE_PERIOD, period);
//        editor.putInt(STATE_HOME_SCORE, hScore);
//        editor.putInt(STATE_GUEST_SCORE, gScore);
//        editor.putInt(STATE_HOME_FOULS, hFouls);
//        editor.putInt(STATE_GUEST_FOULS, gFouls);
//        if (preferences.timeoutRules == Game.TO_RULES.FIBA) {
//            editor.putInt(STATE_HOME_TIMEOUTS, hTimeouts);
//            editor.putInt(STATE_GUEST_TIMEOUTS, gTimeouts);
//        } else if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//            editor.putInt(STATE_HOME_TIMEOUTS_NBA, hTimeouts);
//            editor.putInt(STATE_GUEST_TIMEOUTS_NBA, gTimeouts);
//            editor.putInt(STATE_HOME_TIMEOUTS20, hTimeouts20);
//            editor.putInt(STATE_GUEST_TIMEOUTS20, gTimeouts20);
//        }
//        if (preferences.arrowsOn) {
//            editor.putInt(STATE_POSSESSION, possession);
//        }
//        editor.apply();
//        if (preferences.spOn) {
//            if (leftPanel != null) {
//                leftPanel.saveCurrentData();
//            }
//            if (rightPanel != null) {
//                rightPanel.saveCurrentData();
//            }
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        if (overlaySwitch != null && overlaySwitch.isAdded()) {
            getFragmentManager().putFragment(outState, OverlayFragment.TAG_SWITCH, overlaySwitch);
        }
        if (overlayPanels != null && overlayPanels.isAdded()) {
            getFragmentManager().putFragment(outState, OverlayFragment.TAG_PANELS, overlayPanels);
        }
        if (leftPanel != null && leftPanel.isAdded()) {
            getFragmentManager().putFragment(outState, SidePanelFragment.TAG_LEFT_PANEL, leftPanel);
        }
        if (rightPanel != null && rightPanel.isAdded()) {
            getFragmentManager().putFragment(outState, SidePanelFragment.TAG_RIGHT_PANEL, rightPanel);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle inState){
        if (inState != null) {
            FragmentManager fm = getFragmentManager();
            Fragment overlaySwitch_ = fm.getFragment(inState, OverlayFragment.TAG_SWITCH);
            if (overlaySwitch_ != null) {
                overlaySwitch = (OverlayFragment) overlaySwitch_;
            }

            if (preferences.spOn) {
                Fragment overlayPanels_ = fm.getFragment(inState, OverlayFragment.TAG_PANELS);
                if (overlayPanels_ != null) {
                    overlayPanels = (OverlayFragment) overlayPanels_;
                }
                Fragment leftPanel_ = fm.getFragment(inState, SidePanelFragment.TAG_LEFT_PANEL);
                if (leftPanel_ != null) {
                    leftPanel = (SidePanelFragment) leftPanel_;
                }
                Fragment rightPanel_ = fm.getFragment(inState, SidePanelFragment.TAG_RIGHT_PANEL);
                if (rightPanel_ != null) {
                    rightPanel = (SidePanelFragment) rightPanel_;
                }
                fm.popBackStack();
            }
        }
    }

    private void handleOrientation() {
        if (preferences.fixLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private void initGame() {
        StandardLayout layout = new StandardLayout(this, preferences, this, this);
        setContentView(layout);
        game = Game.newGame(this, layout);
    }

    private void startNewGame(boolean save) {
        if (save) {
            game = game.saveAndNew();
        } else {
            game = Game.newGame(this);
        }
    }

    private void initLayout() {
        overlaySwitch = OverlayFragment.newInstance(OVERLAY_SWITCH);
        overlaySwitch.setRetainInstance(true);
//        layout = new StandardLayout(this, preferences, this, this);
//        setContentView(layout);
        if (preferences.spOn) {
            initSidePanels();
            leftPlayersButtonsGroup.setVisibility(View.VISIBLE);
            rightPlayersButtonsGroup.setVisibility(View.VISIBLE);
        }
        initDrawer();
    }

    private void initSidePanels() {
        ViewStub leftPlayersStub = (ViewStub) findViewById(R.id.left_panel_stub);
        ViewStub rightPlayersStub = (ViewStub) findViewById(R.id.right_panel_stub);
        leftPlayersStub.setLayoutResource(R.layout.side_panel_left_buttons);
        leftPlayersStub.inflate();
        rightPlayersStub.setLayoutResource(R.layout.side_panel_right_buttons);
        rightPlayersStub.inflate();

//        leftPlayersButtonsGroup = (ViewGroup) findViewById(R.id.left_panel);
//        leftPlayersButtons = getAllButtons(leftPlayersButtonsGroup);
//        for (View bu : leftPlayersButtons) {
//            attachLeftButton(bu);
//        }
//
//        rightPlayersButtonsGroup = (ViewGroup) findViewById(R.id.right_panel);
//        rightPlayersButtons = getAllButtons(rightPlayersButtonsGroup);
//        for (View bu : rightPlayersButtons) {
//            attachRightButton(bu);
//        }

        leftPanel = SidePanelFragment.newInstance(true);
        rightPanel = SidePanelFragment.newInstance(false);
        overlayPanels = OverlayFragment.newInstance(OVERLAY_PANELS);
        leftPanel.setRetainInstance(true);
        rightPanel.setRetainInstance(true);
        overlayPanels.setRetainInstance(true);

//        findViewById(R.id.left_panel_toggle).setOnClickListener(this);
//        findViewById(R.id.right_panel_toggle).setOnClickListener(this);
        preferences.spStateChanged = false;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.left_panel_full, leftPanel, SidePanelFragment.TAG_LEFT_PANEL);
        ft.add(R.id.right_panel_full, rightPanel, SidePanelFragment.TAG_RIGHT_PANEL);
        ft.hide(leftPanel).hide(rightPanel);
        ft.addToBackStack(null).commit();
    }

//    private ArrayList<View> getAllButtons(ViewGroup group) {
//        ArrayList<View> res = new ArrayList<>();
//        View button;
//        for (int i = 0; i < group.getChildCount(); i++) {
//            button = group.getChildAt(i);
//            if (button instanceof Button) {
//                res.add(button);
//            } else if (button instanceof ViewGroup) {
//                res.addAll(getAllButtons((ViewGroup) button));
//            }
//        }
//        return res;
//    }
//
//    private void attachLeftButton(View button) {
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SidePanelRow row = (SidePanelRow) v.getTag();
//                if (row == null) {
//                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_select_players), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (leftActionType != ACTION_NONE) {
//                    if (leftActionType == ACTION_PTS) {
//                        row.changePoints(leftActionValue);
//                    } else if (leftActionType == ACTION_FLS) {
//                        row.changeFouls(leftActionValue);
//                    }
//                    leftActionType = ACTION_NONE;
//                    leftActionValue = 0;
//                }
//                if (lastAction != null) {
//                    lastAction.setNumber(row.getNumber());
//                }
//            }
//        });
//        button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
////                longClickPlayerBu = (Button) v;
//                showListDialog(true);
//                return false;
//            }
//        });
//    }
//
//    private void attachRightButton(View button) {
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SidePanelRow row = (SidePanelRow) v.getTag();
//                if (row == null) {
//                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_select_players), Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (rightActionType != ACTION_NONE) {
//                    if (rightActionType == ACTION_PTS) {
//                        row.changePoints(rightActionValue);
//                    } else if (rightActionType == ACTION_FLS) {
//                        row.changeFouls(rightActionValue);
//                    }
//                    rightActionType = ACTION_NONE;
//                    rightActionValue = 0;
//                }
//                if (lastAction != null) {
//                    lastAction.setNumber(row.getNumber());
//                }
//            }
//        });
//        button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
////                longClickPlayerBu = (Button) v;
//                showListDialog(false);
//                return false;
//            }
//        });
//    }

    private void initSounds() {
        int MAX_STREAMS = 5;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(aa)
                    .build();
        } else {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_NOTIFICATION, 1);
        }
        soundWhistleId = soundPool.load(this, R.raw.whistle, 1);
        whistleLength = 190;
        soundHornId = soundPool.load(this, R.raw.airhorn, 1);
        hornLength = 850;
    }

    private void initDrawer() {
        AccountHeader.Result drawerHeader = createDrawerHeader();
        drawer = new Drawer()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withFullscreen(true)
                .withDrawerWidthPx(getResources().getDimensionPixelSize(R.dimen.drawer_width))
                .withAccountHeader(drawerHeader)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(initDrawerItems())
                .withOnDrawerItemClickListener(this)
                .build();
    }

    private AccountHeader.Result createDrawerHeader() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .build();
    }

    private IDrawerItem[] initDrawerItems() {
        return new IDrawerItem[]{
                new SecondaryDrawerItem().withName(R.string.action_new_game).withIcon(getResources().getDrawable(R.drawable.ic_action_replay)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_resluts).withIcon(getResources().getDrawable(R.drawable.ic_action_storage)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(getResources().getDrawable(R.drawable.ic_action_settings)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_share).withIcon(getResources().getDrawable(R.drawable.ic_action_share)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_help).withIcon(getResources().getDrawable(R.drawable.ic_action_about)).withCheckable(false),
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                runSettingsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void runResultsActivity() {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    private void runSettingsActivity() {
        Intent intent = new Intent(this, PrefActivity.class);
        startActivity(intent);
    }

    private void runCameraActivity() {
//        Intent intent = new Intent(this, CameraActivity.class);
//        intent.putExtra("layoutType", preferences.layoutType);
//        intent.putExtra("hName", hName);
//        intent.putExtra("gName", gName);
//        intent.putExtra("hScore", hScore);
//        intent.putExtra("gScore", gScore);
//        intent.putExtra("mainTime", mainTime);
//        if (preferences.layoutType == GAME_TYPE.COMMON) {
//            intent.putExtra("shotTime", shotTime);
//            intent.putExtra("period", period);
//        }
//        startActivityForResult(intent, 1);
    }

    private void runHelpActivity() {
        startActivity(new Intent(this, HelpActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) { return; }
//        hScore = data.getShortExtra("hScore", hScore);
//        gScore = data.getShortExtra("gScore", gScore);
//        mainTime = data.getLongExtra("mainTime", mainTime);
//        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
//        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
//        setMainTimeText(mainTime);

//        if (preferences.layoutType == GAME_TYPE.COMMON) {
//            period = data.getShortExtra("period", period);
//            shotTime = data.getLongExtra("shotTime", shotTime);
////            setPeriod();
////            setShotTimeText(shotTime);
//        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }

        if (preferences.playByPlay != 0 && game != null && game.cancelLastAction()) {
            return;
        }

        if (doubleBackPressedFirst) {
            super.onBackPressed();
            return;
        }
        doubleBackPressedFirst = true;
        Toast.makeText(this, getResources().getString(R.string.toast_confirm_exit), Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressedFirst = false;
            }
        }, 3000);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
        switch (i) {
            case 0:
                game.newGameSave();
                break;
            case 1:
                runResultsActivity();
                break;
            case 2:
                runSettingsActivity();
                break;
            case 3:
                shareResult();
                break;
            case 4:
                runHelpActivity();
                break;
            default:
                break;
        }
    }

//    private void mainTimeClick() {
//        if (!mainTimerOn) {
//            if (preferences.useDirectTimer) {
//                startDirectTimer();
//            } else {
//                startMainCountDownTimer();
//            }
//        } else {
////            pauseGame();
//        }
//    }

//    private void shotTimeClick() {
//        shotTickInterval = SECOND;
//        if (mainTimerOn) {
//            shotTimer.cancel();
//            startShotCountDownTimer(preferences.shotTimePref);
//        } else {
//            if (shotTime == preferences.shotTimePref) {
//                shotTime = preferences.shortShotTimePref;
//            } else {
//                shotTime = preferences.shotTimePref;
//            }
//            setShotTimeText(shotTime);
//        }
//    }

//    private void shotTimeSwitchClick() {
//        shotTickInterval = SECOND;
//        if (shotTimer != null && preferences.enableShotTime && shotTimerOn) {
//            shotTimer.cancel();
//        }
//        shotTime = preferences.shortShotTimePref;
//        if (mainTimerOn) {
//            startShotCountDownTimer(preferences.shortShotTimePref);
//        } else {
//            setShotTimeText(shotTime);
//        }
//        if (preferences.shortShotTimePref < mainTime) {
//            shotTimeView.setVisibility(View.VISIBLE);
//        }
//    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void saveCurrentState() {
//        statePref = getPreferences(MODE_PRIVATE);
//        SharedPreferences.Editor editor = statePref.edit();
//        editor.putString(STATE_HOME_NAME, hName);
//        editor.putString(STATE_GUEST_NAME, gName);
//        editor.putLong(STATE_SHOT_TIME, shotTime);
//        editor.putLong(STATE_MAIN_TIME, mainTime);
//        editor.putInt(STATE_PERIOD, period);
//        editor.putInt(STATE_HOME_SCORE, hScore);
//        editor.putInt(STATE_GUEST_SCORE, gScore);
//        editor.putInt(STATE_HOME_FOULS, hFouls);
//        editor.putInt(STATE_GUEST_FOULS, gFouls);
//        if (preferences.timeoutRules == Game.TO_RULES.FIBA) {
//            editor.putInt(STATE_HOME_TIMEOUTS, hTimeouts);
//            editor.putInt(STATE_GUEST_TIMEOUTS, gTimeouts);
//        } else if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//            editor.putInt(STATE_HOME_TIMEOUTS_NBA, hTimeouts);
//            editor.putInt(STATE_GUEST_TIMEOUTS_NBA, gTimeouts);
//            editor.putInt(STATE_HOME_TIMEOUTS20, hTimeouts20);
//            editor.putInt(STATE_GUEST_TIMEOUTS20, gTimeouts20);
//        }
//        if (preferences.arrowsOn) {
//            editor.putInt(STATE_POSSESSION, possession);
//        }
//        editor.apply();
//        if (preferences.spOn) {
//            if (leftPanel != null) {
//                leftPanel.saveCurrentData();
//            }
//            if (rightPanel != null) {
//                rightPanel.saveCurrentData();
//            }
//        }
    }

    private void getSavedState() {
//        statePref = getPreferences(MODE_PRIVATE);
//        shotTime = statePref.getLong(STATE_SHOT_TIME, 24 * SECOND);
//        mainTime = totalTime = statePref.getLong(STATE_MAIN_TIME, 600 * SECOND);
//        period = (short) statePref.getInt(STATE_PERIOD, 1);
//        hScore = (short) statePref.getInt(STATE_HOME_SCORE, 0);
//        gScore = (short) statePref.getInt(STATE_GUEST_SCORE, 0);
//        hName = statePref.getString(STATE_HOME_NAME, getResources().getString(R.string.home_team_name_default));
//        gName = statePref.getString(STATE_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
//        hFouls = (short) statePref.getInt(STATE_HOME_FOULS, 0);
//        gFouls = (short) statePref.getInt(STATE_GUEST_FOULS, 0);
//        if (preferences.timeoutRules == Game.TO_RULES.FIBA) {
//            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS, 0);
//            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS, 0);
//        } else if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS_NBA, 0);
//            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS_NBA, 0);
//            hTimeouts20 = (short) statePref.getInt(STATE_HOME_TIMEOUTS20, 0);
//            gTimeouts20 = (short) statePref.getInt(STATE_GUEST_TIMEOUTS20, 0);
//        }
//        if (preferences.arrowsOn) {
//            toggleArrow(statePref.getInt(STATE_POSSESSION, possession));
//        }
    }

    private void setSavedState() {
        System.out.println("setSavedState(); = ");
//        setMainTimeText(mainTime);
//        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
//        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
//        setTeamNames();
//
//        if (preferences.layoutType == GAME_TYPE.COMMON) {
//            if (preferences.enableShotTime) {
//                setShotTimeText(shotTime);
//            }
//            hFoulsView.setText(Short.toString(hFouls));
//            gFoulsView.setText(Short.toString(gFouls));
//            long mainTimeTemp = mainTime;
//            setPeriod();
//            mainTime = mainTimeTemp;
//            setTimeouts();
//            hTimeoutsView.setText(Short.toString(hTimeouts));
//            gTimeoutsView.setText(Short.toString(gTimeouts));
//            if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//                hTimeouts20View.setText(Short.toString(hTimeouts20));
//                gTimeouts20View.setText(Short.toString(gTimeouts20));
//            }
//        }
//        if (preferences.arrowsOn) { toggleArrow(possession); }
    }

    private void getSettings() {
        preferences.read();
    }

//    private void setColors() {
//        if (hScoreView != null) {
//            hScoreView.setTextColor(preferences.getColor(Preferences.Elements.HSCORE));
//        }
//        if (gScoreView != null) {
//            hScoreView.setTextColor(preferences.getColor(Preferences.Elements.GSCORE));
//        }
//    }

//    private void zeroState() {
//        mainTimerOn = false;
//        mainTimeFormat = TIME_FORMAT;
//        mainTickInterval = SECOND;
//        if (preferences.useDirectTimer) {
//            mainTime = 0;
//        } else {
//            mainTime = preferences.mainTimePref;
//        }
//        changedUnder2Minutes = false;
//        setMainTimeText(mainTime);
//        hScore = gScore = 0;
//        leftActionType = rightActionType = ACTION_NONE;
//        leftActionValue = rightActionValue = 0;
//        nullScore(LEFT);
//        nullScore(RIGHT);
//        setTeamNames();
//        if (preferences.layoutType == GAME_TYPE.COMMON) {
//            if (preferences.enableShotTime) {
//                shotTimerOn = false;
//                shotTime = preferences.shotTimePref;
//                shotTickInterval = SECOND;
//                setShotTimeText(shotTime);
//                shotTimeView.setVisibility(View.VISIBLE);
//                shotTimeSwitchView.setVisibility(View.VISIBLE);
//            }
//            nullTimeouts(2);
//            nullFouls();
//            period = 1;
//            periodView.setText("1");
//            if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//                nullTimeouts20(2);
//            }
//        }
//        if (preferences.spOn) {
//            try {
//                leftPanel.clear(preferences.spClearDelete);
//                rightPanel.clear(preferences.spClearDelete);
//            } catch (NullPointerException e) {
//                Log.d(TAG, "Left or right panel is null");
//            }
//        }
//        if (preferences.arrowsOn) { clearPossession(); }
//        if (preferences.fixLandscapeChanged) {
//            handleOrientation();
//            preferences.fixLandscapeChanged = false;
//        }
//    }

    private void switchSides() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_in);
        Fragment o = fm.findFragmentByTag(OverlayFragment.TAG_SWITCH);
        if (o != null) {
            if (!o.isVisible()) {
                ft.show(o);
            }
        } else {
            ft.add(R.id.overlay, overlaySwitch, OverlayFragment.TAG_SWITCH);
        }
        ft.addToBackStack(null).commit();

//        TextView _NameView = hNameView;
//        hNameView = gNameView;
//        gNameView = _NameView;
//        setTeamNames(hName, gName);

//        TextView _ScoreView = hScoreView;
//        hScoreView = gScoreView;
//        gScoreView = _ScoreView;
//        setScoresText(hScore, gScore);

//        if (preferences.layoutType == GAME_TYPE.COMMON) {
//            TextView _FoulsView = hFoulsView;
//            hFoulsView = gFoulsView;
//            gFoulsView = _FoulsView;
//            setFoulsText(hFouls, gFouls, gFoulsView.getCurrentTextColor(), hFoulsView.getCurrentTextColor());
//
//            TextView _TimeoutsView = hTimeoutsView;
//            hTimeoutsView = gTimeoutsView;
//            gTimeoutsView = _TimeoutsView;
//            setTimeoutsText(hTimeouts, gTimeouts, gTimeoutsView.getCurrentTextColor(), hTimeoutsView.getCurrentTextColor());
//
//            if (preferences.timeoutRules == Game.TO_RULES.NBA) {
//                TextView _Timeouts20View = hTimeouts20View;
//                hTimeouts20View = gTimeouts20View;
//                gTimeouts20View = _Timeouts20View;
//                setTimeouts20Text(hTimeouts20, gTimeouts20, gTimeouts20View.getCurrentTextColor(), hTimeouts20View.getCurrentTextColor());
//            }
//        }
//        setColors();

        if (preferences.spOn && leftPanel != null) {
            try {
                switchSidePanels();
            } catch (NullPointerException e) {
                Log.d(TAG, "Left or right panel is null");
            }
        }

//        if (preferences.arrowsOn) {
//            switchPossession();
//        }

        fm.beginTransaction()
                .setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out)
                .hide(overlaySwitch)
                .commit();
    }

    private void switchSidePanels() {
        leftPanel.changeRowsSide();
        rightPanel.changeRowsSide();
        leftPanel.clearTable();
        rightPanel.clearTable();
        TreeMap<Integer, SidePanelRow> leftRows = leftPanel.getAllPlayers();
        TreeSet<SidePanelRow> leftActivePlayers = leftPanel.getActivePlayers();
        SidePanelRow leftCaptainPlayer = leftPanel.getCaptainPlayer();
        leftPanel.replaceRows(rightPanel.getAllPlayers(), rightPanel.getActivePlayers(), rightPanel.getCaptainPlayer());
        rightPanel.replaceRows(leftRows, leftActivePlayers, leftCaptainPlayer);
    }

//    private void switchPossession() {
//        if (leftArrow != null && rightArrow != null) {
//            if (possession == NO_TEAM) { return; }
//            possession = 1 - possession;
//                if (possession == HOME) {
//                    leftArrow.setFill();
//                    rightArrow.setStroke();
//                } else if (possession == GUEST) {
//                    rightArrow.setFill();
//                    leftArrow.setStroke();
//            }
//        }
//    }

    private void setPossession(int team) {
//        if (leftArrow != null && rightArrow != null) {
//            switch (team) {
//                case HOME:
//                    leftArrow.setFill();
//                    rightArrow.setStroke();
//                    break;
//                case GUEST:
//                    rightArrow.setFill();
//                    leftArrow.setStroke();
//                    break;
//                case NO_TEAM:
//                    leftArrow.setStroke();
//                    rightArrow.setStroke();
//                    break;
//            }
//        }
//        possession = team;
    }

    private void clearPossession() {
//        if (leftArrow != null && rightArrow != null) {
//            leftArrow.setStroke();
//            rightArrow.setStroke();
//        }
//        possession = NO_TEAM;
    }

//    private SidePanelRow getPlayer(int team, int number) {
//        SidePanelFragment panel = leftIsHome ^ team == HOME ? rightPanel : leftPanel;
//        return panel.getPlayer(number);
//    }

    private void playWhistle() {
        playWhistle(2);
    }

    private void playWhistle(int repeats) {
        if (preferences.pauseOnSound) {
//            pauseGame();
        }
        soundWhistleStreamId = soundPool.play(soundWhistleId, 1, 1, 0, repeats, 1);
        whistleRepeats = repeats;
        if (repeats != -1) {
            new Handler().postDelayed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (whistlePressed) {
                                stopWhistle();
                                playWhistle(-1);
                            }
                        }
                    },
                    whistleLength * repeats);
        }
    }

    private void playHorn() {
        playHorn(preferences.hornUserRepeats);
    }

    private void playHorn(int repeats) {
        if (preferences.pauseOnSound) {
//            pauseGame();
        }
        soundHornStreamId = soundPool.play(soundHornId, 1, 1, 0, repeats, 1);
        hornRepeats = repeats;
        if (repeats != -1) {
            new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (hornPressed) {
                            stopHorn();
                            playHorn(-1);
                        }
                    }
                },
                    hornLength * repeats);
        }
    }

    private void stopWhistle() {
        if (soundWhistleStreamId > 0 && whistleRepeats == -1) {
            soundPool.stop(soundWhistleStreamId);
        }
    }

    private void stopHorn() {
        if (soundHornStreamId > 0 && hornRepeats == -1) {
            soundPool.stop(soundHornStreamId);
        }
    }

    private void shareResult() {
        String mime_type = "text/plain";
        Intent sendIntent = new Intent();
//        saveResult();
//        if (mainTime > 0) {
//            gameResult.setComplete(false);
//        }
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, game.getShareString())
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void showConfirmDialog(String type, boolean won) {
//        ConfirmDialog dialog;
//        if (won) {
//            if (hScore > gScore) {
//                dialog = ConfirmDialog.newInstance(type, hName, hScore, gScore);
//            } else {
//                dialog = ConfirmDialog.newInstance(type, gName, gScore, hScore);
//            }
//        } else {
//            dialog = ConfirmDialog.newInstance(type);
//        }
//        dialog.show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
    }

    private void showListDialog(String type) {
        Fragment frag = getFragmentManager().findFragmentByTag(ListDialog.TAG);
        if (frag != null && frag.isAdded()) {
            return;
        }
        ListDialog.newInstance(type).show(getFragmentManager(), ListDialog.TAG);
    }

    private void showListDialog(boolean left) {
        Fragment frag = getFragmentManager().findFragmentByTag(ListDialog.TAG);
        if (frag != null && frag.isAdded()) {
            return;
        }

        ArrayList<String> numberNameList = new ArrayList<>();
        inactivePlayers = (left ? leftPanel : rightPanel).getInactivePlayers();
        if (inactivePlayers.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.side_panel_no_data), Toast.LENGTH_LONG).show();
            return;
        }
        for (Map.Entry<Integer, SidePanelRow> entry : inactivePlayers.entrySet()) {
            numberNameList.add(String.format("%d: %s", entry.getValue().getNumber(), entry.getValue().getName()));
        }
//        int number = longClickPlayerBu.getTag() != null ? ((SidePanelRow)longClickPlayerBu.getTag()).getNumber() : -1;

//        ListDialog.newInstance("substitute", numberNameList, left, number).show(getFragmentManager(), ListDialog.TAG);
    }

    private void chooseTeamNameDialog(int team, String name) {
        DialogFragment nameEdit = NameEditDialog.newInstance(team, name);
        nameEdit.show(getFragmentManager(), TAG_FRAGMENT_NAME_EDIT);
    }

    private void showMainTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance(game.mainTime, true);
        mainTimePicker.show(getFragmentManager(), TAG_FRAGMENT_MAIN_TIME_PICKER);
    }

    private void showShotTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance(game.shotTime, false);
        mainTimePicker.show(getFragmentManager(), TAG_FRAGMENT_SHOT_TIME_PICKER);
    }

    private void showTimeout(long durSeconds, String team) {
        Fragment frag = getFragmentManager().findFragmentByTag(TAG_FRAGMENT_TIME);
        if (frag != null && frag.isAdded()) {
            return;
        }
        floatingDialog.show(getFragmentManager(), TAG_FRAGMENT_TIME);
        floatingDialog.duration = durSeconds;
        floatingDialog.duration = durSeconds * 1000;
        if (durSeconds > 100) {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_1), durSeconds / 60);
        } else {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_2), team, durSeconds).replace(" ()", "").trim();
        }
        floatingDialog.startCountDownTimer();
    }

    private void showSidePanels(int type) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_in);
        Fragment o = fm.findFragmentByTag(OverlayFragment.TAG_PANELS);
        if (o != null) {
            if (!o.isVisible()) {
                ft.show(o);
            }
        } else {
            ft.add(R.id.overlay, overlayPanels, OverlayFragment.TAG_PANELS);
        }

        boolean connected = preferences.spConnected && getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;

        if (type == SIDE_PANELS_LEFT || connected) {
            ft.setCustomAnimations(R.animator.slide_left_side_show, R.animator.slide_left_side_show);
            Fragment lpanel = fm.findFragmentByTag(SidePanelFragment.TAG_LEFT_PANEL);
            if (lpanel != null) {
                ft.show(lpanel);
            } else {
                ft.add(R.id.left_panel_full, leftPanel, SidePanelFragment.TAG_LEFT_PANEL);
            }
        }

        if (type == SIDE_PANELS_RIGHT || connected) {
            ft.setCustomAnimations(R.animator.slide_right_side_show, R.animator.slide_right_side_show);
            Fragment rpanel = fm.findFragmentByTag(SidePanelFragment.TAG_RIGHT_PANEL);
            if (rpanel != null) {
                ft.show(rpanel);
            } else {
                ft.add(R.id.right_panel_full, rightPanel, SidePanelFragment.TAG_RIGHT_PANEL);
            }
        }
        ft.addToBackStack(null).commit();
    }

    private void endOfGameActions(int dontAskNewGame) {
        switch (dontAskNewGame) {
            case 1:
                break;
            case 2:
                startNewGame(true);
                break;
            case 3:
                startNewGame(false);
                break;
        }
    }

    @Override
    public void onTimeChanged(long value, boolean main) {
        if (main) {
            game.changeMainTime(value);
        } else {
            game.changeShotTime(value);
        }
    }

    @Override
    public void onTimeoutDialogItemClick(int which) {
////        pauseGame();
        int duration;
        switch (which) {
            case 0:
                duration = 120;
                break;
            case 1:
                duration = 900;
                break;
            case 2:
                duration = 20;
                break;
            case 3:
                duration = 60;
                break;
            case 4:
                duration = 100;
                break;
            default:
                duration = 60;
                break;
        }
        showTimeout(duration, "");
    }

    @Override
    public void onNewPeriodDialogItemClick(int which) {
        game.newPeriod(which);
    }

    @Override
    public void onClearPanelDialogItemClick(int which, boolean left) {
        (left ? leftPanel : rightPanel).clear(which == 0);
    }

    @Override
    public void onSubstituteListSelect(boolean left, int newNumber) {
        SidePanelRow row = inactivePlayers.get(newNumber);
//        (left ? leftPanel : rightPanel).substitute(row, (SidePanelRow) longClickPlayerBu.getTag());
//        longClickPlayerBu.setTag(row);
//        longClickPlayerBu.setText(Integer.toString(newNumber));
    }

    @Override
    public void onNameChanged(String value, int team) {
        if (value.length() > 0) {
            game.setTeamName(value, team);
        }
    }

    @Override
    public void onConfirmDialogPositive(String type, boolean dontShow) {
//        dontAskNewGame = dontShow ? 2 : 0;
        game = Game.newGame(this);
    }

    @Override
    public void onConfirmDialogPositive(String type) {
        switch (type) {
            case "new_game":
                startNewGame(false);
                break;
            case "save_result":
                startNewGame(true);
                break;
            case "edit_player_captain":
                EditPlayerDialog f = (EditPlayerDialog) getFragmentManager().findFragmentByTag(EditPlayerDialog.TAG);
                if (f != null) {f.changeCaptainConfirmed();}
                break;
        }
    }

    @Override
    public void onConfirmDialogNeutral(boolean dontShow) {
//        dontAskNewGame = dontShow ? 2 : 0;
        game.saveAndNew();
    }

    @Override
    public void onConfirmDialogNegative(String type, boolean dontShow) {
//        dontAskNewGame = dontShow ? 1 : 0;
    }

    @Override
    public void onConfirmDialogNegative(String type) {
//        if (type.equals("save_result")) {
//            newGame();
//        }
    }

    @Override
    public void onSidePanelClose(boolean left) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (left) {
            ft.setCustomAnimations(R.animator.slide_left_side_hide, R.animator.slide_left_side_hide).hide(leftPanel);
        } else {
            ft.setCustomAnimations(R.animator.slide_right_side_hide, R.animator.slide_right_side_hide).hide(rightPanel);
        }
        if (!(leftPanel.isVisible() && rightPanel.isVisible())) {
            ft.setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out);
            ft.hide(overlayPanels);
        }
        ft.commit();
    }

    @Override
    public void onSidePanelActiveSelected(TreeSet<SidePanelRow> rows, boolean left) {
        ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
        int pos = 0;
        for (SidePanelRow row : rows) {
            View bu = group.get(pos++);
            ((Button) bu).setText(Integer.toString(row.getNumber()));
            bu.setTag(row);
        }
    }

    @Override
    public void onSidePanelNoActive(boolean left) {
        ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
        for (View bu : group) {
            ((Button) bu).setText(R.string.minus);
            bu.setTag(null);
        }
    }

    @Override
    public void onOverlayClick() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        int toClose = 0;
        if (leftPanel.isVisible()) {
            toClose++;
            if (leftPanel.selectionConfirmed()) {
                ft.setCustomAnimations(R.animator.slide_left_side_hide, R.animator.slide_left_side_hide);
                ft.hide(leftPanel);
                toClose--;
            } else {
                Toast.makeText(this, getResources().getString(R.string.side_panel_confirm), Toast.LENGTH_LONG).show();
            }
        }
        if (rightPanel.isVisible()) {
            toClose++;
            if (rightPanel.selectionConfirmed()) {
                ft.setCustomAnimations(R.animator.slide_right_side_hide, R.animator.slide_right_side_hide);
                ft.hide(rightPanel);
                toClose--;
            } else {
                Toast.makeText(this, getResources().getString(R.string.side_panel_confirm), Toast.LENGTH_LONG).show();
            }
        }
        if (overlayPanels.isVisible() && toClose == 0) {
            ft.setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out);
            ft.hide(overlayPanels);
        }
        ft.commit();
    }

    @Override
    public void onOverlayOpenPanel(int type) {
        showSidePanels(type);        
    }

    @Override
    public void onEditPlayerAdd(boolean left, int number, String name, boolean captain) {
        (left ? leftPanel : rightPanel).addRow(number, name, captain);
    }

    @Override
    public void onEditPlayerEdit(boolean left, int id, int number, String name, boolean captain) {
        if ((left ? leftPanel : rightPanel).editRow(id, number, name, captain)) {
            ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
            for (View bu : group) {
                SidePanelRow row = (SidePanelRow) bu.getTag();
                if (row != null && row.getId() == id) {
                    ((Button) bu).setText(Integer.toString(number));
                    break;
                }
            }
        }
    }

    @Override
    public void onEditPlayerDelete(boolean left, int id) {
        if ((left ? leftPanel : rightPanel).deleteRow(id)) {
            ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
            for (View bu : group) {
                SidePanelRow row = (SidePanelRow) bu.getTag();
                if (row != null && row.getId() == id) {
                    ((Button) bu).setText(getResources().getString(R.string.minus));
                    bu.setTag(null);
                    break;
                }
            }
        }
    }

    @Override
    public int onEditPlayerCheck(boolean left, int number, boolean captain) {
        return (left ? leftPanel : rightPanel).checkNewPlayer(number, captain);
    }

    @Override
    public void onMainTimeClick() {

    }

    @Override
    public void onShotTimeClick() {

    }

    @Override
    public void onShotTimeSwitchClick() {

    }

    @Override
    public void onPeriodClick() {
        game.newPeriod(true);
    }

    @Override
    public void onChangeScoreClick(boolean left, int value) {
        game.changeScore(left, value);
    }

    @Override
    public void onTeamClick(boolean left) {
        game.setPossession(left);
    }

    @Override
    public void onFoulsClick(boolean left) {
        game.foul(left);
    }

    @Override
    public void onPanelToggleClick(boolean left) {

    }

    @Override
    public void onTimeoutsClick(boolean left) {
        game.timeout(left);
    }

    @Override
    public void onTimeouts20Click(boolean left) {
        game.timeout20(left);
    }

    @Override
    public void onIconClick(StandardLayout.ICONS icon) {

    }

    @Override
    public boolean onArrowLongClick() {
        game.clearPossession();
        return false;
    }

    @Override
    public boolean onFoulsLongClick(boolean left) {
        game.nullFouls(left);
        return false;
    }

    @Override
    public boolean onMainTimeLongClick() {
        return false;
    }

    @Override
    public boolean onNameLongClick(boolean left) {
        chooseTeamNameDialog(game.getTeam(left), game.getName(left));
        return false;
    }

    @Override
    public boolean onPeriodLongClick() {
        game.newPeriod(false);
        return false;
    }

    @Override
    public boolean onScoreLongClick(boolean left) {
        game.nullScore(left);
        return false;
    }

    @Override
    public boolean onShotTimeLongClick() {
        return false;
    }

    @Override
    public boolean onTimeoutsLongClick(boolean left) {
        game.nullTimeouts(left);
        return false;
    }

    @Override
    public boolean onTimeouts20LongClick(boolean left) {
        game.nullTimeouts20(left);
        return false;
    }
}