package com.smiler.basketball_scoreboard.results;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.smiler.basketball_scoreboard.elements.CABListener;
import com.smiler.basketball_scoreboard.adapters.ResultsRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;

import io.realm.RealmResults;

public class BaseResultsListFragment extends Fragment {

    private static final String TAG = "BS-BaseResultsListFragment";
    protected RealmResults<Results> realmData;
    protected ResultsRecyclerAdapter adapter;
    private ListListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    public void initDataset() {
        realmData = RealmController.with().getResults();
    }

    public void setListener(ListListener listener) {
        this.listener = listener;
        adapter.setListener(listener);
    }

    public boolean updateList() {
        adapter.notifyDataSetChanged();
        return adapter.getItemCount() == 0;
    }

    public void clearSelection() {
        adapter.clearSelection();
    }

    public void deleteSelection() {
    }

    public void setMode(CABListener listener) {}
}
