package com.smiler.basketball_scoreboard.profiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.Team;

import io.realm.RealmResults;

class TeamsRealmRecyclerAdapter extends RealmRecyclerAdapter {
    protected final RealmResults<Team> data;

    TeamsRealmRecyclerAdapter(RealmResults<Team> data) {
        super();
        this.data = data;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Team team = data.get(position);
        viewHolder.setTextView(team.getName());
        viewHolder.setId(team.getId());
        viewHolder.setSelected(selectedIds.indexOf(team.getId()) != -1);
        viewHolder.setCallback(callback);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
