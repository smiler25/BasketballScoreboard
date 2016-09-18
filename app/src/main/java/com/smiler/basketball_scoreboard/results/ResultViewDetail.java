package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ResultViewDetail extends ResultViewExpandable {

    public ResultViewDetail(Context context) {
        super(context);
    }

    ResultViewDetail(Context context, TreeMap<String, String> result) {
        super(context, R.string.results_detail);
        addView(initView(context, result), true);
    }

    private View initView(Context context, TreeMap<String, String> data) {
        final Resources resources = getResources();
        HashMap<String, String> strings = new HashMap<String, String>()
        {{
            put(DbScheme.GameDetailsTable.COLUMN_LEADER_CHANGED, resources.getString(R.string.lead_changed));
            put(DbScheme.GameDetailsTable.COLUMN_TIE, resources.getString(R.string.tie));
            put(DbScheme.GameDetailsTable.COLUMN_HOME_MAX_LEAD, resources.getString(R.string.home_max_lead));
            put(DbScheme.GameDetailsTable.COLUMN_GUEST_MAX_LEAD, resources.getString(R.string.guest_max_lead));
        }};

        String key, val;
        ArrayList<String> parts = new ArrayList<>();
        TextView info = new TextView(context);
        info.setVisibility(View.GONE);
        for (Map.Entry<String, String> entry : data.entrySet()) {
            key = entry.getKey();
            val = entry.getValue();
            parts.add(String.format("%s: %s", (strings.containsKey(key)) ? strings.get(key) : key, val));
        }
        info.setText(TextUtils.join("; ", parts));
        return info;
    }
}
