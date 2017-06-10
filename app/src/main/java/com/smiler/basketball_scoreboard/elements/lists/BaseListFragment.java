package com.smiler.basketball_scoreboard.elements.lists;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.smiler.basketball_scoreboard.elements.CABListener;

abstract public class BaseListFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataset();
    }

    abstract public void initDataset();

    abstract public void setListener(ListListener listener);

    abstract public boolean updateList();

    abstract public void clearSelection();

    abstract public void deleteSelection();

    abstract public void setMode(CABListener listener);
}
