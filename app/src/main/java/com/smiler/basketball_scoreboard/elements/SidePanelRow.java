package com.smiler.basketball_scoreboard.elements;

import android.content.Context;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class SidePanelRow extends TableRow{
    private TextView numberView, nameView, pointsView, foulsView;
    private short points, fouls;

    public SidePanelRow(Context context) {
        super(context);
        createView(context);
    }

    public SidePanelRow(Context context, boolean header) {
        super(context);
        createHeaderRow(context);
    }

    public SidePanelRow(Context context, int number, String name) {
        super(context);
        createView(context);
        setNumber(number);
        setName(name);
    }

    private void createView(Context context) {
        inflate(context, R.layout.side_panel_row, this);
        numberView = ((TextView) findViewById(R.id.left_panel_number));
        nameView = ((TextView) this.findViewById(R.id.left_panel_name));
        pointsView = ((TextView) this.findViewById(R.id.left_panel_points));
        foulsView = ((TextView) this.findViewById(R.id.left_panel_fouls));
        TextView edit = ((TextView) this.findViewById(R.id.left_panel_edit));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                editRow();
                changePoints(2);
            }
        });

    }

    private void createHeaderRow(Context context) {
        inflate(context, R.layout.side_panel_header, this);
    }
    public void setNumber(int value) {
        numberView.setText(String.valueOf(value));
    }
    public void setName(String value) {
        nameView.setText(value);
    }
    public void setPoints(String value) {
        pointsView.setText(value);
    }
    public void changePoints(int value) {
        points += value;
        pointsView.setText(String.valueOf(points));
    }
    public void setFouls(String value) {
        foulsView.setText(value);
    }
    public void changeFouls(int value) {
        fouls += value;
        foulsView.setText(String.valueOf(fouls));
    }
    public void editRow() {
        nameView.setText("Changed name");
        numberView.setText("11");
        pointsView.setText("25");
        foulsView.setText("2");
    }
}
