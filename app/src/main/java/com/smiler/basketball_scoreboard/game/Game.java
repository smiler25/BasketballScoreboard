package com.smiler.basketball_scoreboard.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.CountDownTimer;
import com.smiler.basketball_scoreboard.Level;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.Rules;
import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.dialogs.DialogTypes;
import com.smiler.basketball_scoreboard.layout.BaseLayout;
import com.smiler.basketball_scoreboard.layout.PlayersPanels;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.PrefActivity;
import com.smiler.basketball_scoreboard.preferences.Preferences;
import com.smiler.basketball_scoreboard.results.Result;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.realm.Realm;

import static com.smiler.basketball_scoreboard.Constants.ACTION_FLS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_NONE;
import static com.smiler.basketball_scoreboard.Constants.ACTION_PTS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO20;
import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.MINUTES_2;
import static com.smiler.basketball_scoreboard.Constants.NO_TEAM;
import static com.smiler.basketball_scoreboard.Constants.OVERTIME;
import static com.smiler.basketball_scoreboard.Constants.REGULAR_PERIOD;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;
import static com.smiler.basketball_scoreboard.Constants.SECOND;
import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;
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
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_MILLIS;


public class Game {
    public static final String TAG = "BS-Game";
    private static Game instance;
    private GameListener listener;
    private BaseLayout layout;
    private PlayersPanels panels;

    private boolean mainTimerOn, shotTimerOn;
    private boolean directTimerStopped;
    private int possession = NO_TEAM;
    public long mainTime, shotTime;
    private long startTime, totalTime;
    private boolean infiniteDirectTimer;
    private long timeoutFullDuration;
    private short hScore, gScore;
    private short hScorePrev, gScorePrev;
    private short hFouls, gFouls;
    private short hTimeouts, gTimeouts;
    private short hTimeouts20, gTimeouts20;
    private short takenTimeoutsFull;
    private short maxTimeouts, maxTimeouts20, maxTimeouts100;
    private short period;
    private String hName, gName;
    private Handler customHandler = new Handler();
    private CountDownTimer mainTimer, shotTimer;
    private boolean leftIsHome = true;

    private long mainTickInterval = SECOND;
    private long shotTickInterval = SECOND;
    private boolean changedUnder2Minutes = false;
    private boolean scoreSaved = false;
    private ActionRecord lastAction;
    private int timesTie = 1, timesLeadChanged = 0;
    private int hMaxLead = 0, gMaxLead = 0;
    private short hActionType = ACTION_NONE, gActionType = ACTION_NONE;
    private int hActionValue = 0, gActionValue = 0;

    private Preferences preferences;
    private Result gameResult;
    private SharedPreferences statePref;
    private boolean showTimeoutDialog;

    public interface GameListener {
        void onPlayHorn();
        void onNewGame();
        BaseLayout onInitLayout();
        PlayersPanels onInitPanels();
        void onConfirmDialog(DialogTypes type);
        void onWinDialog(DialogTypes type, String team, int winScore, int loseScore);
        void onShowTimeout(long seconds, String team);
        void onSwitchSides(boolean show);
        void onShowToast(int resId, int len);
    }

    public static Game newGame(Context context, GameListener listener, BaseLayout layout, boolean restore) {
        instance = new Game(context, listener, layout, restore);
        return instance;
    }

    public static Game newGame(Context context, GameListener listener, BaseLayout layout, PlayersPanels panels, boolean restore) {
        instance = new Game(context, listener, layout, panels, restore);
        return instance;
    }

    private Game(Context context, GameListener listener, BaseLayout layout, boolean restore) {
        this(context, listener, layout, null, restore);
    }

    private Game(Context context, GameListener listener, BaseLayout layout, PlayersPanels panels, boolean restore) {
        this.layout = layout;
        this.listener = listener;
        this.panels = panels;
        if (restore) {
            initRestore(context);
        } else {
            init(context);
        }
    }

    private void initRestore(Context context) {
        init(context);
        if (preferences.saveOnExit) {
            getSavedState();
            setSavedState();
        }
    }

    private void init(Context context) {
        statePref = ((Activity)context).getPreferences(Context.MODE_PRIVATE);
        preferences = Preferences.getInstance(context);
        preferences.read();
        gameResult = new Result(hName, gName);
        if (panels != null) {
            deletePlayers();
        }

        if (listener != null) {
            if (layout == null) {
                layout = listener.onInitLayout();
            } else {
                if (preferences.layoutChanged || preferences.timeoutsRulesChanged) {
                    layout = listener.onInitLayout();
                }
            }
        }
        setZeroState();
        handleNames();
        leftIsHome = true;
    }

    private void setZeroState() {
        if (preferences.useDirectTimer) {
            totalTime = preferences.mainTimePref;
            mainTime = 0;
        } else {
            mainTime = totalTime = preferences.mainTimePref;
            infiniteDirectTimer = false;
        }

        layout.setMainTimeFormat(TIME_FORMAT);
        layout.setMainTimeText(mainTime);

        if (preferences.layoutType == BaseLayout.GAME_LAYOUT.COMMON) {
            period = 1;
            layout.setPeriod(Short.toString(period), true);
        }
        if (preferences.layoutType != BaseLayout.GAME_LAYOUT.SIMPLE) {
            if (preferences.enableShotTime) {
                shotTime = preferences.shotTimePref;
                layout.setShotTimeText(shotTime);
            }
            setTimeouts();
            nullFouls();
        }
        nullScore(NO_TEAM);
        if (preferences.arrowsOn) { clearPossession(); }
    }

    public void saveInstanceState(Bundle outState) {
        if (panels != null) {
            savePanelsInstanceState(outState);
        }
    }

    public void restoreInstanceState(Bundle inState) {
        if (panels != null) {
            restorePanelsInstanceState(inState);
        }
    }

    private void savePanelsInstanceState(Bundle outState) {
        panels.saveInstanceState(outState);
    }

