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
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.smiler.basketball_scoreboard.camera.CameraFragment;
import com.smiler.basketball_scoreboard.camera.CameraUtils;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.db.deprecated.DbHelper;
import com.smiler.basketball_scoreboard.elements.NavigationDrawer;
import com.smiler.basketball_scoreboard.elements.OverlayFragment;
import com.smiler.basketball_scoreboard.elements.dialogs.AppUpdatesDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.ConfirmDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.DialogTypes;
import com.smiler.basketball_scoreboard.elements.dialogs.FloatingCountdownTimerDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.ListDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.NewGameDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.PlayerEditDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.SelectPlayersDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.TeamEditInGameDialog;
import com.smiler.basketball_scoreboard.elements.dialogs.TeamSelector;
import com.smiler.basketball_scoreboard.elements.dialogs.TimePickerDialog;
import com.smiler.basketball_scoreboard.game.Game;
import com.smiler.basketball_scoreboard.help.HelpActivity;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.layout.BoardFragment;
import com.smiler.basketball_scoreboard.layout.ClickListener;
import com.smiler.basketball_scoreboard.layout.LayoutFactory;
import com.smiler.basketball_scoreboard.layout.LongClickListener;
import com.smiler.basketball_scoreboard.layout.PlayersPanels;
import com.smiler.basketball_scoreboard.layout.StandardLayout;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;
import com.smiler.basketball_scoreboard.preferences.Preferences;
import com.smiler.basketball_scoreboard.profiles.TeamsActivity;
import com.smiler.basketball_scoreboard.results.ResultsActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_SWITCH;
import static com.smiler.basketball_scoreboard.Constants.PERMISSION_CODE_CAMERA;
import static com.smiler.basketball_scoreboard.Constants.PERMISSION_CODE_STORAGE;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_APP_UPDATES;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_CONFIRM;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_MAIN_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_SHOT_TIME_PICKER;
import static com.smiler.basketball_scoreboard.Constants.TAG_FRAGMENT_TIME;


