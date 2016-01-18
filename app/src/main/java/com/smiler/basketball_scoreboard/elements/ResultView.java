package com.smiler.basketball_scoreboard.elements;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.DbHelper;
import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.Result;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ResultView extends LinearLayout {
    private TextView title;
    private TableLayout playersTable;
    private int sqlId;

    public ResultView(Context context, int sqlId) {
        super(context);
        if(!isInEditMode()) {
            this.sqlId = sqlId;
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.result_view, this);
        title = (TextView)findViewById(R.id.resultViewTitle);

        Result result = getResult();
        TreeMap<String, ArrayList<Object[]>> playersData = getPlayersContent();
        long date = result.getDate();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        String dateStr = dateFormat.format(new Date(date));
        title.setText(dateStr);

        LinearLayout layout = (LinearLayout) findViewById(R.id.result_view);
        layout.addView(new ResultViewScoreTable(getContext(), result));

        if (!playersData.isEmpty()) {
            TextView boxScoreTextView = new TextView(getContext());
            boxScoreTextView.setText(R.string.results_boxscore);
            int padding = getResources().getDimensionPixelSize(R.dimen.results_boxscore_padding);
            boxScoreTextView.setPadding(padding, padding, padding, padding);
            layout.addView(boxScoreTextView);
            for (Map.Entry<String, ArrayList<Object[]>> entry : playersData.entrySet()) {
                layout.addView(createPlayersTable(entry.getKey(), entry.getValue()));
            }
        }
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private View createPlayersTable(String team, ArrayList<Object[]> data) {
        Context context = getContext();
        View tableWithHeader = inflate(context, R.layout.result_view_players_table, null);
        TextView title = (TextView) tableWithHeader.findViewById(R.id.result_view_players_table_title);
        final TableLayout table = (TableLayout) tableWithHeader.findViewById(R.id.result_view_players_table);
        title.setText(team);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                table.setVisibility((table.getVisibility() == VISIBLE) ? GONE : VISIBLE);
            }
        });

        table.addView(new ResultViewPlayerRow(context));
        table.setVisibility(View.GONE);
        for (Object[] value : data) {
            table.addView(new ResultViewPlayerRow(context, value));
        }
        return tableWithHeader;
    }

    private Result getResult() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        Result res = null;
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsTable.COLUMN_NAME_DATE,
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_TEAM,
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_TEAM,
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_SCORE,
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_SCORE,
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_PERIODS,
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_PERIODS,
                    DbScheme.ResultsTable.COLUMN_NAME_REGULAR_PERIODS,
                    DbScheme.ResultsTable.COLUMN_NAME_COMPLETE
            };
            String query = "_id = ?";
            Cursor c = db.query(
                    DbScheme.ResultsTable.TABLE_NAME_GAME,
                    columns, query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );

            if (c.getCount() == 1) {
                c.moveToFirst();
                ArrayList<Integer> hPeriods = new ArrayList<>();
                ArrayList<Integer> gPeriods = new ArrayList<>();
                String hPeriodsString = c.getString(5);
                String gPeriodsString = c.getString(6);
                if (!hPeriodsString.equals("")) {
                    for (String periodString : hPeriodsString.split("-")) {
                        hPeriods.add(Integer.parseInt(periodString));
                    }
                }
                if (!gPeriodsString.equals("")) {
                    for (String periodString : gPeriodsString.split("-")) {
                        gPeriods.add(Integer.parseInt(periodString));
                    }
                }
                res = new Result(c.getString(1), c.getString(2), c.getInt(3), c.getInt(4),
                        hPeriods, gPeriods, (c.getInt(8) > 0), c.getLong(0), c.getInt(7));
            }
            c.close();
        } finally {
            dbHelper.close();
        }
        return res;
    }

    private TreeMap<String, ArrayList<Object[]>> getPlayersContent() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        TreeMap<String, ArrayList<Object[]>> result = new TreeMap<>();
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_TEAM,
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_NUMBER,
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_NAME,
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_POINTS,
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_FOULS,
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_CAPTAIN,
            };
            String query = DbScheme.ResultsPlayersTable.COLUMN_NAME_GAME_ID + " = ?";
            Cursor c = db.query(
                    DbScheme.ResultsPlayersTable.TABLE_NAME_GAME_PLAYERS,
                    columns,
                    query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String team = c.getString(0);
                    if (result.get(team) == null) { result.put(team, new ArrayList<Object[]>()); }
                    result.get(team).add(new Object[] {c.getInt(1), c.getString(2), c.getInt(3), c.getInt(4), c.getInt(5)});
                } while (c.moveToNext());
            }
            c.close();
        } finally {
            dbHelper.close();
        }
        return result;
    }
}
