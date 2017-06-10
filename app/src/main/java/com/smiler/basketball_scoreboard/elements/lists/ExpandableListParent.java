package com.smiler.basketball_scoreboard.elements.lists;

import android.view.View;

import com.smiler.basketball_scoreboard.results.views.ResultView;

public class ExpandableListParent {

    private String parent;
    private View child;
    private int itemId;

    public ExpandableListParent(String parent, int itemId) {
        this.parent = parent;
        this.itemId = itemId;
    }

    public String getParent() {
        return parent;
    }

    public View getChild() {
        return child;
    }

    public void setChild(ResultView child) {
        this.child = child;
    }

    public int getItemId() {
        return itemId;
    }
}
