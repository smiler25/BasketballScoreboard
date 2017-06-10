package com.smiler.basketball_scoreboard.elements;

import android.app.Activity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

public class CAB implements ActionMode.Callback {
    private final CABListener callback;
    private Activity activity;
    private boolean closeAction;

    public CAB(Activity activity, CABListener callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cab_action_delete:
                callback.onMenuDelete();
                closeAction = true;
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_results_cab, menu);
        TextView title = (TextView) activity.getLayoutInflater().inflate(R.layout.cab_title_text, null);
        mode.setCustomView(title);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        if (!closeAction) {
            callback.onFinish();
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }
}