package com.smiler.basketball_scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.CAB;
import com.smiler.basketball_scoreboard.elements.CABListener;
import com.smiler.basketball_scoreboard.elements.lists.BaseListFragment;
import com.smiler.basketball_scoreboard.elements.lists.ExpandableListListener;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;
import com.smiler.basketball_scoreboard.elements.lists.RecyclerListFragment;
import com.smiler.basketball_scoreboard.results.views.ResultViewFragment;

abstract public class BaseListActivity extends AppCompatActivity implements ExpandableListListener {

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

    protected void setListeners(final BaseListFragment list) {
        final String cabString = getResources().getString(R.string.cab_subtitle);

        final CABListener cabListener = new CABListener() {
            @Override
            public void onFinish() { list.clearSelection(); }

            @Override
            public void onMenuClick() {}

            @Override
            public void onMenuDelete() {
                list.deleteSelection();
            }
        };
        if (list != null) {
            list.setMode(cabListener);
            list.setListener(new ListListener() {
                @Override
                public void onListElementClick(int value) {
                    if (!actionModeActive) {
                        menu.setGroupVisible(R.id.group, true);
                        selected = value;
                        if (wide && detailViewFrag != null) {
                            detailViewFrag.updateContent(value);
                        }
                    } else {
                        actionModeText.setText(String.format(cabString, value));
                    }
                }

                @Override
                public void onListElementLongClick(int count) {
                    actionMode = startSupportActionMode(new CAB(BaseListActivity.this, cabListener));
                    actionModeText = (TextView) (actionMode != null ? actionMode.getCustomView() : new TextView(BaseListActivity.this));
                    actionModeText.setText(String.format(cabString, 1));
                    actionModeActive = true;
                }

                @Override
                public void onListEmpty() {
                }
            });
        }
    }

    protected void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    public void onListEmpty() {
        setEmptyLayout();
    }

    @Override
    public void onExpListItemSelected() {
        //menu.setGroupVisible(R.id.group, true);
    }

    @Override
    public void onExpListItemDeleted(boolean empty) {
        if (empty){
            setEmptyLayout();
        }
    }

    private void setEmptyLayout() {
        setContentView(R.layout.activity_results_empty);
        initToolbar();
    }
}