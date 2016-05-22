package com.smiler.basketball_scoreboard.help;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.smiler.basketball_scoreboard.AppUpdatesFragment;
import com.smiler.basketball_scoreboard.Constants;
import com.smiler.basketball_scoreboard.R;

public class HelpActivity extends AppCompatActivity implements HelpListFragment.HelpListListener {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        initToolbar();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
            toolbar.setTitle(R.string.action_help);
        } else {
            finish();
        }
    }

    @Override
    public void onHelpListItemClick(int position) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        View rightLayout = findViewById(R.id.help_fragment);
        Fragment selectedFrag = null;
        switch (position) {
            case 0:
                selectedFrag = HelpFragment.newInstance(R.layout.help_main_fragment);
                break;
            case 1:
                selectedFrag = HelpFragment.newInstance(R.layout.help_panels_fragment);
                break;
            case 2:
                selectedFrag = new HelpRulesFragment();
                break;
            case 3:
                new AppUpdatesFragment().show(getFragmentManager(), Constants.TAG_FRAGMENT_APP_UPDATES);
                return;
        }
        if (selectedFrag != null) {
            if (rightLayout != null) {
                ft.add(R.id.help_fragment, selectedFrag)
                  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                  .commit();
            } else {
                ft.addToBackStack(null)
                  .add(R.id.help_list_frag, selectedFrag)
                  .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                  .commit();
                toolbar.setTitle(getResources().getStringArray(R.array.help_activity_values)[position]);
            }
        }
    }
}