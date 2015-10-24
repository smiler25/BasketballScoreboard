package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

public class ExpListMultiChoice extends BaseMultiChoice {

    private ResultsExpListAdapter adapter;

    public ExpListMultiChoice(AbsListView listView, Activity activity) {
        super(listView, activity);
        adapter = (ResultsExpListAdapter)((ExpandableListView)listView).getExpandableListAdapter();
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long adapterId, boolean checked) {
        if (checked) {
            super.addSelectedId(adapter.getGroupId(position));
        } else {
            super.removeSelectedId(adapter.getGroupId(position));
        }
        super.onItemCheckedStateChanged(mode, position, adapterId, checked);
        adapter.toggleSelection(position, checked);
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        super.onDestroyActionMode(mode);
    }
}