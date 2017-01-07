package com.smiler.basketball_scoreboard.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.CountDownTimer;
import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.layout.StandardLayout;
import com.smiler.basketball_scoreboard.preferences.Preferences;
import com.smiler.basketball_scoreboard.results.Result;

import java.util.Date;

import io.realm.Realm;

import static com.smiler.basketball_scoreboard.Constants.ACTION_FLS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_NONE;
import static com.smiler.basketball_scoreboard.Constants.ACTION_PTS;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO;
import static com.smiler.basketball_scoreboard.Constants.ACTION_TO20;
import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.MINUTES_2;
import static com.smiler.basketball_scoreboard.Constants.NO_TEAM;
import static com.smiler.basketball_scoreboard.Constants.SECOND;
import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_FOULS;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_SCORE;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS20;
import static com.smiler.basketball_scoreboard.Constants.STATE_GUEST_TIMEOUTS_NBA;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_FOULS;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_SCORE;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS20;
import static com.smiler.basketball_scoreboard.Constants.STATE_HOME_TIMEOUTS_NBA;
import static com.smiler.basketball_scoreboard.Constants.STATE_MAIN_TIME;
import static com.smiler.basketball_scoreboard.Constants.STATE_PERIOD;
import static com.smiler.basketball_scoreboard.Constants.STATE_POSSESSION;
import static com.smiler.basketball_scoreboard.Constants.STATE_SHOT_TIME;


public class Game {
    public static final String TAG = "BS-Game";
    private static Game instance;
    private Context context;
    private GameListener listener;
    private StandardLayout layout;

    private boolean mainTimerOn, shotTimerOn;
    private boolean directTimerStopped;
    private int possession = NO_TEAM;
    public long mainTime, shotTime;
    private long startTime, totalTime;
    private long timeoutFullDuration;
    private short hScore, gScore;
    private short hScore_prev, gScore_prev;
    private short hFouls, gFouls;
    private short hTimeouts, gTimeouts;
    private short hTimeouts20, gTimeouts20;
    private short takenTimeoutsFull;
    private short maxTimeouts, maxTimeouts20, maxTimeouts100;
    private short period;
    private String hName, gName;
    private Handler customHandler = new Handler();
    private CountDownTimer mainTimer, shotTimer;
    private float periodViewSize, scoreViewSize;
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
    private Realm realm;
    private SharedPreferences statePref;

    public enum GAME_TYPE {
        COMMON,
        SIMPLE,
        FIBA,
        NBA,
        STREETBALL;

        public static GAME_TYPE fromInteger(int x) {
            switch(x) {
                case 0: return COMMON;
                case 1: return SIMPLE;
                case 2: return FIBA;
                case 3: return NBA;
                case 4: return STREETBALL;
            }
            return null;
        }
    }

    public enum TO_RULES {
        NONE,
        FIBA,
        NBA;

        public static TO_RULES fromInteger(int x) {
            switch(x) {
                case 0: return NONE;
                case 1: return FIBA;
                case 2: return NBA;
            }
            return null;
        }
    }


//    GameListener test = new GameListener() {
//        @Override
//        public void onScoreChange(int team, int current, long mainTime) {
//
//        }
//
//        @Override
//        public void onFoul(int team, int current, long mainTime) {
////            setColorRed(hFoulsView);
////        hFoulsView.setText(Short.toString(hValue));
////        gFoulsView.setText(Short.toString(gValue));
////        hFoulsView.setTextColor(hColor);
////        gFoulsView.setTextColor(gColor);
//        }
//
//        @Override
//        public void onFoulsClear(int team) {
////        hFoulsView.setText("0");
////        gFoulsView.setText("0");
////        setColorGreen(hFoulsView);
////        setColorGreen(gFoulsView);
//        }
//
//        @Override
//        public void onTimeout(int team, int current, long mainTime) {
//
//        }
//
//        @Override
//        public void onTeamMaxFouls(int team) {
//
//        }
//
//        @Override
//        public void onTeamMaxTimeouts(int team) {
//
//        }
//
//        @Override
//        public void onTeamMaxTimeouts20(int team) {
//
//        }
//
//        @Override
//        public void onOvertime(int team) {
//
//        }
//        @Override
//        public void onUnder2Minutes() {
//
//        }
//    };
//

