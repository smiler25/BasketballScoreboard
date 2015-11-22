package com.smiler.basketball_scoreboard;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.elements.ConfirmDialog;
import com.smiler.basketball_scoreboard.elements.NameEditDialog;
import com.smiler.basketball_scoreboard.elements.StartTimeoutDialog;
import com.smiler.basketball_scoreboard.elements.TimePickerFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        View.OnLongClickListener,
        ConfirmDialog.ConfirmDialogListener,
        Drawer.OnDrawerItemClickListener,
        NameEditDialog.OnChangeNameListener,
        StartTimeoutDialog.NewTimeoutDialogListener,
        SoundPool.OnLoadCompleteListener,
        TimePickerFragment.OnChangeTimeListener {

    private SharedPreferences statePref, sharedPref;
    private TextView shotTimeView, mainTimeView, hNameView, gNameView, periodView;
    private TextView hScoreView, gScoreView;
    private TextView hTimeoutsView, hTimeouts20View;
    private TextView gTimeoutsView, gTimeouts20View;
    private TextView hFoulsView, gFoulsView;
    private TextView shotTimeSwitchView;
    private Drawer.Result drawer;

    private int layoutType, autoSaveResults, autoSound, actualTime, timeoutRules;
    private boolean doubleBackPressedFirst, layoutChanged, timeoutsRulesChanged;
    private boolean saveOnExit, autoShowTimeout, autoShowBreak, pauseOnSound, vibrationOn;
    private boolean mainTimerOn, shotTimerOn, enableShotTime, restartShotTimer;
    private boolean useDirectTimer, directTimerStopped;
    private boolean fractionSecondsMain, fractionSecondsShot;
    private long mainTime, mainTimePref, shotTime, shotTimePref, shortShotTimePref, overTimePref;
    private long startTime, totalTime;
    private long timeoutFullDuration;
    private short hScore, gScore;
    private short hFouls, gFouls;
    private short hTimeouts, hTimeouts20;
    private short gTimeouts, gTimeouts20;
    private short takenTimeoutsFull;
    private short maxTimeouts, maxTimeouts20, maxTimeouts100;
    private short maxFouls, numRegularPeriods;
    private short period;
    private String hName, gName;
    private Handler customHandler = new Handler();
    private CountDownTimer mainTimer, shotTimer;

    private int dontAskNewGame;
    private boolean showTimeoutDialog = true;
    private FloatingCountdownTimerDialog floatingDialog;
    private HelpFragment helpFragment;
    private AppUpdatesFragment appUpdatesFragment;

    private SimpleDateFormat mainTimeFormat = Constants.timeFormat;
    private long mainTickInterval = Constants.SECOND;
    private long shotTickInterval = Constants.SECOND;
    public boolean changedUnder2Minutes = false;
    public boolean scoreSaved = false;

    private Animation shotTimeBlinkAnimation = new AlphaAnimation(1, 0);
    private int soundWhistleId, soundHornId;
    private Results gameResult;
    private SoundPool soundPool;
    private Vibrator vibrator;
    private long[] longClickVibrationPattern = {0, 50, 50, 50};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println(getResources().getString(R.string.res_type));

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        getSettings();
        if (sharedPref.getInt("app_version", 1) < BuildConfig.VERSION_CODE) {
            // showUpdatesWindow();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("app_version", BuildConfig.VERSION_CODE);
            editor.apply();
        }

        if (layoutType == Constants.LAYOUT_FULL) {
            initExtensiveLayout();
        } else {
            initSimpleLayout();
        }
        initCommonLayout();

        if (sharedPref.getBoolean("first_launch", true)) {
            drawer.openDrawer();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first_launch", false);
            editor.apply();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        shotTimeBlinkAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        gameResult = new Results(hName, gName);
        floatingDialog = new FloatingCountdownTimerDialog();
        floatingDialog.setCancelable(false);
        helpFragment = new HelpFragment();
        appUpdatesFragment = new AppUpdatesFragment();

        if (saveOnExit) {
            getSavedState();
            setSavedState();
        } else {
            newGame();
        }

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
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 1);
        }
        soundWhistleId = soundPool.load(this, R.raw.whistle, 1);
        soundHornId = soundPool.load(this, R.raw.airhorn_short, 1);
    }

    private void initCommonLayout() {
        mainTimeView = (TextView) findViewById(R.id.mainTimeView);
        hScoreView = (TextView) findViewById(R.id.homeScoreView);
        gScoreView = (TextView) findViewById(R.id.guestScoreView);
        hNameView = (TextView) findViewById(R.id.homeNameView);
        gNameView = (TextView) findViewById(R.id.guestNameView);
        ImageView whistleView = (ImageView) findViewById(R.id.whistleView);
        ImageView hornView = (ImageView) findViewById(R.id.hornView);
        ImageView startTimeoutView = (ImageView) findViewById(R.id.rightIconView);
        ImageView startCameratView = (ImageView) findViewById(R.id.cameraView);
        TextView homeScoreMinus1 = (TextView) findViewById(R.id.minus1HomeView);
        TextView guestScoreMinus1 = (TextView) findViewById(R.id.minus1GuestView);
        TextView homeScorePlus1 = (TextView) findViewById(R.id.plus1HomeView);
        TextView guestScorePlus1 = (TextView) findViewById(R.id.plus1GuestView);
        TextView homeScorePlus3 = (TextView) findViewById(R.id.plus3HomeView);
        TextView guestScorePlus3 = (TextView) findViewById(R.id.plus3GuestView);

        mainTimeView.setOnClickListener(this);
        hScoreView.setOnClickListener(this);
        gScoreView.setOnClickListener(this);
        homeScorePlus1.setOnClickListener(this);
        guestScorePlus1.setOnClickListener(this);
        homeScorePlus3.setOnClickListener(this);
        guestScorePlus3.setOnClickListener(this);
        homeScoreMinus1.setOnClickListener(this);
        guestScoreMinus1.setOnClickListener(this);

        whistleView.setOnClickListener(this);
        hornView.setOnClickListener(this);
        startTimeoutView.setOnClickListener(this);
        startCameratView.setOnClickListener(this);

        mainTimeView.setOnLongClickListener(this);
        hScoreView.setOnLongClickListener(this);
        gScoreView.setOnLongClickListener(this);
        hNameView.setOnLongClickListener(this);
        gNameView.setOnLongClickListener(this);

        layoutChanged = timeoutsRulesChanged = false;

        initDrawer();
    }

    private void initExtensiveLayout() {
        setContentView(R.layout.activity_main);
        ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
        stub.setLayoutResource((timeoutRules == 2) ? R.layout.full_bottom_nba : R.layout.full_bottom_simple);
        stub.inflate();

        periodView = (TextView) findViewById(R.id.periodView);
        hFoulsView = (TextView) findViewById(R.id.homeFoulsView);
        gFoulsView = (TextView) findViewById(R.id.guestFoulsView);
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
            shotTimeView.setVisibility(View.INVISIBLE);
            shotTimeSwitchView.setVisibility(View.INVISIBLE);
        }

        initBottomLineTimeouts();
    }

    private void initBottomLineTimeouts() {
        hTimeoutsView = (TextView) findViewById(R.id.homeTimeoutsView);
        gTimeoutsView = (TextView) findViewById(R.id.guestTimeoutsView);
        hTimeoutsView.setOnClickListener(this);
        gTimeoutsView.setOnClickListener(this);
        hTimeoutsView.setOnLongClickListener(this);
        gTimeoutsView.setOnLongClickListener(this);
        if (timeoutRules == 0) {
            ((TextView) findViewById(R.id.homeTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
            ((TextView) findViewById(R.id.guestTimeoutsLabel)).setText(getResources().getString(R.string.label_timeouts));
        } else if (timeoutRules == 2) {
            hTimeouts20View = (TextView) findViewById(R.id.homeTimeouts20View);
            gTimeouts20View = (TextView) findViewById(R.id.guestTimeouts20View);
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

    @Override
    public void onStop() {
        super.onStop();
        if (saveOnExit) { saveCurrentState(); }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PrefActivity.prefChangedRestart) {
            showConfirmDialog("new_game", false);
        } else if (PrefActivity.prefChangedNoRestart) {
            getAppSettings();
        }
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
        intent.putExtra("hName", hName);
        intent.putExtra("gName", gName);
        intent.putExtra("hScore", hScore);
        intent.putExtra("gScore", gScore);
        intent.putExtra("mainTime", mainTime);
        intent.putExtra("shotTime", shotTime);
        intent.putExtra("period", period);
        // startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {
            return;
        }
        hScore = data.getShortExtra("hScore", hScore);
        gScore = data.getShortExtra("gScore", gScore);
        period = data.getShortExtra("period", period);
        setPeriod();
        mainTime = data.getLongExtra("mainTime", mainTime);
        shotTime = data.getLongExtra("shotTime", shotTime);

        hScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, hScore));
        gScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, gScore));
        setMainTimeText(mainTime);
        setShotTimeText(shotTime);

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
                new SecondaryDrawerItem().withName(R.string.action_share).withIcon(getResources().getDrawable(R.drawable.ic_action_share)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_settings).withIcon(getResources().getDrawable(R.drawable.ic_action_settings)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_help).withIcon(getResources().getDrawable(R.drawable.ic_action_about)).withCheckable(false),
                new SecondaryDrawerItem().withName(R.string.action_whats_new).withIcon(getResources().getDrawable(R.drawable.ic_action_help)).withCheckable(false),
        };
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
        switch (i) {
            case 0:
                newGame(true);
                break;
            case 1:
                runResultsActivity();
                break;
            case 2:
                shareResult();
                break;
            case 3:
                runSettingsActivity();
                break;
            case 4:
                helpFragment.setCancelable(true);
                helpFragment.show(getFragmentManager(), Constants.TAG_FRAGMENT_HELP);
                break;
            case 5:
                appUpdatesFragment.setCancelable(true);
                appUpdatesFragment.show(getFragmentManager(), Constants.TAG_FRAGMENT_APP_UPDATES);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
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
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    }

    @Override
    public void onClick(View v) {
        if (vibrationOn) {
            vibrator.vibrate(100);
        }
        switch (v.getId()) {
            case R.id.homeScoreView:
                changeHomeScore(2);
                break;
            case R.id.guestScoreView:
                changeGuestScore(2);
                break;
            case R.id.mainTimeView:
                mainTimeClick();
                break;
            case R.id.shotTimeView:
                shotTickInterval = Constants.SECOND;
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
                break;
            case R.id.shotTimeSwitch:
                shotTickInterval = Constants.SECOND;
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
                break;
            case R.id.plus1HomeView:
                changeHomeScore(1);
                break;
            case R.id.plus1GuestView:
                changeGuestScore(1);
                break;
            case R.id.plus3HomeView:
                changeHomeScore(3);
                break;
            case R.id.plus3GuestView:
                changeGuestScore(3);
                break;
            case R.id.minus1HomeView:
                if (hScore > 0) {
                    changeHomeScore(-1);
                }
                break;
            case R.id.minus1GuestView:
                if (gScore > 0) {
                    changeGuestScore(-1);
                }
                break;
            case R.id.periodView:
                newPeriod(true);
                break;
            case R.id.whistleView:
                playWhistle();
                break;
            case R.id.hornView:
                playHorn();
                break;
            case R.id.rightIconView:
                showListDialog("timeout");
                break;
            case R.id.newPeriodIconView:
                showListDialog("new_period");
                break;
            case R.id.homeFoulsView:
                foul(0);
                break;
            case R.id.guestFoulsView:
                foul(1);
                break;
            case R.id.homeTimeoutsView:
                timeout(0);
                break;
            case R.id.guestTimeoutsView:
                timeout(1);
                break;
            case R.id.homeTimeouts20View:
                timeout20(0);
                break;
            case R.id.guestTimeouts20View:
                timeout20(1);
                break;
            case R.id.cameraView:
                if (checkCameraHardware(this)) {
                    runCameraActivity();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (vibrationOn) {
            vibrator.vibrate(longClickVibrationPattern, -1);
        }

        if (!mainTimerOn) {
            switch (v.getId()) {
                case R.id.homeScoreView:
                    hScore = 0;
                    hScoreView.setText("00");
                    return true;
                case R.id.guestScoreView:
                    gScore = 0;
                    gScoreView.setText("00");
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
                case R.id.homeFoulsView:
                    hFouls = 0;
                    hFoulsView.setText("0");
                    setColorGreen(hFoulsView);
                    return true;
                case R.id.guestFoulsView:
                    gFouls = 0;
                    gFoulsView.setText("0");
                    setColorGreen(gFoulsView);
                    return true;
                case R.id.homeTimeoutsView:
                    nullTimeouts(0);
                    return true;
                case R.id.guestTimeoutsView:
                    nullTimeouts(1);
                    return true;
                case R.id.homeTimeouts20View:
                    nullTimeouts20(0);
                    return true;
                case R.id.guestTimeouts20View:
                    nullTimeouts20(1);
                    return true;
                case R.id.homeNameView:
                    chooseTeamNameDialog("home", hName);
                    return true;
                case R.id.guestNameView:
                    chooseTeamNameDialog("guest", gName);
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }

    public void mainTimeClick() {
        if (!mainTimerOn) {
            if (useDirectTimer) {
                startDirectTimer();
            } else {
//                mainTimeFormat = Constants.timeFormat;
                startMainCountDownTimer();
            }
        } else {
            pauseGame();
        }
    }

    public boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void saveCurrentState() {
        statePref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = statePref.edit();
        editor.putString(Constants.STATE_HOME_NAME, hName);
        editor.putString(Constants.STATE_GUEST_NAME, gName);
        editor.putLong(Constants.STATE_SHOT_TIME, shotTime);
        editor.putLong(Constants.STATE_MAIN_TIME, mainTime);
        editor.putInt(Constants.STATE_PERIOD, period);
        editor.putInt(Constants.STATE_HOME_SCORE, hScore);
        editor.putInt(Constants.STATE_GUEST_SCORE, gScore);
        editor.putInt(Constants.STATE_HOME_FOULS, hFouls);
        editor.putInt(Constants.STATE_GUEST_FOULS, gFouls);
        if (timeoutRules == 1) {
            editor.putInt(Constants.STATE_HOME_TIMEOUTS, hTimeouts);
            editor.putInt(Constants.STATE_GUEST_TIMEOUTS, gTimeouts);
        } else if (timeoutRules == 2) {
            editor.putInt(Constants.STATE_HOME_TIMEOUTS_NBA, hTimeouts);
            editor.putInt(Constants.STATE_GUEST_TIMEOUTS_NBA, gTimeouts);
            editor.putInt(Constants.STATE_HOME_TIMEOUTS20, hTimeouts20);
            editor.putInt(Constants.STATE_GUEST_TIMEOUTS20, gTimeouts20);
        }
        editor.apply();
    }

    private void getSavedState() {
        statePref = getPreferences(MODE_PRIVATE);
        shotTime = statePref.getLong(Constants.STATE_SHOT_TIME, 24 * Constants.SECOND);
        mainTime = totalTime = statePref.getLong(Constants.STATE_MAIN_TIME, 600 * Constants.SECOND);
        period = (short) statePref.getInt(Constants.STATE_PERIOD, 1);
        hScore = (short) statePref.getInt(Constants.STATE_HOME_SCORE, 0);
        gScore = (short) statePref.getInt(Constants.STATE_GUEST_SCORE, 0);
        hName = statePref.getString(Constants.STATE_HOME_NAME, getResources().getString(R.string.home_team_name_default));
        gName = statePref.getString(Constants.STATE_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
        hFouls = (short) statePref.getInt(Constants.STATE_HOME_FOULS, 0);
        gFouls = (short) statePref.getInt(Constants.STATE_GUEST_FOULS, 0);
        if (timeoutRules == 1) {
            hTimeouts = (short) statePref.getInt(Constants.STATE_HOME_TIMEOUTS, 0);
            gTimeouts = (short) statePref.getInt(Constants.STATE_GUEST_TIMEOUTS, 0);
        } else if (timeoutRules == 2) {
            hTimeouts = (short) statePref.getInt(Constants.STATE_HOME_TIMEOUTS_NBA, 0);
            gTimeouts = (short) statePref.getInt(Constants.STATE_GUEST_TIMEOUTS_NBA, 0);
            hTimeouts20 = (short) statePref.getInt(Constants.STATE_HOME_TIMEOUTS20, 0);
            gTimeouts20 = (short) statePref.getInt(Constants.STATE_GUEST_TIMEOUTS20, 0);
        }
    }

    private void setSavedState() {
        setMainTimeText(mainTime);
        hScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, hScore));
        gScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, gScore));
        setTeamNames();

        if (layoutType == Constants.LAYOUT_FULL) {
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
            if (timeoutRules == 2) {
                hTimeouts20View.setText(Short.toString(hTimeouts20));
                gTimeouts20View.setText(Short.toString(gTimeouts20));
            }
        }
    }

    private void getSettings() {
        getAppSettings();
        getGameSettings();
    }

    private void getAppSettings() {
        autoSound = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_AUTO_SOUND, "0"));
        autoSaveResults = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_AUTO_SAVE_RESULTS, "0"));
        autoShowTimeout = sharedPref.getBoolean(PrefActivity.PREF_AUTO_TIMEOUT, true);
        autoShowBreak = sharedPref.getBoolean(PrefActivity.PREF_AUTO_BREAK, true);
        pauseOnSound = sharedPref.getBoolean(PrefActivity.PREF_PAUSE_ON_SOUND, true);
        vibrationOn = vibrator.hasVibrator() && sharedPref.getBoolean(PrefActivity.PREF_VIBRATION, false);
        saveOnExit = sharedPref.getBoolean(PrefActivity.PREF_SAVE_ON_EXIT, true);
        PrefActivity.prefChangedNoRestart = false;
    }

    private void getGameSettings() {
        int temp_int = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_LAYOUT, "0"));
        if (temp_int != layoutType) {
            layoutChanged = true;
            layoutType = temp_int;
        }
        useDirectTimer = sharedPref.getBoolean(PrefActivity.PREF_DIRECT_TIMER, false);
        fractionSecondsMain = sharedPref.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_MAIN, true);
        fractionSecondsShot = sharedPref.getBoolean(PrefActivity.PREF_FRACTION_SECONDS_SHOT, true);
        useDirectTimer = sharedPref.getBoolean(PrefActivity.PREF_DIRECT_TIMER, false);
        shotTimePref = sharedPref.getInt(PrefActivity.PREF_SHOT_TIME, 24) * 1000;
        enableShotTime = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHOT_TIME, true);
        restartShotTimer = sharedPref.getBoolean(PrefActivity.PREF_SHOT_TIME_RESTART, false);
        boolean enableShortShotTime = sharedPref.getBoolean(PrefActivity.PREF_ENABLE_SHORT_SHOT_TIME, true);
        shortShotTimePref = (enableShortShotTime) ? sharedPref.getInt(PrefActivity.PREF_SHORT_SHOT_TIME, 14) * 1000 : shotTimePref;
        mainTimePref = sharedPref.getInt(PrefActivity.PREF_REGULAR_TIME, 10) * Constants.SECONDS_60;
        overTimePref = sharedPref.getInt(PrefActivity.PREF_OVERTIME, 5) * Constants.SECONDS_60;
        numRegularPeriods = (short) sharedPref.getInt(PrefActivity.PREF_NUM_REGULAR, 4);
        hName = sharedPref.getString(PrefActivity.PREF_HOME_NAME, getResources().getString(R.string.home_team_name_default));
        gName = sharedPref.getString(PrefActivity.PREF_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
        actualTime = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_ACTUAL_TIME, "1"));
        maxFouls = (short) sharedPref.getInt(PrefActivity.PREF_MAX_FOULS, 5);

        temp_int = Integer.parseInt(sharedPref.getString(PrefActivity.PREF_TIMEOUTS_RULES, "0"));
        if (temp_int != timeoutRules) {
            timeoutsRulesChanged = true;
            timeoutRules = temp_int;
        }
        PrefActivity.prefChangedRestart = false;
    }

    private void setTimeouts() {
        if (timeoutRules == 1) {
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
        } else if (timeoutRules == 2) {
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

    private void nullTimeouts(int team) {
        if (timeoutRules == 0) {
            nullTimeoutsNoRules(team);
            return;
        }
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

    private void zeroState() {
        mainTimerOn = false;
        mainTimeFormat = Constants.timeFormat;
        mainTickInterval = Constants.SECOND;
        if (useDirectTimer) {
            mainTime = 0;
        } else {
            mainTime = mainTimePref;
        }
        changedUnder2Minutes = false;
        setMainTimeText(mainTime);
        hScore = gScore = 0;
        changeGuestScore(0);
        changeHomeScore(0);
        setTeamNames();
        if (layoutType == Constants.LAYOUT_FULL) {
            if (enableShotTime) {
                shotTimerOn = false;
                shotTime = shotTimePref;
                shotTickInterval = Constants.SECOND;
                setShotTimeText(shotTime);
                shotTimeView.setVisibility(View.VISIBLE);
                shotTimeSwitchView.setVisibility(View.VISIBLE);
            }
            nullTimeouts(2);
            clearFouls();
            period = 1;
            periodView.setText("1");
            if (timeoutRules == 2) {
                nullTimeouts20(2);
            }
        }
    }

    private void newGame(boolean save) {
        if (autoSaveResults == 0) {
            saveResultDb();
        } else if (autoSaveResults == 2) {
            showConfirmDialog("save_result", false);
        }
        newGame();
    }

    private void newGame() {
        if (PrefActivity.prefChangedRestart || PrefActivity.prefChangedNoRestart) {
            getSettings();
            if (enableShotTime && layoutType == Constants.LAYOUT_FULL && !layoutChanged) {
                shotTimeSwitchView.setText(Long.toString(shortShotTimePref / 1000));
            }
        }
        if (layoutChanged || timeoutsRulesChanged) {
            if (layoutType == Constants.LAYOUT_FULL) {
                initExtensiveLayout();
            } else if (layoutType == 1) {
                initSimpleLayout();
            }
            initCommonLayout();
        }
        pauseGame();
        zeroState();
        if (layoutType == Constants.LAYOUT_FULL) {
            setTimeouts();
        }
        gameResult = new Results(hName, gName);
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
            shotTickInterval = Constants.SECOND;
        }
        mainTickInterval = Constants.SECOND;
        mainTimeFormat = Constants.timeFormat;
        setMainTimeText(mainTime);
        if (period <= numRegularPeriods) {
            clearFouls();
        }
        setTimeouts();
        saveResult();
        scoreSaved = false;
    }

    private void clearFouls() {
        hFouls = gFouls = 0;
        hFoulsView.setText("0");
        gFoulsView.setText("0");
        setColorGreen(hFoulsView);
        setColorGreen(gFoulsView);
    }

    private void timeout20(int team) {
        pauseGame();
        switch (team) {
            case 0:
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
            case 1:
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
    }

    private void timeout(int team) {
        pauseGame();
        takenTimeoutsFull++;
        if (timeoutRules == 0) {
            switch (team) {
                case 0:
                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (autoShowTimeout) {
                        showTimeout(timeoutFullDuration, hName);
                    }
                    break;
                case 1:
                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (autoShowTimeout) {
                        showTimeout(timeoutFullDuration, gName);
                    }
                    break;
            }
        } else if (timeoutRules == 1) {
            switch (team) {
                case 0:
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
                case 1:
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
                case 0:
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
                case 1:
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
    }

    private void foul(int team) {
        if (actualTime > 0) {
            pauseGame();
        }
        if (enableShotTime) {
            shotTime = (shotTime < shortShotTimePref) ? shortShotTimePref : shotTimePref;
        }
        switch (team) {
            case 0:
                if (hFouls < maxFouls) {
                    hFoulsView.setText(Short.toString(++hFouls));
                    if (hFouls == maxFouls) {
                        setColorRed(hFoulsView);
                    }
                }
                break;
            case 1:
                if (gFouls < maxFouls) {
                    gFoulsView.setText(Short.toString(++gFouls));
                    if (gFouls == maxFouls) {
                        setColorRed(gFoulsView);
                    }
                }
                break;
        }
    }

    private void changeScore() {
        if (enableShotTime && layoutType == Constants.LAYOUT_FULL && restartShotTimer) {
            if (mainTimerOn) {
                startShotCountDownTimer(shotTimePref);
            } else {
                shotTime = shotTimePref;
                setShotTimeText(shotTimePref);
            }
        }
        if (actualTime == 2 || (actualTime == 3 && mainTime < Constants.SECONDS_60)) {
            pauseGame();
        }
        scoreSaved = false;
    }

    private void changeGuestScore(int value) {
        gScore += value;
        gScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, gScore));
        if (value != 0) {
            changeScore();
        }
    }

    private void changeHomeScore(int value) {
        hScore += value;
        hScoreView.setText(String.format(Constants.FORMAT_TWO_DIGITS, hScore));
        if (value != 0) {
            changeScore();
        }
    }

    private void setMainTimeText(long millis) {
        mainTimeView.setText(mainTimeFormat.format(millis));
    }

    private void setShotTimeText(long millis) {
        if (millis < 5000 && fractionSecondsShot) {
            shotTimeView.setText(String.format(Constants.TIME_FORMAT_SHORT, millis / 1000, (millis % 1000) / 100));
        } else {
            shotTimeView.setText(String.format(Constants.FORMAT_TWO_DIGITS, (short) Math.ceil(millis / 1000.0)));
        }
    }

    private void setPeriod() {
        if (period <= numRegularPeriods) {
            mainTime = totalTime = mainTimePref;
            periodView.setText(Short.toString(period));
        } else {
            mainTime = totalTime = overTimePref;
            periodView.setText(String.format("OT%d", period - numRegularPeriods));
        }
        if (useDirectTimer) {
            mainTime = 0;
        }
    }

    private void setTeamNames(String home, String guest) {
        hNameView.setText(home);
        gNameView.setText(guest);
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
        if (timeoutRules == 2) {
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
                if (mainTime < Constants.MINUTES_2 && !changedUnder2Minutes) {
                    changedUnder2Minutes = true;
                    under2Minutes();
                }
                if (fractionSecondsMain && mainTime < Constants.SECONDS_60 && mainTickInterval == Constants.SECOND) {
                    this.cancel();
                    mainTickInterval = 100;
                    mainTimeFormat = Constants.timeFormatMillis;
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
                mainTickInterval = Constants.SECOND;
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
                if (fractionSecondsShot && shotTime < 5 * Constants.SECOND && shotTickInterval == Constants.SECOND) {
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
                shotTickInterval = Constants.SECOND;
            }
        }.start();
        shotTimerOn = true;
    }

    private void startDirectTimer() {
        startTime = SystemClock.uptimeMillis() - mainTime;
        if (directTimerStopped) {
            stopDirectTimer();
        }
        mainTimeFormat = Constants.timeFormat;
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
        if (pauseOnSound) {
            pauseGame();
        }
        soundPool.play(soundWhistleId, 1, 1, 0, 0, 1);
    }

    private void playHorn() {
        if (pauseOnSound) {
            pauseGame();
        }
        soundPool.play(soundHornId, 1, 1, 0, 0, 1);
    }

    public void shareResult() {
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
        dialog.show(getFragmentManager(), Constants.TAG_FRAGMENT_CONFIRM);
    }

    private void showListDialog(String type) {
        Fragment frag = getFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_LIST);
        if (frag != null && frag.isAdded()) {
            return;
        }
        StartTimeoutDialog dialog = StartTimeoutDialog.newInstance(type);
        dialog.show(getFragmentManager(), Constants.TAG_FRAGMENT_LIST);
    }

    private void chooseTeamNameDialog(String team, String name) {
        DialogFragment nameEdit = NameEditDialog.newInstance(team, name);
        nameEdit.show(getFragmentManager(), Constants.TAG_FRAGMENT_NAME_EDIT);
    }

    private void showMainTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance(
                (int) (TimeUnit.MILLISECONDS.toMinutes(mainTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mainTime))),
                (int) (TimeUnit.MILLISECONDS.toSeconds(mainTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mainTime))),
                (int) (mainTime % 1000) / 100);
        mainTimePicker.show(getFragmentManager(), Constants.TAG_FRAGMENT_MAIN_TIME_PICKER);
    }

    private void showShotTimePicker() {
        DialogFragment mainTimePicker = TimePickerFragment.newInstance((int) shotTime / 1000, (int) (shotTime % 1000) / 100);
        mainTimePicker.show(getFragmentManager(), Constants.TAG_FRAGMENT_SHOT_TIME_PICKER);
    }

    private void showTimeout(long durSeconds, String team) {
        Fragment frag = getFragmentManager().findFragmentByTag(Constants.TAG_FRAGMENT_TIME);
        if (frag != null && frag.isAdded()) {
            return;
        }
        floatingDialog.show(getFragmentManager(), Constants.TAG_FRAGMENT_TIME);
        floatingDialog.duration = durSeconds;
        floatingDialog.duration = durSeconds * 1000;
        if (durSeconds > 100) {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_1), durSeconds / 60);
        } else {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_2), team, durSeconds).replace(" ()", "").trim();
        }
        floatingDialog.startCountDownTimer();
    }

    public void endOfGameActions(int dontAskNewGame) {
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

    public void saveResult() {
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

    public void saveResultDb() {
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
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_DATE, (new Date()).getTime());
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_HOME_TEAM, hName);
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_GUEST_TEAM, gName);
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_HOME_SCORE, hScore);
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_GUEST_SCORE, gScore);
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_SHARE_STRING, gameResult.getResultString(period > numRegularPeriods));
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_HOME_PERIODS, gameResult.getHomeScoreByPeriodString());
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_GUEST_PERIODS, gameResult.getGuestScoreByPeriodString());
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_REGULAR_PERIODS, numRegularPeriods);
            cv.put(DbScheme.ResultsTable.COLUMN_NAME_COMPLETE, gameResult.isComplete());
            db.insert(DbScheme.ResultsTable.TABLE_NAME, null, cv);
        } finally {
            dbHelper.close();
        }
    }

    @Override
    public void onTimeChanged(int minutes, int seconds, int millis) {
        mainTime = minutes * Constants.SECONDS_60 + seconds * 1000 + millis * 100;
        if (fractionSecondsMain && mainTime < Constants.SECONDS_60) {
            mainTickInterval = 100;
            mainTimeFormat = Constants.timeFormatMillis;
        } else {
            mainTimeFormat = Constants.timeFormat;
        }

        if (mainTime > shotTime) {
            shotTimeView.setVisibility(View.VISIBLE);
            shotTimeSwitchView.setVisibility(View.VISIBLE);
        }

        setMainTimeText(mainTime);
    }

    @Override
    public void onTimeChanged(int seconds, int millis) {
        shotTime = seconds * 1000 + millis * 100;
        if (fractionSecondsShot && shotTime < 5 * Constants.SECOND) {
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
        mainTickInterval = Constants.SECOND;
        switch (which) {
            case 0:
                mainTime = mainTimePref;
                break;
            case 1:
                mainTime = overTimePref;
                break;
        }
        mainTimeFormat = Constants.timeFormat;
        setMainTimeText(mainTime);
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
        if (type.equals("new_game")) {
            newGame();
        } else if (type.equals("save_result")) {
            saveResultDb();
        }
    }

    @Override
    public void onConfirmDialogNeutral(boolean dontShow) {
        dontAskNewGame = (dontShow) ? 2 : 0;
        saveResultDb();
        newGame();
    }

    @Override
    public void onConfirmDialogNegative(boolean dontShow) {
        dontAskNewGame = (dontShow) ? 1 : 0;
    }
}