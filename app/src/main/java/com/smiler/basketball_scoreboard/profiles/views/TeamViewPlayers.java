package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.profiles.TeamViewCallback;

import io.realm.RealmList;

class TeamViewPlayers extends DetailViewExpandable {
    TeamViewCallback listener;

    public TeamViewPlayers(Context context) {
        super(context);
    }

    TeamViewPlayers(Context context, Team data, TeamViewCallback listener) {
        super(context, R.string.profile_players);
        this.listener = listener;
        addView(initView(context, data), false);
    }

    private View initView(Context context, Team data) {
        TableLayout table = getTable(context);
        RealmList<Player> players = data.getPlayers();
        table.addView(new TeamViewPlayersRow(context));
        int line = 0;

        OnClickListener editListener = view -> {
            if (listener != null) {
                listener.onTeamPlayerEdit((Integer) view.getTag());
            }
        };

        for (Player player: players) {
            table.addView(new TeamViewPlayersRow(context, player.getId(), Integer.toString(player.getNumber()), player.getName(), (line & 1) == 0, editListener));
            line++;
        }
        table.addView(new TeamViewPlayersRow(context, view -> {
            if (listener != null) {
                listener.onTeamPlayerAdd();
            }
        }));
        return table;
    }
}