    public static Game getInstance(Context context, StandardLayout layout){
        if (instance == null){
            instance = new Game(context, layout);
        }
        return instance;
    }

    public static Game newGame(Context context,  StandardLayout layout){
        instance = new Game(context, layout);
        return instance;
    }

    public static Game newGame(Context context){
        instance = new Game(context);
        return instance;
    }

    private Game(Context context, StandardLayout layout) {
        this.context = context;
        this.layout = layout;
        statePref = context.getSharedPreferences(Constants.STATE_PREFERENCES, Context.MODE_PRIVATE);
        preferences = Preferences.getInstance(context);
        preferences.read();
        if (hName == null || hName.equals("")) {
            hName = preferences.hName;
        }
        if (gName == null || gName.equals("")) {
            gName = preferences.gName;
        }
        gameResult = new Result(hName, gName);
        leftIsHome = true;

    }

    private Game(Context context) {
        this.context = context;
        if (layout == null) {
            return;
        }

        layout.zeroState();
        statePref = context.getSharedPreferences(Constants.STATE_PREFERENCES, Context.MODE_PRIVATE);
        preferences = Preferences.getInstance(context);
        preferences.read();
        if (hName == null || hName.equals("")) {
            hName = preferences.hName;
        }
        if (gName == null || gName.equals("")) {
            gName = preferences.gName;
        }
        gameResult = new Result(hName, gName);
        leftIsHome = true;

    }

    private void getSavedState() {
        shotTime = statePref.getLong(STATE_SHOT_TIME, 24 * SECOND);
        mainTime = totalTime = statePref.getLong(STATE_MAIN_TIME, 600 * SECOND);
        period = (short) statePref.getInt(STATE_PERIOD, 1);
        hScore = (short) statePref.getInt(STATE_HOME_SCORE, 0);
        gScore = (short) statePref.getInt(STATE_GUEST_SCORE, 0);
//        hName = statePref.getString(STATE_HOME_NAME, getResources().getString(R.string.home_team_name_default));
//        gName = statePref.getString(STATE_GUEST_NAME, getResources().getString(R.string.guest_team_name_default));
        hFouls = (short) statePref.getInt(STATE_HOME_FOULS, 0);
        gFouls = (short) statePref.getInt(STATE_GUEST_FOULS, 0);
        if (preferences.timeoutRules == TO_RULES.FIBA) {
            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS, 0);
            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS, 0);
        } else if (preferences.timeoutRules == TO_RULES.NBA) {
            hTimeouts = (short) statePref.getInt(STATE_HOME_TIMEOUTS_NBA, 0);
            gTimeouts = (short) statePref.getInt(STATE_GUEST_TIMEOUTS_NBA, 0);
            hTimeouts20 = (short) statePref.getInt(STATE_HOME_TIMEOUTS20, 0);
            gTimeouts20 = (short) statePref.getInt(STATE_GUEST_TIMEOUTS20, 0);
        }
        if (preferences.arrowsOn) {
            setPossession(statePref.getInt(STATE_POSSESSION, possession));
        }
    }

    private void getSettings() {
        preferences.read();
    }

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
            case 0:
                mainTime = preferences.mainTimePref;
                break;
            case 1:
                mainTime = preferences.overTimePref;
                break;
        }
