package com.smiler.basketball_scoreboard.elements.lists;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.elements.CABListener;

abstract public class BaseListFragment extends Fragment {

    protected RealmRecyclerAdapter adapter;
    private ListListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    abstract public void initData();

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
