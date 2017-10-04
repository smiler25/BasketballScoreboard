package com.smiler.basketball_scoreboard.results;

import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.BaseRecyclerFragment;

import io.realm.RealmResults;

public class ResultsRecyclerFragment extends BaseRecyclerFragment {
    public static final String TAG = "BS-ResultsRecyclerFragment";
    private RealmResults<Results> data;

    @Override
    protected void initAdapter() {
        adapter = new ResultsRealmRecyclerAdapter(data);
    }

    @Override
    protected void initData() {
        data = RealmController.with().getResults();
    }

    @Override
    public void deleteSelection() {
        adapter.deleteSelection();
    }
}