    private void restorePanelsInstanceState(Bundle inState) {
        panels.restoreInstanceState(inState);
    }

    public void setLayout(BaseLayout layout) {
        this.layout = layout;
        setSavedState();
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public void stopGame() {
        if (preferences != null && preferences.saveOnExit) {
            saveCurrentState();
        }
    }

    public void resumeGame() {
        if (PrefActivity.prefChangedRestart) {
            showDialog(DialogTypes.NEW_GAME, false);
        } else if (PrefActivity.prefChangedNoRestart) {
            if (PrefActivity.prefColorChanged && layout != null) {
                layout.setColors();
            }
            preferences.readNoRestart();
            if ((preferences.layoutChanged || preferences.timeoutsRulesChanged) && listener != null) {
                layout = listener.onInitLayout();
                if (preferences.timeoutsRulesChanged) {
                    try {
                        setTimeouts();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "resumeGame -> setTimeouts error: " + e);
                    }
                }
                preferences.layoutChanged = false;
                preferences.timeoutsRulesChanged = false;
            }
            if (preferences.fixLandscapeChanged) {
                layout.handleScoresSize();
                preferences.fixLandscapeChanged = false;
            }
        }

        if (preferences.spStateChanged) {
            handlePlayersPanels();
        }
        if (preferences.shotTimePrefChanged) {
            handleShotTimes();
        }
        if (preferences.arrowsStateChanged) {
            handleArrowsVisibility();
        }
    }

    private void saveCurrentState() {
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
        switch (preferences.timeoutRules) {
            case FIBA:
            case STREETBALL:
                editor.putInt(STATE_HOME_TIMEOUTS, hTimeouts);
                editor.putInt(STATE_GUEST_TIMEOUTS, gTimeouts);
                break;
            case NBA:
                editor.putInt(STATE_HOME_TIMEOUTS_NBA, hTimeouts);
                editor.putInt(STATE_GUEST_TIMEOUTS_NBA, gTimeouts);
                editor.putInt(STATE_HOME_TIMEOUTS20, hTimeouts20);
                editor.putInt(STATE_GUEST_TIMEOUTS20, gTimeouts20);
                break;
        }
        if (preferences.arrowsOn) {
            editor.putInt(STATE_POSSESSION, possession);
        }
        editor.apply();
        if (preferences.spOn && panels != null) {
            panels.saveState();
        }
    }

    private void clearSavedState() {
        statePref.edit().clear().apply();
        if (preferences.spOn && panels != null) {
            panels.clearSavedState();
        }
    }

