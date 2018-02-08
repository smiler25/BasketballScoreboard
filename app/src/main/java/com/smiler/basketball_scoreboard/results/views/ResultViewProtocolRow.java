package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.results.ProtocolRecord;

import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;

class ResultViewProtocolRow extends TableRow {
    ResultViewProtocolRow(Context context) {
        super(context);
        inflate(context, R.layout.result_view_protocol_row, this);
        setBackground(getResources().getDrawable(R.drawable.res_score_footer_shape));
    }

    ResultViewProtocolRow(Context context, String text) {
        super(context);
        inflate(context, R.layout.result_view_info_table_quarter_row, this);
        ((TextView) findViewById(R.id.results_quarter)).setText(text);
        setBackground(getResources().getDrawable(R.drawable.res_score_delimeter_shape));
    }

    ResultViewProtocolRow(Context context, String hName, String gName) {
        super(context);
        inflate(context, R.layout.result_view_protocol_row, this);
        ((TextView) findViewById(R.id.protocol_l_player)).setText(R.string.home_player);
        ((TextView) findViewById(R.id.protocol_r_player)).setText(R.string.guest_player);
        ((TextView) findViewById(R.id.protocol_l_score)).setText(R.string.score);
        ((TextView) findViewById(R.id.protocol_r_score)).setText(R.string.score);
        ((TextView) findViewById(R.id.protocol_period_time)).setText(R.string.period_time);
        ((TextView) findViewById(R.id.protocol_game_time)).setText(R.string.game_time);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    ResultViewProtocolRow(Context context, ProtocolRecord record, boolean even) {
        super(context);
        inflate(context, R.layout.result_view_protocol_row, this);
        ((TextView) findViewById(R.id.protocol_period_time)).setText(TIME_FORMAT.format(record.getPeriodTime()));
        ((TextView) findViewById(R.id.protocol_game_time)).setText(TIME_FORMAT.format(record.getGameTime()));

        if (record.getTeam() == HOME) {
            ((TextView) findViewById(R.id.protocol_l_player)).setText(Integer.toString(record.getPlayerNumber()));
            ((TextView) findViewById(R.id.protocol_l_score)).setText(Integer.toString(record.getValue()));
        } else {
            ((TextView) findViewById(R.id.protocol_r_player)).setText(Integer.toString(record.getPlayerNumber()));
            ((TextView) findViewById(R.id.protocol_r_score)).setText(Integer.toString(record.getValue()));
        }
        setBackground(even ? getResources().getDrawable(R.drawable.result_table_row) : getResources().getDrawable(R.drawable.dvt_row_even));
    }
}
