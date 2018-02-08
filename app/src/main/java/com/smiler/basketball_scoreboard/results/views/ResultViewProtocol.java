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
        super(context, R.string.results_protocol);
        addView(initView(context, protocol), true);
    }

    private View initView(Context context, Protocol protocol) {
        String quarterFmt = "%s " + getResources().getString(R.string.quarter);
        TableLayout table = getTable(context);
        table.addView(new ResultViewProtocolRow(context, protocol.getHomeName(), protocol.getGuestName()));
        boolean even;
        short currentPeriod = 1;
        for (ArrayList<ProtocolRecord> period : protocol.getPeriods()) {
            even = true;
            table.addView(new ResultViewProtocolRow(context, String.format(quarterFmt, currentPeriod)));
            for (ProtocolRecord record : period) {
                if (record.getAction() == Actions.SCORE) {
                    table.addView(new ResultViewProtocolRow(context, record, even));
                    even = !even;
                }
            }
            currentPeriod += 1;
        }
        table.addView(new ResultViewProtocolRow(context));
        return table;
    }
}