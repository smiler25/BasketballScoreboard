package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;

import java.util.ArrayList;

class TeamViewStats extends DetailViewExpandable {

    public TeamViewStats(Context context) {
        super(context);
    }

    TeamViewStats(Context context, Team team) {
        super(context, R.string.profile_stats);
        addView(initView(context, team), false);
    }

    private View initView(Context context, Team team) {
        TextView info = new TextView(context);
        ArrayList<String> parts = new ArrayList<>();
        Resources res = getResources();
        int total = team.getWins() + team.getLoses();
        parts.add(String.format(res.getString(R.string.team_total_games), total));
        if (total > 0) {
            parts.add(String.format(res.getString(R.string.team_wins), team.getWins()));
            parts.add(String.format(res.getString(R.string.team_avg_pts), String.valueOf(team.getAvgPoints())));
            parts.add(String.format(res.getString(R.string.team_avg_pts_opp), String.valueOf(team.getAvgPointsOpp())));
        }
        info.setText(TextUtils.join("\n", parts));
        return info;
    }
}