public class MainActivity extends AppCompatActivity implements
        Game.GameListener,
        ClickListener,
        LongClickListener,
        CameraFragment.CameraFragmentListener,
        ConfirmDialog.ConfirmDialogListener,
        Drawer.OnDrawerItemClickListener,
        PlayerEditDialog.EditPlayerInGameListener,
        TeamEditInGameDialog.ChangeTeamListener,
        NewGameDialog.NewGameDialogListener,
        OverlayFragment.OverlayFragmentListener,
        SelectPlayersDialog.SelectPlayersDialogListener,
        SidePanelFragment.SidePanelListener,
        SoundPool.OnLoadCompleteListener,
        ListDialog.ListDialogListener,
        TimePickerDialog.ChangeTimeListener {

    public static final String TAG = "BS-MainActivity";
    private NavigationDrawer drawer;
    private Preferences preferences;
    private Game game;
    private BaseLayout layout;
    private PlayersPanels panels;
    private FloatingCountdownTimerDialog floatingDialog;
    private OverlayFragment overlaySwitch;

    private boolean doubleBackPressedFirst;
    private int soundWhistleId, soundHornId, soundWhistleStreamId, soundHornStreamId;
    private int whistleRepeats, hornRepeats, whistleLength, hornLength;
    private boolean whistlePressed, hornPressed;
    private SoundPool soundPool;

    @Override
    public void onStop() {
        super.onStop();
        if (game != null) {
            game.stopGame();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RealmController.close();
    }

    @Override
    public void onResume() {
        super.onResume();
//        System.out.println("res_type = " + getResources().getString(R.string.res_type));
        if (game != null) {
            game.resumeGame();
            game.setListener(this);
            game.setLayout(layout != null ? layout : initGameLayout());
            game.setSavedState();
        }
        handleOrientation();
        preferences.resetChangeStates();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        FragmentManager fm = getFragmentManager();
        if (overlaySwitch != null && overlaySwitch.isAdded()) {
            fm.putFragment(outState, OverlayFragment.TAG_SWITCH, overlaySwitch);
        }

        Fragment layout = fm.getFragment(outState, BoardFragment.FRAGMENT_TAG);
        Fragment cameraLayout = fm.getFragment(outState, CameraFragment.FRAGMENT_TAG);

        if (layout != null && layout.isAdded()) {
            fm.putFragment(outState, CameraFragment.FRAGMENT_TAG, layout);
        }
        if (cameraLayout != null && cameraLayout.isAdded()) {
            fm.putFragment(outState, CameraFragment.FRAGMENT_TAG, cameraLayout);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        initSounds();

        preferences = Preferences.getInstance(getApplicationContext());
        preferences.read();

        handleOrientation();
        initElements();
        initGame(preferences.saveOnExit);
        handleLaunch();
    }

    private void handleLaunch() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPref.getInt("app_version", 1) < BuildConfig.VERSION_CODE) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("app_version", BuildConfig.VERSION_CODE);
            editor.apply();

            new AppUpdatesDialog().show(getFragmentManager(), TAG_FRAGMENT_APP_UPDATES);
            migrateToRealm();
        }
        if (sharedPref.getBoolean("first_launch", true)) {
            drawer.open();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("first_launch", false);
            editor.apply();
        }
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

    private void handleOrientation() {
        if (preferences.fixLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
        switch (i) {
            case 0:
                game.newGame();
                break;
            case 1:
                runResultsActivity();
                break;
            case 2:
                runProfilesActivity();
                break;
            case 3:
                runSettingsActivity();
                break;
            case 4:
                shareResult();
                break;
            case 5:
                runHelpActivity();
                break;
            default:
                break;
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

    @Override
    public void onBackPressed() {
        if (drawer.isOpen()) {
            drawer.close();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
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
        new Handler().postDelayed(() -> doubleBackPressedFirst = false, 3000);
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addCameraView();
                }
            }
            case PERMISSION_CODE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraUtils.enableSaving();
                }
            }
        }
    }

    private void runResultsActivity() {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    private void runSettingsActivity() {
        startActivity(new Intent(this, PrefActivity.class));
    }

    private void runHelpActivity() {
        startActivity(new Intent(this, HelpActivity.class));
    }

    private void runProfilesActivity() {
        Intent intent = new Intent(this, TeamsActivity.class);
        startActivity(intent);
    }

    private void addCameraView() {
        if (!checkCameraHardware(this)) {
            Toast.makeText(this, getResources().getString(R.string.toast_camera_fail), Toast.LENGTH_LONG).show();
            return;
        }
        CameraFragment cameraFrag = CameraFragment.newInstance();
        cameraFrag.setRetainInstance(true);
        cameraFrag.setPreferences(preferences);
        cameraFrag.setGame(game);
        cameraFrag.setListener(this);
        getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .add(R.id.board_layout_place, cameraFrag, CameraFragment.FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private void initElements() {
        drawer = new NavigationDrawer(this);
        overlaySwitch = OverlayFragment.newInstance(OVERLAY_SWITCH);
        overlaySwitch.setRetainInstance(true);
        floatingDialog = new FloatingCountdownTimerDialog();
        floatingDialog.setCancelable(false);
    }

    private void initGame(boolean restore) {
        initGameLayout();
        if (preferences.spOn) {
            if (panels == null) {
                initPlayersPanels();
            }
            game = Game.newGame(this, this, layout, panels, restore);
        } else {
            game = Game.newGame(this, this, layout, restore);
        }
    }

    private BaseLayout initGameLayout() {
        layout = LayoutFactory.getLayout(this, preferences, this, this);
        BoardFragment frag = BoardFragment.newInstance();
        frag.setRetainInstance(true);
        frag.setLayout(layout);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.board_layout_place, frag, BoardFragment.FRAGMENT_TAG)
          .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
          .commit();
        return layout;
    }

    private void initPlayersPanels() {
        panels = new PlayersPanels(this, preferences);
    }

    private void startNewGame(boolean save) {
        if (save) {
            game.saveGame();
        }
        initGame(false);
    }

    private void startNewGameSameTeams(boolean save) {
        if (save) {
            game.saveGame();
        }
        game.initNewGameSameTeams();
    }

    private void startNewGame(boolean save, Team hTeam, Team gTeam) {
        startNewGame(save);
        game.setHomeTeam(hTeam).setGuestTeam(gTeam);
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
        soundHornId = soundPool.load(this, R.raw.buzzer, 1);
        hornLength = 1000;
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
                    () -> {
                        if (whistlePressed) {
                            stopWhistle();
                            playWhistle(-1);
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
            game.pauseGame();
        }
        soundHornStreamId = soundPool.play(soundHornId, 1, 1, 0, repeats-1, 1);
        hornRepeats = repeats;
        if (repeats != -1) {
            new Handler().postDelayed(
                    () -> {
                        if (hornPressed) {
                            stopHorn();
                            playHorn(-1);
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

    private void showConfirmDialog(DialogTypes type) {
        ConfirmDialog.newInstance(type).show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
    }

    private void showNewGameDialog() {
        NewGameDialog dialog = NewGameDialog.newInstance(!game.gameSaved, preferences.autoSaveResults,
                !game.homeTeamSet(), !game.guestTeamSet());
        if (!game.homeTeamSet()) {
            dialog.setHomeName(game.getName(HOME));
        }
        if (!game.guestTeamSet()) {
            dialog.setGuestName(game.getName(GUEST));
        }
        dialog.show(getFragmentManager(), NewGameDialog.TAG);
    }

    private void showWinDialog(String team, int winScore, int loseScore) {
        ConfirmDialog.newInstance(DialogTypes.GAME_END, team, winScore, loseScore).show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
    }

    private void showListDialog(DialogTypes type) {
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
            Toast.makeText(this, getResources().getString(R.string.sp_no_data), Toast.LENGTH_LONG).show();
            return;
        }
        for (Map.Entry<Integer, SidePanelRow> entry : choices.entrySet()) {
            numberNameList.add(String.format("%d: %s", entry.getValue().getNumber(), entry.getValue().getName()));
        }
        Button bu = game.getSelectedPlayer();
        int number = bu.getTag() != null ? ((SidePanelRow)bu.getTag()).getNumber() : -1;
        ListDialog.newInstance(DialogTypes.SUBSTITUTE, numberNameList, left, number).show(getFragmentManager(), ListDialog.TAG);
    }

    private void chooseTeamNameDialog(int team, String name) {
        TeamEditInGameDialog.newInstance(team, name).show(getFragmentManager(), TeamEditInGameDialog.TAG);
    }

    private void showMainTimePicker() {
        DialogFragment mainTimePicker = TimePickerDialog.newInstance(game.mainTime, true);
        mainTimePicker.show(getFragmentManager(), TAG_FRAGMENT_MAIN_TIME_PICKER);
    }

    private void showShotTimePicker() {
        DialogFragment mainTimePicker = TimePickerDialog.newInstance(game.shotTime, false);
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
                duration = 30;
                break;
            case 4:
                duration = 60;
                break;
            case 5:
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
        game.clearPlayersPanel(type, left);
    }

    @Override
    public void onSubstituteListSelect(boolean left, int newNumber) {
        game.substitutePlayer(left, newNumber);
    }

    @Override
    public void onSelectAddPlayers(int which, boolean left) {
        if (which == 0) {
            ListDialog.newInstance(DialogTypes.SELECT_TEAM, game.getTeamType(left)).show(getFragmentManager(), ListDialog.TAG);
        } else {
            game.addPlayers(left);
        }
    }

    @Override
    public void onSelectTeam(int type, Team team) {
        Fragment dialog;
        dialog = getFragmentManager().findFragmentByTag(NewGameDialog.TAG);
        if (dialog == null) {
            dialog = getFragmentManager().findFragmentByTag(TeamEditInGameDialog.TAG);
        }
        if (dialog != null) {
            ((TeamSelector) dialog).handleTeamSelect(type, team);
        } else {
            game.setTeam(team, type);
        }
    }

    @Override
    public void onNameChanged(String value, int team) {
        if (value.length() > 0) {
            game.resetTeamAndSetName(team, value);
        }
    }

    @Override
    public void onTeamChanged(Team value, int team) {
        game.setTeam(value, team);
    }

    @Override
    public void onConfirmDialogPositive(DialogTypes type, int teamType) {
        if (type == DialogTypes.TEAM_ALREADY_SELECTED) {
            game.confirmSetTeam(teamType);
        }
    }

    @Override
    public void onConfirmDialogNegative(DialogTypes type, int teamType) {
        if (type == DialogTypes.TEAM_PLAYERS_FEW) {
            game.confirmSelectTeamPlayers(teamType);
        }
    }

    @Override
    public void onConfirmDialogPositive(DialogTypes type) {
        switch (type) {
            case GAME_END:
                showNewGameDialog();
                break;
            case NEW_GAME:
            case RESULT_SAVE:
                startNewGame(true);
                break;
            case CAPTAIN_ALREADY_ASSIGNED:
                PlayerEditDialog f = (PlayerEditDialog) getFragmentManager().findFragmentByTag(PlayerEditDialog.TAG);
                if (f != null) {f.changeCaptainConfirmed();}
                break;
        }
    }

    @Override
    public void onConfirmDialogNeutral() {
        game.saveGame();
    }

    @Override
    public void onConfirmDialogNegative(DialogTypes type) {
        if (type == DialogTypes.RESULT_SAVE) {
            startNewGame(false);
        }
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
    public void onSidePanelShowConfirmDialog(DialogTypes type, boolean left) {
        ConfirmDialog.newInstance(type, game.getTeamType(left)).show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
    }

    @Override
    public void onSidePanelShowSelectDialog(Team team, boolean left) {
        SelectPlayersDialog.newInstance().setTeam(team, game.getTeamType(left)).show(getFragmentManager(), TAG_FRAGMENT_CONFIRM);
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
    public void onAddPlayerInGame(boolean left, int number, String name, boolean captain) {
        game.addPlayer(left, number, name, captain);
    }

    @Override
    public void onEditPlayerInGame(boolean left, int id, int number, String name, boolean captain) {
        game.editPlayer(left, id, number, name, captain);
    }

    @Override
    public void onDeletePlayerInGame(boolean left, int id) {
        game.deletePlayer(left, id);
    }

    @Override
    public int onCheckPlayerInGame(boolean left, int number, boolean captain) {
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
                addCameraView();
                break;
            case HORN:
                playHorn();
                break;
            case NEW_PERIOD:
                showListDialog(DialogTypes.NEW_PERIOD);
                break;
            case SWITCH_SIDES:
                game.switchSides();
                break;
            case TIMEOUT:
                showListDialog(DialogTypes.TIMEOUT);
                break;
            case WHISTLE:
                break;
        }
    }

    @Override
    public boolean onArrowLongClick() {
        game.clearPossession();
        return true;
    }

    @Override
    public boolean onFoulsLongClick(boolean left) {
        game.nullFouls(left);
        return true;
    }

    @Override
    public boolean onMainTimeLongClick() {
        showMainTimePicker();
        return true;
    }

    @Override
    public boolean onNameLongClick(boolean left) {
        chooseTeamNameDialog(game.getTeamType(left), game.getName(left));
        return true;
    }

    @Override
    public boolean onPeriodLongClick() {
        game.newPeriod(false);
        return true;
    }

    @Override
    public boolean onScoreLongClick(boolean left) {
        game.nullScore(left);
        return true;
    }

    @Override
    public boolean onShotTimeLongClick() {
        showShotTimePicker();
        return true;
    }

    @Override
    public boolean onTimeoutsLongClick(boolean left) {
        game.nullTimeouts(left);
        return true;
    }

    @Override
    public boolean onTimeouts20LongClick(boolean left) {
        game.nullTimeouts20(left);
        return true;
    }

    @Override
    public boolean onPlayerButtonLongClick(boolean left) {
        showListDialog(left);
        return true;
    }

    @Override
    public void onPlayHorn() {
        playHorn();
    }

    @Override
    public BaseLayout onInitLayout() {
        return initGameLayout();
    }

    @Override
    public PlayersPanels onInitPanels() {
        if (panels == null) {
            initPlayersPanels();
        }
        return panels;
    }

    @Override
    public void onDeletePanels() {
        panels = null;
    }

    @Override
    public void onConfirmDialog(DialogTypes type) {
        showConfirmDialog(type);
    }

    @Override
    public void onNewGameDialog() {
        showNewGameDialog();
    }

    @Override
    public void onWinDialog(String team, int winScore, int loseScore) {
        showWinDialog(team, winScore, loseScore);
    }

    @Override
    public void onShowTimeout(long seconds, String team) {
        showTimeout(seconds, team);
    }

    @Override
    public void onSwitchSides(boolean show) {
        showSwitchFragment(show);
    }

    @Override
    public void onShowToast(int resId, int len) {
        Toast.makeText(this, getResources().getString(resId), len).show();
    }

    @Override
    public void onShowToast(int resId, int len, Object... args) {
        Toast.makeText(this, String.format(getResources().getString(resId), args), len).show();
    }

    @Override
    public void onViewCreated(BaseLayout layout) {
        game.setLayout(layout);
    }

    @Override
    public void onCameraPause() {
        getFragmentManager().popBackStack();
        game.setLayout(layout);
    }

    @Override
    public void onStartSameTeams(boolean saveResult) {
        startNewGameSameTeams(saveResult);
    }

    @Override
    public void onStartNewTeams(boolean saveResult, Team hTeam, Team gTeam) {
        startNewGame(false, hTeam, gTeam);
    }

    @Override
    public void onStartNoTeams(boolean saveResult) {
        startNewGame(false, null, null);
    }

    @Override
    public boolean onSaveTeam(int teamType) {
        return game.saveTeam(teamType);
    }

    @Override
    public void onSelectPlayers(int teamType) {
        game.confirmSelectTeamPlayers(teamType);
    }
}