package com.smiler.basketball_scoreboard.elements.lists;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.ResultsRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.results.BaseResultsListFragment;

public class RecyclerListFragment extends BaseResultsListFragment {
    public static final String TAG = "BS-RecyclerListFragment";
    protected RecyclerView recyclerView;
    protected ResultsRecyclerAdapter adapter;
    protected RecyclerView.LayoutManager layoutManager;

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

    @Override
    public void setListener(ListListener listener) {
        if (adapter != null) {
            adapter.setListener(listener);
        }
    }

    @Override
    public boolean updateList() {
        adapter.notifyDataSetChanged();
        return adapter.getItemCount() == 0;
    }

    @Override
    public void clearSelection() {
        adapter.clearSelection();
    }

    @Override
    public void deleteSelection() {
        RealmController.with().deleteResults(adapter.selectedIds.toArray(new Integer[adapter.selectedIds.size()]));
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.cab_success), Toast.LENGTH_LONG).show();
        adapter.deleteSelection();
    }
}
