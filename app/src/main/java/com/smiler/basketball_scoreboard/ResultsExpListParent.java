package com.smiler.basketball_scoreboard;

import com.smiler.basketball_scoreboard.elements.ResultView;

public class ResultsExpListParent {

    private String parent;
    private ResultView child;
    private int sqlId;

    public ResultsExpListParent(String parent, int sqlId) {
        this.parent = parent;
        this.sqlId = sqlId;
    }

    public String getParent() {
        return parent;
    }

    public ResultView getChild() {
        return child;
    }

    public void setChild(ResultView child) {
        this.child = child;
    }

    public int getSqlId() {
        return sqlId;
    }
}
