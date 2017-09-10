package com.smiler.basketball_scoreboard.elements;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


public class ReattachedFragment extends Fragment {
    public void reAttach() {
        FragmentManager f = getFragmentManager();
        if (f != null) {
            f.beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }
}