//        mainTimeFormat = TIME_FORMAT;
//        setMainTimeText(mainTime);
    }

    // TODO как-то проверять, что реализован интерфейс
    interface GameListener {
        void onScoreChange(int team, int current, long mainTime);
        void onFoul(int team, int current, long mainTime);
        void onFoulsClear(int team);
        void onTimeout(int team, int current, long mainTime);
        void onTeamMaxFouls(int team);
        void onTeamMaxTimeouts(int team);
        void onTeamMaxTimeouts20(int team);
        void onOvertime(int team);
        void onUnder2Minutes();
    }


    // SCORE
    public void setScores(short hValue, short gValue) {
        hScore = hValue;
        gScore = gValue;
    }

    public void nullScore(boolean left) {
        if (left == leftIsHome) {
            hScore = 0;
        } else {
            gScore = 0;
        }
    }

    public void changeScore(boolean left, int value) {
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
            hActionType = ACTION_PTS;
            hActionValue += value;
        } else {
            gActionType = ACTION_PTS;
            gActionValue += value;
        }
        updateStats();
    }

    private void changeGuestScore(int value) {
        gScore += value;
        if (value != 0) {
            handleScoreChange();
        }
        layout.setGuestScore(gScore);
    }

    private void changeHomeScore(int value) {
        hScore += value;
        if (value != 0) {
            handleScoreChange();
        }
        layout.setHomeScore(hScore);
    }

    private void handleScoreChange() {
        if (preferences.enableShotTime && preferences.layoutType == GAME_TYPE.COMMON && preferences.restartShotTimer) {
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

    public void revertScore(int team, int value) {
        if (team == HOME) {
            changeHomeScore(-value);
        } else {
            changeGuestScore(-value);
        }
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





    // TIMEOUTS
    public void setTimeouts() {
        if (preferences.timeoutRules == TO_RULES.FIBA) {
            timeoutFullDuration = 60;
            if (period == 1) {
                maxTimeouts = 2;
                nullTimeouts(2);
            } else if (period == 3) {
                maxTimeouts = 3;
                nullTimeouts(2);
            } else if (period == preferences.numRegularPeriods + 1) {
                maxTimeouts = 1;
                nullTimeouts(2);
            }
        } else if (preferences.timeoutRules == TO_RULES.NBA) {
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
            } else if (period == preferences.numRegularPeriods + 1) {
                maxTimeouts100 = 1;
                maxTimeouts = 2;
                nullTimeouts(2);
            }
        } else {
            timeoutFullDuration = 60;
        }
    }

    public void nullTimeouts(boolean left) {
        if (preferences.timeoutRules == TO_RULES.NONE) {
            nullTimeoutsNoRules(left);
            return;
        }
        if (left == leftIsHome) {
            nullTimeouts(HOME);
        } else {
            nullTimeouts(GUEST);
        }
    }

    public void nullTimeoutsNoRules(boolean left) {
        if (left == leftIsHome) {
            nullTimeoutsNoRules(HOME);
        } else {
            nullTimeoutsNoRules(GUEST);
        }
    }

    public void nullTimeouts20(boolean left) {
        if (left == leftIsHome) {
            nullTimeouts20(HOME);
        } else {
            nullTimeouts20(GUEST);
        }
    }

    public void nullTimeouts(int team) {
        if (team > 0) {
            gTimeouts = maxTimeouts;
//            setColorGreen(gTimeoutsView);
//            gTimeoutsView.setText(Short.toString(maxTimeouts));
            if (team == 1) {
                return;
            }
        }
        hTimeouts = maxTimeouts;
//        setColorGreen(hTimeoutsView);
//        hTimeoutsView.setText(Short.toString(maxTimeouts));
    }

    public void nullTimeoutsNoRules(int team) {
        if (team > 0) {
            gTimeouts = 0;
//            setColorGreen(gTimeoutsView);
//            gTimeoutsView.setText("0");
            if (team == 1) {
                return;
            }
        }
        hTimeouts = 0;
//        setColorGreen(hTimeoutsView);
//        hTimeoutsView.setText("0");
    }

    public void nullTimeouts20(int team) {
        if (team > 0) {
            gTimeouts20 = maxTimeouts20;
//            gTimeouts20View.setText(Short.toString(maxTimeouts20));
//            setColorGreen(gTimeouts20View);
            if (team == 1) {
                return;
            }
        }
        hTimeouts20 = maxTimeouts20;
//        hTimeouts20View.setText(Short.toString(maxTimeouts20));
//        setColorGreen(hTimeouts20View);
    }

    public void timeout(boolean left) {
        if (left == leftIsHome) {
            timeout(HOME);
        } else {
            timeout(GUEST);
        }
    }

    public void timeout(int team) {
        pauseGame();
        takenTimeoutsFull++;
        if (preferences.timeoutRules == TO_RULES.NONE) {
            switch (team) {
                case HOME:
//                    hTimeoutsView.setText(Short.toString(++hTimeouts));
//                    if (preferences.autoShowTimeout) {
//                        showTimeout(timeoutFullDuration, hName);
//                    }
                    break;
                case GUEST:
//                    gTimeoutsView.setText(Short.toString(++gTimeouts));
//                    if (preferences.autoShowTimeout) {
//                        showTimeout(timeoutFullDuration, gName);
//                    }
                    break;
            }
        } else if (preferences.timeoutRules == TO_RULES.FIBA) {
            switch (team) {
                case HOME:
//                    if (hTimeouts > 0) {
////                        hTimeoutsView.setText(Short.toString(--hTimeouts));
////                        if (hTimeouts == 0) {
//////                            setColorRed(hTimeoutsView);
////                        }
////                        if (preferences.autoShowTimeout) {
//////                            showTimeout(timeoutFullDuration, hName);
////                        }
//                    }
                    break;
                case GUEST:
//                    if (gTimeouts > 0) {
////                        gTimeoutsView.setText(Short.toString(--gTimeouts));
//                        if (gTimeouts == 0) {
////                            setColorRed(gTimeoutsView);
//                        }
//                        if (preferences.autoShowTimeout) {
////                            showTimeout(timeoutFullDuration, gName);
//                        }
//                    }
                    break;
            }
        } else {
            timeoutFullDuration = takenTimeoutsFull <= maxTimeouts100 ? 100 : 60;
            switch (team) {
                case HOME:
//                    if (hTimeouts > 0) {
////                        hTimeoutsView.setText(Short.toString(--hTimeouts));
//                        if (hTimeouts == 0) {
////                            setColorRed(hTimeoutsView);
//                        }
//                        if (preferences.autoShowTimeout) {
////                            showTimeout(timeoutFullDuration, hName);
//                        }
//                    }
                    break;
                case GUEST:
//                    if (gTimeouts > 0) {
////                        gTimeoutsView.setText(Short.toString(--gTimeouts));
//                        if (gTimeouts == 0) {
////                            setColorRed(gTimeoutsView);
//                        }
//                        if (preferences.autoShowTimeout) {
////                            showTimeout(timeoutFullDuration, gName);
//                        }
//                    }
                    break;
            }
        }
        addAction(ACTION_TO, team, 1);
    }

    public void revertTimeout(int team) {
        takenTimeoutsFull--;
        if (preferences.timeoutRules == TO_RULES.NONE) {
            switch (team) {
                case HOME:
//                    hTimeoutsView.setText(Short.toString(--hTimeouts));
                    break;
                case GUEST:
//                    gTimeoutsView.setText(Short.toString(--gTimeouts));
                    break;
            }
        } else if (preferences.timeoutRules == TO_RULES.FIBA) {
            switch (team) {
                case HOME:
//                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (hTimeouts > 0) {
//                        setColorGreen(hTimeoutsView);
                    }
                    break;
                case GUEST:
//                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (gTimeouts > 0) {
//                        setColorGreen(gTimeoutsView);
                    }
                    break;
            }
        } else {
            timeoutFullDuration = takenTimeoutsFull <= maxTimeouts100 ? 100 : 60;
            switch (team) {
                case HOME:
//                    hTimeoutsView.setText(Short.toString(++hTimeouts));
                    if (hTimeouts > 0) {
//                        setColorGreen(hTimeoutsView);
                    }
                    break;
                case GUEST:
//                    gTimeoutsView.setText(Short.toString(++gTimeouts));
                    if (gTimeouts > 0) {
//                        setColorGreen(gTimeoutsView);
                    }
                    break;
            }
        }
    }

    public void timeout20(boolean left) {
        if (left == leftIsHome) {
            timeout20(HOME);
        } else {
            timeout20(GUEST);
        }
    }

    public void timeout20(int team) {
        pauseGame();
        switch (team) {
            case HOME:
                if (hTimeouts20 > 0) {
//                    hTimeouts20View.setText(Short.toString(--hTimeouts20));
                    if (hTimeouts20 == 0) {
//                        setColorRed(hTimeouts20View);
                    }
                    if (preferences.autoShowTimeout) {
//                        showTimeout(20, hName);
                    }
                }
                break;
            case GUEST:
                if (gTimeouts20 > 0) {
//                    gTimeouts20View.setText(Short.toString(--gTimeouts20));
                    if (gTimeouts20 == 0) {
//                        setColorRed(gTimeouts20View);
                    }
                    if (preferences.autoShowTimeout) {
//                        showTimeout(20, gName);
                    }
                }
                break;
        }
        addAction(ACTION_TO20, team, 1);
    }

    public void revertTimeout20(int team) {
        switch (team) {
            case HOME:
                if (hTimeouts20 > 0) {
//                    setColorGreen(hTimeouts20View);
//                    hTimeouts20View.setText(Short.toString(++hTimeouts20));
                }
                break;
            case GUEST:
                if (gTimeouts20 > 0) {
//                    setColorGreen(gTimeouts20View);
//                    gTimeouts20View.setText(Short.toString(++gTimeouts20));
                }
                break;
        }
    }




    // FOULS
    public void foul(int team) {
        if (preferences.actualTime > 0) {
            pauseGame();
        }
        if (preferences.enableShotTime && shotTime < preferences.shortShotTimePref) {
            shotTime = preferences.shortShotTimePref;
//            setShotTimeText(shotTime);
        }
        switch (team) {
            case HOME:
                if (hFouls < preferences.maxFouls) {
                    listener.onFoul(team, ++hFouls, mainTime);
                    if (hFouls == preferences.maxFouls) {
                        if (listener != null) {
                            listener.onTeamMaxFouls(team);
                        }
                    }
                }
                hActionType = ACTION_FLS;
                hActionValue += 1;
                break;
            case GUEST:
                if (gFouls < preferences.maxFouls) {
                    listener.onFoul(team, ++gFouls, mainTime);
                    if (gFouls == preferences.maxFouls) {
                        if (listener != null) {
                            listener.onTeamMaxFouls(team);
                        }
                    }
                }

                gActionType = ACTION_FLS;
                gActionValue += 1;

                break;
        }

        addAction(ACTION_FLS, team, 1);
    }

    public void nullFouls() {
        hFouls = gFouls = 0;
        listener.onFoulsClear(NO_TEAM);
    }

    public void nullFouls(boolean left) {
        if (left == leftIsHome) {
            hFouls = 0;
        } else {
            gFouls = 0;
        }
    }

    public void revertFoul(int team) {
        switch (team) {
            case HOME:
                if (--hFouls < preferences.maxFouls) {
                    listener.onFoul(team, hFouls, mainTime);
                }
                break;
            case GUEST:
                if (--gFouls < preferences.maxFouls) {
                    listener.onFoul(team, gFouls, mainTime);
                }
                break;
        }
    }




    public void switchPossession() {
        if (possession == NO_TEAM) { return; }
        possession = 1 - possession;
    }

    public void setPossession(int team) {
//        if (leftArrow != null && rightArrow != null) {
//            switch (team) {
//                case HOME:
//                    leftArrow.setFill();
//                    rightArrow.setStroke();
//                    break;
//                case GUEST:
//                    rightArrow.setFill();
//                    leftArrow.setStroke();
//                    break;
//                case NO_TEAM:
//                    leftArrow.setStroke();
//                    rightArrow.setStroke();
//                    break;
//            }
//            possession = team;
//        }
    }

