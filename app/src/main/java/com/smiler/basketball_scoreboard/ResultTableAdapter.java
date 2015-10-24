package com.smiler.basketball_scoreboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultTableAdapter extends ArrayAdapter<String> {

    ArrayList<String> objects;
    Context context;

    public ResultTableAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) convertView;
        if (v == null) {
            v = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.results_table_item, parent, false);
        }

        int headerColor = context.getResources().getColor(R.color.result_table_header_color);
        int size = objects.size();
        v.setText(objects.get(position));
        if (position < size/3) {
            if (position == size/3 -1) {
                v.setBackground(getContext().getResources().getDrawable(R.drawable.result_table_header_right_shape));
            } else {
                v.setBackgroundColor(headerColor);
            }
        }
        return v;
    }
}
