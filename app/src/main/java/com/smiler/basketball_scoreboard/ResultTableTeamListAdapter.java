package com.smiler.basketball_scoreboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ResultTableTeamListAdapter extends ArrayAdapter<String> {

    private String[] objects;

    public ResultTableTeamListAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) convertView;
        if (v == null) {
            v = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.results_table_item, parent, false);
        }
        v.setText(objects[position]);
        if (position == 0) {
            v.setBackground(getContext().getResources().getDrawable(R.drawable.result_table_header_left_shape));
        }
        return v;
    }
}