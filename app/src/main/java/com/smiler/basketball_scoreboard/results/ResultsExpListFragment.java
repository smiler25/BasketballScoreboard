package com.smiler.basketball_scoreboard.results;

import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.BaseMultiChoice;
import com.smiler.basketball_scoreboard.CABListener;
import com.smiler.basketball_scoreboard.ExpListMultiChoice;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.elements.BaseResultsListFragment;

import java.text.DateFormat;
import java.util.List;

public class ResultsExpListFragment extends BaseResultsListFragment {
    public static String TAG = "BS-ResultsExpListFragment";
    private ResultsExpListAdapter adapter;
    private ExpandableListView expListView;
    private SparseArray<ResultsExpListParent> items = new SparseArray<>();
    private SparseIntArray idPositions = new SparseIntArray();
    private ListListener listener;
    private BaseMultiChoice cab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expandable_list, container, false);
        getListEntries();
        if (items.size() == 0) {
            return rootView;
//            listener.onListEmpty();
        }
        adapter = new ResultsExpListAdapter(getActivity(), items, idPositions);
        expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);
        expListView.setAdapter(adapter);
        expListView.setChildDivider(getResources().getDrawable(android.R.color.transparent));
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.indicator));
        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        int margin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);
        int margin2 = getResources().getDimensionPixelSize(R.dimen.indicator_margin2);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width-margin, width-margin2);
        } else {
            expListView.setIndicatorBoundsRelative(width-margin, width-margin2);
        }

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                listener.onListElementClick(groupPosition);
                ResultsExpListParent parent = adapter.getParent().get(groupPosition);
                if (parent.getChild() == null) {
                    parent.setChild(new ResultView(getActivity(), parent.getItemId()));
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetInvalidated();
                }
            }
        });

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (cab.actionModeEnabled) {
                    expListView.setItemChecked(groupPosition, !expListView.isItemChecked(groupPosition));
                }
                return cab.actionModeEnabled;
            }
        });

        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
        return rootView;
    }

    private boolean updateList(List<String> selectedIds) {
        adapter.deleteItems(selectedIds);
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        return adapter.getGroupCount() == 0;
    }

    @Override
    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    @Override
    public void setMode(CABListener listener) {
        if (expListView != null) {
            expListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            cab = new ExpListMultiChoice(expListView, getActivity(), listener);
            expListView.setMultiChoiceModeListener(cab);
        }
    }

    @Override
    public void deleteSelection() {
        RealmController.with(this).deleteResults(adapter.selectedIds.toArray(new Integer[adapter.selectedIds.size()]));
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.cab_success), Toast.LENGTH_LONG).show();
        adapter.deleteSelection();
    }

    private void getListEntries() {
        int pos = 0;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

        realmData = RealmController.with(this).getResults();
        try {
            for (Results results : realmData) {
                items.put(pos, new ResultsExpListParent(String.format("%s\n%s - %s", dateFormat.format(results.getDate()),
                        results.getHomeTeam(), results.getGuestTeam()), results.getId()));
                idPositions.put(results.getId(), pos++);
            }
        } catch (NullPointerException e) {

        }
    }
}