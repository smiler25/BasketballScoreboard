package com.smiler.basketball_scoreboard.elements;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class DetailViewExpandable extends LinearLayout {

    private View view;

    public DetailViewExpandable(Context context) {
        super(context);
    }

    public DetailViewExpandable(Context context, int titleText) {
        super(context);
        inflate(context, R.layout.detail_view_expandable, this);
        initTitle(titleText);
    }

    private void initTitle(int textRes) {
        TextView title = findViewById(R.id.dve_title);
        title.setText(textRes);
        int padding = getResources().getDimensionPixelSize(R.dimen.results_boxscore_padding);
        title.setPadding(padding, padding, padding, padding);
        title.setOnClickListener(v -> {
            if (view != null) {
                view.setVisibility(view.getVisibility() == VISIBLE ? GONE : VISIBLE);
            }

        });
    }

    public void addView(View view, boolean hidden) {
        LinearLayout ll = findViewById(R.id.detail_view_expandable);
        ll.addView(view);
        this.view = view;
        if (hidden) {
            view.setVisibility(GONE);
        }
    }

    @NonNull
    public TableLayout getTable(Context context) {
        TableLayout table = new TableLayout(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        table.setStretchAllColumns(true);
        table.setBackground(getResources().getDrawable(R.drawable.dve_table_shape));
        table.setLayoutParams(params);
        return table;
    }

}
