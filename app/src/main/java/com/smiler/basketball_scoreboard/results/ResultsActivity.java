package com.smiler.basketball_scoreboard.results;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.RecyclerViewFragment;

public class ResultsActivity extends ActionBarActivity  implements ResultsListListener,
        ResultsExpListListener {

    private Menu menu;
    private int selected = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_results);
        initToolbar();
//        DbHelper helper = DbHelper.getInstance(this);
//        helper.getReadableDatabase();
        RecyclerViewFragment list = (RecyclerViewFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag);
        if (list != null) {
            list.setListener(new ResultsListListener() {
                @Override
                public void onListItemSelected(int itemId) {
                    menu.setGroupVisible(R.id.group, true);
                    selected = itemId;
                    System.out.println("ResultsActivity inner itemId onListItemSelected = " + itemId);
                    ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
                    detailViewFrag.updateContent(itemId);
                }

                @Override
                public void onListItemDeleted(boolean empty) {
                    System.out.println("ResultsActivity inner itemId onListItemDeleted");

                }

                @Override
                public void onListEmpty() {
                    System.out.println("ResultsActivity inner onListEmpty ");
                }
            });
        }
        System.out.println("ResultsActivity onCreate2");
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

        RecyclerViewFragment list = (RecyclerViewFragment) getSupportFragmentManager().findFragmentById(R.id.list_frag);
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
    public void onListItemSelected(int itemId) {
        menu.setGroupVisible(R.id.group, true);
        selected = itemId;
        System.out.println("ResultsActivity itemId = " + itemId);
        ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
        detailViewFrag.updateContent(itemId);
    }

    @Override
    public void onListItemDeleted(boolean empty) {
        System.out.println("ResultsActivity onDelete ");
        if (!empty){
            ResultViewFragment detailViewFrag = (ResultViewFragment) getFragmentManager().findFragmentById(R.id.details_frag);
            detailViewFrag.clear();
        } else {
            setEmptyLayout();
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