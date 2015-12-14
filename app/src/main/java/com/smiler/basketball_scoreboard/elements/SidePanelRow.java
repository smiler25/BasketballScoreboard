package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class SidePanelRow extends TableRow{
    private TextView numberView, nameView, pointsView, foulsView;
    private short points, fouls;
    private String name, number;
    private boolean captain = false, onCourt = false, selected = false;
    private Context context;
    private static int count = 0;
    private int id = 0;

    public SidePanelRow(Context context) {
        super(context);
        createView(context);
    }

    public SidePanelRow(Context context, boolean header) {
        super(context);
        createHeaderRow(context);
    }

    public SidePanelRow(Context context, String number, String name, boolean captain) {
        super(context);
        createView(context);
        edit(number, name, captain);
    }

    private void createView(Context context) {
        inflate(context, R.layout.side_panel_row, this);
        this.context = context;
        numberView = ((TextView) findViewById(R.id.left_panel_number));
        nameView = ((TextView) this.findViewById(R.id.left_panel_name));
        pointsView = ((TextView) this.findViewById(R.id.left_panel_points));
        foulsView = ((TextView) this.findViewById(R.id.left_panel_fouls));
        TextView edit = ((TextView) this.findViewById(R.id.left_panel_edit));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePoints(2);
                edit();
            }
        });
        id = count++;
    }

    private void createHeaderRow(Context context) {
        inflate(context, R.layout.side_panel_header, this);
    }

    public void setNumber(String value) {
        if (captain) {value += "*";}
        numberView.setText(value);
    }

    public String getNumber() {
        return number;
    }

    public int getId() {
        return id;
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

    public void edit() {
        EditPlayerDialog.newInstance(id, number, name, captain)
                .show(((Activity) context).getFragmentManager(), EditPlayerDialog.TAG);
    }

    public void edit(String number, String name, boolean captain) {
        this.captain = captain;
        this.name = name;
        this.number = number;
        setNumber(number);
        setName(name);
    }

    public void cancelCaptain() {
        this.captain = false;
    }

    public boolean select() {
        selected = !selected;
        if (selected) {
            this.setBackgroundColor(Color.DKGRAY);
        } else {
            this.setBackgroundColor(Color.WHITE);
        }
        return selected;
    }

    public void setActive() {
        this.onCourt = true;
    }

    public void setInactive() {
        this.onCourt = false;
    }
}
