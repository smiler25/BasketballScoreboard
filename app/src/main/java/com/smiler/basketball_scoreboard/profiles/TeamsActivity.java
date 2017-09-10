package com.smiler.basketball_scoreboard.profiles;

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
import com.smiler.basketball_scoreboard.elements.dialogs.TeamEditDialog;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;
import com.smiler.basketball_scoreboard.elements.lists.RecyclerListFragment;
import com.smiler.basketball_scoreboard.results.views.ResultViewFragment;

public class TeamsActivity extends AppCompatActivity implements
        TeamEditDialog.EditTeamCallback
{
    private Menu menu;
    private int selected = -1;
    private boolean actionModeActive;
    private boolean wide;
    private ActionMode actionMode;
    private TextView actionModeText;
    private TeamViewFragment detailViewFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initFragments();
        initToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        setListeners();
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

    private void initFragments() {
        TeamsRecyclerFragment fragment = new TeamsRecyclerFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.recycler_content_fragment, fragment, TeamsRecyclerFragment.TAG);

        View view = findViewById(R.id.details_frag);
        if (view != null) {
            wide = true;
            detailViewFrag = new TeamViewFragment();
            transaction.replace(R.id.details_frag, detailViewFrag, TeamViewFragment.TAG);
        } else {
            wide = false;
            detailViewFrag = null;
        }
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_profiles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                menuAdd();
                return true;
            case R.id.menu_delete:
                menuDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void menuAdd() {
        showCreateTeamDialog();
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
//        setContentView(R.layout.activity_results_empty);
//        initToolbar();
    }

    private void showCreateTeamDialog() {
        TeamEditDialog.newInstance().show(getFragmentManager(), TeamEditDialog.TAG);
    }

    @Override
    public void onCreateTeam(String name, boolean active) {
        RealmController.with().createTeam(name, active);
        updateData();
    }

    @Override
    public void onEditTeam(int id, String name, boolean active) {

    }

    private void updateData() {
        TeamsRecyclerFragment list = (TeamsRecyclerFragment) getSupportFragmentManager().findFragmentByTag(TeamsRecyclerFragment.TAG);
        if (list == null) {return;}
        list.initData();
        list.updateList();
    }

    private void setListeners() {
        final TeamsRecyclerFragment list = (TeamsRecyclerFragment) getSupportFragmentManager().findFragmentByTag(TeamsRecyclerFragment.TAG);
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
                        openTeamInfo();
                    } else {
                        actionModeText.setText(String.format(cabString, value));
                    }
                }

                @Override
                public void onListElementLongClick(int count) {
                    actionMode = startSupportActionMode(new CAB(TeamsActivity.this, cabListener));
                    actionModeText = (TextView) (actionMode != null ? actionMode.getCustomView() : new TextView(TeamsActivity.this));
                    actionModeText.setText(String.format(cabString, 1));
                    actionModeActive = true;
                }

                @Override
                public void onListEmpty() {
                }
            });
        }
    }

    private void openTeamInfo() {
        System.out.println("BS-TeamsActivity openTeamInfo: selected = " + selected);
        if (wide && detailViewFrag != null) {
            detailViewFrag.updateContent(selected);
        } else {
            Fragment selectedFrag = TeamViewFragment.newInstance(selected);
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.recycler_content_fragment, selectedFrag, TeamViewFragment.TAG)
                    .setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }
    }
}
