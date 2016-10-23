package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.widget.AbsListView;

import com.smiler.basketball_scoreboard.results.ResultsCursorAdapter;

public class ListMultiChoice extends BaseMultiChoice {

    private ResultsCursorAdapter adapter;

    public ListMultiChoice(AbsListView listView, Activity activity) {
        super(listView, activity);
        adapter = (ResultsCursorAdapter)listView.getAdapter();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long adapterId, boolean checked) {
        if (checked) {
            super.addSelectedId((int) adapterId);
        } else {
            super.removeSelectedId((int) adapterId);
        }
        adapter.toggleSelection(position, checked);
        super.onItemCheckedStateChanged(mode, position, adapterId, checked);
    }
}