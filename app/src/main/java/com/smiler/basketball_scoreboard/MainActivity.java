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
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.camera.CameraActivity;
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

import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_APP_UPDATES;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_CONFIRM;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_MAIN_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_NAME_EDIT;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_SHOT_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_TIME;


public class MainActivity extends AppCompatActivity implements
        Game.GameListener,
        StandardLayout.ClickListener,
        StandardLayout.LongClickListener,
        ConfirmDialog.ConfirmDialogListener,
        Drawer.OnDrawerItemClickListener,
        EditPlayerDialog.OnPanelsListener,
        NameEditDialog.OnChangeNameListener,
        OverlayFragment.OverlayFragmentListener,
        SidePanelFragment.SidePanelListener,
        SoundPool.OnLoadCompleteListener,
        ListDialog.NewTimeoutDialogListener,
        TimePickerFragment.OnChangeTimeListener {

    public static final String TAG = "BS-MainActivity";
    private Drawer.Result drawer;
    private Preferences preferences;
    private Game game;
    private Realm realm;

    private boolean doubleBackPressedFirst;
    private FloatingCountdownTimerDialog floatingDialog;
    private OverlayFragment overlaySwitch;

    private int soundWhistleId, soundHornId, soundWhistleStreamId, soundHornStreamId;
    private int whistleRepeats, hornRepeats, whistleLength, hornLength;
    private boolean whistlePressed, hornPressed;
    private SoundPool soundPool;
    private static Context mainActivityContext;

    public static Context getContext() {
        return mainActivityContext;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (game != null) {
            game.stopGame();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handleOrientation();
//         System.out.println("res_type = " + getResources().getString(R.string.res_type));
        if (game != null) {
            game.resumeGame();
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
        initSounds();

        preferences = Preferences.getInstance(getApplicationContext());
        preferences.read();
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

        floatingDialog = new FloatingCountdownTimerDialog();
        floatingDialog.setCancelable(false);
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
//        getSavedState();
//        SharedPreferences prefs = getSharedPreferences(Constants.STATE_PREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
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
        if (game != null) {
            game.saveInstanceState(outState);
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
            if (game != null) {
                game.restoreInstanceState(inState);
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
        game = Game.newGame(this, initGameLayout(), this);
    }

    private StandardLayout initGameLayout() {
        StandardLayout layout = new StandardLayout(this, preferences, this, this);
        setContentView(layout);
        initLayout();
        return layout;
    }

    private void startNewGame(boolean save) {
        if (save) {
            game = game.saveAndNew();
        } else {
            game = Game.newGame(this, this);
        }
    }

    private void initLayout() {
        overlaySwitch = OverlayFragment.newInstance(OVERLAY_SWITCH);
        overlaySwitch.setRetainInstance(true);
        initDrawer();
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
        if (!checkCameraHardware(this)) {
            Toast.makeText(this, getResources().getString(R.string.toast_camera_fail), Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, CameraActivity.class);
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
        startActivityForResult(intent, 1);
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

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void showSwitchFragment(boolean show) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (show) {
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
        } else {
            fm.beginTransaction()
                    .setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out)
                    .hide(overlaySwitch)
                    .commit();
        }
    }

    private void playWhistle() {
        playWhistle(2);
    }

    private void playWhistle(int repeats) {
        if (preferences.pauseOnSound) {
            game.pauseGame();
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

    private void showConfirmDialog(String type) {
        ConfirmDialog dialog;
        dialog = ConfirmDialog.newInstance(type);
        dialog.show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
    }

    private void showWinDialog(String type, String team, int winScore, int loseScore) {
        ConfirmDialog dialog;
        dialog = ConfirmDialog.newInstance(type, team, winScore, loseScore);
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
        TreeMap<Integer, SidePanelRow> choices = game.getInactivePlayers(left);
        if (choices == null || choices.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.side_panel_no_data), Toast.LENGTH_LONG).show();
            return;
        }
        for (Map.Entry<Integer, SidePanelRow> entry : choices.entrySet()) {
            numberNameList.add(String.format("%d: %s", entry.getValue().getNumber(), entry.getValue().getName()));
        }
        Button bu = game.getSelectedPlayer();
        int number = bu.getTag() != null ? ((SidePanelRow)bu.getTag()).getNumber() : -1;
        ListDialog.newInstance("substitute", numberNameList, left, number).show(getFragmentManager(), ListDialog.TAG);
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

    private void showTimeout(long seconds, String team) {
        Fragment frag = getFragmentManager().findFragmentByTag(TAG_FRAGMENT_TIME);
        if (frag != null && frag.isAdded()) {
            return;
        }
        floatingDialog.show(getFragmentManager(), TAG_FRAGMENT_TIME);
        floatingDialog.duration = seconds;
        floatingDialog.duration = seconds * 1000;
        if (seconds > 100) {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_1), seconds / 60);
        } else {
            floatingDialog.title = String.format(getResources().getString(R.string.timeout_format_2), team, seconds).replace(" ()", "").trim();
        }
        floatingDialog.startCountDownTimer();
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
        game.pauseGame();
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
    public void onClearPanelDialogItemClick(int type, boolean left) {
        game.deletePlayers(type, left);
    }

    @Override
    public void onSubstituteListSelect(boolean left, int newNumber) {
//        SidePanelRow row = inactivePlayers.get(newNumber);
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
        game.setDontAskNewGame(dontShow ? 2 : 0);
        game = Game.newGame(this, this);
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
        game.setDontAskNewGame(dontShow ? 2 : 0);
        game.saveAndNew();
    }

    @Override
    public void onConfirmDialogNegative(String type, boolean dontShow) {
        game.setDontAskNewGame(dontShow ? 1 : 0);
    }

    @Override
    public void onConfirmDialogNegative(String type) {
//        if (type.equals("save_result")) {
//            newGame();
//        }
    }

    @Override
    public void onSidePanelClose(boolean left) {
        game.closeSidePanel(left);
    }

    @Override
    public void onSidePanelActiveSelected(TreeSet<SidePanelRow> rows, boolean left) {
        game.selectActivePlayers(rows, left);
    }

    @Override
    public void onSidePanelNoActive(boolean left) {
        game.deleteActivePlayers(left);
    }

    @Override
    public void onOverlayClick() {
        game.checkCloseSidePanels();
    }

    @Override
    public void onOverlayOpenPanel(boolean left) {
        game.openPanel(left);
    }

    @Override
    public void onPanelAddPlayer(boolean left, int number, String name, boolean captain) {
        game.addPlayer(left, number, name, captain);
    }

    @Override
    public void onPanelEditPlayer(boolean left, int id, int number, String name, boolean captain) {
        game.editPlayer(left, id, number, name, captain);
    }

    @Override
    public void onPanelDeletePlayer(boolean left, int id) {
        game.deletePlayer(left, id);
    }

    @Override
    public int onPanelCheckPlayer(boolean left, int number, boolean captain) {
        return game.validatePlayer(left, number, captain);
    }

    @Override
    public void onMainTimeClick() {
        game.mainTimeClick();
    }

    @Override
    public void onShotTimeClick() {
        game.shotTimeClick();
    }

    @Override
    public void onShotTimeSwitchClick() {
        game.shotTimeSwitch();
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
    public void onTimeoutsClick(boolean left) {
        game.timeout(left);
    }

    @Override
    public void onTimeouts20Click(boolean left) {
        game.timeout20(left);
    }

    @Override
    public void onPlayerButtonClick(boolean left, SidePanelRow player) {
        game.playerAction(left, player);
    }

    @Override
    public void onHornAction(boolean play) {
        if (play) {
            playHorn();
            hornPressed = true;
        } else {
            stopHorn();
            hornPressed = false;
        }
    }

    @Override
    public void onWhistleAction(boolean play) {
        if (play) {
            playWhistle();
            whistlePressed = true;
        } else {
            stopWhistle();
            whistlePressed = false;
        }

    }

    @Override
    public void onOpenPanelClick(boolean left) {
        game.openPanel(left);
    }

    @Override
    public void onIconClick(StandardLayout.ICONS icon) {
        switch (icon) {
            case CAMERA:
                runCameraActivity();
                break;
            case HORN:
                playHorn();
                break;
            case NEW_PERIOD:
                showListDialog("new_period");
                break;
            case SWITCH_SIDES:
                game.switchSides();
                break;
            case TIMEOUT:
                showListDialog("timeout");
                break;
            case WHISTLE:
                break;
        }
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
        showMainTimePicker();
        return true;
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
        showShotTimePicker();
        return true;
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

    @Override
    public boolean onPlayerButtonLongClick(boolean left) {
        showListDialog(left);
        return false;
    }

    @Override
    public void onPlayHorn() {
        playHorn();
    }

    @Override
    public void onNewGame(Game game) {
        this.game = game;
    }

    @Override
    public StandardLayout onInitLayout() {
        return initGameLayout();
    }

    @Override
    public void onConfirmDialog(String type) {
        showConfirmDialog(type);
    }

    @Override
    public void onWinDialog(String type, String team, int winScore, int loseScore) {
        showWinDialog(type, team, winScore, loseScore);
    }

    @Override
    public void onShowTimeout(long seconds, String team) {
        showTimeout(seconds, team);
    }

    @Override
    public void onSwitchSides(boolean show) {
        showSwitchFragment(show);
    }
}