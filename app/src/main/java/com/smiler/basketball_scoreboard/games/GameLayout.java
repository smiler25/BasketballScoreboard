package com.smiler.basketball_scoreboard.games;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.smiler.basketball_scoreboard.CountDownTimer;
import com.smiler.basketball_scoreboard.FloatingCountdownTimerDialog;
import com.smiler.basketball_scoreboard.OverlayFragment;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.TriangleView;
import com.smiler.basketball_scoreboard.models.ActionRecord;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.results.Result;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;


import static com.smiler.basketball_scoreboard.Constants.ACTION_NONE;
import static com.smiler.basketball_scoreboard.Constants.NO_TEAM;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;
import static com.smiler.basketball_scoreboard.Constants.SECOND;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;

public class GameLayout extends LinearLayout {

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

    private boolean mainTimerOn, shotTimerOn;
    private boolean fractionSecondsMain, fractionSecondsShot;
    private int possession = NO_TEAM;
    private long mainTime, shotTime;
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
    private short period;
    private String hName, gName;

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

    public GameLayout(Context context) {
        super(context);
    }

    private GameLayout initLayout(ViewStub stub, OnClickListener clickListener, OnLongClickListener longClickListener) {
//        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
//        stub.setLayoutResource(timeoutRules == TO_RULES_NBA ? R.layout.full_bottom_nba : R.layout.full_bottom_simple);

//        stub.setLayoutResource(R.layout.board_layout);
        stub.setLayoutResource(R.layout.board_central);
        stub.inflate();

        leftIsHome = true;
        overlaySwitch = OverlayFragment.newInstance(OVERLAY_SWITCH);
        overlaySwitch.setRetainInstance(true);
//        if (layoutType == LAYOUT_FULL) {
//            initExtensiveLayout();
//        } else {
//            initSimpleLayout();
//        }
//        initCommonLayout();
//        if (spOn) {
//            initSidePanels();
//            leftPlayersButtonsGroup.setVisibility(View.VISIBLE);
//            rightPlayersButtonsGroup.setVisibility(View.VISIBLE);
//        }
//        initArrows();
//    }
//    private void initCommonLayout() {
        mainTimeView = (TextView) findViewById(R.id.mainTimeView);
        hScoreView = (TextView) findViewById(R.id.leftScoreView);
        gScoreView = (TextView) findViewById(R.id.rightScoreView);
        hNameView = (TextView) findViewById(R.id.leftNameView);
        gNameView = (TextView) findViewById(R.id.rightNameView);

        mainTimeView.setOnClickListener(clickListener);
        mainTimeView.setOnLongClickListener(longClickListener);
        hScoreView.setOnClickListener(clickListener);
        hScoreView.setOnLongClickListener(longClickListener);
        gScoreView.setOnClickListener(clickListener);
        gScoreView.setOnLongClickListener(longClickListener);
        hNameView.setOnClickListener(clickListener);
        hNameView.setOnLongClickListener(longClickListener);
        gNameView.setOnClickListener(clickListener);
        gNameView.setOnLongClickListener(longClickListener);

        findViewById(R.id.leftMinus1View).setOnClickListener(clickListener);
        findViewById(R.id.rightMinus1View).setOnClickListener(clickListener);
        findViewById(R.id.leftPlus1View).setOnClickListener(clickListener);
        findViewById(R.id.rightPlus1View).setOnClickListener(clickListener);
        findViewById(R.id.leftPlus3View).setOnClickListener(clickListener);
        findViewById(R.id.rightPlus3View).setOnClickListener(clickListener);

        View whistleView = findViewById(R.id.whistleView);
        View hornView = findViewById(R.id.hornView);
        hornView.setOnClickListener(clickListener);
        findViewById(R.id.timeoutIconView).setOnClickListener(clickListener);
        findViewById(R.id.cameraView).setOnClickListener(clickListener);
        findViewById(R.id.switchSidesView).setOnClickListener(clickListener);

//        hornView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        stopHorn();
//                        hornPressed = false;
//                        break;
//                    case MotionEvent.ACTION_DOWN:
//                        playHorn();
//                        hornPressed = true;
//                        break;
//                }
//                return true;
//            }
//        });

//        whistleView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        stopWhistle();
//                        whistlePressed = false;
//                        break;
//                    case MotionEvent.ACTION_DOWN:
//                        playWhistle();
//                        whistlePressed = true;
//                        break;
//                }
//                return true;
//            }
//        });
//
//        layoutChanged = timeoutsRulesChanged = false;
        return this;

    }

//    private void initArrows() {
//        try {
//            leftArrow = (TriangleView) findViewById(R.id.leftArrowView);
//            rightArrow = (TriangleView) findViewById(R.id.rightArrowView);
//            leftArrow.setOnClickListener(clickListener);
//            rightArrow.setOnClickListener(this);
//            leftArrow.setOnLongClickListener(this);
//            rightArrow.setOnLongClickListener(this);
//        } catch (NullPointerException e) {
//            Log.d(TAG, "initArrows: " + e.getMessage());
//        }
//        handleArrowsVisibility();
//    }
//
//    private void handleArrowsVisibility() {
//        if (arrowsOn) {
//            showArrows();
//        } else {
//            hideArrows();
//        }
//    }
//
//    private void hideArrows() {
//        leftArrow.setVisibility(View.GONE);
//        rightArrow.setVisibility(View.GONE);
//    }
//
//    private void showArrows() {
//        leftArrow.setVisibility(View.VISIBLE);
//        rightArrow.setVisibility(View.VISIBLE);
//    }



}
