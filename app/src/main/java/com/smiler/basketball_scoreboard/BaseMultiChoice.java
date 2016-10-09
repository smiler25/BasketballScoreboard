package com.smiler.basketball_scoreboard;

import android.app.Activity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.Results;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class BaseMultiChoice implements AbsListView.MultiChoiceModeListener {

    private AbsListView listView;
    private Activity activity;
    private List<String> selectedIds = new ArrayList<>();
    public boolean actionModeEnabled;
    private TextView title;
    private ActionMode mode;
    private RealmConfiguration realmConfig;
    private CabDeletedListener listener;

    BaseMultiChoice(AbsListView listView, Activity activity) {
        this.activity = activity;
        this.listView = listView;
        Realm.init(activity.getApplicationContext());
        realmConfig = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();
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
                Realm realm = Realm.getInstance(realmConfig);
                String[] array = selectedIds.toArray(new String[selectedIds.size()]);

                final RealmResults<Results> results = realm.where(Results.class)
                        .in("id", array)
                        .findAll();

                final RealmResults<PlayersResults> playersResults = realm.where(PlayersResults.class)
                        .in("game.id", array)
                        .findAll();
//
//                final RealmResults<GameDetails> details = realm.where(GameDetails.class)
//                        .in("game.id", array)
//                        .findAll();

                for (Results one : results) {
                    System.out.println("one = " + one);
                }
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
//                        results.deleteAllFromRealm();
//                        playersResults.deleteAllFromRealm();
////                        details.deleteAllFromRealm();
                    }
                });
//                DbHelper dbHelper = DbHelper.getInstance(activity);
//                dbHelper.delete(selectedIds.toArray(new String[selectedIds.size()]));
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