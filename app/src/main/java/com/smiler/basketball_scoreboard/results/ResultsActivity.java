package com.smiler.basketball_scoreboard.results;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.CAB;
import com.smiler.basketball_scoreboard.CABListener;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.BaseResultsListFragment;
import com.smiler.basketball_scoreboard.elements.RecyclerListFragment;

public class ResultsActivity extends ActionBarActivity  implements ResultsExpListListener {

    private Menu menu;
    private int selected = -1;
    private boolean actionModeActive;
    private ActionMode actionMode;
    private TextView actionModeText;
    private ResultViewFragment detailViewFrag;
    private BaseResultsListFragment list;
    private boolean wide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results);
        initToolbar();
//        DbHelper helper = DbHelper.getInstance(this);
//        helper.getReadableDatabase();
//        System.out.println(helper.getShareString(0));
        final String cabString = getResources().getString(R.string.cab_subtitle);

        list = (RecyclerListFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag);
        if (list == null) {
            list = (ResultsExpListFragment) getSupportFragmentManager().findFragmentById(R.id.expandable_list_frag);
        }
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
        detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
        if (detailViewFrag != null) { wide = true; }

        if (list != null) {
            list.setMode(cabListener);
            list.setListener(new ListListener() {
                @Override
                public void onListElementClick(int value) {
                    if (!actionModeActive) {
                        menu.setGroupVisible(R.id.group, true);
                        selected = value;
                        if (wide) {
                            detailViewFrag.updateContent(value);
                        }
                    } else {
                        actionModeText.setText(String.format(cabString, value));
                    }
                }

                @Override
                public void onListElementLongClick(int count) {
                    actionMode = ResultsActivity.this.startSupportActionMode(new CAB(ResultsActivity.this, cabListener));
                    actionModeText = (TextView) (actionMode != null ? actionMode.getCustomView() : new TextView(ResultsActivity.this));
                    actionModeText.setText(String.format(cabString, 1));
                    actionModeActive = true;
                }

                @Override
                public void onListEmpty() {
                    System.out.println("ResultsActivity inner onListEmpty ");
                }
            });
        }
    }

    private void initToolbar() {
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
        getMenuInflater().inflate(R.menu.menu_results, menu);
        menu.setGroupVisible(R.id.group, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private void menuShare() {
        if (selected == -1) { return; }
        String mime_type = "text/plain";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, RealmController.with(this).getShareString(selected))
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void menuDelete() {
        System.out.println("menu delete = " + selected);
        menu.setGroupVisible(R.id.group, false);
        if (selected == -1) { return; }
        RealmController.with(this).deleteResult(selected);

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
        System.out.println("ResultsActivity onListEmpty ");
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