package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.widget.AbsListView;

public class ListMultiChoice extends BaseMultiChoice {

    private ResultsCursorAdapter adapter;

    public ListMultiChoice(AbsListView listView, Activity activity) {
        super(listView, activity);
        adapter = (ResultsCursorAdapter)listView.getAdapter();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long adapterId, boolean checked) {
        if (checked) {
            super.addSelectedId(adapterId);
        } else {
            super.removeSelectedId(adapterId);
        }
        adapter.toggleSelection(position, checked);
        super.onItemCheckedStateChanged(mode, position, adapterId, checked);
    }
}