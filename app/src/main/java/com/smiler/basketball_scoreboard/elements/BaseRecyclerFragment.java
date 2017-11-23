package com.smiler.basketball_scoreboard.elements;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;

abstract public class BaseRecyclerFragment extends Fragment {
    public static final String TAG = "BS-BaseRecyclerFragment";
    protected RecyclerView recyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    protected RealmRecyclerAdapter adapter;
    protected ListListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view_frag, container, false);
        rootView.setTag(TAG);
        recyclerView = rootView.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        setRecyclerViewLayoutManager();
        initAdapter();
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public void setRecyclerViewLayoutManager() {
        int scrollPosition = 0;
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    abstract protected void initAdapter();

    abstract protected void initData();

    public boolean updateList() {
        adapter.notifyDataSetChanged();
        return adapter.getItemCount() == 0;
    }

    public void setListener(ListListener listener) {
        this.listener = listener;
        adapter.setListener(listener);
    }

    public void setMode(CABListener listener) {}

    public void clearSelection() {
        adapter.clearSelection();
    }

    public void deleteSelection() {
    }

}
