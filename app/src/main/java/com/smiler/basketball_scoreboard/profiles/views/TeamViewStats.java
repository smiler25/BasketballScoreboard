package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.profiles.TeamStats;

import java.util.ArrayList;

class TeamViewStats extends DetailViewExpandable {

    public TeamViewStats(Context context) {
        super(context);
    }

    TeamViewStats(Context context, TeamStats data) {
        super(context, R.string.profile_stats);
        addView(initView(context, data), false);
    }

    private View initView(Context context, TeamStats data) {
        TextView info = new TextView(context);
        info.setVisibility(View.GONE);
        ArrayList<String> parts = new ArrayList<>();
        parts.add(String.format("%s: %s", "Total games", data.getTotalGames()));
        parts.add(String.format("%s: %s", "Wins", data.getWins()));
        parts.add(String.format("%s: %s", "Average points", data.getAvgPoints()));
        parts.add(String.format("%s: %s", "Opponent average points", data.getAvgPointsOpponent()));

        info.setText(TextUtils.join(";\n", parts));
        return info;
    }
}
