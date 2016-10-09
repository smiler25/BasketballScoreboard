package com.smiler.basketball_scoreboard.elements;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.ResultsRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.Results;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RecyclerViewFragment extends Fragment {

    private static final String TAG = "BS-RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
//    private static final int DATASET_COUNT = 60;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType currentLayoutManagerType;

    protected RecyclerView recyclerView;
    protected ResultsRecyclerAdapter adapter;
    protected RecyclerView.LayoutManager layoutManager;
    protected RealmResults<Results> realmData;
    protected RealmConfiguration realmConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(getContext());
        realmConfig = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();

        initDataset();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.results_recycler_view, container, false);
        rootView.setTag(TAG);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.results_recycler_view);
        layoutManager = new LinearLayoutManager(getActivity());
        currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
           currentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(currentLayoutManagerType);

        adapter = new ResultsRecyclerAdapter(realmData);
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                currentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                layoutManager = new LinearLayoutManager(getActivity());
                currentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, currentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataset() {
        Realm realm = Realm.getInstance(realmConfig);
        realmData = realm.where(Results.class).findAll();
    }
}
