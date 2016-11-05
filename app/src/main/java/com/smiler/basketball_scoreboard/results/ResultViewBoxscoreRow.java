package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.models.Player;

class ResultViewBoxscoreRow extends TableRow {

    public ResultViewBoxscoreRow(Context context) {
        super(context);
        inflate(context, R.layout.result_view_info_table_row, this);
        findViewById(R.id.results_player_number);
        findViewById(R.id.results_player_name);
        findViewById(R.id.results_player_points);
        findViewById(R.id.results_player_fouls);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    ResultViewBoxscoreRow(Context context, Player record, boolean even, boolean last) {
        super(context);
        inflate(context, R.layout.result_view_info_table_row, this);
        ((TextView) findViewById(R.id.results_player_number)).setText(String.valueOf(record.getNumber()));
        ((TextView) findViewById(R.id.results_player_name)).setText(!record.isCaptain() ? String.valueOf(record.getName()) : String.valueOf(record.getName()) + "*");
        ((TextView) findViewById(R.id.results_player_points)).setText(String.valueOf(record.getPoints()));
        ((TextView) findViewById(R.id.results_player_fouls)).setText(String.valueOf(record.getFouls()));
        if (last) {
            setBackground(even ? getResources().getDrawable(R.drawable.result_table_row_last) : getResources().getDrawable(R.drawable.result_table_row_even_last));
        } else {
            setBackground(even ? getResources().getDrawable(R.drawable.result_table_row) : getResources().getDrawable(R.drawable.result_table_row_even));
        }
    }
}
