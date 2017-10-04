package com.smiler.basketball_scoreboard.results;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.CAB;
import com.smiler.basketball_scoreboard.elements.CABListener;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;
import com.smiler.basketball_scoreboard.results.views.ResultViewFragment;

public class ResultsActivity extends AppCompatActivity {

    private Menu menu;
    private int selected = -1;
    private boolean actionModeActive;
    private ActionMode actionMode;
    private TextView actionModeText;
    private ResultViewFragment detailViewFrag;
    private boolean wide;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initFragments();
        initToolbar();
    }

    private void initFragments() {
        ResultsRecyclerFragment fragment = new ResultsRecyclerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.recycler_content_fragment, fragment, ResultsRecyclerFragment.TAG);

        View view = findViewById(R.id.details_frag);
        if (view != null) {
            wide = true;
            detailViewFrag = new ResultViewFragment();
            transaction.replace(R.id.details_frag, detailViewFrag, ResultViewFragment.TAG);
        } else {
            wide = false;
            detailViewFrag = null;
        }
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        setListeners();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
            toolbar.setTitle(R.string.title_activity_results);
        } else {
            finish();
        }
    }

    private void setListeners() {
        final ResultsRecyclerFragment list = (ResultsRecyclerFragment) getSupportFragmentManager().findFragmentByTag(ResultsRecyclerFragment.TAG);
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
                        openResultInfo();
                    } else {
                        actionModeText.setText(String.format(cabString, value));
                    }
                }

                @Override
                public void onListElementLongClick(int count) {
                    actionMode = startSupportActionMode(new CAB(ResultsActivity.this, cabListener));
                    actionModeText = (TextView) (actionMode != null ? actionMode.getCustomView() : new TextView(ResultsActivity.this));
                    actionModeText.setText(String.format(cabString, 1));
                    actionModeActive = true;
                }

                @Override
                public void onListEmpty() {
                }
            });
        }
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowHomeEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

        ResultsRecyclerFragment list = (ResultsRecyclerFragment) getSupportFragmentManager().findFragmentByTag(ResultsRecyclerFragment.TAG);
        if (list != null) {
            if (!list.updateList()) {
                ResultViewFragment detailViewFrag = (ResultViewFragment) getSupportFragmentManager().findFragmentById(R.id.details_frag);
                if (detailViewFrag != null) {
                    detailViewFrag.clear();
                } else {
                    closeInfo();
                }
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

    private void openResultInfo() {
        if (wide && detailViewFrag != null) {
            detailViewFrag.updateContent(selected);
        } else {
            Fragment selectedFrag = ResultViewFragment.newInstance(selected);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.recycler_content_fragment, selectedFrag, ResultViewFragment.TAG)
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }

    public void closeInfo() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0 ){
            getSupportFragmentManager().popBackStack();
            toolbar.setTitle(R.string.title_activity_results);
        }
    }
}