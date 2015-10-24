package com.smiler.basketball_scoreboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ResultsActivity extends ActionBarActivity  implements ResultsListFragment.ResultsListListener,
        ResultsExpListFragment.ExpListListener {

    private Menu menu;
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        initToolbar();
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
        DbHelper dbHelper = DbHelper.getInstance(this);
        String mime_type = "text/plain";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, dbHelper.getShareString(selected))
                .setType(mime_type);
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.action_share_via)));
    }

    private void menuDelete() {
        menu.setGroupVisible(R.id.group, false);
        if (selected == -1) { return; }
        DbHelper dbHelper = DbHelper.getInstance(this);
        dbHelper.delete(new String[]{Integer.toString(selected)});

        ResultsListFragment list = (ResultsListFragment) getFragmentManager().findFragmentById(R.id.list_frag);
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
    public void onListItemSelected(int sqlId) {
        menu.setGroupVisible(R.id.group, true);
        selected = sqlId;
        ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
        detailViewFrag.updateContent(sqlId);
    }

    @Override
    public void onListItemDeleted(boolean empty) {
        if (!empty){
            ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
            detailViewFrag.clear();
        } else {
            setEmptyLayout();
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