package com.smiler.basketball_scoreboard.profiles;

import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.elements.BaseRecyclerFragment;

import io.realm.RealmResults;

public class TeamsRecyclerFragment extends BaseRecyclerFragment {
    public static final String TAG = "BS-TeamsRecyclerFragment";
    private RealmResults<Team> data;

    @Override
    protected void initAdapter() {
        adapter = new TeamsRealmRecyclerAdapter(data);
    }

    @Override
    protected void initData() {
        data = RealmController.with().getTeams();
    }

    @Override
    public void deleteSelection() {
        adapter.deleteSelection();
    }
}
