package com.smiler.basketball_scoreboard;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.camera.CameraActivity;
import com.smiler.basketball_scoreboard.elements.ConfirmDialog;
import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.elements.ListDialog;
import com.smiler.basketball_scoreboard.elements.NameEditDialog;
import com.smiler.basketball_scoreboard.elements.TimePickerFragment;
import com.smiler.basketball_scoreboard.elements.TriangleView;
import com.smiler.basketball_scoreboard.help.HelpActivity;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;
import com.smiler.basketball_scoreboard.models.ActionRecord;
import com.smiler.basketball_scoreboard.results.Result;
import com.smiler.basketball_scoreboard.results.ResultsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static com.smiler.basketball_scoreboard.Constants.ACTION_FLS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_NONE;
import static com.smiler.basketball_scoreboard.Constants.ACTION_PTS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO20;
import static com.smiler.basketball_scoreboard.Constants.API16_TIME_REGEX;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_FIBA_MAIN_TIME;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_FIBA_PLAYER_FOULS;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_HORN_LENGTH;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_MAX_FOULS;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_NUM_REGULAR;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_OVERTIME;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_SHORT_SHOT_TIME;
import static com.smiler.basketball_scoreboard.Constants.DEFAULT_SHOT_TIME;
import static com.smiler.basketball_scoreboard.Constants.FORMAT_TWO_DIGITS;
import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.LAYOUT_FULL;
import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.MINUTES_2;
import static com.smiler.basketball_scoreboard.Constants.NO_TEAM;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_PANELS;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;
import static com.smiler.basketball_scoreboard.Constants.SECOND;
import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;
import static com.smiler.basketball_scoreboard.Constants.SIDE_PANELS_LEFT;
import static com.smiler.basketball_scoreboard.Constants.SIDE_PANELS_RIGHT;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_FOULS;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_NAME;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_SCORE;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS20;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS_NBA;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_FOULS;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_NAME;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_SCORE;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS20;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS_NBA;
import static com.smiler.basketball_scoreboard.Constants.STATE_MAIN_TIME;
import static com.smiler.basketball_scoreboard.Constants.STATE_PERIOD;
import static com.smiler.basketball_scoreboard.Constants.STATE_POSSESSION;
import static com.smiler.basketball_scoreboard.Constants.STATE_SHOT_TIME;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_APP_UPDATES;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_CONFIRM;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_MAIN_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_NAME_EDIT;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_SHOT_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_TIME;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_MILLIS;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_SHORT;
import static com.smiler.basketball_scoreboard.Constants.TO_RULES_FIBA;
import static com.smiler.basketball_scoreboard.Constants.TO_RULES_NBA;
import static com.smiler.basketball_scoreboard.Constants.TO_RULES_NONE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener,
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
    private SharedPreferences statePref, sharedPref;
    private TextView mainTimeView, shotTimeView, shotTimeSwitchView, periodView;
    private TextView hNameView, gNameView;
    private TextView hScoreView, gScoreView;
    private TextView hTimeoutsView, gTimeoutsView;
    private TextView hTimeouts20View, gTimeouts20View;
    private TextView hFoulsView, gFoulsView;
    private ViewGroup leftPlayersButtonsGroup, rightPlayersButtonsGroup;
    private TriangleView leftArrow, rightArrow;
    private Button longClickPlayerBu;
    private Drawer.Result drawer;

    private int layoutType, autoSaveResults, autoSound, actualTime, timeoutRules;
    private int playByPlay;
    private boolean fixLandscape, fixLandscapeChanged;
    private boolean doubleBackPressedFirst, layoutChanged, timeoutsRulesChanged;
    private boolean autoShowTimeout, autoShowBreak, autoSwitchSides;
    private boolean saveOnExit, pauseOnSound, vibrationOn;
    private boolean mainTimerOn, shotTimerOn;
    private boolean enableShotTime, enableShotTimeChanged, restartShotTimer;
    private boolean useDirectTimer, directTimerStopped;
    private boolean fractionSecondsMain, fractionSecondsShot;
    private boolean spOn, spStateChanged, spConnected, spClearDelete;
    private boolean arrowsOn, arrowsStateChanged;
    private int possession = NO_TEAM;
    private long mainTime, shotTime;
    private long mainTimePref, shotTimePref, shortShotTimePref, overTimePref;
    private long startTime, totalTime;
    private long timeoutFullDuration;
    private short hScore, gScore;
    private short hScore_prev, gScore_prev;
    private short hFouls, gFouls;
    private short hTimeouts, gTimeouts;
    private short hTimeouts20, gTimeouts20;
    private short takenTimeoutsFull;
    private short maxTimeouts, maxTimeouts20, maxTimeouts100;
    private short maxFouls;
    private short period, numRegularPeriods;
    private String hName, gName;
    private Handler customHandler = new Handler();
    private CountDownTimer mainTimer, shotTimer;
    private short leftActionType = ACTION_NONE, rightActionType = ACTION_NONE;
    private int leftActionValue = 0, rightActionValue = 0;
    private float periodViewSize, scoreViewSize;
    private boolean leftIsHome = true;

    private int dontAskNewGame;
    private boolean showTimeoutDialog = true;
    private FloatingCountdownTimerDialog floatingDialog;
    private SidePanelFragment leftPanel, rightPanel;
    private OverlayFragment overlayPanels, overlaySwitch;
    private ArrayList<View> leftPlayersButtons = new ArrayList<>();
    private ArrayList<View> rightPlayersButtons = new ArrayList<>();

    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;
    private long mainTickInterval = SECOND;
    private long shotTickInterval = SECOND;
    private boolean changedUnder2Minutes = false;
    private boolean scoreSaved = false;
