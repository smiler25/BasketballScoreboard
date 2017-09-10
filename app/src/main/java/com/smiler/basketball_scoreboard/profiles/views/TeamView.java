package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.profiles.TeamViewCallback;

public class TeamView extends LinearLayout {
    public static String TAG = "BS-TeamView";
    private TextView title;
    private RealmController realmController;
    private Team team;

    public TeamView(Context context, int teamId, TeamViewCallback listener) {
        super(context);
        realmController = RealmController.with();
        if(!isInEditMode()) {
            init(teamId, listener);
        }
    }

    private void init(int teamId, TeamViewCallback listener) {
        inflate(getContext(), R.layout.detail_scroll_view, this);
        title = (TextView)findViewById(R.id.detail_scroll_view_title);
        LinearLayout layout = (LinearLayout) findViewById(R.id.container);

        getData(teamId);
        if (team == null) {
            return;
        }
        title.setText(team.getName());
        layout.addView(new TeamViewStats(getContext(), team));
        layout.addView(new TeamViewPlayers(getContext(), team, listener));
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private void getData(int teamId) {
        team = realmController.getTeam(teamId);
    }

    public void setListener() {

    }
}
