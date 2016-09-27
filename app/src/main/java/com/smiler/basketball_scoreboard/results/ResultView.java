package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.DbHelper;
import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.models.Player;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

class ResultView extends LinearLayout {
    public static String TAG = "BS-ResultView";
    private TextView title;
    private int sqlId;

    ResultView(Context context, int sqlId) {
        super(context);
        if(!isInEditMode()) {
            this.sqlId = sqlId;
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.result_view, this);
        title = (TextView)findViewById(R.id.result_view_title);

        Result result = getResult();
        TreeMap<String, ArrayList<Player>> playersData = getPlayersContent();
        TreeMap<String, String> detailData = getDetailContent();
        JSONArray playByPlay = getPlayByPlay();
        long date = result.getDate();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        String dateStr = dateFormat.format(new Date(date));
        title.setText(dateStr);

        LinearLayout layout = (LinearLayout) findViewById(R.id.result_view);
        layout.addView(new ResultViewScoreTable(getContext(), result));

        if (!detailData.isEmpty()) {
            layout.addView(new ResultViewDetail(getContext(), detailData));
        }

        if (!playersData.isEmpty()) {
            layout.addView(new ResultViewBoxscore(getContext(), playersData));
        }

        if (playByPlay != null && playByPlay.length() > 0) {
            SparseArray<Player> hPlayers = new SparseArray<>();
            SparseArray<Player> gPlayers = new SparseArray<>();
            if (!playersData.isEmpty()) {
                for (Map.Entry<String, ArrayList<Player>> entry : playersData.entrySet()) {
                    SparseArray<Player> players = (entry.getKey().equals(result.getHomeName())) ? hPlayers : gPlayers;
                    for (Player player : entry.getValue()) {
                        players.put(player.getNumber(), player);
                    }
                }
            }
            layout.addView(new ResultViewPlayByPlay(getContext(), playByPlay, hPlayers, gPlayers, result.getHomeName(), result.getGuestName()));
        }
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private Result getResult() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        Result res = null;
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsTable.COLUMN_DATE,
                    DbScheme.ResultsTable.COLUMN_HOME_TEAM,
                    DbScheme.ResultsTable.COLUMN_GUEST_TEAM,
                    DbScheme.ResultsTable.COLUMN_HOME_SCORE,
                    DbScheme.ResultsTable.COLUMN_GUEST_SCORE,
                    DbScheme.ResultsTable.COLUMN_HOME_PERIODS,
                    DbScheme.ResultsTable.COLUMN_GUEST_PERIODS,
                    DbScheme.ResultsTable.COLUMN_REGULAR_PERIODS,
                    DbScheme.ResultsTable.COLUMN_COMPLETE
            };
            String query = "_id = ?";
            Cursor c = db.query(
                    DbScheme.ResultsTable.TABLE_NAME,
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
                        try {
                            hPeriods.add(Integer.parseInt(periodString));
                        } catch (NumberFormatException e) {
                            Log.d(TAG, "getResult (h): " + hPeriodsString + " - " + periodString);
                        }
                    }
                }
                if (!gPeriodsString.equals("")) {
                    for (String periodString : gPeriodsString.split("-")) {
                        try {
                            gPeriods.add(Integer.parseInt(periodString));
                        } catch (NumberFormatException e) {
                            Log.d(TAG, "getResult (g): " + gPeriodsString + " - " + periodString);
                        }
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

    private TreeMap<String, ArrayList<Player>> getPlayersContent() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        TreeMap<String, ArrayList<Player>> result = new TreeMap<>();
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_TEAM,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NUMBER,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NAME,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_POINTS,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_FOULS,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_CAPTAIN,
            };
            String query = DbScheme.ResultsPlayersTable.COLUMN_GAME_ID + " = ?";
            Cursor c = db.query(
                    DbScheme.ResultsPlayersTable.TABLE_NAME,
                    columns,
                    query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String team = c.getString(0);
                    if (result.get(team) == null) { result.put(team, new ArrayList<Player>()); }
                    result.get(team).add(new Player(c.getInt(1), c.getString(2), c.getInt(3), c.getInt(4), c.getInt(5) == 1));
                } while (c.moveToNext());
            }
            c.close();
        } finally {
            dbHelper.close();
        }
        return result;
    }

    private TreeMap<String, String> getDetailContent() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        TreeMap<String, String> result = new TreeMap<>();
        try {
            String[] columns = new String[] {
                    DbScheme.GameDetailsTable.COLUMN_LEADER_CHANGED,
                    DbScheme.GameDetailsTable.COLUMN_TIE,
                    DbScheme.GameDetailsTable.COLUMN_HOME_MAX_LEAD,
                    DbScheme.GameDetailsTable.COLUMN_GUEST_MAX_LEAD,
            };
            String query = DbScheme.GameDetailsTable.COLUMN_GAME_ID + " = ?";
            Cursor c = db.query(
                    DbScheme.GameDetailsTable.TABLE_NAME,
                    columns,
                    query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    result.put(DbScheme.GameDetailsTable.COLUMN_LEADER_CHANGED, c.getString(0));
                    result.put(DbScheme.GameDetailsTable.COLUMN_TIE, c.getString(1));
                    result.put(DbScheme.GameDetailsTable.COLUMN_HOME_MAX_LEAD, c.getString(2));
                    result.put(DbScheme.GameDetailsTable.COLUMN_GUEST_MAX_LEAD, c.getString(3));
                } while (c.moveToNext());
            }
            c.close();
        } finally {
            dbHelper.close();
        }
        return result;
    }

    private JSONArray getPlayByPlay() {
        DbHelper dbHelper = DbHelper.getInstance(getContext());
        SQLiteDatabase db = dbHelper.open();
        JSONArray result = null;
        try {
            String[] columns = new String[] {
                    DbScheme.GameDetailsTable.COLUMN_PLAY_BY_PLAY,
            };
            String query = DbScheme.GameDetailsTable.COLUMN_GAME_ID + " = ?";
            Cursor c = db.query(
                    DbScheme.GameDetailsTable.TABLE_NAME,
                    columns,
                    query,
                    new String[]{Integer.toString(sqlId)},
                    null, null, null
            );
            if (c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String val = c.getString(0);
                    if (val != null) {
                        result = new JSONArray(val);
                    }
                } while (c.moveToNext());
            }
            c.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
        return result;
    }
}
