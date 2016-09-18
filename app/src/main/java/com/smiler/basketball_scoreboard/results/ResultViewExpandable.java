package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

class ResultViewExpandable extends LinearLayout {

    private View view;

    public ResultViewExpandable(Context context) {
        super(context);
    }

    ResultViewExpandable(Context context, int titleText) {
        super(context);
        inflate(context, R.layout.result_view_expandable, this);
        initTitle(titleText);
    }

    private void initTitle(int textRes) {
        TextView title = (TextView) findViewById(R.id.result_view_expandable_title);
        title.setText(textRes);
        int padding = getResources().getDimensionPixelSize(R.dimen.results_boxscore_padding);
        title.setPadding(padding, padding, padding, padding);
        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view != null) {
                    view.setVisibility((view.getVisibility() == VISIBLE) ? GONE : VISIBLE);
                }

            }
        });
    }

    public void addView(View view, boolean hidden) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.result_view_expandable);
        ll.addView(view);
        this.view = view;
        if (hidden) {
            view.setVisibility(GONE);
        }

    }
}
