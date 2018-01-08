package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.View;
import android.widget.TableLayout;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.game.ActionRecord;
import com.smiler.basketball_scoreboard.game.InGamePlayer;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;

import static com.smiler.basketball_scoreboard.Constants.HOME;
import static com.smiler.basketball_scoreboard.Constants.SECONDS_60;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT;
import static com.smiler.basketball_scoreboard.Constants.TIME_FORMAT_MILLIS;

class ResultViewPlayByPlay extends DetailViewExpandable {

    private String hName, gName;

    public ResultViewPlayByPlay(Context context) {
        super(context);
    }

    ResultViewPlayByPlay(Context context, JSONArray data, SparseArray hPlayers, SparseArray gPlayers, String hName, String gName) {
        super(context, R.string.results_play_by_play);
        this.hName = hName;
        this.gName = gName;
        addView(initView(context, data, hPlayers, gPlayers), true);
    }

    private View initView(Context context, JSONArray data, SparseArray hPlayers, SparseArray gPlayers) {
        int periods = data.length();
        int count;
        ActionRecord record;
        JSONArray period_data;
        String left, center, right;
        String recordFmt = "%s: %s";
        String text;
        String quarterFmt = "%s " + getResources().getString(R.string.quarter);
        SimpleDateFormat mainTimeFormat;
        TableLayout table = getTable(context);
        SparseArray players;
        long time;
        try {
            table.addView(new ResultViewPlayByPlayRow(context, hName, gName));
            for (int i = 0; i < periods; i++) {
                table.addView(new ResultViewPlayByPlayRow(context, String.format(quarterFmt, i+1)));
                period_data = data.getJSONArray(i);
                count = period_data.length();
                for (int y = 0; y < count; y++) {
                    record = new ActionRecord(period_data.getJSONObject(y));
                    time = record.getTime();
                    mainTimeFormat = 0 < time && time < SECONDS_60 ? TIME_FORMAT_MILLIS : TIME_FORMAT;
                    center = mainTimeFormat.format(time);
                    players = record.getTeam() == HOME ? hPlayers : gPlayers;
                    text = String.format(recordFmt, playerString(record, players), actionString(record));
                    if (record.getTeam() == HOME) {
                        left = text;
                        right = "";
                    } else {
                        left = "";
                        right = text;
                    }
                    table.addView(new ResultViewPlayByPlayRow(context, left, center, right, (y & 1) == 0, i+1 == periods && y+1 == count));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return table;
    }

    private String playerString(ActionRecord record, SparseArray players) {
        if (record.getNumber() == -1) {
            return record.getTeam() == HOME ? hName : gName;
        }
        return String.format("%s (%d)", players.indexOfKey(record.getNumber()) > -1 ? ((InGamePlayer)players.get(record.getNumber())).getName() : getResources().getString(R.string.text_player), record.getNumber());
    }

    private String actionString(ActionRecord record) {
        Resources res = getResources();
        switch (record.getAction()) {
            case SCORE:
                return res.getQuantityString(R.plurals.points, record.getValue(), record.getValue());
            case FOUL:
                return res.getString(R.string.foul);
            case TIMEOUT:
                return res.getString(R.string.timeout);
            case TIMEOUT_20:
                return res.getString(R.string.timeout20);
            default:
                return "";
        }
    }
}