//    private void switchSides() {
//        FragmentManager fm = getFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_in);
//        Fragment o = fm.findFragmentByTag(OverlayFragment.TAG_SWITCH);
//        if (o != null) {
//            if (!o.isVisible()) {
//                ft.show(o);
//            }
//        } else {
//            ft.add(R.id.overlay, overlaySwitch, OverlayFragment.TAG_SWITCH);
//        }
//        ft.addToBackStack(null).commit();
//
////        TextView _NameView = hNameView;
////        hNameView = gNameView;
////        gNameView = _NameView;
////        setTeamNames(hName, gName);
//
////        TextView _ScoreView = hScoreView;
////        hScoreView = gScoreView;
////        gScoreView = _ScoreView;
////        setScoresText(hScore, gScore);
//
////        if (preferences.layoutType == GAME_TYPE.COMMON) {
////            TextView _FoulsView = hFoulsView;
////            hFoulsView = gFoulsView;
////            gFoulsView = _FoulsView;
////            setFoulsText(hFouls, gFouls, gFoulsView.getCurrentTextColor(), hFoulsView.getCurrentTextColor());
////
////            TextView _TimeoutsView = hTimeoutsView;
////            hTimeoutsView = gTimeoutsView;
////            gTimeoutsView = _TimeoutsView;
////            setTimeoutsText(hTimeouts, gTimeouts, gTimeoutsView.getCurrentTextColor(), hTimeoutsView.getCurrentTextColor());
////
////            if (preferences.timeoutRules == Game.TO_RULES.NBA) {
////                TextView _Timeouts20View = hTimeouts20View;
////                hTimeouts20View = gTimeouts20View;
////                gTimeouts20View = _Timeouts20View;
////                setTimeouts20Text(hTimeouts20, gTimeouts20, gTimeouts20View.getCurrentTextColor(), hTimeouts20View.getCurrentTextColor());
////            }
////        }
////        setColors();
//
//        if (preferences.spOn && leftPanel != null) {
//            try {
//                switchSidePanels();
//            } catch (NullPointerException e) {
//                Log.d(TAG, "Left or right panel is null");
//            }
//        }
//
////        if (preferences.arrowsOn) {
////            switchPossession();
////        }
//
//        leftIsHome = !leftIsHome;
//
//        fm.beginTransaction()
//                .setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out)
//                .hide(overlaySwitch)
//                .commit();
//    }

    private void addAction(int type, int team, int value) {
        if (preferences.playByPlay != 0) {
            lastAction = gameResult.addAction(mainTime, type, team, value);
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
//                    cancelPlayerScore(lastAction.getTeam(), lastAction.getNumber(), lastAction.getValue());
                }
                break;
            case ACTION_FLS:
                revertFoul(lastAction.getTeam());
                if (preferences.spOn) {
//                    cancelPlayerFoul(lastAction.getTeam(), lastAction.getNumber(), lastAction.getValue());
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

    public void setPeriod() {
        if (period <= preferences.numRegularPeriods) {
            mainTime = totalTime = preferences.mainTimePref;
//            periodView.setText(Short.toString(period));
            if (periodViewSize != 0) {
//                periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize);
            }
        } else {
            mainTime = totalTime = preferences.overTimePref;
//            periodView.setText(String.format("OT%d", period - preferences.numRegularPeriods));
//            if (periodViewSize == 0) {
//                periodViewSize = getResources().getDimension(R.dimen.bottom_line_size);
//            }
//            periodView.setTextSize(TypedValue.COMPLEX_UNIT_PX, periodViewSize * 0.75f);
        }
        if (preferences.useDirectTimer) {
            mainTime = 0;
        }
    }

    public void setTeamNames(String home, String guest) {
        gameResult.setHomeName(home);
        gameResult.setGuestName(guest);
    }

    public void setTeamNames() {
        setTeamNames(hName, gName);
    }

    public void setTeamName(String value, int team) {
        if (team == HOME){
            setHomeName(value);
        } else {
            setGuestName(value);
        }
    }

    private void setHomeName(String value) {
        hName = value;
        gameResult.setHomeName(value);
    }

    private void setGuestName(String value) {
        gName = value;
        gameResult.setGuestName(value);
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

//    private void cancelPlayerScore(int team, int number, int value) {
//        SidePanelRow player = getPlayer(team, number);
//        if (player != null) { player.changePoints(-value); }
//    }
//
//    private void cancelPlayerFoul(int team, int number, int value) {
//        SidePanelRow player = getPlayer(team, number);
//        if (player != null) { player.changeFouls(-value); }
//    }






    // TIMER
    public void under2Minutes() {
        if (preferences.timeoutRules == TO_RULES.NBA) {
            if (period == 4) {
                if (hTimeouts == 2 || hTimeouts == 3) {
                    hTimeouts = 1;
                    hTimeouts20++;
//                    hTimeoutsView.setText("1");
//                    hTimeouts20View.setText(Short.toString(hTimeouts20));
                }
                if (gTimeouts == 2 || gTimeouts == 3) {
                    gTimeouts = 1;
                    gTimeouts20++;
//                    gTimeoutsView.setText("1");
//                    gTimeouts20View.setText(Short.toString(gTimeouts20));
                }
            }
        }
    }

    public void startMainCountDownTimer() {
        mainTimer = new CountDownTimer(mainTime, mainTickInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                mainTime = millisUntilFinished;
//                setMainTimeText(mainTime);
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
//                    mainTimeFormat = TIME_FORMAT_MILLIS;
                    startMainCountDownTimer();
                }
//                if (preferences.enableShotTime && mainTime < shotTime && shotTimeView.getVisibility() == View.VISIBLE) {
////                    shotTimeView.setVisibility(View.INVISIBLE);
//                } else if (preferences.enableShotTime && mainTime < preferences.shortShotTimePref && shotTimeSwitchView.getVisibility() == View.VISIBLE) {
////                    shotTimeSwitchView.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void onFinish() {
                mainTimerOn = false;
                if (preferences.autoSound >= 2) {
//                    playHorn();
                }
                mainTickInterval = SECOND;
//                setMainTimeText(0);
                if (preferences.enableShotTime && shotTimerOn) {
                    shotTimer.cancel();
//                    setShotTimeText(0);
                }
                save();
                if (period >= preferences.numRegularPeriods && hScore != gScore) {
//                    if (dontAskNewGame == 0) {
////                        showConfirmDialog("new_game", true);
//                    } else {
////                        endOfGameActions(dontAskNewGame);
//                    }
//                    showTimeoutDialog = false;
                }
//                if (preferences.autoShowBreak && showTimeoutDialog) {
//                    if (period == 2) {
////                        showTimeout(900, "");
//                    } else {
////                        showTimeout(120, "");
//                    }
//                }
            }
        }.start();
        mainTimerOn = true;
        if (preferences.enableShotTime && !shotTimerOn && mainTime > shotTime) {
            startShotCountDownTimer();
        }
    }

    public void startShotCountDownTimer(long startValue) {
        if (shotTimerOn) {
            shotTimer.cancel();
        }
        shotTime = startValue;
        startShotCountDownTimer();
    }

    public void startShotCountDownTimer() {
//        shotTimer = new CountDownTimer(shotTime, shotTickInterval) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                shotTime = millisUntilFinished;
////                setShotTimeText(shotTime);
////                if (shotTime < 5 * SECOND && shotTickInterval == SECOND) {
////                    shotTickInterval = 100;
////                    shotTimer.cancel();
////                    startShotCountDownTimer();
////                }
////            }
////
////            @Override
////            public void onFinish() {
////                pauseGame();
////                if (preferences.autoSound == 1 || preferences.autoSound == 3) {
//////                    playHorn();
////                }
////                setShotTimeText(0);
////                shotTimeView.startAnimation(shotTimeBlinkAnimation);
//                shotTime = preferences.shotTimePref;
//                shotTickInterval = SECOND;
//            }
//        }.start();
        shotTimerOn = true;
    }

    public void startDirectTimer() {
        startTime = SystemClock.uptimeMillis() - mainTime;
        if (directTimerStopped) {
            stopDirectTimer();
        }
//        mainTimeFormat = TIME_FORMAT;
        mainTimerOn = true;
        customHandler.postDelayed(directTimerThread, 0);
        if (preferences.enableShotTime) {
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
            if (mainTime >= totalTime) {
                stopDirectTimer();
                return;
            }
            customHandler.postDelayed(this, 1000);
        }
    };



    public void changeMainTime(long value) {
        mainTime = value;
        if (preferences.fractionSecondsMain && mainTime < SECONDS_60) {
            mainTickInterval = 100;
//            mainTimeFormat = TIME_FORMAT_MILLIS;
        } else {
//            mainTimeFormat = TIME_FORMAT;
        }

        if (preferences.enableShotTime && mainTime > shotTime) {
//            shotTimeView.setVisibility(View.VISIBLE);
//            shotTimeSwitchView.setVisibility(View.VISIBLE);
        }
//        setMainTimeText(mainTime);
    }

    public void changeShotTime(long value) {
        shotTime = value;
        if (shotTime < 5 * SECOND) {
            shotTickInterval = 100;
        }
//        setShotTimeText(shotTime);
    }

////    private void endOfGameActions(int dontAskNewGame) {
//    TODO интерфейс
//        switch (dontAskNewGame) {
//            case 1:
//                break;
//            case 2:
//                saveDb();
//                newGame();
//                break;
//            case 3:
//                newGame();
//                break;
//        }
//    }
    public void newGameSave() {
    if (preferences.autoSaveResults == 0) {
//            saveResult();
//            saveResultDb();
//            newGame();
    } else if (preferences.autoSaveResults == 2) {
//            showConfirmDialog("save_result", false);
    }
}

    public void save() {
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

    public void saveDb() {
        if (hScore == 0 && gScore == 0) {
            return;
        }
        if (period < preferences.numRegularPeriods || mainTime != 0 || mainTime != preferences.mainTimePref) {
            gameResult.setComplete(false);
        } else {
            gameResult.setComplete(true);
        }

        realm = RealmController.with(context).getRealm();
        Number lastId = realm.where(Results.class).max("id");
        final long nextID  = lastId != null ? (long) lastId + 1 : 0;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Results result = realm.createObject(Results.class, nextID);
                result.setDate(new Date())
                        .setHomeTeam(hName)
                        .setGuestTeam(gName)
                        .setHomeScore(hScore)
                        .setGuestScore(gScore)
                        .setHomePeriods(gameResult.getHomeScoreByPeriodString())
                        .setGuestPeriods(gameResult.getGuestScoreByPeriodString())
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

//                if (preferences.spOn) {
//                    TreeMap<Integer, SidePanelRow> allHomePlayers = leftPanel.getAllPlayers();
//                    TreeMap<Integer, SidePanelRow> allGuestPlayers = rightPanel.getAllPlayers();
//                    for (Map.Entry<Integer, SidePanelRow> entry : allHomePlayers.entrySet()) {
//                        SidePanelRow row = entry.getValue();
//                        PlayersResults playersResults = realm.createObject(PlayersResults.class);
//                        playersResults.setGame(result)
//                                .setTeam(hName)
//                                .setNumber(row.getNumber())
//                                .setName(row.getName())
//                                .setPoints(row.getPoints())
//                                .setFouls(row.getFouls())
//                                .setCaptain(row.getCaptain());
//                    }
//                    for (Map.Entry<Integer, SidePanelRow> entry : allGuestPlayers.entrySet()) {
//                        SidePanelRow row = entry.getValue();
//                        PlayersResults playersResults = realm.createObject(PlayersResults.class);
//                        playersResults.setGame(result)
//                                .setTeam(gName)
//                                .setNumber(row.getNumber())
//                                .setName(row.getName())
//                                .setPoints(row.getPoints())
//                                .setFouls(row.getFouls())
//                                .setCaptain(row.getCaptain());
//                    }
//                }
            }
        });
    }

    public Game saveAndNew() {
        save();
        saveDb();
        return newGame(context);
    }

    public String getShareString() {
        return gameResult.getResultString(period > preferences.numRegularPeriods);
    }
}
