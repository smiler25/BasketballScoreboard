package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;

import org.json.JSONException;
import org.json.JSONObject;

public class SidePanelRow extends TableRow implements Comparable<SidePanelRow>{

    private static int maxFouls;
    private TextView numberView, nameView, pointsView, foulsView;
    private int number;
    private short points, fouls;
    private String name;
    private boolean left, captain = false, selected = false;
    private Context context;
    private static int count = 0;
    private int id = 0;
    private int colorSelected = getResources().getColor(R.color.side_panel_selected);
    private int colorNotSelected = getResources().getColor(R.color.light_grey_background);
    private int colorFouledOut = getResources().getColor(R.color.side_panel_fouled_out);

    public SidePanelRow(Context context) {
        super(context);
    }

    public SidePanelRow(Context context, boolean left) {
        super(context);
        this.left = left;
        createHeaderRow(context);
    }

    public SidePanelRow(Context context, int number, String name, boolean captain, boolean left) {
        super(context);
        this.left = left;
        createView(context);
        edit(number, name, captain);
    }

    private void createView(Context context) {
        this.context = context;
        View edit;
        if (left) {
            inflate(context, R.layout.side_panel_row_left, this);
            numberView = (TextView) findViewById(R.id.left_panel_number);
            nameView = (TextView) this.findViewById(R.id.left_panel_name);
            pointsView = (TextView) this.findViewById(R.id.left_panel_points);
            foulsView = (TextView) this.findViewById(R.id.left_panel_fouls);
            edit = this.findViewById(R.id.left_panel_edit);

        } else {
            inflate(context, R.layout.side_panel_row_right, this);
            numberView = (TextView) findViewById(R.id.right_panel_number);
            nameView = (TextView) this.findViewById(R.id.right_panel_name);
            pointsView = (TextView) this.findViewById(R.id.right_panel_points);
            foulsView = (TextView) this.findViewById(R.id.right_panel_fouls);
            edit = this.findViewById(R.id.right_panel_edit);
        }
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });
        id = count++;
    }

    private void createHeaderRow(Context context) {
        inflate(context, (left) ? R.layout.side_panel_header_left : R.layout.side_panel_header_right, this);
    }

    public void setNumber(int value) {
        this.number = value;
        numberView.setText((captain) ? "" + value + "*" : "" + value);
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getFouls() {
        return fouls;
    }

    public boolean getCaptain() {
        return captain;
    }

    public void setName(String value) {
        this.name = value;
        nameView.setText(value);
    }

    public void changePoints(int value) {
        points += value;
        pointsView.setText(String.valueOf(points));
    }

    public void changeFouls(int value) {
        fouls += value;
        foulsView.setText(String.valueOf(fouls));
        if (fouls >= maxFouls) {
            Toast.makeText(
                    getContext(),
                    String.format(getResources().getString((left) ? R.string.side_panel_fouls_limit_home : R.string.side_panel_fouls_limit_guest), number, name),
                    Toast.LENGTH_SHORT).show();
            this.setBackgroundColor(colorFouledOut);

        }
    }

    public void edit() {
        EditPlayerDialog.newInstance(left, id, number, name, captain)
                .show(((Activity) context).getFragmentManager(), EditPlayerDialog.TAG);
    }

    public void edit(int number, String name, boolean captain) {
        this.captain = captain;
        this.name = (!name.trim().equals("")) ? name.trim() : String.format(getResources().getString(R.string.side_panel_player_name), number);
        this.number = number;
        setNumber(number);
        setName(this.name);
    }

    public void cancelCaptain() {
        this.captain = false;
    }

    public boolean toggleSelected() {
        selected = !selected;
        if (selected) {
            this.setBackgroundColor(colorSelected);
        } else {
            this.setBackgroundColor(colorNotSelected);
        }
        return selected;
    }

    public JSONObject getFullInfo() throws JSONException {
        JSONObject object = new JSONObject();
//        try {
            object.put("name", name);
            object.put("number", number);
            object.put("points", points);
            object.put("fouls", fouls);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return object;
    }

//    public static
    public SidePanelRow restoreFromJson(JSONObject object) throws JSONException {
        this.name = (String) object.get("name");
        this.number = (int) object.get("number");
        this.points = (short) object.get("points");
        this.fouls = (short) object.get("fouls");
        return this;
    }

    @Override
    public int compareTo(@NonNull SidePanelRow another) {
        return this.number - another.getNumber();
    }

    public static void setMaxFouls(int value) {
        maxFouls = value;
    }

    public void clear() {
        points = 0;
        pointsView.setText("0");
        fouls = 0;
        foulsView.setText("0");
    }
}
