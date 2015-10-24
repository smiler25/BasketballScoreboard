package com.smiler.basketball_scoreboard;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;




public class BSTimer {

    private long total, millisPast, millisLeft;
    private TextView view;
    private long mainTickInterval = Constants.SECOND;
    private long shotTickInterval = Constants.SECOND;
    private Handler timerHandler = new Handler();
    private boolean direct;
    private long startTime;

    public BSTimer(TextView view, long total, boolean direct) {
        this.view = view;
        this.total = total;
        this.direct = direct;
        initTimer();
    }

    private void initTimer() {
        if (direct) {
            startDirectTimer();
        } else {
//            startCountdownTimer();
        }
    }


    public long getPastTime() {
        return this.millisPast;
    }

    public long getLeftTime() {
        return this.millisLeft;
    }


    /*public void startCountdownTimer() {
        mainTimer = new CountDownTimer(millisLeft, mainTickInterval) {
            public void onTick(long millisUntilFinished) {
                millisLeft = millisUntilFinished;
                setMainTimeText(millisLeft);
                if (enableShotTime && millisLeft < shotTime && shotTimerOn) {
                    shotTimer.cancel();
                }
                if (millisLeft < Constants.MINUTES_2 && !changedUnder2Minutes) {
                    changedUnder2Minutes = true;
                    under2Minutes();
                }
                if (millisLeft < Constants.SECONDS_60 && mainTickInterval == Constants.SECOND) {
                    this.cancel();
                    mainTickInterval = 100;
                    mainTimeFormat = Constants.timeFormatMillis;
                    startCountdownTimer();
                }
                if (enableShotTime && millisLeft < shotTime && shotTimeView.getVisibility() == View.VISIBLE) {
                    shotTimeView.setVisibility(View.INVISIBLE);
                }
            }
            public void onFinish() {
                mainTimerOn = false;
                if (autoSound >= 2) { playHorn(); }
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
        if (enableShotTime && mainTime > shotTime) {
            startShotCountDownTimer();
        }
    }

    public void startShotCountDownTimer(long startValue) {
        if (shotTimerOn){
            shotTimer.cancel();
        }
        shotTime = startValue;
        startShotCountDownTimer();
    }

    public void startShotCountDownTimer() {
        shotTimer = new CountDownTimer(shotTime, shotTickInterval) {
            public void onTick(long millisUntilFinished) {
                shotTime = millisUntilFinished;
                setShotTimeText(shotTime);
                if (shotTime < 5000 && shotTickInterval == Constants.SECOND) {
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
                shotTickInterval = Constants.SECOND;
            }
        }.start();
        shotTimerOn = true;
    }

*/




    public void startDirectTimer() {
        stopDirectTimer();
        startTime = SystemClock.uptimeMillis();
//        mainTimeFormat = Constants.timeFormat;
        timerHandler.postDelayed(directTimerThread, 0);
    }

    public void stopDirectTimer() {
        startTime = SystemClock.uptimeMillis();
        millisPast = 0;
        timerHandler.removeCallbacks(directTimerThread);
    }

    public void pauseDirectTimer() {
        timerHandler.removeCallbacks(directTimerThread);
    }

    public Runnable directTimerThread = new Runnable() {
        public void run() {
            millisPast = SystemClock.uptimeMillis() - startTime;
//            setViewText(millisPast);
            timerHandler.postDelayed(this, 1000);
        }
    };


}