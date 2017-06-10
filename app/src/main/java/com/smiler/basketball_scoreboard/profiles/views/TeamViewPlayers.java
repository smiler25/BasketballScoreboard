package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.profiles.TeamProfile;

import java.util.ArrayList;

class TeamViewPlayers extends DetailViewExpandable {

    public TeamViewPlayers(Context context) {
        super(context);
    }

    TeamViewPlayers(Context context, TeamProfile data) {
        super(context, R.string.profile_players);
        addView(initView(context, data, null), false);
    }

    private View initView(Context context, TeamProfile data, OnClickListener listener) {
        TableLayout table = getTable(context);
        ArrayList<Player> players = data.getPlayers();
        table.addView(new TeamViewPlayersRow(context));
        int line = 0;
        for (Player player: players) {
            table.addView(new TeamViewPlayersRow(context, Integer.toString(player.getNumber()), player.getName(), (line & 1) == 0, listener));
            line++;
        }
        table.addView(new TeamViewPlayersRow(context, listener));
        return table;
    }
}
