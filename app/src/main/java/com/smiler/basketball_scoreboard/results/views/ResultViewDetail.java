package com.smiler.basketball_scoreboard.results.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.DetailViewExpandable;
import com.smiler.basketball_scoreboard.results.ResultGameDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class ResultViewDetail extends DetailViewExpandable {

    public ResultViewDetail(Context context) {
        super(context);
    }

    ResultViewDetail(Context context, TreeMap<String, Integer> result) {
        super(context, R.string.results_detail);
        addView(initView(context, result), true);
    }

    private View initView(Context context, TreeMap<String, Integer> data) {
        HashMap<String, String> strings = ResultGameDetails.strings(getResources());

        String key;
        Integer val;
        ArrayList<String> parts = new ArrayList<>();
        TextView info = new TextView(context);
        info.setVisibility(View.GONE);
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            key = entry.getKey();
            val = entry.getValue();
            parts.add(String.format("%s: %s", strings.containsKey(key) ? strings.get(key) : key, val));
        }
        info.setText(TextUtils.join("\n", parts));
        return info;
    }
}
