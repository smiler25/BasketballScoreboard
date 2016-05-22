package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class ResultViewPlayerRow extends TableRow{

    public ResultViewPlayerRow(Context context) {
        super(context);
        inflate(context, R.layout.result_view_players_table_row, this);
        findViewById(R.id.results_player_number);
        findViewById(R.id.results_player_name);
        findViewById(R.id.results_player_points);
        findViewById(R.id.results_player_fouls);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    public ResultViewPlayerRow(Context context, Object[] values) {
        super(context);
        inflate(context, R.layout.result_view_players_table_row, this);
        ((TextView) findViewById(R.id.results_player_number)).setText(String.valueOf(values[0]));
        ((TextView) findViewById(R.id.results_player_name)).setText((((int) values[4]) == 0) ? String.valueOf(values[1]) : String.valueOf(values[1]) + "*");
        ((TextView) findViewById(R.id.results_player_points)).setText(String.valueOf(values[2]));
        ((TextView) findViewById(R.id.results_player_fouls)).setText(String.valueOf(values[3]));
    }
}
