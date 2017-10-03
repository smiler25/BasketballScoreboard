package com.smiler.basketball_scoreboard.profiles;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.Team;

import io.realm.RealmResults;

class TeamsRealmRecyclerAdapter extends RealmRecyclerAdapter {
    protected final RealmResults<Team> data;
    private String TAG = "BS-RealmRecyclerAdapter";

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
        try {
            return data.size();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error getting size data size");
            return 0;
        }
    }
}
