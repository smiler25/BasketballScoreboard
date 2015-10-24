package com.smiler.basketball_scoreboard.elements;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.DbHelper;
import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.ResultTableAdapter;
import com.smiler.basketball_scoreboard.ResultTableTeamListAdapter;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class ResultView extends LinearLayout {
    private TextView title;
    private ListView teamsList;
    private GridView scoresGrid;
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
        ViewGroup tableView = (ViewGroup) findViewById(R.id.resultViewTable);
        teamsList = (ListView)findViewById(R.id.resultViewTableTeams);
        scoresGrid = (GridView)findViewById(R.id.resultViewTableScores);

        ArrayList<String> data = getGridContent();
        boolean complete = true;
        if (data.size() % 2 != 0) {
            complete = false;
            data.remove(data.size() - 1);
        }
        String hName = data.get(0);
        data.remove(0);
        String gName = data.get(0);
        data.remove(0);
        long date = Long.parseLong(data.get(data.size() - 1));
        data.remove(data.size() - 1);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        String dateStr = dateFormat.format(new Date(date));

        createScoresGrid(data);
        createTeamsList(hName, gName);
        title.setText(dateStr);
        tableView.setBackground(getContext().getResources().getDrawable(R.drawable.result_table_shape));
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private ListView createTeamsList(String hName, String gName) {
        ArrayAdapter adapter = new ResultTableTeamListAdapter(getContext(), R.layout.results_table_item,
                new String[] {"", hName, gName});
        teamsList.setAdapter(adapter);
        return teamsList;
    }

    private GridView createScoresGrid(ArrayList<String> data) {
        int size = data.size();
        int numRegular = Integer.parseInt(data.get(--size));
        data.remove(size);
        int columns = size / 2;
        ArrayList<String> values = new ArrayList<>();
        for (int i = 1; i <= columns-1; i++) {
            if (i <= numRegular) {
                values.add(Integer.toString(i));
            } else {
                values.add("OT" + (i - numRegular));
            }
        }
        values.add(getContext().getResources().getString(R.string.final_result));

        for (int i = 0; i < size; i++){
            values.add(data.get(i));
        }
        scoresGrid.setNumColumns(columns);
        ResultTableAdapter adapter = new ResultTableAdapter(getContext(), R.layout.results_table_item, values);
        scoresGrid.setAdapter(adapter);
        return scoresGrid;
    }

    private ArrayList<String> getGridContent() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        ArrayList<String> result = new ArrayList<>();
        try {
            String[] columns = new String[] {DbScheme.ResultsTable.COLUMN_NAME_DATE,
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
            Cursor c = db.query(DbScheme.ResultsTable.TABLE_NAME,
                    columns, query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );

            if (c.getCount() == 1) {
                c.moveToFirst();
                long date = c.getLong(0);
                String hName = " " + c.getString(1);
                String gName = " " + c.getString(2);
                int hScore = c.getInt(3);
                int gScore = c.getInt(4);
                String[] hPeriods = c.getString(5).split("-");
                String[] gPeriods = c.getString(6).split("-");
                int numRegular = c.getInt(7);
                int complete = c.getInt(8);

                result.add(hName);
                result.add(gName);
                if (!hPeriods[0].equals("")) Collections.addAll(result, hPeriods);
                result.add(Integer.toString(hScore));
                if (!gPeriods[0].equals("")) Collections.addAll(result, gPeriods);
                result.add(Integer.toString(gScore));
                result.add(Integer.toString(numRegular));
                result.add(Long.toString(date));
                if (complete > 0) result.add(Integer.toString(complete));
            }
            c.close();
        } finally {
            dbHelper.close();
        }
        return result;
    }
}
