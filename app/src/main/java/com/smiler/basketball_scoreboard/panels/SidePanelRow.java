package com.smiler.basketball_scoreboard.panels;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.elements.EditPlayerDialog;
import com.smiler.basketball_scoreboard.models.Player;

public class SidePanelRow extends TableRow implements Comparable<SidePanelRow>{

    private static int maxFouls;
    private static int count = 0;
    private TextView numberView, nameView, pointsView, foulsView;
    private boolean left, selected = false;
    private Context context;
    private int id = 0;
    private int colorSelected = getResources().getColor(R.color.side_panel_selected);
    private int colorNotSelected = getResources().getColor(R.color.light_grey_background);
    private int colorFouledOut = getResources().getColor(R.color.side_panel_fouled_out);
    private Player player;

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
        player = new Player();
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

    public int getId() {
        return id;
    }

    public int getNumber() {
        return player.getNumber();
    }

    public void setNumber(int value) {
        player.setNumber(value);
        numberView.setText((player.isCaptain()) ? "" + value + "*" : "" + value);
    }

    public String getName() {
        return player.getName();
    }

    public void setName(String value) {
        player.setName(value);
        nameView.setText(value);
    }

    public int getPoints() {
        return player.getPoints();
    }

    public int getFouls() {
        return player.getFouls();
    }

    public boolean getCaptain() {
        return player.isCaptain();
    }

    public void changePoints(int value) {
        pointsView.setText(String.valueOf(player.changePoints(value)));
    }

    public void changeFouls(int value) {
        int fouls = player.changeFouls(value);
        foulsView.setText(String.valueOf(fouls));
        if (fouls >= maxFouls) {
            Toast.makeText(
                getContext(),
                String.format(getResources().getString((left) ? R.string.side_panel_fouls_limit_home : R.string.side_panel_fouls_limit_guest), player.getNumber(), player.getName()),
                Toast.LENGTH_SHORT).show();
            this.setBackgroundColor(colorFouledOut);
        }
    }

    private void edit() {
        EditPlayerDialog.newInstance(left, id, player.getNumber(), player.getName(), player.isCaptain())
                .show(((Activity) context).getFragmentManager(), EditPlayerDialog.TAG);
    }

    public void edit(int number, String name, boolean captain) {
        player.setInfo(
            number,
            (!name.trim().equals("")) ? name.trim() : String.format(getResources().getString(R.string.side_panel_player_name), number),
            captain
        );
        setNumber(number);
        setName(player.getName());
    }

    public void cancelCaptain() {
        player.setCaptain(false);
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

    public boolean getSelected() {
        return selected;
    }

    @Override
    public int compareTo(@NonNull SidePanelRow another) {
        return player.getNumber() - another.getNumber();
    }

    public static void setMaxFouls(int value) {
        maxFouls = value;
    }

    public void clear() {
        player.clear();
        pointsView.setText("0");
        foulsView.setText("0");
    }

    public void changeSide() {
        left = !left;
        recreateView();
    }

    private void recreateView() {
        this.removeAllViews();
        createView(context);
        foulsView.setText(String.valueOf(player.getFouls()));
        nameView.setText(player.getName());
        numberView.setText((player.isCaptain()) ? "" + player.getNumber() + "*" : "" + player.getNumber());
        pointsView.setText(String.valueOf(player.getPoints()));
        if (player.getFouls() >= maxFouls) {
            this.setBackgroundColor(colorFouledOut);
        }
    }

//    public JSONObject getFullInfo() throws JSONException {
//        JSONObject object = new JSONObject();
////        try {
//            object.put("name", name);
//            object.put("number", number);
//            object.put("points", points);
//            object.put("fouls", fouls);
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
//        return object;
//    }

//    public SidePanelRow restoreFromJson(JSONObject object) throws JSONException {
//        this.name = (String) object.get("name");
//        this.number = (int) object.get("number");
//        this.points = (short) object.get("points");
//        this.fouls = (short) object.get("fouls");
//        return this;
//    }

}
