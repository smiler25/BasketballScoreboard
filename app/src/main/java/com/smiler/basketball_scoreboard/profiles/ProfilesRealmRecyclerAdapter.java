package com.smiler.basketball_scoreboard.profiles;

import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.Team;

import io.realm.RealmResults;

public class ProfilesRealmRecyclerAdapter extends RealmRecyclerAdapter {
    private final RealmResults<Team> data;

    public ProfilesRealmRecyclerAdapter(RealmResults<Team> data) {
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
    public int getItemCount() {
        return data.size();
    }

}
