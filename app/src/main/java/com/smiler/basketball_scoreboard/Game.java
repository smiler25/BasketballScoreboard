package com.smiler.basketball_scoreboard;

public class Game {

    private boolean mainTimerOn, shotTimerOn, enableShotTime;

    private long mainTime, mainTimePref, shotTime, shotTimePref, shortShotTimePref, overTimePref;
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
    private CountDownTimer mainTimer, shotTimer;

    private Result gameResult;

    public Game() {
    }


   /* private void setTimeouts() {
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
        } else if (timeoutRules == 2){
            takenTimeoutsFull = 0;
            maxTimeouts20 = 1;
            nullTimeouts20(2);
            if (period == 1) {
                maxTimeouts = 6;
                nullTimeouts(2);
            } else if (period == 4 && maxTimeouts > 3) {
                maxTimeouts = 3;
                if (hTimeouts > maxTimeouts) { nullTimeouts(0); }
                if (gTimeouts > maxTimeouts) { nullTimeouts(1); }
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
        } else { timeoutFullDuration = 60; }
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
        mainTickInterval = SECOND;
        mainTimerOn = false;
        mainTime = mainTimePref;
        changedUnder2Minutes = false;
        hScore = gScore = 0;
        setMainTimeText(mainTime);
        changeGuestScore(0);
        changeHomeScore(0);
        setTeamNames();
        if (layout == 0) {
            if (enableShotTime) {
                shotTimerOn = false;
                shotTime = shotTimePref;
                shotTickInterval = SECOND;
                setShotTimeText(shotTime);
                shotTimeView.setVisibility(View.VISIBLE);
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
        if (PrefActivity.gamePrefChanged || PrefActivity.appPrefChanged) { getSettings(); }
        if (layoutChanged || timeoutsRulesChanged) {
            if (layout == 0) {
                initExtensiveLayout();
            } else if (layout == 1) {
                initSimpleLayout();
            }
            initCommonLayout();
        }
        pauseGame();
        zeroState();
        setTimeouts();
        gameResult = new Result(hName, gName);
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
            shotTickInterval = SECOND;
        }
        mainTickInterval = SECOND;
        setMainTimeText(mainTime);
        clearFouls();
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
                    if (hTimeouts20 == 0) { setColorRed(hTimeouts20View); }
                    if (autoShowTimeout) { showTimeout(20, hName); }
                }
                break;
            case 1:
                if (gTimeouts20 > 0) {
                    gTimeouts20View.setText(Short.toString(--gTimeouts20));
                    if (gTimeouts20 == 0) { setColorRed(gTimeouts20View); }
                    if (autoShowTimeout) { showTimeout(20, gName); }
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
        if (actualTime > 0) { pauseGame(); }
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
        if (enableShotTime && layout == 0) {
            if (mainTimerOn) {
                startShotCountDownTimer(shotTimePref);
            } else {
                shotTime = shotTimePref;
            }
        }
        if (actualTime == 2 || (actualTime == 3 && mainTime < SECONDS_60)) {
            pauseGame();
        }
    }

    private void changeGuestScore(int value) {
        gScore += value;
        if (value != 0) { changeScore(); }
    }

    private void changeHomeScore(int value) {
        hScore += value;
        if (value != 0) { changeScore(); }
    }

    private void setPeriod() {
        if (period <= numRegularPeriods) {
            mainTime = mainTimePref;
        } else {
            mainTime = overTimePref;
        }
    }

    private void pauseGame() {
        if (mainTimerOn) {
            mainTimer.cancel();
            if (enableShotTime && shotTimerOn) { shotTimer.cancel(); }
        }
        mainTimerOn = shotTimerOn = false;
    }

    private void under2Minutes() {
        if (timeoutRules == 2) {
            if (period ==4) {
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
                if (mainTime < MINUTES_2 && !changedUnder2Minutes) {
                    changedUnder2Minutes = true;
                    under2Minutes();
                }
                if (mainTime < SECONDS_60 && mainTickInterval == SECOND) {
                    this.cancel();
                    mainTickInterval = 100;
                    startMainCountDownTimer();
                }
                if (enableShotTime && mainTime < shotTime && shotTimeView.getVisibility() == View.VISIBLE) {
                    shotTimeView.setVisibility(View.INVISIBLE);
                }
            }
            public void onFinish() {
                mainTimerOn = false;
                if (autoSound >= 2) { playHorn(); }
                mainTickInterval = SECOND;
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
        if (enableShotTime && mainTime > shotTime) {
            startShotCountDownTimer();
        }
    }

    private void startShotCountDownTimer(long startValue) {
        if (shotTimerOn){
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
                if (shotTime < 5000 && shotTickInterval == SECOND) {
                    shotTickInterval = 100;
                    shotTimer.cancel();
                    startShotCountDownTimer();
                }
            }
            public void onFinish() {
                pauseGame();
                if (autoSound == 1 || autoSound == 3) { playHorn(); }
                setShotTimeText(0);
                shotTimeView.startAnimation(shotTimeBlinkAnimation);
                shotTime = shotTimePref;
                shotTickInterval = SECOND;
            }
        }.start();
        shotTimerOn = true;
    }
*/
}