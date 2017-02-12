package com.smiler.basketball_scoreboard.layout;

import com.smiler.basketball_scoreboard.panels.SidePanelRow;

public interface ClickListener {
    void onChangeScoreClick(boolean left, int value);
    void onFoulsClick(boolean left);
    void onIconClick(StandardLayout.ICONS icon);
    void onMainTimeClick();
    void onPeriodClick();
    void onShotTimeClick();
    void onShotTimeSwitchClick();
    void onTeamClick(boolean left);
    void onTimeoutsClick(boolean left);
    void onTimeouts20Click(boolean left);
    void onPlayerButtonClick(boolean left, SidePanelRow row);
    void onHornAction(boolean play);
    void onWhistleAction(boolean play);
    void onOpenPanelClick(boolean left);
}
