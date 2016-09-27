package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.models.Player;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

class ResultViewBoxscore extends ResultViewExpandable {

    public ResultViewBoxscore(Context context) {
        super(context);
    }

    ResultViewBoxscore(Context context, TreeMap<String, ArrayList<Player>> result) {
        super(context, R.string.results_boxscore);
        addView(initView(context, result), true);
    }

    private View initView(Context context, TreeMap<String, ArrayList<Player>> data) {
        LinearLayout layout = new LinearLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        layout.setOrientation(VERTICAL);
        layout.setLayoutParams(params);
        for (Map.Entry<String, ArrayList<Player>> entry : data.entrySet()) {
            layout.addView(createPlayersTable(context, entry.getKey(), entry.getValue()));
        }
        return layout;
    }

    private View createPlayersTable(Context context, String team, ArrayList<Player> data) {
        View tableWithHeader = inflate(context, R.layout.result_view_info_table, null);
        TextView title = (TextView) tableWithHeader.findViewById(R.id.result_view_players_table_title);
        final TableLayout table = (TableLayout) tableWithHeader.findViewById(R.id.result_view_players_table);
        title.setText(team);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                table.setVisibility((table.getVisibility() == VISIBLE) ? GONE : VISIBLE);
            }
        });

        table.setVisibility(View.GONE);
        table.addView(new ResultViewBoxscoreRow(context));
        int l = data.size();
        for (int i = 0; i < l; i++) {
            table.addView(new ResultViewBoxscoreRow(context, data.get(i), (i & 1) == 0, i+1 == l));
        }
        table.setBackground(getResources().getDrawable(R.drawable.result_table_shape));

        int margin = getResources().getDimensionPixelSize(R.dimen.results_boxscore_margin);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, margin, 0, margin);
        tableWithHeader.setLayoutParams(lp);
        return tableWithHeader;
    }
}
