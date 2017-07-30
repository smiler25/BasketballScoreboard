package com.smiler.basketball_scoreboard.profiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.BaseListActivity;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.lists.BaseListFragment;
import com.smiler.basketball_scoreboard.elements.lists.RecyclerListFragment;
import com.smiler.basketball_scoreboard.results.views.ResultViewFragment;

//public class ProfilesActivity extends AppCompatActivity implements ExpandableListListener {
public class ProfilesActivity extends BaseListActivity {

    private Menu menu;
    private int selected = -1;
    private boolean actionModeActive;
    private ActionMode actionMode;
    private TextView actionModeText;
    private ResultViewFragment detailViewFrag;
    private boolean wide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        initToolbar();
        initList();
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    private void initList() {
        detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
        BaseListFragment list;
        if (detailViewFrag != null && detailViewFrag.isAdded()) {
            wide = true;
            list = (BaseListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag);
            if (list != null) {
                setListeners(list);
            }
        } else {
            wide = false;
            list = (BaseListFragment) getSupportFragmentManager().findFragmentById(R.id.expandable_list_frag);
            if (list != null) {
                setListeners(list);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_profiles, menu);
        menu.setGroupVisible(R.id.group, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                menuAdd();
                return true;
            case R.id.menu_share:
                menuShare();
                return true;
            case R.id.menu_delete:
                menuDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void menuAdd() {
        if (selected == -1) { return; }
        String mime_type = "text/plain";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, RealmController.with().getShareString(selected))
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void menuShare() {
        if (selected == -1) { return; }
        String mime_type = "text/plain";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, RealmController.with().getShareString(selected))
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void menuDelete() {
        menu.setGroupVisible(R.id.group, false);
        if (selected == -1) { return; }
        if (!RealmController.with().deleteResult(selected)) {
            Toast.makeText(this, String.format(getResources().getString(R.string.toast_result_delete_error), selected), Toast.LENGTH_SHORT).show();
        }

        RecyclerListFragment list = (RecyclerListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag);
        if (list != null) {
            if (!list.updateList()) {
                ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
                detailViewFrag.clear();
            } else {
                setEmptyLayout();
            }
            selected = -1;
        }
    }

    private void setEmptyLayout() {
        setContentView(R.layout.activity_results_empty);
        initToolbar();
    }
}