    private void getSavedState() {
        shotTime = statePref.getLong(STATE_SHOT_TIME, preferences.shotTimePref);
        mainTime = totalTime = statePref.getLong(STATE_MAIN_TIME, preferences.mainTimePref);
        period = (short) statePref.getInt(STATE_PERIOD, 1);
        hScore = (short) statePref.getInt(STATE_HOME_SCORE, 0);
        gScore = (short) statePref.getInt(STATE_GUEST_SCORE, 0);
        hName = statePref.getString(STATE_HOME_NAME, preferences.hName);
        gName = statePref.getString(STATE_GUEST_NAME, preferences.gName);
        hFouls = (short) statePref.getInt(STATE_HOME_FOULS, 0);
        gFouls = (short) statePref.getInt(STATE_GUEST_FOULS, 0);
        switch (preferences.timeoutRules) {
            case FIBA:
            case STREETBALL:
                hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS, 0);
                gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS, 0);
                break;
            case NBA:
                hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS_NBA, 0);
                gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS_NBA, 0);
                hTimeouts20 = (short) statePref.getInt(STATE_HOME_TIMEOUTS20, 0);
                gTimeouts20 = (short) statePref.getInt(STATE_GUEST_TIMEOUTS20, 0);
                break;
        }
        possession = statePref.getInt(STATE_POSSESSION, possession);
    }

    public void setSavedState() {
        setTeamNames();
        layout.setHomeScore(hScore);
        layout.setGuestScore(gScore);

        switch (preferences.layoutType) {
            case COMMON:
                setStandardState();
                break;
            case SIMPLE:
                setSimpleState();
                break;
            case STREETBALL:
                setStreetballState();
                break;
        }
        layout.setMainTimeText(mainTime);
        if (preferences.arrowsOn) { setPossession(possession); }
    }

    private void setStandardState() {
        if (preferences.enableShotTime) {
            layout.setShotTimeText(shotTime);
        }
        layout.setHomeFoul(Short.toString(hFouls), getFoulLevel(hFouls));
        layout.setGuestFoul(Short.toString(gFouls), getFoulLevel(gFouls));
        long mainTimeTemp = mainTime;
        setPeriod();
        mainTime = mainTimeTemp;
        layout.setHomeTimeouts(Short.toString(hTimeouts), noTimeouts(hTimeouts));
        layout.setGuestTimeouts(Short.toString(gTimeouts), noTimeouts(gTimeouts));
        if (preferences.timeoutRules == Rules.TO_RULES.NBA) {
            layout.setHomeTimeouts20(Short.toString(hTimeouts20), noTimeouts(gTimeouts20));
            layout.setGuestTimeouts20(Short.toString(gTimeouts20), noTimeouts(gTimeouts20));
        }
    }

    private void setSimpleState() {
        if (preferences.useDirectTimer) {
            mainTime = 0;
        } else {
            mainTime = totalTime = preferences.mainTimePref;
        }
        layout.setMainTimeFormat(TIME_FORMAT);
        layout.setMainTimeText(mainTime);
    }

    private void setStreetballState() {
        if (preferences.enableShotTime) {
            layout.setShotTimeText(shotTime);
        }
        layout.setHomeFoul(Short.toString(hFouls), getFoulLevel(hFouls));
        layout.setGuestFoul(Short.toString(gFouls), getFoulLevel(gFouls));
        layout.setHomeTimeouts(Short.toString(hTimeouts), noTimeouts(hTimeouts));
        layout.setGuestTimeouts(Short.toString(gTimeouts), noTimeouts(gTimeouts));
    }

    private void showDialog(DialogTypes type, boolean win) {
        if (win) {
            if (hScore > gScore) {
                listener.onWinDialog(type, hName, hScore, gScore);
            } else {
                listener.onWinDialog(type, gName, gScore, hScore);
            }
        } else {
            listener.onConfirmDialog(type);
        }
    }

    private void showTimeout(long duration, String team) {
        listener.onShowTimeout(duration, team);
    }

    private void showTimeout() {
        if (preferences.layoutType == BaseLayout.GAME_LAYOUT.STREETBALL) {
            showTimeout(30, "");
        } else {
            if (period == 2) {
                showTimeout(900, "");
            } else {
                showTimeout(120, "");
            }
        }
    }

    public int getTeam(boolean left) {
        if (left == leftIsHome) {
            return HOME;
        } else {
            return GUEST;
        }
    }


    // common actions
    private void endOfGameActions(int dontAskNewGame) {
        switch (dontAskNewGame) {
            case 1:
                break;
            case 2:
                saveDb();
//                newGame();
                break;
            case 3:
//                newGame();
                break;
        }
    }

    public void newGame() {
        if (preferences.autoSaveResults == 0) {
            newGameSave();
        } else if (preferences.autoSaveResults == 2) {
            showDialog(DialogTypes.RESULT_SAVE, false);
        }
        clearSavedState();
    }

    public void newGameSave() {
        saveGame();
        listener.onNewGame();
        clearSavedState();
    }

    private void save() {
        if (!scoreSaved) {
            // возможно, надо учитывать сброс периода
            if (period > 0 && gameResult.getHomeScoreByPeriod().size() == period) {
                gameResult.replacePeriodScores(period, hScore, gScore);
            } else {
                gameResult.addPeriodScores(hScore, gScore);
            }
            scoreSaved = true;
        }
    }

    private void saveDb() {
        if (hScore == 0 && gScore == 0) {
            return;
        }
        if (period < preferences.numRegularPeriods || mainTime != 0 || mainTime != preferences.mainTimePref) {
            gameResult.setComplete(false);
        } else {
            gameResult.setComplete(true);
        }

        Realm realm = RealmController.with().getRealm();
        Number lastId = realm.where(Results.class).max("id");
        final long nextID = lastId != null ? (long) lastId + 1 : 0;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Results result = realm.createObject(Results.class, nextID);
                result.setDate(new Date())
                        .setFirstTeamName(hName)
                        .setSecondTeamName(gName)
                        .setFirstScore(hScore)
                        .setSecondScore(gScore)
                        .setFirstPeriods(gameResult.getHomeScoreByPeriodString())
                        .setSecondPeriods(gameResult.getGuestScoreByPeriodString())
                        .setShareString(gameResult.getResultString(period > preferences.numRegularPeriods))
                        .setRegularPeriods(preferences.numRegularPeriods)
                        .setComplete(gameResult.isComplete());

                GameDetails details = realm.createObject(GameDetails.class);
                details.setLeadChanged(timesLeadChanged)
                        .setHomeMaxLead(hMaxLead)
                        .setGuestMaxLead(gMaxLead)
                        .setTie(timesTie);
                if (preferences.playByPlay == 2) {
                    details.setPlayByPlay(gameResult.toString());
                }
                result.setDetails(details);

                if (preferences.spOn) {
                    TreeMap<Integer, SidePanelRow> allHomePlayers = getHomePlayers();
                    TreeMap<Integer, SidePanelRow> allGuestPlayers = getGuestPlayers();
                    for (Map.Entry<Integer, SidePanelRow> entry : allHomePlayers.entrySet()) {
                        SidePanelRow row = entry.getValue();
                        PlayersResults playersResults = realm.createObject(PlayersResults.class);
                        playersResults.setGame(result)
                                .setTeam(hName)
                                .setNumber(row.getNumber())
                                .setName(row.getName())
                                .setPoints(row.getPoints())
                                .setFouls(row.getFouls())
                                .setCaptain(row.getCaptain());
                    }
                    for (Map.Entry<Integer, SidePanelRow> entry : allGuestPlayers.entrySet()) {
                        SidePanelRow row = entry.getValue();
                        PlayersResults playersResults = realm.createObject(PlayersResults.class);
                        playersResults.setGame(result)
                                .setTeam(gName)
                                .setNumber(row.getNumber())
                                .setName(row.getName())
                                .setPoints(row.getPoints())
                                .setFouls(row.getFouls())
                                .setCaptain(row.getCaptain());
                    }
                }
            }
        });
    }

    public void saveGame() {
        save();
        saveDb();
    }

    public String getShareString() {
        return gameResult.getResultString(period > preferences.numRegularPeriods);
    }

    public void switchSides() {
        listener.onSwitchSides(true);
        layout.switchSides();
        if (panels != null) {
            try {
                panels.switchSides();
            } catch (NullPointerException e) {
                Log.d(TAG, "Left or right panel is null");
            }
        }
        if (preferences.arrowsOn) {
            switchPossession();
        }
        leftIsHome = !leftIsHome;
        listener.onSwitchSides(false);
    }

    private void addAction(int type, int team, int value) {
        if (preferences.playByPlay != 0) {
            lastAction = gameResult.addAction(mainTime, type, team, value);
        }
    }

    private void updateStats() {
        if (hScore == gScore) {
            timesTie++;
        } else if (hScore > gScore != hScorePrev > gScorePrev) {
            timesLeadChanged++;
        }
        if (hScore - gScore > hMaxLead) {
            hMaxLead = hScore - gScore;
        }
        if (gScore - hScore > gMaxLead) {
            gMaxLead = gScore - hScore;
        }
    }

    public boolean cancelLastAction() {
        lastAction = gameResult.getLastAction();
        if (lastAction == null) {
            return false;
        }
        switch (lastAction.getType()) {
            case ACTION_PTS:
                revertScore(lastAction.getTeam(), lastAction.getValue());
                if (preferences.spOn) {
                    cancelPlayerScore(lastAction.getTeam(), lastAction.getNumber(), lastAction.getValue());
                }
                break;
            case ACTION_FLS:
                revertFoul(lastAction.getTeam());
                if (preferences.spOn) {
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


    // scores
    public void nullScore(boolean left) {
        nullScore(left == leftIsHome ? HOME : GUEST);
    }

    public void nullScore(int team) {
        switch (team) {
            case HOME:
                hScore = 0;
                layout.setHomeScore(0);
                break;
            case GUEST:
                gScore = 0;
                layout.setGuestScore(0);
                break;
            default:
                hScore = gScore = 0;
                layout.setHomeScore(0);
                layout.setGuestScore(0);
                break;
        }
    }

    public void changeScore(boolean left, int value) {
        changeScore(left == leftIsHome ? HOME : GUEST, value);
    }

    public void changeScore(int team, int value) {
        if (team == HOME) {
            hScorePrev = hScore;
            if (!changeHomeScore(value)) {
                return;
            }
            hActionType = ACTION_PTS;
            hActionValue += value;
            addAction(ACTION_PTS, HOME, value);
        } else {
            gScorePrev = gScore;
            if (!changeGuestScore(value)) {
                return;
            }
            gActionType = ACTION_PTS;
            gActionValue += value;
            addAction(ACTION_PTS, GUEST, value);
        }
        updateStats();
    }

    private boolean changeGuestScore(int value) {
        if (value < 0 && gScore < -value) {
            return false;
        }
        gScore += value;
        if (value != 0) {
            handleScoreChange();
        }
        layout.setGuestScore(gScore);
        return true;
    }

    private boolean changeHomeScore(int value) {
        if (value < 0 && hScore < -value) {
            return false;
        }
        hScore += value;
        if (value != 0) {
            handleScoreChange();
        }
        layout.setHomeScore(hScore);
        return true;
    }

    private void handleScoreChange() {
        if (preferences.layoutType != BaseLayout.GAME_LAYOUT.SIMPLE &&
                preferences.enableShotTime && preferences.restartShotTimer) {
            if (mainTimerOn) {
                startShotCountDownTimer(preferences.shotTimePref);
            } else {
                shotTime = preferences.shotTimePref;
            }
        }
        if (preferences.actualTime == 2 || preferences.actualTime == 3 && mainTime < SECONDS_60) {
            pauseGame();
        }
        scoreSaved = false;
    }

    private void revertScore(int team, int value) {
        if (team == HOME) {
            changeHomeScore(-value);
        } else {
            changeGuestScore(-value);
        }
    }


    // times
    public void mainTimeClick() {
        if (!mainTimerOn) {
            if (preferences.useDirectTimer && mainTime == 0) {
                startDirectTimer();
            } else if (mainTime > 0){
                startMainCountDownTimer();
            }
            layout.setBlockLongClick(true);
        } else {
            pauseGame();
            layout.setBlockLongClick(false);
        }
    }

    public void shotTimeClick() {
        shotTickInterval = SECOND;
        if (mainTimerOn) {
            shotTimer.cancel();
            startShotCountDownTimer(preferences.shotTimePref);
        } else {
            if (shotTime == preferences.shotTimePref) {
                shotTime = preferences.shortShotTimePref;
            } else {
                shotTime = preferences.shotTimePref;
            }
            layout.setShotTimeText(shotTime);
        }
    }

    private void startMainCountDownTimer() {
        mainTimer = new CountDownTimer(mainTime, mainTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mainTime = millisUntilFinished;
                layout.setMainTimeText(mainTime);
                if (preferences.enableShotTime && mainTime < shotTime && shotTimerOn) {
                    shotTimer.cancel();
                }
                if (mainTime < MINUTES_2 && !changedUnder2Minutes) {
                    changedUnder2Minutes = true;
                    under2Minutes();
                }
                if (preferences.fractionSecondsMain && mainTime < SECONDS_60 && mainTickInterval == SECOND) {
                    cancel();
                    mainTickInterval = 100;
                    layout.setMainTimeFormat(TIME_FORMAT_MILLIS);
                    startMainCountDownTimer();
                }
                if (preferences.enableShotTime && mainTime < shotTime && layout.shotTimeVisible()) {
                    layout.hideShotTime();
                } else if (preferences.enableShotTime && mainTime < preferences.shortShotTimePref && layout.shotTimeSwitchVisible()) {
                    layout.hideShotTimeSwitch();
                }
            }

            @Override
            public void onFinish() {
                handleMainCountDownEnd();
            }
        }.start();
        mainTimerOn = true;
        if (preferences.layoutType != BaseLayout.GAME_LAYOUT.SIMPLE &&
                preferences.enableShotTime && !shotTimerOn && mainTime > shotTime) {
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
        layout.showShotTime();
        shotTimer = new CountDownTimer(shotTime, shotTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                shotTime = millisUntilFinished;
                layout.setShotTimeText(shotTime);
                if (shotTime < 5 * SECOND && shotTickInterval == SECOND) {
                    shotTickInterval = 100;
                    shotTimer.cancel();
                    startShotCountDownTimer();
                }
            }

            @Override
            public void onFinish() {
                pauseGame();
                if (preferences.autoSound == 1 || preferences.autoSound == 3) {
                    playHorn();
                }
                layout.setShotTimeText(0);
                layout.blinkShotTime();
                shotTime = preferences.shotTimePref;
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
        layout.setMainTimeFormat(TIME_FORMAT);
        mainTimerOn = true;
        customHandler.postDelayed(directTimerThread, 0);
        if (preferences.layoutType != BaseLayout.GAME_LAYOUT.SIMPLE && preferences.enableShotTime) {
            startShotCountDownTimer();
        }
    }

    private void stopDirectTimer() {
        startTime = SystemClock.uptimeMillis();
        mainTime = 0;
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && preferences.enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        directTimerStopped = true;
        mainTimerOn = shotTimerOn = false;
        layout.setBlockLongClick(false);
    }

    private void pauseDirectTimer() {
        customHandler.removeCallbacks(directTimerThread);
        if (shotTimer != null && preferences.enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        mainTimerOn = shotTimerOn = directTimerStopped = false;
    }

    private Runnable directTimerThread = new Runnable() {
        @Override
        public void run() {
            mainTime = SystemClock.uptimeMillis() - startTime;
            layout.setMainTimeText(mainTime);
            if (!infiniteDirectTimer &&  mainTime >= totalTime) {
                stopDirectTimer();
                return;
            }
            customHandler.postDelayed(this, SECOND);
        }
    };

    private void handleMainCountDownEnd() {
        mainTime = 0;
        mainTimerOn = false;
        mainTickInterval = SECOND;
        if (preferences.autoSound >= 2) {
            playHorn();
        }
        layout.setMainTimeText(0);
        if (preferences.enableShotTime && shotTimerOn) {
            shotTimer.cancel();
            layout.setShotTimeText(0);
        }
        save();

        switch (preferences.layoutType) {
            case STREETBALL:
                handleEndTime3x3();
                break;
            default:
                handleEndTimeCommon();
                break;
        }
        layout.setBlockLongClick(false);
    }

    private void handleEndTimeCommon() {
        if (period >= preferences.numRegularPeriods && hScore != gScore) {
            handleWin();
        }
        if (preferences.autoShowBreak && showTimeoutDialog) {
            showTimeout();
        }
        if (preferences.autoSwitchSides) {
            switchSides();
        }
    }

    private void handleEndTime3x3() {
        if (hScore != gScore) {
            handleWin();
        } else {
            infiniteDirectTimer = true;
            preferences.useDirectTimer = true;
            if (preferences.autoShowBreak) {
                showTimeout();
            }
        }
    }

    private void handleWin() {
        if (preferences.dontAskNewGame == 0) {
            showDialog(DialogTypes.NEW_GAME, true);
        } else {
            endOfGameActions(preferences.dontAskNewGame);
        }
        showTimeoutDialog = false;
    }

    private void under2Minutes() {
        if (preferences.timeoutRules == Rules.TO_RULES.NBA) {
            if (period == 4) {
                if (hTimeouts == 2 || hTimeouts == 3) {
                    hTimeouts = 1;
                    hTimeouts20++;
                    layout.setHomeTimeouts("1", noTimeouts(hTimeouts));
                    layout.setHomeTimeouts20(Short.toString(hTimeouts20), noTimeouts(hTimeouts20));
                }
                if (gTimeouts == 2 || gTimeouts == 3) {
                    gTimeouts = 1;
                    gTimeouts20++;
                    layout.setGuestTimeouts("1", noTimeouts(gTimeouts));
                    layout.setGuestTimeouts20(Short.toString(gTimeouts20), noTimeouts(gTimeouts20));
                }
            }
        }
    }

    public void pauseGame() {
        if (preferences.useDirectTimer) {
            pauseDirectTimer();
        } else if (mainTimerOn) {
            mainTimer.cancel();
        }
        if (shotTimer != null && preferences.enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        mainTimerOn = shotTimerOn = false;
    }

    public void changeMainTime(long value) {
        mainTime = value;
        if (preferences.fractionSecondsMain && mainTime < SECONDS_60) {
            mainTickInterval = 100;
            layout.setMainTimeFormat(TIME_FORMAT_MILLIS);
        } else {
            layout.setMainTimeFormat(TIME_FORMAT);
        }

        if (preferences.enableShotTime && mainTime > shotTime) {
            layout.showShotTime();
            layout.showShotTimeSwitch();
        }
        layout.setMainTimeText(mainTime);
    }

    public void changeShotTime(long value) {
        shotTime = value;
        if (shotTime < 5 * SECOND) {
            shotTickInterval = 100;
        }
        layout.setShotTimeText(shotTime);
    }

    public void shotTimeSwitch() {
        shotTickInterval = SECOND;
        if (shotTimer != null && preferences.enableShotTime && shotTimerOn) {
            shotTimer.cancel();
        }
        shotTime = preferences.shortShotTimePref;
        if (mainTimerOn) {
            startShotCountDownTimer(preferences.shortShotTimePref);
        } else {
            layout.setShotTimeText(shotTime);
        }
        if (preferences.shortShotTimePref < mainTime) {
            layout.showShotTime();
        }
    }

    private void handleShotTimes() {
        if (preferences.layoutType != BaseLayout.GAME_LAYOUT.SIMPLE) {
            if (!preferences.enableShotTime) {
                layout.hideShotTime();
                layout.hideShotTimeSwitch();
            } else {
                layout.showShotTime();
                if (preferences.enableShortShotTime) {
                    layout.showShotTimeSwitch();
                    layout.setShotTimeSwitchText(preferences.shortShotTimePref / 1000);
                } else {
                    layout.hideShotTimeSwitch();
                }
            }
        }
    }

    private void playHorn() {
        if (listener != null) {
            listener.onPlayHorn();
        }
    }


    // period
    public void newPeriod(boolean next) {
        pauseGame();
        changedUnder2Minutes = false;
        if (next) {
            period++;
        } else {
            period = 1;
        }
        setPeriod();
        if (preferences.enableShotTime) {
            shotTime = preferences.shotTimePref;
            shotTickInterval = SECOND;
            handleShotTimes();
            layout.setShotTimeText(shotTime);
        }
        mainTickInterval = SECOND;
        if (period <= preferences.numRegularPeriods) {
            nullFouls();
        }
        setTimeouts();
        save();
        scoreSaved = false;
    }

    public void newPeriod(int type) {
        mainTickInterval = SECOND;
        switch (type) {
            case REGULAR_PERIOD:
                mainTime = preferences.mainTimePref;
                break;
            case OVERTIME:
                mainTime = preferences.overTimePref;
                break;
        }
        layout.setMainTimeFormat(TIME_FORMAT);
        layout.setMainTimeText(mainTime);
    }

    private void setPeriod() {
        if (period <= preferences.numRegularPeriods) {
            mainTime = totalTime = preferences.mainTimePref;
            layout.setPeriod(Short.toString(period), true);
        } else {
            mainTime = totalTime = preferences.overTimePref;
            layout.setPeriod(Short.toString(period), false);
        }
        if (preferences.useDirectTimer) {
            mainTime = 0;
        }
        layout.setMainTimeFormat(TIME_FORMAT);
        layout.setMainTimeText(mainTime);
    }


    // timeouts
    private void setTimeouts() {
        switch (preferences.timeoutRules) {
            case FIBA:
                setTimeoutsFIBA();
                break;
            case NBA:
                setTimeoutsNBA();
                break;
            case STREETBALL:
                setTimeouts3x3();
                break;
            default:
                timeoutFullDuration = 60;
                nullTimeoutsNoRules(NO_TEAM);
                break;
        }
    }

    private void setTimeoutsFIBA() {
        timeoutFullDuration = 60;
        if (period == 1) {
            maxTimeouts = 2;
        } else if (period == 3) {
            maxTimeouts = 3;
        } else if (period == preferences.numRegularPeriods + 1) {
            maxTimeouts = 1;
        }
        nullTimeouts(NO_TEAM);
    }

    private void setTimeoutsNBA() {
        takenTimeoutsFull = 0;
        maxTimeouts20 = 1;
        nullTimeouts20(NO_TEAM);
        if (period == 1) {
            maxTimeouts = 6;
            nullTimeouts(NO_TEAM);
        } else if (period == 4 && maxTimeouts > 3) {
            maxTimeouts = 3;
            if (hTimeouts > maxTimeouts) {
                nullTimeouts(HOME);
            }
            if (gTimeouts > maxTimeouts) {
                nullTimeouts(GUEST);
            }
        }
        if (period == 1 || period == 3) {
            maxTimeouts100 = 2;
        } else if (period == 2 || period == 4) {
            maxTimeouts100 = 3;
        } else if (period == preferences.numRegularPeriods + 1) {
            maxTimeouts100 = 1;
            maxTimeouts = 2;
            nullTimeouts(NO_TEAM);
        }
    }

    private void setTimeouts3x3() {
        timeoutFullDuration = 30;
        maxTimeouts = 1;
        nullTimeouts(NO_TEAM);
    }

    public void nullTimeouts(boolean left) {
        if (preferences.timeoutRules == Rules.TO_RULES.NONE) {
            nullTimeoutsNoRules(left == leftIsHome ? HOME : GUEST);
            return;
        }
        nullTimeouts(left == leftIsHome ? HOME : GUEST);
    }

    private void nullTimeoutsNoRules(int team) {
        if (team == HOME) {
            hTimeouts = 0;
            layout.nullHomeTimeouts("0");
        } else if (team == GUEST) {
            gTimeouts = 0;
            layout.nullGuestTimeouts("0");
        } else {
            hTimeouts = gTimeouts = 0;
            layout.nullHomeTimeouts("0");
            layout.nullGuestTimeouts("0");
        }
    }

    public void nullTimeouts20(boolean left) {
        nullTimeouts20(left == leftIsHome ? HOME : GUEST);
    }

    private void nullTimeouts(int team) {
        if (team == HOME) {
            hTimeouts = maxTimeouts;
            layout.nullHomeTimeouts(Short.toString(maxTimeouts));
        } else if (team == GUEST) {
            gTimeouts = maxTimeouts;
            layout.nullGuestTimeouts(Short.toString(maxTimeouts));
        } else {
            hTimeouts = gTimeouts = maxTimeouts;
            layout.nullHomeTimeouts(Short.toString(maxTimeouts));
            layout.nullGuestTimeouts(Short.toString(maxTimeouts));
        }
    }

    private void nullTimeouts20(int team) {
        if (team == HOME) {
            hTimeouts20 = maxTimeouts20;
            layout.nullHomeTimeouts20(Short.toString(maxTimeouts20));
        } else if (team == GUEST) {
            gTimeouts20 = maxTimeouts20;
            layout.nullGuestTimeouts20(Short.toString(maxTimeouts20));
        } else {
            hTimeouts20 = gTimeouts20 = maxTimeouts20;
            layout.nullHomeTimeouts20(Short.toString(maxTimeouts20));
            layout.nullGuestTimeouts20(Short.toString(maxTimeouts20));
        }
    }

    public void timeout(boolean left) {
        timeout(left == leftIsHome ? HOME : GUEST);
    }

    public void timeout(int team) {
        pauseGame();
        takenTimeoutsFull++;
        if (preferences.timeoutRules == Rules.TO_RULES.NONE) {
            if (team == HOME) {
                layout.setHomeTimeouts(Short.toString(++hTimeouts), false);
                if (preferences.autoShowTimeout) {
                    showTimeout(timeoutFullDuration, hName);
                }
            } else if (team == GUEST) {
                layout.setGuestTimeouts(Short.toString(++gTimeouts), false);
                if (preferences.autoShowTimeout) {
                    showTimeout(timeoutFullDuration, gName);
                }
            }
        } else {
            if (preferences.timeoutRules == Rules.TO_RULES.NBA) {
                timeoutFullDuration = takenTimeoutsFull <= maxTimeouts100 ? 100 : 60;
            }
            if (team == HOME) {
                if (hTimeouts > 0) {
                    layout.setHomeTimeouts(Short.toString(--hTimeouts), noTimeouts(hTimeouts));
                    if (preferences.autoShowTimeout) {
                        showTimeout(timeoutFullDuration, hName);
                    }
                }
            } else if (team == GUEST) {
                if (gTimeouts > 0) {
                    layout.setGuestTimeouts(Short.toString(--gTimeouts), noTimeouts(gTimeouts));
                    if (preferences.autoShowTimeout) {
                        showTimeout(timeoutFullDuration, gName);
                    }
                }
            }
        }
        addAction(ACTION_TO, team, 1);
    }

    private void revertTimeout(int team) {
        takenTimeoutsFull--;
        if (preferences.timeoutRules == Rules.TO_RULES.NONE) {
            if (team == HOME) {
                layout.setHomeTimeouts(Short.toString(--hTimeouts), false);
            } else if (team == GUEST) {
                layout.setGuestTimeouts(Short.toString(--gTimeouts), false);
            }
        } else {
            if (preferences.timeoutRules == Rules.TO_RULES.NBA) {
                timeoutFullDuration = takenTimeoutsFull <= maxTimeouts100 ? 100 : 60;
            }
            if (team == HOME) {
                layout.setHomeTimeouts(Short.toString(++hTimeouts), false);
                if (!noTimeouts(hTimeouts)) {
                    layout.setHomeTimeoutsGreen();
                }
            } else if (team == GUEST) {
                layout.setGuestTimeouts(Short.toString(++gTimeouts), false);
                if (!noTimeouts(gTimeouts)) {
                    layout.setGuestTimeoutsGreen();
                }
            }
        }
    }

    public void timeout20(boolean left) {
        timeout20(left == leftIsHome ? HOME : GUEST);
    }

    private void timeout20(int team) {
        pauseGame();
        if (team == HOME) {
            if (hTimeouts20 > 0) {
                layout.setHomeTimeouts20(Short.toString(--hTimeouts20), noTimeouts(hTimeouts20));
                if (preferences.autoShowTimeout) {
                    showTimeout(20, hName);
                }
            }

        } else if (team == GUEST) {
            if (gTimeouts20 > 0) {
                layout.setGuestTimeouts20(Short.toString(--gTimeouts20), noTimeouts(gTimeouts20));
                if (preferences.autoShowTimeout) {
                    showTimeout(20, gName);
                }
            }

        }
        addAction(ACTION_TO20, team, 1);
    }

    private void revertTimeout20(int team) {
        layout.setHomeTimeouts(Short.toString(hTimeouts), noTimeouts(hTimeouts));
        layout.setGuestTimeouts(Short.toString(gTimeouts), noTimeouts(gTimeouts));
        if (team == HOME) {
            if (hTimeouts20 > 0) {
                layout.setHomeTimeouts20(Short.toString(hTimeouts20), false);
                if (!noTimeouts(hTimeouts20)) {
                    layout.setHomeTimeouts20Green();
                }
            }

        } else if (team == GUEST) {
            if (gTimeouts20 > 0) {
                layout.setGuestTimeouts20(Short.toString(gTimeouts20), false);
                if (!noTimeouts(gTimeouts20)) {
                    layout.setGuestTimeouts20Green();
                }
            }
        }
    }

    private boolean noTimeouts(short value) {
        return preferences.timeoutRules != Rules.TO_RULES.NONE && value == 0;
    }


    // fouls
    public void foul(boolean left) {
        foul(left == leftIsHome ? HOME : GUEST);
    }

    private void foul(int team) {
        if (preferences.actualTime > 0) {
            pauseGame();
        }
        if (preferences.enableShotTime && shotTime < preferences.shortShotTimePref) {
            shotTime = preferences.shortShotTimePref;
        }
        switch (team) {
            case HOME:
                if (hFouls < preferences.maxFouls) {
                    layout.setHomeFoul(Short.toString(++hFouls), getFoulLevel(hFouls));
                }
                hActionType = ACTION_FLS;
                hActionValue += 1;
                break;
            case GUEST:
                if (gFouls < preferences.maxFouls) {
                    layout.setGuestFoul(Short.toString(++gFouls), getFoulLevel(gFouls));
                }

                gActionType = ACTION_FLS;
                gActionValue += 1;
                break;
        }

        addAction(ACTION_FLS, team, 1);
    }

    private void nullFouls() {
        hFouls = gFouls = 0;
        layout.nullHomeFouls();
        layout.nullGuestFouls();
    }

    public void nullFouls(boolean left) {
        if (left == leftIsHome) {
            hFouls = 0;
            layout.nullHomeFouls();
        } else {
            gFouls = 0;
            layout.nullGuestFouls();
        }
    }

    private void revertFoul(int team) {
        if (team == HOME) {
            layout.setHomeFoul(Short.toString(--hFouls), getFoulLevel(hFouls));
        } else if (team == GUEST) {
            layout.setGuestFoul(Short.toString(--gFouls), getFoulLevel(gFouls));
        }
    }

    private Level getFoulLevel(int value) {
        if (value == preferences.maxFouls) {
            return Level.LIMIT;
        }
        if (value >= preferences.maxFoulsWarn) {
            return Level.WARN;
        }
        return Level.OK;
    }


    // possession
    private void handleArrowsVisibility() {
        layout.handleArrowsVisibility();
    }

    private void switchPossession() {
        if (possession == NO_TEAM) {
            return;
        }
        possession = 1 - possession;
        setPossession(possession);
    }

    private void setPossession(int team) {
        possession = team;
        if (team == NO_TEAM) {
            layout.clearPossession();
        } else {
            layout.toggleArrow(team == HOME && leftIsHome);
        }
    }

    public void setPossession(boolean left) {
        setPossession(left == leftIsHome ? HOME : GUEST);
    }

    public void clearPossession() {
        setPossession(NO_TEAM);
    }


    // names
    private void handleNames() {
        if (hName == null || hName.equals("")) {
            hName = preferences.hName;
        }
        if (gName == null || gName.equals("")) {
            gName = preferences.gName;
        }
        setTeamNames();
    }

    private void setTeamNames(String home, String guest) {
        gameResult.setHomeName(home);
        gameResult.setGuestName(guest);
        layout.setHomeName(home);
        layout.setGuestName(guest);
    }

    private void setTeamNames() {
        setTeamNames(hName, gName);
    }

    public void setTeamName(String value, int team) {
        if (team == HOME) {
            setHomeName(value);
        } else {
            setGuestName(value);
        }
    }

    private void setHomeName(String value) {
        hName = value;
        gameResult.setHomeName(value);
        layout.setHomeName(value);
        preferences.setTeamName(HOME, value);
    }

    private void setGuestName(String value) {
        gName = value;
        gameResult.setGuestName(value);
        layout.setGuestName(value);
        preferences.setTeamName(GUEST, value);
    }

    public String getName(boolean left) {
        if (left == leftIsHome) {
            return hName;
        } else {
            return gName;
        }
    }

    public String getName(int team) {
        if (team == HOME) {
            return hName;
        } else {
            return gName;
        }
    }


    // players
    public void playerAction(boolean left, SidePanelRow player) {
        if (player == null) {
            listener.onShowToast(R.string.toast_select_players, Toast.LENGTH_SHORT);
            return;
        }
        if (left == leftIsHome) {
            hPlayerAction(player);
        } else {
            gPlayerAction(player);
        }
        if (lastAction != null) {
            lastAction.setNumber(player.getNumber());
        }
    }

    private void hPlayerAction(SidePanelRow player) {
        if (hActionType != ACTION_NONE) {
            if (hActionType == ACTION_PTS) {
                player.changePoints(hActionValue);
            } else if (hActionType == ACTION_FLS) {
                player.changeFouls(hActionValue);
            }
            hActionType = ACTION_NONE;
            hActionValue = 0;
        }
    }

    private void gPlayerAction(SidePanelRow player) {
        if (gActionType != ACTION_NONE) {
            if (gActionType == ACTION_PTS) {
                player.changePoints(gActionValue);
            } else if (gActionType == ACTION_FLS) {
                player.changeFouls(gActionValue);
            }
            gActionType = ACTION_NONE;
            gActionValue = 0;
        }
    }

    private void cancelPlayerScore(int team, int number, int value) {
        SidePanelRow player = getPlayer(team, number);
        if (player != null) { player.changePoints(-value); }
    }

    private void cancelPlayerFoul(int team, int number, int value) {
        SidePanelRow player = getPlayer(team, number);
        if (player != null) { player.changeFouls(-value); }
    }

    private void handlePlayersPanels() {
        if (preferences.spOn) {
            if (panels == null && listener != null) {
                panels = listener.onInitPanels();
            }
            layout.showPlayersButtons();
        } else {
            layout.hidePlayersButtons();
        }
    }

    private TreeMap<Integer, SidePanelRow> getHomePlayers() {
        if (leftIsHome) {
            return panels.getLeftPlayers();
        } else {
            return panels.getRightPlayers();
        }
    }

    private TreeMap<Integer, SidePanelRow> getGuestPlayers() {
        if (leftIsHome) {
            return panels.getRightPlayers();
        } else {
            return panels.getLeftPlayers();
        }
    }

    public void openPanel(boolean left) {
        if (panels != null) {
            if (left) {
                panels.showLeft();
            } else {
                panels.showRight();
            }
        }
    }

    private SidePanelRow getPlayer(int team, int number) {
        return panels.getPlayer(leftIsHome == (team == HOME), number);
    }

    public void closeSidePanel(boolean left) {
        if (left) {
            panels.closeLeft();
        } else {
            panels.closeRight();
        }
    }

    public void checkCloseSidePanels() {
        if (!panels.closePanels()) {
            listener.onShowToast(R.string.side_panel_confirm, Toast.LENGTH_LONG);
        }
    }

    public TreeMap<Integer, SidePanelRow> getInactivePlayers(boolean left) {
        if (panels != null) {
            if (left) {
                return panels.getLeftInactivePlayers();
            } else {
                return panels.getRightInactivePlayers();
            }
        }
        return null;
    }

    public void selectActivePlayers(TreeSet<SidePanelRow> rows, boolean left) {
        layout.setPlayersButtons(left, rows);
    }

    public void deleteActivePlayers(boolean left) {
        layout.setPlayersButtonsEmpty(left);
    }

    public void addPlayer(boolean left, int number, String name, boolean captain) {
        panels.addPlayer(left, number, name, captain);
    }

    public void editPlayer(boolean left, int id, int number, String name, boolean captain) {
        if (panels.editPlayer(left, id, number, name, captain)) {
            layout.updatePlayerButton(left, id, number);
        }
    }

    public void deletePlayer(boolean left, int id) {
        if (panels.deletePlayer(left, id)) {
            layout.clearPlayerButton(left, id);
        }
    }

    public void deletePlayers(int type, boolean left) {
        panels.deletePlayers(type, left);
    }

    private void deletePlayers() {
        panels.deletePlayers(LEFT);
        panels.deletePlayers(RIGHT);
    }

    public Button getSelectedPlayer() {
        return layout.getSelectedPlayerButton();
    }

    public int validatePlayer(boolean left, int number, boolean captain) {
        return panels.validatePlayer(left, number, captain);
    }

    public void substitutePlayer(boolean left, int newNumber) {
        if (panels == null) {
            return;
        }
        SidePanelRow in = getInactivePlayers(left).get(newNumber);
        SidePanelRow out = layout.substitutePlayer(in, newNumber);
        panels.substitute(left, in, out);
    }
}
