package com.smiler.basketball_scoreboard.elements;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.ResultsRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.results.ResultsListListener;

import io.realm.RealmResults;

public class RecyclerViewFragment extends Fragment {

    private static final String TAG = "BS-RecyclerViewFragment";
    protected RecyclerView recyclerView;
    protected ResultsRecyclerAdapter adapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected RealmResults<Results> realmData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.results_recycler_view, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ResultsRecyclerAdapter(realmData);
        recyclerView.setAdapter(adapter);
        setRecyclerViewLayoutManager();
        return rootView;
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    private void initDataset() {
        realmData = RealmController.with(this).getResults();
    }

    public void setListener(ResultsListListener listener) {
        adapter.setListener(listener);
    }
    public boolean updateList() {
        adapter.notifyDataSetChanged();
        return adapter.getItemCount() == 0;
    }
}
