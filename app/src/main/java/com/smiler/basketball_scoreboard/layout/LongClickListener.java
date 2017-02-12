package com.smiler.basketball_scoreboard.layout;

public interface LongClickListener {
    boolean onArrowLongClick();
    boolean onFoulsLongClick(boolean left);
    boolean onMainTimeLongClick();
    boolean onNameLongClick(boolean left);
    boolean onPeriodLongClick();
    boolean onScoreLongClick(boolean left);
    boolean onShotTimeLongClick();
    boolean onTimeoutsLongClick(boolean left);
    boolean onTimeouts20LongClick(boolean left);
    boolean onPlayerButtonLongClick(boolean left);
}
