package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.smiler.basketball_scoreboard.Constants.API16_TIME_REGEX;
import static com.smiler.basketball_scoreboard.Constants.FORMAT_TWO_DIGITS;
import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;

public class SimpleLayout extends LinearLayout implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "BS-SimpleLayout";
    private final Preferences preferences;
    private TextView mainTimeView;
    private TextView hNameView, gNameView;
    private TextView hScoreView, gScoreView;
    private ClickListener clickListener;
    private LongClickListener longClickListener;
    private boolean blockLongClick;
    private float scoreViewSize;
    private Vibrator vibrator;
    private long[] longClickVibrationPattern = {0, 50, 50, 50};
    private ViewGroup leftPlayersButtonsGroup, rightPlayersButtonsGroup;
    private ArrayList<View> leftPlayersButtons = new ArrayList<>();
    private ArrayList<View> rightPlayersButtons = new ArrayList<>();

    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;

    private Button longClickPlayerBu;

    @Override
    public void onClick(View v) {
        if (preferences.vibrationOn) {
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
            case R.id.timeoutIconView:
                clickListener.onIconClick(ICONS.TIMEOUT);
                break;
            case R.id.newPeriodIconView:
                clickListener.onIconClick(ICONS.NEW_PERIOD);
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
            vibrator.vibrate(longClickVibrationPattern, -1);
        }

        if (!blockLongClick) {
            switch (v.getId()) {
                case R.id.leftScoreView:
                    return longClickListener.onScoreLongClick(LEFT);
                case R.id.rightScoreView:
                    return longClickListener.onScoreLongClick(RIGHT);
                case R.id.mainTimeView:
                    return longClickListener.onMainTimeLongClick();
                case R.id.leftNameView:
                    return longClickListener.onNameLongClick(LEFT);
                case R.id.rightNameView:
                    return longClickListener.onNameLongClick(RIGHT);
                default:
                    return true;
            }
        }
        return false;
    }

    public interface ClickListener {
        void onChangeScoreClick(boolean left, int value);
        void onIconClick(ICONS icon);
        void onMainTimeClick();
        void onTeamClick(boolean left);
        void onPlayerButtonClick(boolean left, SidePanelRow row);
        void onHornAction(boolean play);
        void onWhistleAction(boolean play);
        void onOpenPanelClick(boolean left);
    }

    public interface LongClickListener {
        boolean onMainTimeLongClick();
        boolean onNameLongClick(boolean left);
        boolean onScoreLongClick(boolean left);
        boolean onPlayerButtonLongClick(boolean left);
    }

    public enum ICONS {
        HORN, WHISTLE, CAMERA, TIMEOUT, NEW_PERIOD, SWITCH_SIDES
    }

    public SimpleLayout(Context context, Preferences preferences,
                        ClickListener clickListener, LongClickListener longClickListener) {
        super(context);
        this.preferences = preferences;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        LayoutInflater.from(context).inflate(R.layout.activity_main_simple, this);
        initSimple();
        init();
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private SimpleLayout init() {
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
//        if (preferences.spOn) {
//            initPlayersButtons();
//        }
        return this;
    }

    private void initSimple() {
        ImageView startNewPeriodView = (ImageView) findViewById(R.id.newPeriodIconView);
        startNewPeriodView.setOnClickListener(this);
        preferences.enableShotTime = false;
    }

    public void zeroState() {
        mainTimeFormat = TIME_FORMAT;
        setMainTimeText(preferences.useDirectTimer ? 0 : preferences.mainTimePref);
//        nullScore(LEFT);
//        nullScore(RIGHT);
//        setTeamNames();
        if (preferences.fixLandscapeChanged) {
//            handleOrientation();
            preferences.fixLandscapeChanged = false;
        }
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
    }

    public void setBlockLongClick(boolean state) {
        blockLongClick = state;
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

    public void nullScores() {
        hScoreView.setText("00");
        gScoreView.setText("00");
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


    // times
    public void setMainTimeFormat(SimpleDateFormat value) {
        mainTimeFormat = value;
    }

    public void setMainTimeText(long millis) {
        mainTimeView.setText(mainTimeFormat.format(millis).replaceAll(API16_TIME_REGEX, "$1"));
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
}
