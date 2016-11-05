package com.smiler.basketball_scoreboard.help;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;

import com.smiler.basketball_scoreboard.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelpRulesFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expandable_list, container, false);
        ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>() {{
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", getResources().getString(R.string.fiba));
            }});
            add(new HashMap<String, String>() {{
                put("ROOT_NAME", getResources().getString(R.string.nba));
            }});
        }};


        List<List<Map<String, String>>> listOfChildGroups = new ArrayList<>();

        List<Map<String, String>> childGroupForFirstGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", getResources().getString(R.string.help_fiba_rules));
            }});
        }};
        listOfChildGroups.add(childGroupForFirstGroupRow);

        List<Map<String, String>> childGroupForSecondGroupRow = new ArrayList<Map<String, String>>(){{
            add(new HashMap<String, String>() {{
                put("CHILD_NAME", getResources().getString(R.string.help_nba_rules));
            }});
        }};
        listOfChildGroups.add(childGroupForSecondGroupRow);
        ExpandableListAdapter listAdapter = new SimpleExpandableListAdapter(
                getActivity(),

                groupData,
                R.layout.results_list_item,
                new String[]{"ROOT_NAME"},
                new int[]{android.R.id.text1},

                listOfChildGroups,
                R.layout.results_list_item,
                new String[]{"CHILD_NAME", "CHILD_NAME"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );
        expListView.setAdapter(listAdapter);
        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        int margin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);
        int margin2 = getResources().getDimensionPixelSize(R.dimen.indicator_margin2);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width-margin, width-margin2);
        } else {
            expListView.setIndicatorBoundsRelative(width-margin, width-margin2);
        }

        return rootView;
    }
}
