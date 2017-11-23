package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.results.Result;

class ResultViewScoreTable extends TableLayout {
    private Result result;
    private int cellLayoutId = R.layout.result_view_table_item;

    public ResultViewScoreTable(Context context) {
        super(context);
    }

    ResultViewScoreTable(Context context, Result result) {
        super(context);
        this.result = result;
        inflate(context, R.layout.result_view_score_table, this);
        initHeader();
        initHomeRow();
        initGuestRow();
    }

    private void initHeader() {
        TableRow headerRow = findViewById(R.id.res_score_table_title_row);
        int numRegular = result.getNumRegular();
        int pos = 1;
        for (int i=0; i < result.getHomeScoreByPeriod().size(); i++, pos++) {
            TextView view = (TextView) inflate(getContext(), cellLayoutId, null);
            view.setText(i < numRegular ? String.valueOf(pos) : "OT" + (pos - numRegular));
            headerRow.addView(view, pos);
        }
        ((TextView) findViewById(R.id.res_score_table_title_final)).setText(R.string.final_result);

    }

    private void initHomeRow() {
        TableRow row = findViewById(R.id.res_score_table_home_row);
        ((TextView) findViewById(R.id.res_score_table_home_name)).setText(result.getHomeName());
        for (int score : result.getHomeScoreByPeriod()) {
            TextView view = (TextView) inflate(getContext(), cellLayoutId, null);
            view.setText(String.valueOf(score));
            row.addView(view);
        }
        TextView view = (TextView) inflate(getContext(), cellLayoutId, null);
        view.setText(String.valueOf(result.getHomeScore()));
        row.addView(view);
    }

    private void initGuestRow() {
        TableRow row = findViewById(R.id.res_score_table_guest_row);
        ((TextView) findViewById(R.id.res_score_table_guest_name)).setText(result.getGuestName());
        for (int score : result.getGuestScoreByPeriod()) {
            TextView view = (TextView) inflate(getContext(), cellLayoutId, null);
            view.setText(String.valueOf(score));
            row.addView(view);
        }
        TextView view = (TextView) inflate(getContext(), cellLayoutId, null);
        view.setText(String.valueOf(result.getGuestScore()));
        row.addView(view);
    }
}
