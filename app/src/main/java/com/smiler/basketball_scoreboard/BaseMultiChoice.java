package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class BaseMultiChoice implements AbsListView.MultiChoiceModeListener {

    private AbsListView listView;
    private Activity activity;
    private List<String> selectedIds = new ArrayList<>();
    public boolean actionModeEnabled;
    private TextView title;
    private ActionMode mode;

    BaseMultiChoice(AbsListView listView, Activity activity) {
        this.activity = activity;
        this.listView = listView;
    }

    public void close() {
        mode.finish();
    }

    public interface CabDeletedListener {
        void onCabDelete(List<String> selectedIds);
    }

    public void setCabDeleteListener(CabDeletedListener listener) {
        this.listener = listener;
    }

    private CabDeletedListener listener;

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

    void addSelectedId(long id){
        selectedIds.add(Long.toString(id));
    }

    void removeSelectedId(long id){
        selectedIds.remove(Long.toString(id));

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
                DbHelper dbHelper = DbHelper.getInstance(activity);
                dbHelper.delete(selectedIds.toArray(new String[selectedIds.size()]));
                listener.onCabDelete(selectedIds);
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