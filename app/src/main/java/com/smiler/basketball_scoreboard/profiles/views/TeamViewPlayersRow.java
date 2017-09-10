package com.smiler.basketball_scoreboard.profiles.views;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

class TeamViewPlayersRow extends TableRow {

    public TeamViewPlayersRow(Context context) {
        super(context);
        inflate(context, R.layout.profile_view_player_row, this);
        findViewById(R.id.profile_player_edit).setVisibility(INVISIBLE);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    public TeamViewPlayersRow(Context context, OnClickListener listener) {
        super(context);
        inflate(context, R.layout.profile_view_player_row_add, this);
        if (listener != null) {
            findViewById(R.id.profile_player_add).setOnClickListener(listener);
        }
    }

    TeamViewPlayersRow(Context context, int playerId, String number, String name, boolean even, OnClickListener listener) {
        super(context);
        inflate(context, R.layout.profile_view_player_row, this);
        ((TextView) findViewById(R.id.profile_player_number)).setText(number);
        ((TextView) findViewById(R.id.profile_player_name)).setText(name);
        if (listener != null) {
            View view = findViewById(R.id.profile_player_edit);
            view.setTag(playerId);
            view.setOnClickListener(listener);
        }
        setBackground(even ? getResources().getDrawable(R.drawable.result_table_row) : getResources().getDrawable(R.drawable.dvt_row_even));
    }
}