//    private String actionString;
    private ActionRecord lastAction;
    private int timesTie = 1, timesLeadChanged = 0;
    private int hMaxLead = 0, gMaxLead = 0;

    private Animation shotTimeBlinkAnimation = new AlphaAnimation(1, 0);
    private int soundWhistleId, soundHornId, soundWhistleStreamId, soundHornStreamId;
    private int whistleRepeats, hornRepeats, whistleLength, hornLength, hornUserRepeats;
    private boolean whistlePressed, hornPressed;
    private Result gameResult;
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
        if (saveOnExit) {
            saveCurrentState();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handleScoreViewSize();
        // System.out.println("res_type = " + getResources().getString(R.string.res_type));

        if (PrefActivity.prefChangedRestart) {
            showConfirmDialog("new_game", false);
        } else if (PrefActivity.prefChangedNoRestart) {
            getSettingsNoRestart();
            if (fixLandscapeChanged) {
                handleOrientation();
                fixLandscapeChanged = false;
            }

            if (spStateChanged) {
                if (!spOn) {
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
            if (enableShotTimeChanged && layoutType == LAYOUT_FULL) {
                try {
                    if (!enableShotTime) {
                        shotTimeView.setVisibility(View.GONE);
                        shotTimeSwitchView.setVisibility(View.GONE);
                    } else {
                        shotTimeView.setVisibility(View.VISIBLE);
                        if (shortShotTimePref != shotTimePref) {
                            shotTimeSwitchView.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (NullPointerException e) {
                    Log.d(TAG, e.getMessage() + ((shotTimeView != null) ? shotTimeView.toString() : "shotTimeView == null"));
                }
            }
            if (arrowsStateChanged) {
                handleArrowsVisibility();
            }
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
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getSettings();
        if (sharedPref.getInt("app_version", 1) < BuildConfig.VERSION_CODE) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("app_version", BuildConfig.VERSION_CODE);
            editor.apply();
            new AppUpdatesFragment().show(getFragmentManager(), TAG_FRAGMENT_APP_UPDATES);
        }

        handleOrientation();
        initLayout();

        if (sharedPref.getBoolean("first_launch", true)) {
            drawer.openDrawer();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first_launch", false);
            editor.apply();
        }

        shotTimeBlinkAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        gameResult = new Result(hName, gName);
        floatingDialog = new FloatingCountdownTimerDialog();
        floatingDialog.setCancelable(false);

        if (saveOnExit) {
            getSavedState();
            setSavedState();
        } else {
            newGame();
        }
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

            if (spOn) {
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
        if (fixLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private void initLayout() {
        leftIsHome = true;
        overlaySwitch = OverlayFragment.newInstance(OVERLAY_SWITCH);
        overlaySwitch.setRetainInstance(true);
        if (layoutType == LAYOUT_FULL) {
            initExtensiveLayout();
        } else {
            initSimpleLayout();
        }
        initCommonLayout();
        if (spOn) {
            initSidePanels();
            leftPlayersButtonsGroup.setVisibility(View.VISIBLE);
            rightPlayersButtonsGroup.setVisibility(View.VISIBLE);
        }
        initArrows();
    }

    private void initCommonLayout() {
        mainTimeView = (TextView) findViewById(R.id.mainTimeView);
        hScoreView = (TextView) findViewById(R.id.leftScoreView);
        gScoreView = (TextView) findViewById(R.id.rightScoreView);
        hNameView = (TextView) findViewById(R.id.leftNameView);
        gNameView = (TextView) findViewById(R.id.rightNameView);

        mainTimeView.setOnClickListener(this);
        mainTimeView.setOnLongClickListener(this);
        hScoreView.setOnClickListener(this);
        hScoreView.setOnLongClickListener(this);
        gScoreView.setOnClickListener(this);
        gScoreView.setOnLongClickListener(this);
        hNameView.setOnClickListener(this);
        hNameView.setOnLongClickListener(this);
        gNameView.setOnClickListener(this);
        gNameView.setOnLongClickListener(this);

        (findViewById(R.id.leftMinus1View)).setOnClickListener(this);
        (findViewById(R.id.rightMinus1View)).setOnClickListener(this);
        (findViewById(R.id.leftPlus1View)).setOnClickListener(this);
        (findViewById(R.id.rightPlus1View)).setOnClickListener(this);
        (findViewById(R.id.leftPlus3View)).setOnClickListener(this);
        (findViewById(R.id.rightPlus3View)).setOnClickListener(this);

        View whislteView = findViewById(R.id.whistleView);
        View hornView = findViewById(R.id.hornView);
        hornView.setOnClickListener(this);
        (findViewById(R.id.timeoutIconView)).setOnClickListener(this);
        (findViewById(R.id.cameraView)).setOnClickListener(this);
        (findViewById(R.id.switchSidesView)).setOnClickListener(this);

        hornView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopHorn();
                        hornPressed = false;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        playHorn();
                        hornPressed = true;
                        break;
                }
                return true;
            }
        });

        whislteView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        stopWhistle();
                        whistlePressed = false;
                        break;
                    case MotionEvent.ACTION_DOWN:
                        playWhistle();
                        whistlePressed = true;
                        break;
                }
                return true;
            }
        });

        layoutChanged = timeoutsRulesChanged = false;

        initDrawer();
    }

    private void initExtensiveLayout() {
        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource((timeoutRules == TO_RULES_NBA) ? R.layout.full_bottom_nba : R.layout.full_bottom_simple);
        stub.inflate();

        periodView = (TextView) findViewById(R.id.periodView);
        hFoulsView = (TextView) findViewById(R.id.leftFoulsView);
        gFoulsView = (TextView) findViewById(R.id.rightFoulsView);
        shotTimeView = (TextView) findViewById(R.id.shotTimeView);
        shotTimeSwitchView = (TextView) findViewById(R.id.shotTimeSwitch);

        periodView.setOnClickListener(this);
        hFoulsView.setOnClickListener(this);
        gFoulsView.setOnClickListener(this);

        periodView.setOnLongClickListener(this);
        hFoulsView.setOnLongClickListener(this);
        gFoulsView.setOnLongClickListener(this);

        if (enableShotTime) {
            shotTimeView.setOnClickListener(this);
            shotTimeView.setOnLongClickListener(this);
            shotTimeSwitchView.setOnClickListener(this);
            shotTimeSwitchView.setText(Long.toString(shortShotTimePref / 1000));
        } else {
            try {
                shotTimeView.setVisibility(View.INVISIBLE);
                shotTimeSwitchView.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                Log.e(TAG, "shotTimeView or shotTimeSwitchView is null");
            }
        }
        initBottomLineTimeouts();
    }

    private void initSidePanels() {
        ViewStub leftPlayersStub = (ViewStub) findViewById(R.id.left_panel_stub);
        ViewStub rightPlayersStub = (ViewStub) findViewById(R.id.right_panel_stub);
        leftPlayersStub.setLayoutResource(R.layout.side_panel_left_buttons);
        leftPlayersStub.inflate();
        rightPlayersStub.setLayoutResource(R.layout.side_panel_right_buttons);
        rightPlayersStub.inflate();

        leftPlayersButtonsGroup = (ViewGroup) findViewById(R.id.left_panel);
        leftPlayersButtons = getAllButtons(leftPlayersButtonsGroup);
        for (View bu : leftPlayersButtons) {
            attachLeftButton(bu);
        }

        rightPlayersButtonsGroup = (ViewGroup) findViewById(R.id.right_panel);
        rightPlayersButtons = getAllButtons(rightPlayersButtonsGroup);
        for (View bu : rightPlayersButtons) {
            attachRightButton(bu);
        }

        leftPanel = SidePanelFragment.newInstance(true);
        rightPanel = SidePanelFragment.newInstance(false);
        overlayPanels = OverlayFragment.newInstance(OVERLAY_PANELS);
        leftPanel.setRetainInstance(true);
        rightPanel.setRetainInstance(true);
        overlayPanels.setRetainInstance(true);

        (findViewById(R.id.left_panel_toggle)).setOnClickListener(this);
        (findViewById(R.id.right_panel_toggle)).setOnClickListener(this);
        spStateChanged = false;

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.left_panel_full, leftPanel, SidePanelFragment.TAG_LEFT_PANEL);
        ft.add(R.id.right_panel_full, rightPanel, SidePanelFragment.TAG_RIGHT_PANEL);
        ft.hide(leftPanel).hide(rightPanel);
        ft.addToBackStack(null).commit();
    }

    private ArrayList<View> getAllButtons(ViewGroup group) {
        ArrayList<View> res = new ArrayList<>();
        View button;
        for (int i = 0; i < group.getChildCount(); i++) {
            button = group.getChildAt(i);
            if (button instanceof Button) {
                res.add(button);
            } else if (button instanceof ViewGroup) {
                res.addAll(getAllButtons((ViewGroup) button));
            }
        }
        return res;
    }

    private void attachLeftButton(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SidePanelRow row = ((SidePanelRow) v.getTag());
                if (row == null) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_select_players), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (leftActionType != ACTION_NONE) {
                    if (leftActionType == ACTION_PTS) {
                        row.changePoints(leftActionValue);
                    } else if (leftActionType == ACTION_FLS) {
                        row.changeFouls(leftActionValue);
                    }
                    leftActionType = ACTION_NONE;
                    leftActionValue = 0;
                }
                if (lastAction != null) {
                    lastAction.setNumber(row.getNumber());
                }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickPlayerBu = (Button) v;
                showListDialog(true);
                return false;
            }
        });
    }

    private void attachRightButton(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SidePanelRow row = ((SidePanelRow) v.getTag());
                if (row == null) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.toast_select_players), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rightActionType != ACTION_NONE) {
                    if (rightActionType == ACTION_PTS) {
                        row.changePoints(rightActionValue);
                    } else if (rightActionType == ACTION_FLS) {
                        row.changeFouls(rightActionValue);
                    }
                    rightActionType = ACTION_NONE;
                    rightActionValue = 0;
                }
                if (lastAction != null) {
                    lastAction.setNumber(row.getNumber());
                }
            }
        });
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClickPlayerBu = (Button) v;
                showListDialog(false);
                return false;
            }
        });
    }

    private void initBottomLineTimeouts() {
        hTimeoutsView = (TextView) findViewById(R.id.leftTimeoutsView);
        gTimeoutsView = (TextView) findViewById(R.id.rightTimeoutsView);
        hTimeoutsView.setOnClickListener(this);
        gTimeoutsView.setOnClickListener(this);
        hTimeoutsView.setOnLongClickListener(this);
        gTimeoutsView.setOnLongClickListener(this);
        if (timeoutRules == TO_RULES_NONE) {
            ((TextView) findViewById(R.id.leftTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
            ((TextView) findViewById(R.id.rightTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
        } else if (timeoutRules == TO_RULES_NBA) {
            hTimeouts20View = (TextView) findViewById(R.id.leftTimeouts20View);
            gTimeouts20View = (TextView) findViewById(R.id.rightTimeouts20View);
            hTimeouts20View.setOnClickListener(this);
            hTimeouts20View.setOnLongClickListener(this);
            gTimeouts20View.setOnClickListener(this);
            gTimeouts20View.setOnLongClickListener(this);
        }
    }

    private void initSimpleLayout() {
        setContentView(R.layout.activity_main_simple);
        ImageView startNewPeriodView = (ImageView) findViewById(R.id.newPeriodIconView);
        startNewPeriodView.setOnClickListener(this);
        enableShotTime = false;
    }

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

    private void initArrows() {
        try {
            leftArrow = (TriangleView) findViewById(R.id.leftArrowView);
            rightArrow = (TriangleView) findViewById(R.id.rightArrowView);
            leftArrow.setOnClickListener(this);
            rightArrow.setOnClickListener(this);
            leftArrow.setOnLongClickListener(this);
            rightArrow.setOnLongClickListener(this);
        } catch (NullPointerException e) {
            Log.d(TAG, "initArrows: " + e.getMessage());
        }
        handleArrowsVisibility();
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

    private void handleArrowsVisibility() {
        if (arrowsOn) {
            showArrows();
        } else {
            hideArrows();
        }
    }

    private void hideArrows() {
        leftArrow.setVisibility(View.GONE);
        rightArrow.setVisibility(View.GONE);
    }

    private void showArrows() {
        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.VISIBLE);
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
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("layoutType", layoutType);
        intent.putExtra("hName", hName);
        intent.putExtra("gName", gName);
        intent.putExtra("hScore", hScore);
        intent.putExtra("gScore", gScore);
        intent.putExtra("mainTime", mainTime);
        if (layoutType == LAYOUT_FULL) {
            intent.putExtra("shotTime", shotTime);
            intent.putExtra("period", period);
        }
        startActivityForResult(intent, 1);
    }

    private void runHelpActivity() {
        startActivity(new Intent(this, HelpActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) { return; }
        hScore = data.getShortExtra("hScore", hScore);
        gScore = data.getShortExtra("gScore", gScore);
        mainTime = data.getLongExtra("mainTime", mainTime);
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
        setMainTimeText(mainTime);

        if (layoutType == LAYOUT_FULL) {
            period = data.getShortExtra("period", period);
            shotTime = data.getLongExtra("shotTime", shotTime);
            setPeriod();
            setShotTimeText(shotTime);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
            return;
        }

        if (playByPlay != 0 && cancelLastAction()) {
            return;
        }

        if (doubleBackPressedFirst) {
            super.onBackPressed();
            return;
        }
        this.doubleBackPressedFirst = true;
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

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
        switch (i) {
            case 0:
                newGameSave();
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

    @Override
    public void onClick(View v) {
        if (vibrationOn) {
            vibrator.vibrate(100);
        }
        switch (v.getId()) {
            case R.id.leftScoreView:
                changeScore(LEFT, 2);
                break;
            case R.id.rightScoreView:
                changeScore(RIGHT, 2);
                break;
            case R.id.mainTimeView:
                mainTimeClick();
                break;
            case R.id.shotTimeView:
                shotTimeClick();
                break;
            case R.id.shotTimeSwitch:
                shotTimeSwitchClick();
                break;
            case R.id.leftPlus1View:
                changeScore(LEFT, 1);
                break;
            case R.id.rightPlus1View:
                changeScore(RIGHT, 1);
                break;
            case R.id.leftPlus3View:
                changeScore(LEFT, 3);
                break;
            case R.id.rightPlus3View:
                changeScore(RIGHT, 3);
                break;
            case R.id.leftMinus1View:
                if (hScore > 0) { changeScore(LEFT, -1); }
                break;
            case R.id.rightMinus1View:
                if (gScore > 0) { changeScore(RIGHT, -1); }
                break;
            case R.id.periodView:
                newPeriod(true);
                break;
            case R.id.timeoutIconView:
                showListDialog("timeout");
                break;
            case R.id.newPeriodIconView:
                showListDialog("new_period");
                break;
            case R.id.leftFoulsView:
                foul(LEFT);
                break;
            case R.id.rightFoulsView:
                foul(RIGHT);
                break;
            case R.id.leftTimeoutsView:
                timeout(LEFT);
                break;
            case R.id.rightTimeoutsView:
                timeout(RIGHT);
                break;
            case R.id.leftTimeouts20View:
                timeout20(LEFT);
                break;
            case R.id.rightTimeouts20View:
                timeout20(RIGHT);
                break;
            case R.id.cameraView:
                if (checkCameraHardware(this)) { runCameraActivity(); }
                break;
            case R.id.switchSidesView:
                switchSides();
                break;
            case R.id.left_panel_toggle:
                showSidePanels(SIDE_PANELS_LEFT);
                break;
            case R.id.right_panel_toggle:
                showSidePanels(SIDE_PANELS_RIGHT);
                break;
            case R.id.leftNameView:
            case R.id.leftArrowView:
                if (arrowsOn) { setPossession(HOME); }
                break;
            case R.id.rightNameView:
            case R.id.rightArrowView:
                if (arrowsOn) { setPossession(GUEST); }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (vibrationOn) { vibrator.vibrate(longClickVibrationPattern, -1); }

        if (!mainTimerOn) {
            switch (v.getId()) {
                case R.id.leftScoreView:
                    nullScore(LEFT);
                    return true;
                case R.id.rightScoreView:
                    nullScore(RIGHT);
                    return true;
                case R.id.mainTimeView:
                    showMainTimePicker();
                    return true;
                case R.id.shotTimeView:
                    showShotTimePicker();
                    return true;
                case R.id.periodView:
                    newPeriod(false);
                    return true;
                case R.id.leftFoulsView:
                    nullFouls(LEFT);
                    return true;
                case R.id.rightFoulsView:
                    nullFouls(RIGHT);
                    return true;
                case R.id.leftTimeoutsView:
                    nullTimeouts(LEFT);
                    return true;
                case R.id.rightTimeoutsView:
                    nullTimeouts(RIGHT);
                    return true;
                case R.id.leftTimeouts20View:
                    nullTimeouts20(LEFT);
                    return true;
                case R.id.rightTimeouts20View:
                    nullTimeouts20(RIGHT);
                    return true;
                case R.id.leftNameView:
                    chooseTeamNameDialog("home", hName);
                    return true;
                case R.id.rightNameView:
                    chooseTeamNameDialog("guest", gName);
                    return true;
                case R.id.leftArrowView:
                case R.id.rightArrowView:
                    clearPossession();
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    private void mainTimeClick() {
        if (!mainTimerOn) {
            if (useDirectTimer) {
                startDirectTimer();
            } else {
                startMainCountDownTimer();
            }
        } else {
            pauseGame();
        }
    }

    private void shotTimeClick() {
        shotTickInterval = SECOND;
        if (mainTimerOn) {
            shotTimer.cancel();
            startShotCountDownTimer(shotTimePref);
        } else {
            if (shotTime == shotTimePref) {
                shotTime = shortShotTimePref;
            } else {
                shotTime = shotTimePref;
            }
            setShotTimeText(shotTime);
        }
    }

    private void shotTimeSwitchClick() {
        shotTickInterval = SECOND;
        if (shotTimer != null && enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        shotTime = shortShotTimePref;
        if (mainTimerOn) {
            startShotCountDownTimer(shortShotTimePref);
        } else {
            setShotTimeText(shotTime);
        }
        if (shortShotTimePref < mainTime) {
            shotTimeView.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void saveCurrentState() {
        statePref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = statePref.edit();
        editor.putString(STATE_HOME_NAME, hName);
        editor.putString(STATE_GUEST_NAME, gName);
        editor.putLong(STATE_SHOT_TIME, shotTime);
        editor.putLong(STATE_MAIN_TIME, mainTime);
        editor.putInt(STATE_PERIOD, period);
        editor.putInt(STATE_HOME_SCORE, hScore);
        editor.putInt(STATE_GUEST_SCORE, gScore);
        editor.putInt(STATE_HOME_FOULS, hFouls);
        editor.putInt(STATE_GUEST_FOULS, gFouls);
        if (timeoutRules == TO_RULES_FIBA) {
            editor.putInt(STATE_HOME_TIMEOUTS, hTimeouts);
            editor.putInt(STATE_GUEST_TIMEOUTS, gTimeouts);
        } else if (timeoutRules == TO_RULES_NBA) {
            editor.putInt(STATE_HOME_TIMEOUTS_NBA, hTimeouts);
            editor.putInt(STATE_GUEST_TIMEOUTS_NBA, gTimeouts);
            editor.putInt(STATE_HOME_TIMEOUTS20, hTimeouts20);
            editor.putInt(STATE_GUEST_TIMEOUTS20, gTimeouts20);
        }
        if (arrowsOn) {
            editor.putInt(STATE_POSSESSION, possession);
        }
        editor.apply();
        if (spOn) {
            if (leftPanel != null) {
                leftPanel.saveCurrentData(statePref);
            }
            if (rightPanel != null) {
                rightPanel.saveCurrentData(statePref);
            }
        }
    }

    private void getSavedState() {
        statePref = getPreferences(MODE_PRIVATE);
        shotTime = statePref.getLong(STATE_SHOT_TIME, 24 * SECOND);
        mainTime = totalTime = statePref.getLong(STATE_MAIN_TIME, 600 * SECOND);
        period = (short) statePref.getInt(STATE_PERIOD, 1);
        hScore = (short) statePref.getInt(STATE_HOME_SCORE, 0);
        gScore = (short) statePref.getInt(STATE_GUEST_SCORE, 0);
        hName = statePref.getString(STATE_HOME_NAME, getResources().getString(R.string.home_team_name_default));
        gName = statePref.getString(STATE_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
        hFouls = (short) statePref.getInt(STATE_HOME_FOULS, 0);
        gFouls = (short) statePref.getInt(STATE_GUEST_FOULS, 0);
        if (timeoutRules == TO_RULES_FIBA) {
            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS, 0);
            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS, 0);
        } else if (timeoutRules == TO_RULES_NBA) {
            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS_NBA, 0);
            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS_NBA, 0);
            hTimeouts20 = (short) statePref.getInt(STATE_HOME_TIMEOUTS20, 0);
            gTimeouts20 = (short) statePref.getInt(STATE_GUEST_TIMEOUTS20, 0);
        }
    }

    private void setSavedState() {
        setMainTimeText(mainTime);
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
        setTeamNames();

        if (layoutType == LAYOUT_FULL) {
            if (enableShotTime) {
                setShotTimeText(shotTime);
            }
            hFoulsView.setText(Short.toString(hFouls));
            gFoulsView.setText(Short.toString(gFouls));
            long mainTimeTemp = mainTime;
            setPeriod();
            mainTime = mainTimeTemp;
            setTimeouts();
            hTimeoutsView.setText(Short.toString(hTimeouts));
            gTimeoutsView.setText(Short.toString(gTimeouts));
            if (timeoutRules == TO_RULES_NBA) {
                hTimeouts20View.setText(Short.toString(hTimeouts20));
                gTimeouts20View.setText(Short.toString(gTimeouts20));
            }
        }
        if (arrowsOn) { setPossession(possession); }
    }

    private void getSettings() {
        getSettingsNoRestart();
        getSettingsRestart();
    }

    private void getSettingsNoRestart() {
        boolean fixLandscape_ = sharedPref.getBoolean(PrefActivity.PREF_FIX_LANDSCAPE, true);
        fixLandscapeChanged = fixLandscape != fixLandscape_;
        fixLandscape = fixLandscape_;
        autoSound = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_AUTO_SOUND, "0"));
        hornUserRepeats = (sharedPref.getInt(PrefActivity.PREF_HORN_LENGTH, DEFAULT_HORN_LENGTH) * Math.round(hornLength / 1000f));
        autoSaveResults = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_AUTO_SAVE_RESULTS, "0"));
        autoShowTimeout = sharedPref.getBoolean(PrefActivity.PREF_AUTO_TIMEOUT, true);
        autoShowBreak = sharedPref.getBoolean(PrefActivity.PREF_AUTO_BREAK, true);
        autoSwitchSides = sharedPref.getBoolean(PrefActivity.PREF_AUTO_SWITCH_SIDES, false);
        pauseOnSound = sharedPref.getBoolean(PrefActivity.PREF_PAUSE_ON_SOUND, true);
        vibrationOn = vibrator.hasVibrator() && sharedPref.getBoolean(PrefActivity.PREF_VIBRATION, false);
        saveOnExit = sharedPref.getBoolean(PrefActivity.PREF_SAVE_ON_EXIT, true);
        fractionSecondsMain = sharedPref.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_MAIN, true);
        fractionSecondsShot = sharedPref.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_SHOT, true);

        shotTimePref = sharedPref.getInt(PrefActivity.PREF_SHOT_TIME, DEFAULT_SHOT_TIME) * 1000;
        boolean enableShotTime_ = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHOT_TIME, true);
        enableShotTimeChanged = enableShotTime != enableShotTime_;
        enableShotTime = enableShotTime_;
        boolean enableShortShotTime = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHORT_SHOT_TIME, true);
        shortShotTimePref = (enableShortShotTime) ? sharedPref.getInt(PrefActivity.PREF_SHORT_SHOT_TIME, DEFAULT_SHORT_SHOT_TIME) * 1000 : shotTimePref;
        mainTimePref = sharedPref.getInt(PrefActivity.PREF_REGULAR_TIME, DEFAULT_FIBA_MAIN_TIME) * SECONDS_60;
        overTimePref = sharedPref.getInt(PrefActivity.PREF_OVERTIME, DEFAULT_OVERTIME) * SECONDS_60;
        numRegularPeriods = (short) sharedPref.getInt(PrefActivity.PREF_NUM_REGULAR, DEFAULT_NUM_REGULAR);
        hName = sharedPref.getString(PrefActivity.PREF_HOME_NAME, getResources().getString(R.string.home_team_name_default));
        gName = sharedPref.getString(PrefActivity.PREF_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
        actualTime = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_ACTUAL_TIME, "1"));
        maxFouls = (short) sharedPref.getInt(PrefActivity.PREF_MAX_FOULS, DEFAULT_MAX_FOULS);
        mainTimeFormat = (fractionSecondsMain && 0 < mainTime && mainTime < SECONDS_60) ? TIME_FORMAT_MILLIS : TIME_FORMAT;
        boolean sidePanelsOn_ = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SIDE_PANELS, false);
        if (sidePanelsOn_ != spOn) {
            spOn = sidePanelsOn_;
            spStateChanged = true;
        }
        spClearDelete = sharedPref.getString(PrefActivity.PREF_SIDE_PANELS_CLEAR, "0").equals("0");
        spConnected = sharedPref.getBoolean(PrefActivity.PREF_SIDE_PANELS_CONNECTED, false);
        SidePanelRow.setMaxFouls(sharedPref.getInt(PrefActivity.PREF_SIDE_PANELS_FOULS_MAX, DEFAULT_FIBA_PLAYER_FOULS));

        restartShotTimer = sharedPref.getBoolean(PrefActivity.PREF_SHOT_TIME_RESTART, true);
        playByPlay = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_PLAY_BY_PLAY, "0"));

        boolean arrowsOn_ = sharedPref.getBoolean(PrefActivity.PREF_POSSESSION_ARROWS, false);
        if (arrowsOn != arrowsOn_) {
            arrowsStateChanged = true;
            arrowsOn = arrowsOn_;
        }

        PrefActivity.prefChangedNoRestart = false;
    }

    private void getSettingsRestart() {
        int temp_int = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_LAYOUT, "0"));
        if (temp_int != layoutType) {
            layoutChanged = true;
            layoutType = temp_int;
        }
        useDirectTimer = sharedPref.getBoolean(PrefActivity.PREF_DIRECT_TIMER, false);
        temp_int = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_TIMEOUTS_RULES, "0"));
        if (temp_int != timeoutRules) {
            timeoutsRulesChanged = true;
            timeoutRules = temp_int;
        }
        PrefActivity.prefChangedRestart = false;
    }

    private void zeroState() {
        mainTimerOn = false;
        mainTimeFormat = TIME_FORMAT;
        mainTickInterval = SECOND;
        if (useDirectTimer) {
            mainTime = 0;
        } else {
            mainTime = mainTimePref;
        }
        changedUnder2Minutes = false;
        setMainTimeText(mainTime);
        hScore = gScore = 0;
        leftActionType = rightActionType = ACTION_NONE;
        leftActionValue = rightActionValue = 0;
        nullScore(LEFT);
        nullScore(RIGHT);
        setTeamNames();
        if (layoutType == LAYOUT_FULL) {
            if (enableShotTime) {
                shotTimerOn = false;
                shotTime = shotTimePref;
                shotTickInterval = SECOND;
                setShotTimeText(shotTime);
                shotTimeView.setVisibility(View.VISIBLE);
                shotTimeSwitchView.setVisibility(View.VISIBLE);
            }
            nullTimeouts(2);
            nullFouls();
            period = 1;
            periodView.setText("1");
            if (timeoutRules == TO_RULES_NBA) {
                nullTimeouts20(2);
            }
        }
        if (spOn) {
            try {
                leftPanel.clear(spClearDelete);
                rightPanel.clear(spClearDelete);
            } catch (NullPointerException e) {
                Log.d(TAG, "Left or right panel is null");
            }
        }
        if (arrowsOn) { clearPossession(); }
        if (fixLandscapeChanged) {
            handleOrientation();
            fixLandscapeChanged = false;
        }
    }

    private void newGameSave() {
        if (autoSaveResults == 0) {
            saveResult();
            saveResultDb();
        } else if (autoSaveResults == 2) {
            showConfirmDialog("save_result", false);
        }
//        newGame();
    }

    private void newGame() {
        if (PrefActivity.prefChangedRestart || PrefActivity.prefChangedNoRestart) {
            getSettings();
            if (enableShotTime && layoutType == LAYOUT_FULL && !layoutChanged) {
                shotTimeSwitchView.setText(Long.toString(shortShotTimePref / 1000));
            }
        }
        if (layoutChanged || timeoutsRulesChanged) { initLayout(); }
        pauseGame();
        zeroState();
        if (layoutType == LAYOUT_FULL) { setTimeouts(); }
        if (spOn) { SidePanelFragment.clearCurrentData(); }
        if (arrowsOn) { clearPossession(); }
        gameResult = new Result(hName, gName);
    }

    private void newPeriod(boolean next) {
        pauseGame();
        changedUnder2Minutes = false;
        if (next) {
            period++;
        } else {
            period = 1;
        }
        setPeriod();
        if (enableShotTime) {
            shotTime = shotTimePref;
            setShotTimeText(shotTime);
            shotTimeView.setVisibility(View.VISIBLE);
            shotTimeSwitchView.setVisibility(View.VISIBLE);
            shotTickInterval = SECOND;
        }
        mainTickInterval = SECOND;
        mainTimeFormat = TIME_FORMAT;
        setMainTimeText(mainTime);
        if (period <= numRegularPeriods) {
            nullFouls();
        }
        setTimeouts();
        saveResult();
        scoreSaved = false;
        if (period == 3 && autoSwitchSides) {
            switchSides();
        }
    }

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

        TextView _NameView = hNameView;
        hNameView = gNameView;
        gNameView = _NameView;
        setTeamNames(hName, gName);

        TextView _ScoreView = hScoreView;
        hScoreView = gScoreView;
        gScoreView = _ScoreView;
        setScoresText(hScore, gScore);

        if (layoutType == LAYOUT_FULL) {
            TextView _FoulsView = hFoulsView;
            hFoulsView = gFoulsView;
            gFoulsView = _FoulsView;
            setFoulsText(hFouls, gFouls, gFoulsView.getCurrentTextColor(), hFoulsView.getCurrentTextColor());

            TextView _TimeoutsView = hTimeoutsView;
            hTimeoutsView = gTimeoutsView;
            gTimeoutsView = _TimeoutsView;
            setTimeoutsText(hTimeouts, gTimeouts, gTimeoutsView.getCurrentTextColor(), hTimeoutsView.getCurrentTextColor());

            if (timeoutRules == TO_RULES_NBA) {
                TextView _Timeouts20View = hTimeouts20View;
                hTimeouts20View = gTimeouts20View;
                gTimeouts20View = _Timeouts20View;
                setTimeouts20Text(hTimeouts20, gTimeouts20, gTimeouts20View.getCurrentTextColor(), hTimeouts20View.getCurrentTextColor());
            }
        }

        if (spOn && leftPanel != null) {
            try {
                switchSidePanels();
            } catch (NullPointerException e) {
                Log.d(TAG, "Left or right panel is null");
            }
        }

        if (arrowsOn) {
            switchPossession();
        }

        leftIsHome = !leftIsHome;

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

    private void switchPossession() {
        if (leftArrow != null && rightArrow != null) {
            if (possession == NO_TEAM) { return; }
            possession = 1 - possession;
                if (possession == HOME) {
                    leftArrow.setFill();
                    rightArrow.setStroke();
                } else if (possession == GUEST) {
                    rightArrow.setFill();
                    leftArrow.setStroke();
            }
        }
    }

    private void setTimeoutsText(short hValue, short gValue, int hColor, int gColor) {
        hTimeoutsView.setText(Short.toString(hValue));
        gTimeoutsView.setText(Short.toString(gValue));
        hTimeoutsView.setTextColor(hColor);
        gTimeoutsView.setTextColor(gColor);
    }

    private void setTimeouts20Text(short hValue, short gValue, int hColor, int gColor) {
        hTimeouts20View.setText(Short.toString(hValue));
        gTimeouts20View.setText(Short.toString(gValue));
        hTimeouts20View.setTextColor(hColor);
        gTimeouts20View.setTextColor(gColor);
    }

    private void setTimeouts() {
        if (timeoutRules == TO_RULES_FIBA) {
            timeoutFullDuration = 60;
            if (period == 1) {
                maxTimeouts = 2;
                nullTimeouts(2);
            } else if (period == 3) {
                maxTimeouts = 3;
                nullTimeouts(2);
            } else if (period == numRegularPeriods + 1) {
                maxTimeouts = 1;
                nullTimeouts(2);
            }
        } else if (timeoutRules == TO_RULES_NBA) {
            takenTimeoutsFull = 0;
            maxTimeouts20 = 1;
            nullTimeouts20(2);
            if (period == 1) {
                maxTimeouts = 6;
                nullTimeouts(2);
            } else if (period == 4 && maxTimeouts > 3) {
                maxTimeouts = 3;
                if (hTimeouts > maxTimeouts) {
                    nullTimeouts(0);
                }
                if (gTimeouts > maxTimeouts) {
                    nullTimeouts(1);
                }
            }
            if (period == 1 || period == 3) {
                maxTimeouts100 = 2;
            } else if (period == 2 || period == 4) {
                maxTimeouts100 = 3;
            } else if (period == numRegularPeriods + 1) {
                maxTimeouts100 = 1;
                maxTimeouts = 2;
                nullTimeouts(2);
            }
        } else {
            timeoutFullDuration = 60;
        }
    }

    private void nullTimeouts(boolean left) {
        if (timeoutRules == TO_RULES_NONE) {
            nullTimeoutsNoRules(left);
            return;
        }
        if (left == leftIsHome) {
            nullTimeouts(HOME);
        } else {
            nullTimeouts(GUEST);
        }
    }

    private void nullTimeoutsNoRules(boolean left) {
        if (left == leftIsHome) {
            nullTimeoutsNoRules(HOME);
        } else {
            nullTimeoutsNoRules(GUEST);
        }
    }

    private void nullTimeouts20(boolean left) {
        if (left == leftIsHome) {
            nullTimeouts20(HOME);
        } else {
            nullTimeouts20(GUEST);
        }
    }

    private void nullTimeouts(int team) {
        if (team > 0) {
            gTimeouts = maxTimeouts;
            setColorGreen(gTimeoutsView);
            gTimeoutsView.setText(Short.toString(maxTimeouts));
            if (team == 1) {
                return;
            }
        }
        hTimeouts = maxTimeouts;
        setColorGreen(hTimeoutsView);
        hTimeoutsView.setText(Short.toString(maxTimeouts));
    }

    private void nullTimeoutsNoRules(int team) {
        if (team > 0) {
            gTimeouts = 0;
            setColorGreen(gTimeoutsView);
            gTimeoutsView.setText("0");
            if (team == 1) {
                return;
            }
        }
        hTimeouts = 0;
        setColorGreen(hTimeoutsView);
        hTimeoutsView.setText("0");
    }

    private void nullTimeouts20(int team) {
        if (team > 0) {
            gTimeouts20 = maxTimeouts20;
            gTimeouts20View.setText(Short.toString(maxTimeouts20));
            setColorGreen(gTimeouts20View);
            if (team == 1) {
                return;
            }
        }
        hTimeouts20 = maxTimeouts20;
        hTimeouts20View.setText(Short.toString(maxTimeouts20));
        setColorGreen(hTimeouts20View);
    }

    private void timeout(boolean left) {
        if (left == leftIsHome) {
            timeout(HOME);
        } else {
            timeout(GUEST);
        }
    }

    private void timeout(int team) {
        pauseGame();
        takenTimeoutsFull++;
        if (timeoutRules == TO_RULES_NONE) {
            switch (team) {
                case HOME:
                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (autoShowTimeout) {
                        showTimeout(timeoutFullDuration, hName);
                    }
                    break;
                case GUEST:
                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (autoShowTimeout) {
                        showTimeout(timeoutFullDuration, gName);
                    }
                    break;
            }
        } else if (timeoutRules == TO_RULES_FIBA) {
            switch (team) {
                case HOME:
                    if (hTimeouts > 0) {
                        hTimeoutsView.setText(Short.toString(--hTimeouts));
                        if (hTimeouts == 0) {
                            setColorRed(hTimeoutsView);
                        }
                        if (autoShowTimeout) {
                            showTimeout(timeoutFullDuration, hName);
                        }
                    }
                    break;
                case GUEST:
                    if (gTimeouts > 0) {
                        gTimeoutsView.setText(Short.toString(--gTimeouts));
                        if (gTimeouts == 0) {
                            setColorRed(gTimeoutsView);
                        }
                        if (autoShowTimeout) {
                            showTimeout(timeoutFullDuration, gName);
                        }
                    }
                    break;
            }
        } else {
            timeoutFullDuration = (takenTimeoutsFull <= maxTimeouts100) ? 100 : 60;
            switch (team) {
                case HOME:
                    if (hTimeouts > 0) {
                        hTimeoutsView.setText(Short.toString(--hTimeouts));
                        if (hTimeouts == 0) {
                            setColorRed(hTimeoutsView);
                        }
                        if (autoShowTimeout) {
                            showTimeout(timeoutFullDuration, hName);
                        }
                    }
                    break;
                case GUEST:
                    if (gTimeouts > 0) {
                        gTimeoutsView.setText(Short.toString(--gTimeouts));
                        if (gTimeouts == 0) {
                            setColorRed(gTimeoutsView);
                        }
                        if (autoShowTimeout) {
                            showTimeout(timeoutFullDuration, gName);
                        }
                    }
                    break;
            }
        }
        addAction(ACTION_TO, team, 1);
    }

    private void revertTimeout(int team) {
        takenTimeoutsFull--;
        if (timeoutRules == TO_RULES_NONE) {
            switch (team) {
                case HOME:
                    hTimeoutsView.setText(Short.toString(--hTimeouts));
                    break;
                case GUEST:
                    gTimeoutsView.setText(Short.toString(--gTimeouts));
                    break;
            }
        } else if (timeoutRules == TO_RULES_FIBA) {
            switch (team) {
                case HOME:
                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (hTimeouts > 0) {
                        setColorGreen(hTimeoutsView);
                    }
                    break;
                case GUEST:
                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (gTimeouts > 0) {
                        setColorGreen(gTimeoutsView);
                    }
                    break;
            }
        } else {
            timeoutFullDuration = (takenTimeoutsFull <= maxTimeouts100) ? 100 : 60;
            switch (team) {
                case HOME:
                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (hTimeouts > 0) {
                        setColorGreen(hTimeoutsView);
                    }
                    break;
                case GUEST:
                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (gTimeouts > 0) {
                        setColorGreen(gTimeoutsView);
                    }
                    break;
            }
        }
    }

    private void timeout20(boolean left) {
        if (left == leftIsHome) {
            timeout20(HOME);
        } else {
            timeout20(GUEST);
        }
    }

    private void timeout20(int team) {
        pauseGame();
        switch (team) {
            case HOME:
                if (hTimeouts20 > 0) {
                    hTimeouts20View.setText(Short.toString(--hTimeouts20));
                    if (hTimeouts20 == 0) {
                        setColorRed(hTimeouts20View);
                    }
                    if (autoShowTimeout) {
                        showTimeout(20, hName);
                    }
                }
                break;
            case GUEST:
                if (gTimeouts20 > 0) {
                    gTimeouts20View.setText(Short.toString(--gTimeouts20));
                    if (gTimeouts20 == 0) {
                        setColorRed(gTimeouts20View);
                    }
                    if (autoShowTimeout) {
                        showTimeout(20, gName);
                    }
                }
                break;
        }
        addAction(ACTION_TO20, team, 1);
    }

    private void revertTimeout20(int team) {
        switch (team) {
            case HOME:
                if (hTimeouts20 > 0) {
                    if (hTimeouts20 == 0) {
                        setColorGreen(hTimeouts20View);
                    }
                    hTimeouts20View.setText(Short.toString(++hTimeouts20));
                }
                break;
            case GUEST:
                if (gTimeouts20 > 0) {
                    if (gTimeouts20 == 0) {
                        setColorGreen(gTimeouts20View);
                    }
                    gTimeouts20View.setText(Short.toString(++gTimeouts20));
                }
                break;
        }
    }

    private void foul(boolean left) {
        if (left == leftIsHome) {
            foul(HOME);
        } else {
            foul(GUEST);
        }
        if (left) {
            leftActionType = ACTION_FLS;
            leftActionValue += 1;
        } else {
            rightActionType = ACTION_FLS;
            rightActionValue += 1;
        }
    }

    private void foul(int team) {
        if (actualTime > 0) {
            pauseGame();
        }
        if (enableShotTime && shotTime < shortShotTimePref) {
            shotTime = shortShotTimePref;
            setShotTimeText(shotTime);
        }
        switch (team) {
            case HOME:
                if (hFouls < maxFouls) {
                    hFoulsView.setText(Short.toString(++hFouls));
                    if (hFouls == maxFouls) {
                        setColorRed(hFoulsView);
                    }
                }
                break;
            case GUEST:
                if (gFouls < maxFouls) {
                    gFoulsView.setText(Short.toString(++gFouls));
                    if (gFouls == maxFouls) {
                        setColorRed(gFoulsView);
                    }
                }
                break;
        }
        addAction(ACTION_FLS, team, 1);
    }

    private void nullFouls() {
        hFouls = gFouls = 0;
        hFoulsView.setText("0");
        gFoulsView.setText("0");
        setColorGreen(hFoulsView);
        setColorGreen(gFoulsView);
    }

    private void nullFouls(boolean left) {
        if (left == leftIsHome) {
            hFouls = 0;
            hFoulsView.setText("0");
            setColorGreen(hFoulsView);
        } else {
            gFouls = 0;
            gFoulsView.setText("0");
            setColorGreen(gFoulsView);
        }
    }

    private void revertFoul(int team) {
        switch (team) {
            case HOME:
                if (hFouls < maxFouls) {
                    if (hFouls == maxFouls) {
                        setColorGreen(hFoulsView);
                    }
                    hFoulsView.setText(Short.toString(--hFouls));
                }
                break;
            case GUEST:
                if (gFouls < maxFouls) {
                    if (gFouls == maxFouls) {
                        setColorGreen(gFoulsView);
                    }
                    gFoulsView.setText(Short.toString(--gFouls));
                }
                break;
        }
        if (spOn) {}
    }

    private void setFoulsText(short hValue, short gValue, int hColor, int gColor) {
        hFoulsView.setText(Short.toString(hValue));
        gFoulsView.setText(Short.toString(gValue));
        hFoulsView.setTextColor(hColor);
        gFoulsView.setTextColor(gColor);
    }

    private void setPossession(int team) {
        if (leftArrow != null && rightArrow != null) {
            switch (team) {
                case HOME:
                    leftArrow.setFill();
                    rightArrow.setStroke();
                    break;
                case GUEST:
                    rightArrow.setFill();
                    leftArrow.setStroke();
                    break;
                case NO_TEAM:
                    leftArrow.setStroke();
                    rightArrow.setStroke();
                    break;
            }
        possession = team;
        }
    }

    private void clearPossession() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setStroke();
            rightArrow.setStroke();
            possession = NO_TEAM;
        }
    }

    private void setScoresText(short hValue, short gValue) {
        hScore = hValue;
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
        gScore = gValue;
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
    }

    private void nullScore(boolean left) {
        if (left == leftIsHome) {
            hScore = 0;
            hScoreView.setText("00");
        } else {
            gScore = 0;
            gScoreView.setText("00");
        }
        handleScoreViewSize();
    }

    private void changeScore(boolean left, int value) {
        hScore_prev = hScore;
        gScore_prev = gScore;
        if (left == leftIsHome) {
            changeHomeScore(value);
            addAction(ACTION_PTS, HOME, value);
        } else {
            changeGuestScore(value);
            addAction(ACTION_PTS, GUEST, value);
        }
        if (left) {
            leftActionType = ACTION_PTS;
            leftActionValue += value;
        } else {
            rightActionType = ACTION_PTS;
            rightActionValue += value;
        }
        updateStats();
    }

    private void updateStats() {
        if (hScore == gScore) {
            timesTie++;
        } else if (hScore > gScore != hScore_prev > gScore_prev) {
            timesLeadChanged++;
        }
        if (hScore - gScore > hMaxLead) {
            hMaxLead = hScore - gScore;
        }
        if (gScore - hScore > gMaxLead) {
            gMaxLead = gScore - hScore;
        }
    }

    private void revertScore(int team, int value) {
        if (team == HOME) {
            changeHomeScore(-value);
        } else {
            changeGuestScore(-value);
        }
//        if (left) {
//            leftActionValue += value;
//        } else {
//            rightActionValue += value;
//        }

    }

    private void handleScoreViewSize() {
        if (gScore >= 100 || hScore >= 100) {
            if (scoreViewSize == 0) {
                scoreViewSize = getResources().getDimension(R.dimen.score_size);
            }
            hScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize * 0.75f);
            gScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize * 0.75f);
        } else {
            if (scoreViewSize != 0) {
                hScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize);
                gScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize);
            }
        }
    }

    private void handleScoreChange() {
        if (enableShotTime && layoutType == LAYOUT_FULL && restartShotTimer) {
            if (mainTimerOn) {
                startShotCountDownTimer(shotTimePref);
            } else {
                shotTime = shotTimePref;
                setShotTimeText(shotTimePref);
            }
        }
        if (actualTime == 2 || (actualTime == 3 && mainTime < SECONDS_60)) {
            pauseGame();
        }
        scoreSaved = false;
    }

    private void changeGuestScore(int value) {
        gScore += value;
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
        if (value != 0) {
            handleScoreChange();
        }
        handleScoreViewSize();
    }

    private void changeHomeScore(int value) {
        hScore += value;
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
        if (value != 0) {
            handleScoreChange();
        }
        handleScoreViewSize();
    }

    private void addAction(int type, int team, int value) {
        if (playByPlay != 0) {
            lastAction = gameResult.addAction(mainTime, type, team, value);
        }
    }

    private boolean cancelLastAction() {
        lastAction = gameResult.getLastAction();
        if (lastAction == null) {
            return false;
        }
        switch (lastAction.getType()) {
            case ACTION_PTS:
                revertScore(lastAction.getTeam(), lastAction.getValue());
                if (spOn) {
                    cancelPlayerScore(lastAction.getTeam(), lastAction.getNumber(), lastAction.getValue());
                }
                break;
            case ACTION_FLS:
                revertFoul(lastAction.getTeam());
                if (spOn) {
                    cancelPlayerFoul(lastAction.getTeam(), lastAction.getNumber(), lastAction.getValue());
                }
                break;
            case ACTION_TO:
                revertTimeout(lastAction.getTeam());
                break;
            case ACTION_TO20:
                revertTimeout20(lastAction.getTeam());
                break;
        }
        return true;
    }

    private SidePanelRow getPlayer(int team, int number) {
        SidePanelFragment panel = (leftIsHome ^ (team == HOME)) ? rightPanel : leftPanel;
        return panel.getPlayer(number);
    }

    private void cancelPlayerScore(int team, int number, int value) {
        SidePanelRow player = getPlayer(team, number);
        if (player != null) { player.changePoints(-value); }
    }

    private void cancelPlayerFoul(int team, int number, int value) {
        SidePanelRow player = getPlayer(team, number);
        if (player != null) { player.changeFouls(-value); }
    }

    private void setMainTimeText(long millis) {
        mainTimeView.setText(mainTimeFormat.format(millis).replaceAll(API16_TIME_REGEX, "$1"));
    }

    private void setShotTimeText(long millis) {
        if (millis < 5000 && fractionSecondsShot) {
            shotTimeView.setText(String.format(TIME_FORMAT_SHORT, millis / 1000, (millis % 1000) / 100));
        } else {
            shotTimeView.setText(String.format(FORMAT_TWO_DIGITS, (short) Math.ceil(millis / 1000.0)));
        }
    }

    private void setPeriod() {
        if (period <= numRegularPeriods) {
            mainTime = totalTime = mainTimePref;
            periodView.setText(Short.toString(period));
            if (periodViewSize != 0) {
                periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize);
            }
        } else {
            mainTime = totalTime = overTimePref;
            periodView.setText(String.format("OT%d", period - numRegularPeriods));
            if (periodViewSize == 0) {
                periodViewSize = getResources().getDimension(R.dimen.bottom_line_size);
            }
            periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize * 0.75f);
        }
        if (useDirectTimer) {
            mainTime = 0;
        }
    }

    private void setTeamNames(String home, String guest) {
        hNameView.setText(home);
        gNameView.setText(guest);
        gameResult.setHomeName(home);
        gameResult.setGuestName(guest);
    }

    private void setTeamNames() {
        setTeamNames(hName, gName);
    }

    private void pauseGame() {
        if (useDirectTimer) {
            pauseDirectTimer();
        } else if (mainTimerOn) {
            mainTimer.cancel();
        }
        if (shotTimer != null && enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        mainTimerOn = shotTimerOn = false;
    }

    private void under2Minutes() {
        if (timeoutRules == TO_RULES_NBA) {
            if (period == 4) {
                if (hTimeouts == 2 || hTimeouts == 3) {
                    hTimeouts = 1;
                    hTimeouts20++;
                    hTimeoutsView.setText("1");
                    hTimeouts20View.setText(Short.toString(hTimeouts20));
                }
                if (gTimeouts == 2 || gTimeouts == 3) {
                    gTimeouts = 1;
                    gTimeouts20++;
                    gTimeoutsView.setText("1");
                    gTimeouts20View.setText(Short.toString(gTimeouts20));
                }
            }
        }
    }

    private void startMainCountDownTimer() {
        mainTimer = new CountDownTimer(mainTime, mainTickInterval) {
            public void onTick(long millisUntilFinished) {
                mainTime = millisUntilFinished;
                setMainTimeText(mainTime);
                if (enableShotTime && mainTime < shotTime && shotTimerOn) {
                    shotTimer.cancel();
                }
                if (mainTime < MINUTES_2 && !changedUnder2Minutes) {
                    changedUnder2Minutes = true;
                    under2Minutes();
                }
                if (fractionSecondsMain && mainTime < SECONDS_60 && mainTickInterval == SECOND) {
                    this.cancel();
                    mainTickInterval = 100;
                    mainTimeFormat = TIME_FORMAT_MILLIS;
                    startMainCountDownTimer();
                }
                if (enableShotTime && mainTime < shotTime && shotTimeView.getVisibility() == View.VISIBLE) {
                    shotTimeView.setVisibility(View.INVISIBLE);
                } else if (enableShotTime && mainTime < shortShotTimePref && shotTimeSwitchView.getVisibility() == View.VISIBLE) {
                    shotTimeSwitchView.setVisibility(View.INVISIBLE);
                }
            }

            public void onFinish() {
                mainTimerOn = false;
                if (autoSound >= 2) {
                    playHorn();
                }
                mainTickInterval = SECOND;
                setMainTimeText(0);
                if (enableShotTime && shotTimerOn) {
                    shotTimer.cancel();
                    setShotTimeText(0);
                }
                saveResult();
                if (period >= numRegularPeriods && hScore != gScore) {
                    if (dontAskNewGame == 0) {
                        showConfirmDialog("new_game", true);
                    } else {
                        endOfGameActions(dontAskNewGame);
                    }
                    showTimeoutDialog = false;
                }
                if (autoShowBreak && showTimeoutDialog) {
                    if (period == 2) {
                        showTimeout(900, "");
                    } else {
                        showTimeout(120, "");
                    }
                }
            }
        }.start();
        mainTimerOn = true;
        if (enableShotTime && !shotTimerOn && mainTime > shotTime) {
            startShotCountDownTimer();
        }
    }

    private void startShotCountDownTimer(long startValue) {
        if (shotTimerOn) {
            shotTimer.cancel();
        }
        shotTime = startValue;
        startShotCountDownTimer();
    }

    private void startShotCountDownTimer() {
        shotTimer = new CountDownTimer(shotTime, shotTickInterval) {
            public void onTick(long millisUntilFinished) {
                shotTime = millisUntilFinished;
                setShotTimeText(shotTime);
                if (shotTime < 5 * SECOND && shotTickInterval == SECOND) {
                    shotTickInterval = 100;
                    shotTimer.cancel();
                    startShotCountDownTimer();
                }
            }

            public void onFinish() {
                pauseGame();
                if (autoSound == 1 || autoSound == 3) {
                    playHorn();
                }
                setShotTimeText(0);
                shotTimeView.startAnimation(shotTimeBlinkAnimation);
                shotTime = shotTimePref;
                shotTickInterval = SECOND;
            }
        }.start();
        shotTimerOn = true;
    }

    private void startDirectTimer() {
        startTime = SystemClock.uptimeMillis() - mainTime;
        if (directTimerStopped) {
            stopDirectTimer();
        }
        mainTimeFormat = TIME_FORMAT;
        mainTimerOn = true;
        customHandler.postDelayed(directTimerThread, 0);
        if (enableShotTime) {
            startShotCountDownTimer();
        }
    }

    private void stopDirectTimer() {
        startTime = SystemClock.uptimeMillis();
        mainTime = 0;
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        directTimerStopped = true;
        mainTimerOn = shotTimerOn = false;
    }

    private void pauseDirectTimer() {
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        mainTimerOn = shotTimerOn = directTimerStopped = false;
    }

    private Runnable directTimerThread = new Runnable() {
        public void run() {
            mainTime = SystemClock.uptimeMillis() - startTime;
            setMainTimeText(mainTime);
            if (mainTime >= totalTime) {
                stopDirectTimer();
                return;
            }
            customHandler.postDelayed(this, 1000);
        }
    };

    private void setColor(TextView v, int color) {
        v.setTextColor(color);
    }

    private void setColorRed(TextView v) {
        setColor(v, getResources().getColor(R.color.red));
    }

    private void setColorGreen(TextView v) {
        setColor(v, getResources().getColor(R.color.green));
    }

    private void playWhistle() {
        playWhistle(2);
    }

    private void playWhistle(int repeats) {
        if (pauseOnSound) {
            pauseGame();
        }
        soundWhistleStreamId = soundPool.play(soundWhistleId, 1, 1, 0, repeats, 1);
        whistleRepeats = repeats;
        if (repeats != -1) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
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
        playHorn(hornUserRepeats);
    }

    private void playHorn(int repeats) {
        if (pauseOnSound) {
            pauseGame();
        }
        soundHornStreamId = soundPool.play(soundHornId, 1, 1, 0, repeats, 1);
        hornRepeats = repeats;
        if (repeats != -1) {
            new android.os.Handler().postDelayed(
                new Runnable() {
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
        saveResult();
        if (mainTime > 0) {
            gameResult.setComplete(false);
        }
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT,
                        gameResult.getResultString(period > numRegularPeriods))
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void showConfirmDialog(String type, boolean won) {
        ConfirmDialog dialog;
        if (won) {
            if (hScore > gScore) {
                dialog = ConfirmDialog.newInstance(type, hName, hScore, gScore);
            } else {
                dialog = ConfirmDialog.newInstance(type, gName, gScore, hScore);
            }
        } else {
            dialog = ConfirmDialog.newInstance(type);
        }
        dialog.show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
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
        inactivePlayers = ((left) ? leftPanel : rightPanel).getInactivePlayers();
        if (inactivePlayers.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.side_panel_no_data), Toast.LENGTH_LONG).show();
            return;
        }
        for (Map.Entry<Integer, SidePanelRow> entry : inactivePlayers.entrySet()) {
            numberNameList.add(String.format("%d: %s", entry.getValue().getNumber(), entry.getValue().getName()));
        }
        int number = (longClickPlayerBu.getTag() != null) ? ((SidePanelRow)longClickPlayerBu.getTag()).getNumber() : -1;

        ListDialog.newInstance("substitute", numberNameList, left, number).show(getFragmentManager(), ListDialog.TAG);
    }

    private void chooseTeamNameDialog(String team, String name) {
        DialogFragment nameEdit = NameEditDialog.newInstance(team, name);
        nameEdit.show(getFragmentManager(), TAG_FRAGMENT_NAME_EDIT);
    }

    private void showMainTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance(
                (int) (TimeUnit.MILLISECONDS.toMinutes(mainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mainTime))),
                (int) (TimeUnit.MILLISECONDS.toSeconds(mainTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mainTime))),
                (int) (mainTime % 1000) / 100);
        mainTimePicker.show(getFragmentManager(), TAG_FRAGMENT_MAIN_TIME_PICKER);
    }

    private void showShotTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance((int) shotTime / 1000, (int) (shotTime % 1000) / 100);
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

        boolean connected = spConnected && getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT;

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
                saveResultDb();
                newGame();
                break;
            case 3:
                newGame();
                break;
        }
    }

    private void saveResult() {
        if (!scoreSaved) {
            // возможно, надо учитывать сброс периода
            if (gameResult.getHomeScoreByPeriod().size() == period) {
                gameResult.replacePeriodScores(period, hScore, gScore);
            } else {
                gameResult.addPeriodScores(hScore, gScore);
            }
            scoreSaved = true;
        }
    }

    private void saveResultDb() {
        if (hScore == 0 && gScore == 0) {
            return;
        }
        if (period < numRegularPeriods || mainTime != 0 || mainTime != mainTimePref) {
            gameResult.setComplete(false);
        } else {
            gameResult.setComplete(true);
        }

        DbHelper dbHelper = DbHelper.getInstance(this);
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DbScheme.ResultsTable.COLUMN_DATE, (new Date()).getTime());
            cv.put(DbScheme.ResultsTable.COLUMN_HOME_TEAM, hName);
            cv.put(DbScheme.ResultsTable.COLUMN_GUEST_TEAM, gName);
            cv.put(DbScheme.ResultsTable.COLUMN_HOME_SCORE, hScore);
            cv.put(DbScheme.ResultsTable.COLUMN_GUEST_SCORE, gScore);
            cv.put(DbScheme.ResultsTable.COLUMN_SHARE_STRING, gameResult.getResultString(period > numRegularPeriods));
            cv.put(DbScheme.ResultsTable.COLUMN_HOME_PERIODS, gameResult.getHomeScoreByPeriodString());
            cv.put(DbScheme.ResultsTable.COLUMN_GUEST_PERIODS, gameResult.getGuestScoreByPeriodString());
            cv.put(DbScheme.ResultsTable.COLUMN_REGULAR_PERIODS, numRegularPeriods);
            cv.put(DbScheme.ResultsTable.COLUMN_COMPLETE, gameResult.isComplete());
            long gameId = db.insert(DbScheme.ResultsTable.TABLE_NAME, null, cv);

            if (spOn) {
                ContentValues cv2;
                TreeMap<Integer, SidePanelRow> allHomePlayers = leftPanel.getAllPlayers();
                TreeMap<Integer, SidePanelRow> allGuestPlayers = rightPanel.getAllPlayers();
                for (Map.Entry<Integer, SidePanelRow> entry : allHomePlayers.entrySet()) {
                    cv2 = new ContentValues();
                    SidePanelRow row = entry.getValue();
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_GAME_ID, gameId);
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_TEAM, hName);
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NUMBER, row.getNumber());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NAME, row.getName());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_POINTS, row.getPoints());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_FOULS, row.getFouls());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_CAPTAIN, (row.getCaptain()) ? 1 : 0);
                    db.insert(DbScheme.ResultsPlayersTable.TABLE_NAME, null, cv2);
                }
                for (Map.Entry<Integer, SidePanelRow> entry : allGuestPlayers.entrySet()) {
                    cv2 = new ContentValues();
                    SidePanelRow row = entry.getValue();
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_GAME_ID, gameId);
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_TEAM, gName);
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NUMBER, row.getNumber());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NAME, row.getName());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_POINTS, row.getPoints());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_FOULS, row.getFouls());
                    cv2.put(DbScheme.ResultsPlayersTable.COLUMN_PLAYER_CAPTAIN, (row.getCaptain()) ? 1 : 0);
                    db.insert(DbScheme.ResultsPlayersTable.TABLE_NAME, null, cv2);
                }
            }

            ContentValues cv3 = new ContentValues();
            cv3.put(DbScheme.GameDetailsTable.COLUMN_GAME_ID, gameId);
            cv3.put(DbScheme.GameDetailsTable.COLUMN_TIE, timesTie);
            cv3.put(DbScheme.GameDetailsTable.COLUMN_LEADER_CHANGED, timesLeadChanged);
            cv3.put(DbScheme.GameDetailsTable.COLUMN_HOME_MAX_LEAD, hMaxLead);
            cv3.put(DbScheme.GameDetailsTable.COLUMN_GUEST_MAX_LEAD, gMaxLead);
            if (playByPlay == 2) {
                cv3.put(DbScheme.GameDetailsTable.COLUMN_PLAY_BY_PLAY, gameResult.toString());
            }
            db.insert(DbScheme.GameDetailsTable.TABLE_NAME, null, cv3);


        } finally {
            dbHelper.close();
        }
    }

    @Override
    public void onTimeChanged(int minutes, int seconds, int millis) {
        mainTime = minutes * SECONDS_60 + seconds * 1000 + millis * 100;
        if (fractionSecondsMain && mainTime < SECONDS_60) {
            mainTickInterval = 100;
            mainTimeFormat = TIME_FORMAT_MILLIS;
        } else {
            mainTimeFormat = TIME_FORMAT;
        }

        if (enableShotTime && mainTime > shotTime) {
            shotTimeView.setVisibility(View.VISIBLE);
            shotTimeSwitchView.setVisibility(View.VISIBLE);
        }

        setMainTimeText(mainTime);
    }

    @Override
    public void onTimeChanged(int seconds, int millis) {
        shotTime = seconds * 1000 + millis * 100;
        if (shotTime < 5 * SECOND) {
            shotTickInterval = 100;
        }

        setShotTimeText(shotTime);
    }

    @Override
    public void onTimeoutDialogItemClick(int which) {
        pauseGame();
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
        pauseGame();
        mainTickInterval = SECOND;
        switch (which) {
            case 0:
                mainTime = mainTimePref;
                break;
            case 1:
                mainTime = overTimePref;
                break;
        }
        mainTimeFormat = TIME_FORMAT;
        setMainTimeText(mainTime);
    }

    @Override
    public void onClearPanelDialogItemClick(int which, boolean left) {
        ((left) ? leftPanel : rightPanel).clear(which == 0);
    }

    @Override
    public void onSubstituteListSelect(boolean left, int newNumber) {
        SidePanelRow row = inactivePlayers.get(newNumber);
        ((left) ? leftPanel : rightPanel).substitute(row, (SidePanelRow) longClickPlayerBu.getTag());
        longClickPlayerBu.setTag(row);
        longClickPlayerBu.setText(Integer.toString(newNumber));
    }

    @Override
    public void onNameChanged(String value, String team) {
        if (value.length() > 0) {
            switch (team) {
                case "home":
                    hName = value;
                    hNameView.setText(value);
                    gameResult.setHomeName(value);
                    break;
                case "guest":
                    gName = value;
                    gNameView.setText(value);
                    gameResult.setGuestName(value);
                    break;
            }
        }
    }

    @Override
    public void onConfirmDialogPositive(String type, boolean dontShow) {
        dontAskNewGame = (dontShow) ? 2 : 0;
        newGame();
    }

    @Override
    public void onConfirmDialogPositive(String type) {
        switch (type) {
            case "new_game":
                newGame();
                break;
            case "save_result":
                saveResult();
                saveResultDb();
                newGame();
                break;
            case "edit_player_captain":
                EditPlayerDialog f = (EditPlayerDialog) getFragmentManager().findFragmentByTag(EditPlayerDialog.TAG);
                if (f != null) {f.changeCaptainConfirmed();}
                break;
        }
    }

    @Override
    public void onConfirmDialogNeutral(boolean dontShow) {
        dontAskNewGame = (dontShow) ? 2 : 0;
        saveResult();
        saveResultDb();
        newGame();
    }

    @Override
    public void onConfirmDialogNegative(String type, boolean dontShow) {
        dontAskNewGame = (dontShow) ? 1 : 0;
    }

    @Override
    public void onConfirmDialogNegative(String type) {
        if (type.equals("save_result")) {
            newGame();
        }
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
        ArrayList<View> group = (left) ? leftPlayersButtons : rightPlayersButtons;
        int pos = 0;
        for (SidePanelRow row : rows) {
            View bu = group.get(pos++);
            ((Button) bu).setText(Integer.toString(row.getNumber()));
            bu.setTag(row);
        }
    }

    @Override
    public void onSidePanelNoActive(boolean left) {
        ArrayList<View> group = (left) ? leftPlayersButtons : rightPlayersButtons;
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
        ((left) ? leftPanel : rightPanel).addRow(number, name, captain);
    }

    @Override
    public void onEditPlayerEdit(boolean left, int id, int number, String name, boolean captain) {
        if (((left) ? leftPanel : rightPanel).editRow(id, number, name, captain)) {
            ArrayList<View> group = (left) ? leftPlayersButtons : rightPlayersButtons;
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
        if (((left) ? leftPanel : rightPanel).deleteRow(id)) {
            ArrayList<View> group = (left) ? leftPlayersButtons : rightPlayersButtons;
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
        return ((left) ? leftPanel : rightPanel).checkNewPlayer(number, captain);
    }
}