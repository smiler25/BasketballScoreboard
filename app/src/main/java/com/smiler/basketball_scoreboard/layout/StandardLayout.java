package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.Level;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.Rules;
import com.smiler.basketball_scoreboard.elements.TriangleView;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

import static com.smiler.basketball_scoreboard.Constants.API16_TIME_REGEX;
import static com.smiler.basketball_scoreboard.Constants.FORMAT_TWO_DIGITS;
import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.LONG_CLICK_VIBE_PAT;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_SHORT;

public class StandardLayout extends BaseLayout implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "BS-StandardLayout";
    private final Preferences preferences;
    private TextView mainTimeView, shotTimeView, shotTimeSwitchView, periodView;
    private TextView hNameView, gNameView;
    private TextView hScoreView, gScoreView;
    private TextView hTimeoutsView, gTimeoutsView;
    private TextView hTimeouts20View, gTimeouts20View;
    private TextView hFoulsView, gFoulsView;
    private TriangleView leftArrow, rightArrow;
    private Rules.TO_RULES timeoutRules;
    private GAME_LAYOUT layoutType;
    private ClickListener clickListener;
    private LongClickListener longClickListener;
    private boolean blockLongClick;
    private float periodViewSize, scoreViewSize;
    private Animation shotTimeBlinkAnimation = new AlphaAnimation(1, 0);
    private Vibrator vibrator;
    private ViewGroup leftPlayersButtonsGroup, rightPlayersButtonsGroup;
    private ArrayList<View> leftPlayersButtons = new ArrayList<>();
    private ArrayList<View> rightPlayersButtons = new ArrayList<>();

    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;

    private Button longClickPlayerBu;

    @Override
    public void onClick(View v) {
        if (preferences.vibrationOn) {
//            && vibrator.hasVibrator()
            vibrator.vibrate(100);
        }
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
                break;
            case R.id.left_panel_toggle:
                clickListener.onOpenPanelClick(LEFT);
                break;
            case R.id.right_panel_toggle:
                clickListener.onOpenPanelClick(RIGHT);
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
        if (preferences.vibrationOn) {
            vibrator.vibrate(LONG_CLICK_VIBE_PAT, -1);
        }

        if (blockLongClick) {
            return true;
        }
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
        switch (layoutType) {
            case COMMON:
                LayoutInflater.from(context).inflate(R.layout.board_layout, this);
                initExtended();
                break;
            case SIMPLE:
                LayoutInflater.from(context).inflate(R.layout.board_layout_simple, this);
                initSimple();
                break;
        }
        init();
        shotTimeBlinkAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private StandardLayout init() {
        mainTimeView = findViewById(R.id.mainTimeView);
        hScoreView = findViewById(R.id.leftScoreView);
        gScoreView = findViewById(R.id.rightScoreView);
        hNameView = findViewById(R.id.leftNameView);
        gNameView = findViewById(R.id.rightNameView);

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

        initScoreChangers();
        initIcons();
        initArrows();
        setColors();
        if (preferences.spOn) {
            initPlayersButtons();
        }
        return this;
    }

    private void initExtended() {
        ViewStub stub = findViewById(R.id.bottom_line_stub);
        stub.setLayoutResource(preferences.timeoutRules == Rules.TO_RULES.NBA ? R.layout.full_bottom_nba : R.layout.full_bottom_simple);
        stub.inflate();

        shotTimeView = findViewById(R.id.shotTimeView);
        shotTimeSwitchView = findViewById(R.id.shotTimeSwitch);
        if (preferences.enableShotTime) {
            shotTimeView.setOnClickListener(this);
            shotTimeView.setOnLongClickListener(this);
            shotTimeSwitchView.setOnClickListener(this);
            shotTimeSwitchView.setText(Long.toString(preferences.shortShotTimePref / 1000));
        } else {
            try {
                shotTimeView.setVisibility(View.INVISIBLE);
                shotTimeSwitchView.setVisibility(View.GONE);
            } catch (NullPointerException e) {
                Log.e(TAG, "shotTimeView or shotTimeSwitchView is null");
            }
        }

        periodView = findViewById(R.id.periodView);
        periodView.setOnClickListener(this);
        periodView.setOnLongClickListener(this);

        initFouls();
        initTimeouts();
    }

    private void initSimple() {
        ImageView startNewPeriodView = findViewById(R.id.newPeriodIconView);
        startNewPeriodView.setOnClickListener(this);
        preferences.enableShotTime = false;
    }

    private void initFouls() {
        hFoulsView = findViewById(R.id.leftFoulsView);
        gFoulsView = findViewById(R.id.rightFoulsView);
        hFoulsView.setOnClickListener(this);
        gFoulsView.setOnClickListener(this);
        hFoulsView.setOnLongClickListener(this);
        gFoulsView.setOnLongClickListener(this);
    }

    private void initTimeouts() {
        hTimeoutsView = findViewById(R.id.leftTimeoutsView);
        gTimeoutsView = findViewById(R.id.rightTimeoutsView);
        hTimeoutsView.setOnClickListener(this);
        gTimeoutsView.setOnClickListener(this);
        hTimeoutsView.setOnLongClickListener(this);
        gTimeoutsView.setOnLongClickListener(this);
        if (preferences.timeoutRules == Rules.TO_RULES.NONE) {
            ((TextView) findViewById(R.id.leftTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
            ((TextView) findViewById(R.id.rightTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
        } else if (preferences.timeoutRules == Rules.TO_RULES.NBA) {
            hTimeouts20View = findViewById(R.id.leftTimeouts20View);
            gTimeouts20View = findViewById(R.id.rightTimeouts20View);
            hTimeouts20View.setOnClickListener(this);
            hTimeouts20View.setOnLongClickListener(this);
            gTimeouts20View.setOnClickListener(this);
            gTimeouts20View.setOnLongClickListener(this);
        }
    }

    private void initIcons() {
        findViewById(R.id.timeoutIconView).setOnClickListener(this);
        findViewById(R.id.cameraView).setOnClickListener(this);
        findViewById(R.id.switchSidesView).setOnClickListener(this);

        View whistleView = findViewById(R.id.whistleView);
        View hornView = findViewById(R.id.hornView);
        hornView.setOnClickListener(this);
        hornView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        clickListener.onHornAction(false);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        clickListener.onHornAction(true);
                        break;
                }
                return true;
            }
        });

        whistleView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        clickListener.onWhistleAction(false);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        clickListener.onWhistleAction(true);
                        break;
                }
                return true;
            }
        });
    }

    private void initScoreChangers() {
        findViewById(R.id.leftMinus1View).setOnClickListener(this);
        findViewById(R.id.rightMinus1View).setOnClickListener(this);
        findViewById(R.id.leftPlus1View).setOnClickListener(this);
        findViewById(R.id.rightPlus1View).setOnClickListener(this);
        findViewById(R.id.leftPlus3View).setOnClickListener(this);
        findViewById(R.id.rightPlus3View).setOnClickListener(this);
    }

    private void initPlayersButtons() {
        try {
            ViewStub leftPlayersStub = findViewById(R.id.left_panel_stub);
            ViewStub rightPlayersStub = findViewById(R.id.right_panel_stub);
            leftPlayersStub.setLayoutResource(R.layout.sp_left_buttons);
            leftPlayersStub.inflate();
            rightPlayersStub.setLayoutResource(R.layout.sp_right_buttons);
            rightPlayersStub.inflate();
            findViewById(R.id.left_panel_toggle).setOnClickListener(this);
            findViewById(R.id.right_panel_toggle).setOnClickListener(this);

            leftPlayersButtonsGroup = findViewById(R.id.left_panel);
            leftPlayersButtons = getAllButtons(leftPlayersButtonsGroup);
            for (View bu : leftPlayersButtons) {
                attachLeftButton(bu);
            }

            rightPlayersButtonsGroup = findViewById(R.id.right_panel);
            rightPlayersButtons = getAllButtons(rightPlayersButtonsGroup);
            for (View bu : rightPlayersButtons) {
                attachRightButton(bu);
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Unable to initiate side panels");
        }
    }

    private void initArrows() {
        try {
            leftArrow = findViewById(R.id.leftArrowView);
            rightArrow = findViewById(R.id.rightArrowView);
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

    public void switchSides() {
        TextView _NameView = hNameView;
        hNameView = gNameView;
        gNameView = _NameView;
        setTeamNames(gNameView.getText(), hNameView.getText());

        TextView _ScoreView = hScoreView;
        hScoreView = gScoreView;
        gScoreView = _ScoreView;
        setScores(gScoreView.getText(), hScoreView.getText());

        if (layoutType != BaseLayout.GAME_LAYOUT.SIMPLE) {
            TextView _FoulsView = hFoulsView;
            hFoulsView = gFoulsView;
            gFoulsView = _FoulsView;
            setFouls(gFoulsView.getText(), hFoulsView.getText(), gFoulsView.getCurrentTextColor(), hFoulsView.getCurrentTextColor());

            TextView _TimeoutsView = hTimeoutsView;
            hTimeoutsView = gTimeoutsView;
            gTimeoutsView = _TimeoutsView;
            setTimeouts(gTimeoutsView.getText(), hTimeoutsView.getText(), gTimeoutsView.getCurrentTextColor(), hTimeoutsView.getCurrentTextColor());

            if (timeoutRules == Rules.TO_RULES.NBA) {
                TextView _Timeouts20View = hTimeouts20View;
                hTimeouts20View = gTimeouts20View;
                gTimeouts20View = _Timeouts20View;
                setTimeouts20(gTimeouts20View.getText(), hTimeouts20View.getText(), gTimeouts20View.getCurrentTextColor(), hTimeouts20View.getCurrentTextColor());
            }
        }
        setColors();
    }

    public void setBlockLongClick(boolean state) {
        blockLongClick = state;
    }

    public void setColors() {
        if (hScoreView != null) {
            hScoreView.setTextColor(preferences.getColor(Preferences.Elements.HSCORE));
        }
        if (gScoreView != null) {
            gScoreView.setTextColor(preferences.getColor(Preferences.Elements.GSCORE));
        }
        if (mainTimeView != null) {
            mainTimeView.setTextColor(preferences.getColor(Preferences.Elements.MAIN_TIME));
        }
        if (shotTimeView != null) {
            shotTimeView.setTextColor(preferences.getColor(Preferences.Elements.SHOT_TIME));
        }
    }


    // scores
    public void setScores(CharSequence home, CharSequence guest) {
        hScoreView.setText(home);
        gScoreView.setText(guest);
        handleScoresSize();
    }

    public void setGuestScore(int value) {
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
        handleScoresSize();
    }

    public void setHomeScore(int value) {
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
        handleScoresSize();
    }

    public void handleScoresSize() {
        if (hScoreView.getText().length() >= 3 || gScoreView.getText().length() >= 3) {
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


    // fouls
    public void setFouls(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hFoulsView.setText(hValue);
        gFoulsView.setText(gValue);
        hFoulsView.setTextColor(hColor);
        gFoulsView.setTextColor(gColor);
    }

    public void setHomeFoul(String value, Level level) {
        hFoulsView.setText(value);
        if (level == Level.LIMIT) {
            setColorRed(hFoulsView);
        } else if (level == Level.WARN) {
            setColorOrange(hFoulsView);
        }
    }

    public void setGuestFoul(String value, Level level) {
        gFoulsView.setText(value);
        if (level == Level.LIMIT) {
            setColorRed(gFoulsView);
        } else if (level == Level.WARN) {
            setColorOrange(gFoulsView);
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

    public void setHomeFoulsGreen() {
        setColorGreen(hFoulsView);
    }

    public void setGuestFoulsGreen() {
        setColorGreen(gTimeoutsView);
    }


    // timeouts
    public void setTimeouts(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hTimeoutsView.setText(hValue);
        gTimeoutsView.setText(gValue);
        hTimeoutsView.setTextColor(hColor);
        gTimeoutsView.setTextColor(gColor);
    }

    public void setTimeouts20(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
        hTimeouts20View.setText(hValue);
        gTimeouts20View.setText(gValue);
        hTimeouts20View.setTextColor(hColor);
        gTimeouts20View.setTextColor(gColor);
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

    public void setHomeTimeoutsGreen() {
        setColorGreen(hTimeoutsView);
    }

    public void setGuestTimeoutsGreen() {
        setColorGreen(gTimeoutsView);
    }

    public void setHomeTimeouts20Green() {
        setColorGreen(hTimeouts20View);
    }

    public void setGuestTimeouts20Green() {
        setColorGreen(gTimeouts20View);
    }

    public void nullTimeouts() {
        hTimeoutsView.setText("0");
        gTimeoutsView.setText("0");
        setColorGreen(hTimeoutsView);
        setColorGreen(gTimeoutsView);
    }

    public void nullTimeouts20() {
        hTimeouts20View.setText("0");
        gTimeouts20View.setText("0");
        setColorGreen(hTimeouts20View);
        setColorGreen(gTimeouts20View);
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
    public void setMainTimeFormat(SimpleDateFormat value) {
        mainTimeFormat = value;
    }

    public void setMainTimeText(long millis) {
        mainTimeView.setText(mainTimeFormat.format(millis).replaceAll(API16_TIME_REGEX, "$1"));
    }

    public void setShotTimeText(long value) {
        if (value < 5000 && preferences.fractionSecondsShot) {
            shotTimeView.setText(String.format(TIME_FORMAT_SHORT, value / 1000, value % 1000 / 100));
        } else {
            shotTimeView.setText(String.format(FORMAT_TWO_DIGITS, (short) Math.ceil(value / 1000.0)));
        }
    }

    public void setShotTimeSwitchText(long value) {
        shotTimeSwitchView.setText(Long.toString(value));
    }

    public void hideShotTime() {
        if (shotTimeView != null) {
            shotTimeView.setVisibility(View.INVISIBLE);
        }
    }

    public void showShotTime() {
        if (shotTimeView != null) {
            shotTimeView.setVisibility(View.VISIBLE);
        }
    }

    public void blinkShotTime() {
        shotTimeView.startAnimation(shotTimeBlinkAnimation);

    }

    public boolean shotTimeVisible() {
        return shotTimeView.getVisibility() == View.VISIBLE;
    }

    public boolean shotTimeSwitchVisible() {
        return shotTimeSwitchView.getVisibility() == View.VISIBLE;
    }

    public void hideShotTimeSwitch() {
        if (shotTimeSwitchView != null) {
            shotTimeSwitchView.setVisibility(View.GONE);
        }
    }

    public void showShotTimeSwitch() {
        if (shotTimeSwitchView != null) {
            shotTimeSwitchView.setVisibility(View.VISIBLE);
        }
    }


    // period
    public void setPeriod(String value, boolean regular) {
        if (periodView != null) {
            if (regular) {
                periodView.setText(value);
                if (periodViewSize != 0) {
                    periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize);
                }
            } else {
                periodView.setText("OT" + value);
                if (periodViewSize == 0) {
                    periodViewSize = getResources().getDimension(R.dimen.bottom_line_size);
                }
                periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize * 0.75f);
            }
        }
    }


    // names
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


    // players
    private void attachLeftButton(View button) {
        button.setOnClickListener(v -> clickListener.onPlayerButtonClick(LEFT, (SidePanelRow) v.getTag()));
        button.setOnLongClickListener(v -> {
            longClickPlayerBu = (Button) v;
            longClickListener.onPlayerButtonLongClick(LEFT);
            return false;
        });
    }

    private void attachRightButton(View button) {
        button.setOnClickListener(v -> clickListener.onPlayerButtonClick(RIGHT, (SidePanelRow) v.getTag()));
        button.setOnLongClickListener(v -> {
            longClickPlayerBu = (Button) v;
            longClickListener.onPlayerButtonLongClick(RIGHT);
            return false;
        });
    }

    public boolean playersButtonsInitiated() {
        return leftPlayersButtonsGroup != null && rightPlayersButtonsGroup != null;
    }

    public void hidePlayersButtons() {
        if (playersButtonsInitiated()) {
            leftPlayersButtonsGroup.setVisibility(View.GONE);
            rightPlayersButtonsGroup.setVisibility(View.GONE);
        }
    }

    public void showPlayersButtons() {
        if (playersButtonsInitiated()) {
            leftPlayersButtonsGroup.setVisibility(View.VISIBLE);
            rightPlayersButtonsGroup.setVisibility(View.VISIBLE);
        } else {
            initPlayersButtons();
        }
    }

    public void setPlayersButtons(boolean left, TreeSet<SidePanelRow> rows) {
        ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
        int pos = 0;
        for (SidePanelRow row : rows) {
            View bu = group.get(pos++);
            ((Button) bu).setText(Integer.toString(row.getNumber()));
            bu.setTag(row);
        }
    }

    public void setPlayersButtonsEmpty(boolean left) {
        ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
        for (View bu : group) {
            ((Button) bu).setText(R.string.minus);
            bu.setTag(null);
        }
    }

    public void updatePlayerButton(boolean left, int id, int number) {
        ArrayList<View> group = left ? leftPlayersButtons : rightPlayersButtons;
        for (View bu : group) {
            SidePanelRow row = (SidePanelRow) bu.getTag();
            if (row != null && row.getId() == id) {
                ((Button) bu).setText(Integer.toString(number));
                break;
            }
        }
    }

    public void clearPlayerButton(boolean left, int id) {
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

    public Button getSelectedPlayerButton() {
        return longClickPlayerBu;
    }

    public SidePanelRow substitutePlayer(SidePanelRow newTag, int newNumber) {
        SidePanelRow old = (SidePanelRow) longClickPlayerBu.getTag();
        longClickPlayerBu.setTag(newTag);
        longClickPlayerBu.setText(Integer.toString(newNumber));
        return old;
    }
}
