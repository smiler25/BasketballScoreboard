package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.db.RealmController;

import java.util.ArrayList;

public class BaseMultiChoice implements AbsListView.MultiChoiceModeListener {

    private AbsListView listView;
    private Activity activity;
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    public boolean actionModeEnabled;
    private TextView title;
    private ActionMode mode;
    private RealmController realmController;
    private CABListener listener;

    BaseMultiChoice(AbsListView listView, Activity activity, CABListener listener) {
        this.activity = activity;
        this.listView = listView;
        this.listener = listener;
        realmController = RealmController.with(activity);
    }

//    public void close() {
//        mode.finish();
//    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        this.mode = mode;
        MenuInflater inflater = mode.getMenuInflater();
        title = (TextView) activity.getLayoutInflater().inflate(R.layout.cab_title_text, null);
        inflater.inflate(R.menu.menu_results_cab, menu);
        mode.setCustomView(title);
        actionModeEnabled = true;
        return true;
    }

    void addSelectedId(int id){
        selectedIds.add(id);
    }

    void removeSelectedId(int id){
        selectedIds.remove((Integer) id);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long adapterId, boolean checked) {
        title.setText(String.format(activity.getResources().getString(R.string.cab_subtitle), listView.getCheckedItemCount()));
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.cab_action_delete:
                realmController.deleteResults(selectedIds.toArray(new Integer[selectedIds.size()]));
                if (listener != null) {
                    listener.onMenuDelete();
                }
                Toast.makeText(activity, activity.getResources().getString(R.string.cab_success), Toast.LENGTH_LONG).show();
                mode.finish();
                return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionModeEnabled = false;
        mode.finish();
    }
}