package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.TriangleView;
import com.smiler.basketball_scoreboard.models.Game;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.text.SimpleDateFormat;

import static com.smiler.basketball_scoreboard.Constants.API16_TIME_REGEX;
import static com.smiler.basketball_scoreboard.Constants.FORMAT_TWO_DIGITS;
import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_SHORT;

public class StandardLayout extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "BS-StandardLayout";
    private final Preferences preferences;
    private TextView mainTimeView, shotTimeView, shotTimeSwitchView, periodView;
    private TextView hNameView, gNameView;
    private TextView hScoreView, gScoreView;
    private TextView hTimeoutsView, gTimeoutsView;
    private TextView hTimeouts20View, gTimeouts20View;
    private TextView hFoulsView, gFoulsView;
    private TriangleView leftArrow, rightArrow;
    private boolean leftIsHome = true;
    private Game.TO_RULES timeoutRules;
    private Game.GAME_TYPE layoutType;
    private ClickListener clickListener;
    private LongClickListener longClickListener;
    private boolean blockLongClick;

    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;

    @Override
    public void onClick(View v) {
//        if (preferences.vibrationOn) {
//            vibrator.vibrate(100);
//        }
        switch (v.getId()) {
            case R.id.leftScoreView:
                clickListener.onChangeScoreClick(LEFT, 2);
                break;
            case R.id.rightScoreView:
                clickListener.onChangeScoreClick(RIGHT, 2);
                break;
            case R.id.mainTimeView:
                clickListener.onMainTimeClick();
                break;
            case R.id.shotTimeView:
                clickListener.onShotTimeClick();
                break;
            case R.id.shotTimeSwitch:
                clickListener.onShotTimeSwitchClick();
                break;
            case R.id.leftPlus1View:
                clickListener.onChangeScoreClick(LEFT, 1);
                break;
            case R.id.rightPlus1View:
                clickListener.onChangeScoreClick(RIGHT, 1);
                break;
            case R.id.leftPlus3View:
                clickListener.onChangeScoreClick(LEFT, 3);
                break;
            case R.id.rightPlus3View:
                clickListener.onChangeScoreClick(RIGHT, 3);
                break;
            case R.id.leftMinus1View:
                clickListener.onChangeScoreClick(LEFT, -1);
                break;
            case R.id.rightMinus1View:
                clickListener.onChangeScoreClick(RIGHT, -1);
                break;
            case R.id.periodView:
                clickListener.onPeriodClick();
                break;
            case R.id.timeoutIconView:
                clickListener.onIconClick(ICONS.TIMEOUT);
                break;
            case R.id.newPeriodIconView:
                clickListener.onIconClick(ICONS.NEW_PERIOD);
                break;
            case R.id.leftFoulsView:
                clickListener.onFoulsClick(LEFT);
                break;
            case R.id.rightFoulsView:
                clickListener.onFoulsClick(RIGHT);
                break;
            case R.id.leftTimeoutsView:
                clickListener.onTimeoutsClick(LEFT);
                break;
            case R.id.rightTimeoutsView:
                clickListener.onTimeoutsClick(RIGHT);
                break;
            case R.id.leftTimeouts20View:
                clickListener.onTimeouts20Click(LEFT);
                break;
            case R.id.rightTimeouts20View:
                clickListener.onTimeouts20Click(RIGHT);
                break;
            case R.id.cameraView:
                clickListener.onIconClick(ICONS.CAMERA);
                break;
            case R.id.switchSidesView:
                clickListener.onIconClick(ICONS.SWITCH_SIDES);
//                switchSides();
                break;
            case R.id.left_panel_toggle:
                clickListener.onPanelToggleClick(LEFT);
                break;
            case R.id.right_panel_toggle:
                clickListener.onPanelToggleClick(RIGHT);
                break;
            case R.id.leftNameView:
            case R.id.leftArrowView:
                clickListener.onTeamClick(LEFT);
                break;
            case R.id.rightNameView:
            case R.id.rightArrowView:
                clickListener.onTeamClick(RIGHT);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
//        if (preferences.vibrationOn) { vibrator.vibrate(longClickVibrationPattern, -1); }

//        if (!mainTimerOn) {
        if (!blockLongClick) {
            switch (v.getId()) {
                case R.id.leftScoreView:
                    return longClickListener.onScoreLongClick(LEFT);
                case R.id.rightScoreView:
                    return longClickListener.onScoreLongClick(RIGHT);
                case R.id.mainTimeView:
                    return longClickListener.onMainTimeLongClick();
                case R.id.shotTimeView:
                    return longClickListener.onShotTimeLongClick();
                case R.id.periodView:
                    return longClickListener.onPeriodLongClick();
                case R.id.leftFoulsView:
                    return longClickListener.onFoulsLongClick(LEFT);
                case R.id.rightFoulsView:
                    return longClickListener.onFoulsLongClick(RIGHT);
                case R.id.leftTimeoutsView:
                    return longClickListener.onTimeoutsLongClick(LEFT);
                case R.id.rightTimeoutsView:
                    return longClickListener.onTimeoutsLongClick(RIGHT);
                case R.id.leftTimeouts20View:
                    return longClickListener.onTimeouts20LongClick(LEFT);
                case R.id.rightTimeouts20View:
                    return longClickListener.onTimeouts20LongClick(RIGHT);
                case R.id.leftNameView:
                    return longClickListener.onNameLongClick(LEFT);
                case R.id.rightNameView:
                    return longClickListener.onNameLongClick(RIGHT);
                case R.id.leftArrowView:
                case R.id.rightArrowView:
                    return longClickListener.onArrowLongClick();
                default:
                    return true;
            }
        }
        return false;
    }

    public interface ClickListener {
        void onChangeScoreClick(boolean left, int value);
        void onFoulsClick(boolean left);
        void onIconClick(ICONS icon);
        void onMainTimeClick();
        void onPanelToggleClick(boolean left);
        void onPeriodClick();
        void onShotTimeClick();
        void onShotTimeSwitchClick();
        void onTeamClick(boolean left);
        void onTimeoutsClick(boolean left);
        void onTimeouts20Click(boolean left);
    }

    public interface LongClickListener {
        boolean onArrowLongClick();
        boolean onFoulsLongClick(boolean left);
        boolean onMainTimeLongClick();
        boolean onNameLongClick(boolean left);
        boolean onPeriodLongClick();
        boolean onScoreLongClick(boolean left);
        boolean onShotTimeLongClick();
        boolean onTimeoutsLongClick(boolean left);
        boolean onTimeouts20LongClick(boolean left);
    }

    public enum ICONS {
        HORN, WHISTLE, CAMERA, TIMEOUT, NEW_PERIOD, SWITCH_SIDES
    }

    public StandardLayout(Context context, Preferences preferences,
                          ClickListener clickListener, LongClickListener longClickListener) {
        super(context);
        this.preferences = preferences;
        layoutType = preferences.layoutType;
        timeoutRules = preferences.timeoutRules;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        LayoutInflater.from(context).inflate(R.layout.activity_main, this);
        switch (layoutType) {
            case COMMON:
            case FIBA:
            case NBA:
                LayoutInflater.from(context).inflate(R.layout.activity_main, this);
                initExtended();
                break;
            case SIMPLE:
                LayoutInflater.from(context).inflate(R.layout.activity_main, this);
                initSimple();
                break;
        }
        init();
    }

    private StandardLayout init() {
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
//        stub.setLayoutResource(R.layout.board_layout);
        stub.setLayoutResource(R.layout.board_central);
        stub.inflate();

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

        findViewById(R.id.leftMinus1View).setOnClickListener(this);
        findViewById(R.id.rightMinus1View).setOnClickListener(this);
        findViewById(R.id.leftPlus1View).setOnClickListener(this);
        findViewById(R.id.rightPlus1View).setOnClickListener(this);
        findViewById(R.id.leftPlus3View).setOnClickListener(this);
        findViewById(R.id.rightPlus3View).setOnClickListener(this);

        findViewById(R.id.timeoutIconView).setOnClickListener(this);
        findViewById(R.id.cameraView).setOnClickListener(this);
        findViewById(R.id.switchSidesView).setOnClickListener(this);

        View whistleView = findViewById(R.id.whistleView);
        View hornView = findViewById(R.id.hornView);
        hornView.setOnClickListener(this);
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
//
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
        initArrows();
        setColors();
        return this;
    }

    private void initExtended() {
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource(preferences.timeoutRules == Game.TO_RULES.NBA ? R.layout.full_bottom_nba : R.layout.full_bottom_simple);
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

        if (preferences.enableShotTime) {
            shotTimeView.setOnClickListener(this);
            shotTimeView.setOnLongClickListener(this);
            shotTimeSwitchView.setOnClickListener(this);
            shotTimeSwitchView.setText(Long.toString(preferences.shortShotTimePref / 1000));
        } else {
            try {
                shotTimeView.setVisibility(View.INVISIBLE);
                shotTimeSwitchView.setVisibility(View.INVISIBLE);
            } catch (NullPointerException e) {
                Log.e(TAG, "shotTimeView or shotTimeSwitchView is null");
            }
        }
        initTimeouts();
    }

    private void initSimple() {
        ImageView startNewPeriodView = (ImageView) findViewById(R.id.newPeriodIconView);
        startNewPeriodView.setOnClickListener(this);
        preferences.enableShotTime = false;
    }

    private void initTimeouts() {
        hTimeoutsView = (TextView) findViewById(R.id.leftTimeoutsView);
        gTimeoutsView = (TextView) findViewById(R.id.rightTimeoutsView);
        hTimeoutsView.setOnClickListener(this);
        gTimeoutsView.setOnClickListener(this);
        hTimeoutsView.setOnLongClickListener(this);
        gTimeoutsView.setOnLongClickListener(this);
        if (preferences.timeoutRules == Game.TO_RULES.NONE) {
            ((TextView) findViewById(R.id.leftTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
            ((TextView) findViewById(R.id.rightTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
        } else if (preferences.timeoutRules == Game.TO_RULES.NBA) {
            hTimeouts20View = (TextView) findViewById(R.id.leftTimeouts20View);
            gTimeouts20View = (TextView) findViewById(R.id.rightTimeouts20View);
            hTimeouts20View.setOnClickListener(this);
            hTimeouts20View.setOnLongClickListener(this);
            gTimeouts20View.setOnClickListener(this);
            gTimeouts20View.setOnLongClickListener(this);
        }
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

    public void handleArrowsVisibility() {
        if (preferences.arrowsOn) {
            showArrows();
        } else {
            hideArrows();
        }
    }

    public void hideArrows() {
        leftArrow.setVisibility(View.GONE);
        rightArrow.setVisibility(View.GONE);
    }

    public void showArrows() {
        leftArrow.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(View.VISIBLE);
    }

//    @Override
//    public void onClick(View v) {
//        if (preferences.vibrationOn) {
//            vibrator.vibrate(100);
//        }
//        switch (v.getId()) {
//            case R.id.leftScoreView:
//                changeScore(LEFT, 2);
//                break;
//            case R.id.rightScoreView:
//                changeScore(RIGHT, 2);
//                break;
//            case R.id.mainTimeView:
//                mainTimeClick();
//                break;
//            case R.id.shotTimeView:
//                shotTimeClick();
//                break;
//            case R.id.shotTimeSwitch:
//                shotTimeSwitchClick();
//                break;
//            case R.id.leftPlus1View:
//                changeScore(LEFT, 1);
//                break;
//            case R.id.rightPlus1View:
//                changeScore(RIGHT, 1);
//                break;
//            case R.id.leftPlus3View:
//                changeScore(LEFT, 3);
//                break;
//            case R.id.rightPlus3View:
//                changeScore(RIGHT, 3);
//                break;
//            case R.id.leftMinus1View:
//                if (hScore > 0) { changeScore(LEFT, -1); }
//                break;
//            case R.id.rightMinus1View:
//                if (gScore > 0) { changeScore(RIGHT, -1); }
//                break;
//            case R.id.periodView:
//                newPeriod(true);
//                break;
//            case R.id.timeoutIconView:
//                showListDialog("timeout");
//                break;
//            case R.id.newPeriodIconView:
//                showListDialog("new_period");
//                break;
//            case R.id.leftFoulsView:
//                foul(LEFT);
//                break;
//            case R.id.rightFoulsView:
//                foul(RIGHT);
//                break;
//            case R.id.leftTimeoutsView:
//                timeout(LEFT);
//                break;
//            case R.id.rightTimeoutsView:
//                timeout(RIGHT);
//                break;
//            case R.id.leftTimeouts20View:
//                timeout20(LEFT);
//                break;
//            case R.id.rightTimeouts20View:
//                timeout20(RIGHT);
//                break;
//            case R.id.cameraView:
//                if (checkCameraHardware(this)) { runCameraActivity(); }
//                break;
//            case R.id.switchSidesView:
//                switchSides();
//                break;
//            case R.id.left_panel_toggle:
//                showSidePanels(SIDE_PANELS_LEFT);
//                break;
//            case R.id.right_panel_toggle:
//                showSidePanels(SIDE_PANELS_RIGHT);
//                break;
//            case R.id.leftNameView:
//            case R.id.leftArrowView:
//                if (preferences.arrowsOn) { toggleArrow(HOME); }
//                break;
//            case R.id.rightNameView:
//            case R.id.rightArrowView:
//                if (preferences.arrowsOn) { toggleArrow(GUEST); }
//                break;
//            default:
//                break;
//        }
//    }

//    @Override
//    public boolean onLongClick(View v) {
//        if (preferences.vibrationOn) { vibrator.vibrate(longClickVibrationPattern, -1); }
//
//        if (!mainTimerOn) {
//            switch (v.getId()) {
//                case R.id.leftScoreView:
//                    nullScore(LEFT);
//                    return true;
//                case R.id.rightScoreView:
//                    nullScore(RIGHT);
//                    return true;
//                case R.id.mainTimeView:
//                    showMainTimePicker();
//                    return true;
//                case R.id.shotTimeView:
//                    showShotTimePicker();
//                    return true;
//                case R.id.periodView:
//                    newPeriod(false);
//                    return true;
//                case R.id.leftFoulsView:
//                    nullFouls(LEFT);
//                    return true;
//                case R.id.rightFoulsView:
//                    nullFouls(RIGHT);
//                    return true;
//                case R.id.leftTimeoutsView:
//                    nullTimeouts(LEFT);
//                    return true;
//                case R.id.rightTimeoutsView:
//                    nullTimeouts(RIGHT);
//                    return true;
//                case R.id.leftTimeouts20View:
//                    nullTimeouts20(LEFT);
//                    return true;
//                case R.id.rightTimeouts20View:
//                    nullTimeouts20(RIGHT);
//                    return true;
//                case R.id.leftNameView:
//                    chooseTeamNameDialog("home", hName);
//                    return true;
//                case R.id.rightNameView:
//                    chooseTeamNameDialog("guest", gName);
//                    return true;
//                case R.id.leftArrowView:
//                case R.id.rightArrowView:
//                    clearPossession();
//                    return true;
//                default:
//                    return true;
//            }
//        }
//        return false;
//    }

//    private void mainTimeClick() {
//        if (!mainTimerOn) {
//            if (preferences.useDirectTimer) {
//                startDirectTimer();
//            } else {
//                startMainCountDownTimer();
//            }
//        } else {
//            pauseGame();
//        }
//    }
//
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

//    private void setSavedState() {
//        setMainTimeText(mainTime);
//        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, hScore));
//        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, gScore));
//        setTeamNames();
//
//        if (preferences.layoutType == LAYOUT_FULL) {
//            if (preferences.enableShotTime) {
//                setShotTimeText(shotTime);
//            }
//            hFoulsView.setText(Short.toString(hFouls));
//            gFoulsView.setText(Short.toString(gFouls));
//            setPeriod();
//            setTimeouts();
//            hTimeoutsView.setText(Short.toString(hTimeouts));
//            gTimeoutsView.setText(Short.toString(gTimeouts));
//            if (preferences.timeoutRules == TO_RULES_NBA) {
//                hTimeouts20View.setText(Short.toString(hTimeouts20));
//                gTimeouts20View.setText(Short.toString(gTimeouts20));
//            }
//        }
//        if (preferences.arrowsOn) { toggleArrow(possession); }
//    }

    public void zeroState() {
        mainTimeFormat = TIME_FORMAT;
        setMainTimeText(preferences.useDirectTimer ? 0 : preferences.mainTimePref);
//        nullScore(LEFT);
//        nullScore(RIGHT);
//        setTeamNames();
        if (preferences.layoutType == Game.GAME_TYPE.COMMON) {
            if (preferences.enableShotTime) {
                setShotTimeText(preferences.shotTimePref, false);
                shotTimeView.setVisibility(View.VISIBLE);
                shotTimeSwitchView.setVisibility(View.VISIBLE);
            }
            nullTimeouts();
            nullFouls();
            periodView.setText("1");
            if (preferences.timeoutRules == Game.TO_RULES.NBA) {
                nullTimeouts20();
            }
        }
        if (preferences.arrowsOn) { clearPossession(); }
        if (preferences.fixLandscapeChanged) {
//            handleOrientation();
            preferences.fixLandscapeChanged = false;
        }
    }

//    public void newPeriod(boolean next) {
//        pauseGame();
//        changedUnder2Minutes = false;
//        if (next) {
//            period++;
//        } else {
//            period = 1;
//        }
//        setPeriod();
//        if (preferences.enableShotTime) {
//            shotTime = preferences.shotTimePref;
//            setShotTimeText(shotTime);
//            shotTimeView.setVisibility(View.VISIBLE);
//            shotTimeSwitchView.setVisibility(View.VISIBLE);
//            shotTickInterval = SECOND;
//        }
//        mainTickInterval = SECOND;
//        mainTimeFormat = TIME_FORMAT;
//        setMainTimeText(mainTime);
//        if (period <= preferences.numRegularPeriods) {
//            nullFouls();
//        }
//        setTimeouts();
//        saveResult();
//        scoreSaved = false;
//        if (period == 3 && preferences.autoSwitchSides) {
//            switchSides();
//        }
//    }

    public void switchSides() {
        TextView _NameView = hNameView;
        hNameView = gNameView;
        gNameView = _NameView;
        setTeamNames(gNameView.getText(), hNameView.getText());

        TextView _ScoreView = hScoreView;
        hScoreView = gScoreView;
        gScoreView = _ScoreView;
        setScores(gScoreView.getText(), hScoreView.getText());

        if (layoutType == Game.GAME_TYPE.COMMON) {
            TextView _FoulsView = hFoulsView;
            hFoulsView = gFoulsView;
            gFoulsView = _FoulsView;
            setFouls(gFoulsView.getText(), hFoulsView.getText(), gFoulsView.getCurrentTextColor(), hFoulsView.getCurrentTextColor());

            TextView _TimeoutsView = hTimeoutsView;
            hTimeoutsView = gTimeoutsView;
            gTimeoutsView = _TimeoutsView;
            setTimeouts(gTimeoutsView.getText(), hTimeoutsView.getText(), gTimeoutsView.getCurrentTextColor(), hTimeoutsView.getCurrentTextColor());

            if (timeoutRules == Game.TO_RULES.NBA) {
                TextView _Timeouts20View = hTimeouts20View;
                hTimeouts20View = gTimeouts20View;
                gTimeouts20View = _Timeouts20View;
                setTimeouts20(gTimeouts20View.getText(), hTimeouts20View.getText(), gTimeouts20View.getCurrentTextColor(), hTimeouts20View.getCurrentTextColor());
            }
        }

//        if (preferences.arrowsOn) {
//            switchPossession();
//        }

        leftIsHome = !leftIsHome;
    }

    public void switchPossession(int team) {
        if (leftArrow != null && rightArrow != null) {
            team = 1 - team;
            if (team == HOME) {
                leftArrow.setFill();
                rightArrow.setStroke();
            } else if (team == GUEST) {
                rightArrow.setFill();
                leftArrow.setStroke();
            }
        }
    }


    // scores
    public void setScores(short home, short guest) {
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, home));
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, guest));
    }

    public void setScores(CharSequence home, CharSequence guest) {
        hScoreView.setText(home);
        gScoreView.setText(guest);
    }

    public void setScore(boolean left, int value) {
        if (left == leftIsHome) {
            setHomeScore(value);
        } else {
            setGuestScore(value);
        }
    }

    public void setGuestScore(int value) {
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
        handleScoreViewSize();
    }

    public void setHomeScore(int value) {
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
        handleScoreViewSize();
    }

    public void nullScores() {
        hScoreView.setText("00");
        gScoreView.setText("00");
        handleScoreViewSize();
    }

    public void handleScoreViewSize() {
//        if (gScore >= 100 || hScore >= 100) {
//            if (scoreViewSize == 0) {
//                scoreViewSize = getResources().getDimension(R.dimen.score_size);
//            }
//            hScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize * 0.75f);
//            gScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize * 0.75f);
//        } else {
//            if (scoreViewSize != 0) {
//                hScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize);
//                gScoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX, scoreViewSize);
//            }
//        }
    }


    // fouls
    public void setFouls(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hFoulsView.setText(hValue);
        gFoulsView.setText(gValue);
        hFoulsView.setTextColor(hColor);
        gFoulsView.setTextColor(gColor);
    }

    public void setHomeFoul(String value, boolean limit) {
        hFoulsView.setText(value);
        if (limit) {
            setColorRed(hFoulsView);
        }
    }

    public void setGuestFoul(String value, boolean limit) {
        gFoulsView.setText(value);
        if (limit) {
            setColorRed(gFoulsView);
        }
    }

    public void nullFouls() {
        hFoulsView.setText("0");
        gFoulsView.setText("0");
        setColorGreen(hFoulsView);
        setColorGreen(gFoulsView);
    }

    public void nullHomeFouls() {
        hFoulsView.setText("0");
        setColorGreen(hFoulsView);
    }

    public void nullGuestFouls() {
        gFoulsView.setText("0");
        setColorGreen(gFoulsView);
    }


    // timeouts
    public void setTimeouts(short hValue, short gValue, int hColor, int gColor) {
        hTimeoutsView.setText(Short.toString(hValue));
        gTimeoutsView.setText(Short.toString(gValue));
        hTimeoutsView.setTextColor(hColor);
        gTimeoutsView.setTextColor(gColor);
    }

    public void setTimeouts(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hTimeoutsView.setText(hValue);
        gTimeoutsView.setText(gValue);
        hTimeoutsView.setTextColor(hColor);
        gTimeoutsView.setTextColor(gColor);
    }

    public void setTimeouts20(short hValue, short gValue, int hColor, int gColor) {
        hTimeouts20View.setText(Short.toString(hValue));
        gTimeouts20View.setText(Short.toString(gValue));
        hTimeouts20View.setTextColor(hColor);
        gTimeouts20View.setTextColor(gColor);
    }

    public void setTimeouts20(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hTimeouts20View.setText(hValue);
        gTimeouts20View.setText(gValue);
        hTimeouts20View.setTextColor(hColor);
        gTimeouts20View.setTextColor(gColor);
    }

    public void nullAllTimeouts() {
        hTimeoutsView.setText("0");
        gTimeoutsView.setText("0");
        hTimeouts20View.setText("0");
        gTimeouts20View.setText("0");
    }

    public void nullHomeTimeouts(String value) {
        hTimeoutsView.setText(value);
        setColorGreen(hTimeoutsView);
    }

    public void nullGuestTimeouts(String value) {
        gTimeoutsView.setText(value);
        setColorGreen(gTimeoutsView);
    }

    public void nullHomeTimeouts20(String value) {
        hTimeouts20View.setText(value);
        setColorGreen(hTimeouts20View);
    }

    public void nullGuestTimeouts20(String value) {
        gTimeouts20View.setText(value);
        setColorGreen(gTimeouts20View);
    }

    public void setHomeTimeouts(String value, boolean limit) {
        hTimeoutsView.setText(value);
        if (limit) {
            setColorRed(hTimeoutsView);
        }
    }

    public void setGuestTimeouts(String value, boolean limit) {
        gTimeoutsView.setText(value);
        if (limit) {
            setColorRed(gTimeoutsView);
        }
    }

    public void setHomeTimeouts20(String value, boolean limit) {
        hTimeouts20View.setText(value);
        if (limit) {
            setColorRed(hTimeouts20View);
        }
    }

    public void setGuestTimeouts20(String value, boolean limit) {
        gTimeouts20View.setText(value);
        if (limit) {
            setColorRed(gTimeouts20View);
        }
    }

    public void nullTimeouts() {
        hTimeoutsView.setText("0");
        gTimeoutsView.setText("0");
    }

    public void nullTimeouts20() {
        hTimeouts20View.setText("0");
        gTimeouts20View.setText("0");
    }


    // possession
    public void toggleArrow(boolean left) {
        if (leftArrow != null && rightArrow != null) {
            if (left) {
                leftArrow.setFill();
                rightArrow.setStroke();
            } else {
                rightArrow.setFill();
                leftArrow.setStroke();
            }
        }
    }

    public void clearPossession() {
        if (leftArrow != null && rightArrow != null) {
            leftArrow.setStroke();
            rightArrow.setStroke();
        }
    }


    // times
    public void setMainTimeText(long millis) {
        mainTimeView.setText(mainTimeFormat.format(millis).replaceAll(API16_TIME_REGEX, "$1"));
    }

    public void setShotTimeText(long value, boolean millis) {
        if (millis) {
            shotTimeView.setText(String.format(TIME_FORMAT_SHORT, value / 1000, value % 1000 / 100));
        } else {
            shotTimeView.setText(String.format(FORMAT_TWO_DIGITS, (short) Math.ceil(value / 1000.0)));
        }
    }

    public void hideShotTime() {
        shotTimeView.setVisibility(View.INVISIBLE);
    }

    public void showShotTime() {
        shotTimeView.setVisibility(View.VISIBLE);
    }

    public void hideShotTimeSwitch() {
        shotTimeSwitchView.setVisibility(View.INVISIBLE);
    }

    public void showShotTimeSwitch() {
        shotTimeSwitchView.setVisibility(View.VISIBLE);
    }


    // period
    public void setPeriod(int value, boolean regular) {
        if (regular) {
            periodView.setText(Integer.toString(value));
//            if (periodViewSize != 0) {
//                periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize);
//            }
        } else {
            periodView.setText(String.format("OT%d", value));
//            if (periodViewSize == 0) {
//                periodViewSize = getResources().getDimension(R.dimen.bottom_line_size);
//            }
//            periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize * 0.75f);
        }
    }


    // names
    public void setTeamNames(String home, String guest) {
        hNameView.setText(home);
        gNameView.setText(guest);
    }

    public void setTeamNames(CharSequence home, CharSequence guest) {
        hNameView.setText(home);
        gNameView.setText(guest);
    }

    public void setHomeName(CharSequence value) {
        hNameView.setText(value);
    }

    public void setGuestName(CharSequence value) {
        gNameView.setText(value);
    }

    public void setColors() {
        if (hScoreView != null) {
            hScoreView.setTextColor(preferences.getColor(Preferences.Elements.HSCORE));
        }
        if (gScoreView != null) {
            hScoreView.setTextColor(preferences.getColor(Preferences.Elements.GSCORE));
        }
    }

    public void setColor(TextView v, int color) {
        v.setTextColor(color);
    }

    public void setColorRed(TextView v) {
        setColor(v, getResources().getColor(R.color.red));
    }

    private void setColorGreen(TextView v) {
        setColor(v, getResources().getColor(R.color.green));
    }
}
