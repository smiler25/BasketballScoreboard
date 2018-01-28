package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.view.View;
import android.widget.TableLayout;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.game.Actions;
import com.smiler.basketball_scoreboard.results.Protocol;
import com.smiler.basketball_scoreboard.results.ProtocolRecord;

import java.util.ArrayList;

class ResultViewProtocol extends DetailViewExpandable {
    public ResultViewProtocol(Context context) {
        super(context);
    }

    ResultViewProtocol(Context context, Protocol protocol) {
        super(context, R.string.results_play_by_play);
        addView(initView(context, protocol), true);
    }

    private View initView(Context context, Protocol protocol) {
//        String quarterFmt = "%s " + getResources().getString(R.string.quarter);
        TableLayout table = getTable(context);
        table.addView(new ResultViewProtocolRow(context, protocol.getHomeName(), protocol.getGuestName()));
        int totalPeriods = protocol.getPeriods().size();
        boolean even = true;
        for (ArrayList<ProtocolRecord> period : protocol.getPeriods()) {
//            table.addView(new ResultViewProtocolRow(context, String.format(quarterFmt, i+1)));
            for (ProtocolRecord record : period) {
                if (record.getAction() == Actions.SCORE) {
                    table.addView(new ResultViewProtocolRow(context, record, even));
                }
            }
            even = !even;
        }
        return table;
    }
}