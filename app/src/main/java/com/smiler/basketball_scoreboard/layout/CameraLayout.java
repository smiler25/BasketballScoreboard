package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.camera.CameraView;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.text.SimpleDateFormat;

import static com.smiler.basketball_scoreboard.Constants.API16_TIME_REGEX;
import static com.smiler.basketball_scoreboard.Constants.FORMAT_TWO_DIGITS;
import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.LONG_CLICK_VIBE_PAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_SHORT;

public class CameraLayout extends BaseLayout implements
        View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = "BS-CameraLayout";
    private final Preferences preferences;
    private ClickListener clickListener;
    private LongClickListener longClickListener;
    private TextView mainTimeView, shotTimeView, periodView;
    private TextView hNameView, gNameView;
    private TextView hScoreView, gScoreView;
    private Vibrator vibrator;

    private SimpleDateFormat mainTimeFormat = TIME_FORMAT;

    public interface ClickListener {
        void onChangeScoreClick(int team);
        void onMainTimeClick();
        void onPeriodClick();
        void onShotTimeClick();
        void onTakePictureClick();
    }

    public interface LongClickListener {
        boolean onScoreLongClick(int team);
        boolean onMainTimeLongClick();
        boolean onPeriodLongClick();
        boolean onShotTimeLongClick();
    }

    @Override
    public void onClick(View v) {
        if (preferences.vibrationOn) {
            vibrator.vibrate(100);
        }
        switch (v.getId()) {
            case R.id.camera_take_picture:
                clickListener.onTakePictureClick();
                break;
            case R.id.camera_home_score:
            case R.id.camera_home_name:
                clickListener.onChangeScoreClick(HOME);
                break;
            case R.id.camera_guest_score:
            case R.id.camera_guest_name:
                clickListener.onChangeScoreClick(GUEST);
                break;
            case R.id.camera_period:
                clickListener.onPeriodClick();
                break;
            case R.id.camera_time:
                clickListener.onMainTimeClick();
                break;
            case R.id.camera_shot_clock:
                clickListener.onShotTimeClick();
                break;
//            case R.id.left_panel_toggle:
//                clickListener.onPanelToggleClick(LEFT);
//                break;
//            case R.id.right_panel_toggle:
//                clickListener.onPanelToggleClick(RIGHT);
//                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (preferences.vibrationOn) {
            vibrator.vibrate(LONG_CLICK_VIBE_PAT, -1);
        }
        switch (v.getId()) {
            case R.id.camera_home_score:
            case R.id.camera_home_name:
                return longClickListener.onScoreLongClick(HOME);
            case R.id.camera_guest_score:
            case R.id.camera_guest_name:
                return longClickListener.onScoreLongClick(GUEST);
            case R.id.camera_period:
                return longClickListener.onPeriodLongClick();
        }
        return false;
    }


    public CameraLayout(Context context, Preferences preferences, CameraView camera,
                        ClickListener clickListener, LongClickListener longClickListener) {
        super(context);
        this.preferences = preferences;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        switch (preferences.layoutType) {
            case SIMPLE:
                LayoutInflater.from(context).inflate(R.layout.camera_layout_simple, this);
                break;
            case STREETBALL:
                LayoutInflater.from(context).inflate(R.layout.camera_layout_3x3, this);
                init3X3();
                break;
            default:
                LayoutInflater.from(context).inflate(R.layout.camera_layout_full, this);
                initFull();
                break;
        }
        init();
        FrameLayout view = findViewById(R.id.camera_preview);
        view.addView(camera);
    }

    private void initFull() {
        periodView = findViewById(R.id.camera_period);
        periodView.setOnClickListener(this);
        periodView.setOnLongClickListener(this);
        initShotTime();
    }

    private void init3X3() {
        initShotTime();
    }

    private void initShotTime() {
        shotTimeView = findViewById(R.id.camera_shot_clock);
        shotTimeView.setOnClickListener(this);
        shotTimeView.setOnLongClickListener(this);
    }

    private CameraLayout init() {
        ImageView takePictureBu = findViewById(R.id.camera_take_picture);
        mainTimeView = findViewById(R.id.camera_time);
        hScoreView = findViewById(R.id.camera_home_score);
        gScoreView = findViewById(R.id.camera_guest_score);
        hNameView = findViewById(R.id.camera_home_name);
        gNameView = findViewById(R.id.camera_guest_name);

        takePictureBu.setOnClickListener(this);
        mainTimeView.setOnClickListener(this);
        hScoreView.setOnClickListener(this);
        gScoreView.setOnClickListener(this);
        hNameView.setOnClickListener(this);
        gNameView.setOnClickListener(this);

        mainTimeView.setOnLongClickListener(this);
        hScoreView.setOnLongClickListener(this);
        gScoreView.setOnLongClickListener(this);
        hNameView.setOnLongClickListener(this);
        gNameView.setOnLongClickListener(this);

        return this;
    }


    // scores
    public void setScores(CharSequence home, CharSequence guest) {
        hScoreView.setText(home);
        gScoreView.setText(guest);
    }

    public void setGuestScore(int value) {
        gScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
    }

    public void setHomeScore(int value) {
        hScoreView.setText(String.format(FORMAT_TWO_DIGITS, value));
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

    public void hideShotTime() {
        shotTimeView.setVisibility(View.INVISIBLE);
    }

    public void showShotTime() {
        shotTimeView.setVisibility(View.VISIBLE);
    }


    // period
    public void setPeriod(String value, boolean regular) {
        if (regular) {
            periodView.setText(value);
        } else {
            periodView.setText("OT" + value);
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
}
