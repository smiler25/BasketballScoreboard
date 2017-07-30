package com.smiler.basketball_scoreboard.results;


import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.lists.BaseListFragment;

import io.realm.RealmResults;

public class ResultsListFragment extends BaseListFragment {

    private static final String TAG = "BS-ResultsListFragment";
    protected RealmResults<Results> realmData;

    @Override
    public void initData() {
        realmData = RealmController.with().getResults();
    }
}
