package com.smiler.basketball_scoreboard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Team;

import io.realm.RealmResults;

public class TeamsListAdapter extends ArrayAdapter<Team> {

    public TeamsListAdapter(Context context, RealmResults<Team> teams) {
        super(context, 0, teams);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Team team = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list_item, parent, false);
        }

        TextView tvName = (TextView) convertView.findViewById(R.id.list_text);
        if (team != null && tvName != null) {
            tvName.setText(team.getName());
            tvName.setId(team.getId());
        }
        return convertView;
    }
}