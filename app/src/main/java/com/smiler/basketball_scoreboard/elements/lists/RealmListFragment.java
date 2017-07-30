package com.smiler.basketball_scoreboard.elements.lists;


import com.smiler.basketball_scoreboard.elements.CABListener;

public interface RealmListFragment {
    void initData();

    void setListener(ListListener listener);

    boolean updateList();

    void clearSelection();

    void deleteSelection();

    void setMode(CABListener listener);
}
