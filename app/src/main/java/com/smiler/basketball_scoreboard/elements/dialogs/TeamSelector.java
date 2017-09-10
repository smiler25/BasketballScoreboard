package com.smiler.basketball_scoreboard.elements.dialogs;

import com.smiler.basketball_scoreboard.db.Team;

public interface TeamSelector {
    void handleTeamSelect(int type, Team team);
}
