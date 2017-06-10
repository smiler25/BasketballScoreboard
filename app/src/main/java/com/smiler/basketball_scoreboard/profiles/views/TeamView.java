package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.profiles.TeamProfile;
import com.smiler.basketball_scoreboard.profiles.TeamStats;

class TeamView extends LinearLayout {
    public static String TAG = "BS-TeamView";
    private TextView title;
    private String teamName;
    private RealmController realmController;
    private Team team;

    TeamView(Context context, String teamName) {
        super(context);
        realmController = RealmController.with();
        if(!isInEditMode()) {
            this.teamName = teamName;
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.detail_scroll_view, this);
        title = (TextView)findViewById(R.id.detail_scroll_view_title);
        LinearLayout layout = (LinearLayout) findViewById(R.id.container);

        getData();
        title.setText(team.getName());
        layout.addView(new TeamViewPlayers(getContext(), new TeamProfile(team)));
        if (!team.getGames().isEmpty()) {
            layout.addView(new TeamViewStats(getContext(), new TeamStats(team)));
        }
//
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private void getData() {
        team = realmController.getTeam(teamName);
    }
}
