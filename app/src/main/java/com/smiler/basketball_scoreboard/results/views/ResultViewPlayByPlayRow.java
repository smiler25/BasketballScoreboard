package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

class ResultViewPlayByPlayRow extends TableRow {

    public ResultViewPlayByPlayRow(Context context) {
        super(context);
        inflate(context, R.layout.result_view_info_table_row, this);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    ResultViewPlayByPlayRow(Context context, String text) {
        super(context);
        inflate(context, R.layout.result_view_info_table_quarter_row, this);
        ((TextView) findViewById(R.id.results_quarter)).setText(text);
        setBackground(getResources().getDrawable(R.drawable.res_score_delimeter_shape));
    }

    ResultViewPlayByPlayRow(Context context, String hName, String gName) {
        super(context);
        inflate(context, R.layout.result_view_play_by_play_row, this);
        ((TextView) findViewById(R.id.results_play_by_play_left)).setText(hName);
        ((TextView) findViewById(R.id.results_play_by_play_right)).setText(gName);
        setBackground(getResources().getDrawable(R.drawable.res_score_header_shape));
    }

    ResultViewPlayByPlayRow(Context context, String left, String center, String right, boolean even, boolean last) {
        super(context);
        inflate(context, R.layout.result_view_play_by_play_row, this);
        ((TextView) findViewById(R.id.results_play_by_play_left)).setText(left);
        ((TextView) findViewById(R.id.results_play_by_play_center)).setText(center);
        ((TextView) findViewById(R.id.results_play_by_play_right)).setText(right);
        if (last) {
            setBackground(even ? getResources().getDrawable(R.drawable.dvt_row_last) : getResources().getDrawable(R.drawable.dvt_row_even_last));
        } else {
            setBackground(even ? getResources().getDrawable(R.drawable.result_table_row) : getResources().getDrawable(R.drawable.dvt_row_even));
        }
    }
}
