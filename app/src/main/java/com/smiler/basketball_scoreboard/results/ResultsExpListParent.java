package com.smiler.basketball_scoreboard.results;

class ResultsExpListParent {

    private String parent;
    private ResultView child;
    private int itemId;

    ResultsExpListParent(String parent, int itemId) {
        this.parent = parent;
        this.itemId = itemId;
    }

    String getParent() {
        return parent;
    }

    ResultView getChild() {
        return child;
    }

    void setChild(ResultView child) {
        this.child = child;
    }

    int getItemId() {
        return itemId;
    }
}
