package com.smiler.basketball_scoreboard.results;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.smiler.basketball_scoreboard.BaseMultiChoice;
import com.smiler.basketball_scoreboard.DbHelper;
import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.ExpListMultiChoice;
import com.smiler.basketball_scoreboard.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ResultsExpListFragment extends Fragment {

    private ResultsExpListAdapter adapter;
    private ExpandableListView expListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.expandable_list, container, false);
        Object[] content = getListEntries();
        HashMap<Integer, ResultsExpListParent> posItems = (HashMap<Integer, ResultsExpListParent>) content[0];
        if (posItems.isEmpty()) {
            listener.onListEmpty();
            return rootView;
        }
        adapter = new ResultsExpListAdapter(getActivity(), posItems, (HashMap<Integer, Integer>) content[1]);
        expListView = (ExpandableListView) rootView.findViewById(R.id.expListView);
        expListView.setAdapter(adapter);
        expListView.setChildDivider(getResources().getDrawable(android.R.color.transparent));
        expListView.setGroupIndicator(getResources().getDrawable(R.drawable.indicator));
        Display newDisplay = getActivity().getWindowManager().getDefaultDisplay();
        int width = newDisplay.getWidth();
        int margin = getResources().getDimensionPixelSize(R.dimen.indicator_margin);
        int margin2 = getResources().getDimensionPixelSize(R.dimen.indicator_margin2);
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width-margin, width-margin2);
        } else {
            expListView.setIndicatorBoundsRelative(width-margin, width-margin2);
        }

        expListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        final BaseMultiChoice cab = new ExpListMultiChoice(expListView, getActivity());
        expListView.setMultiChoiceModeListener(cab);
//        cab.setCabDeleteListener(new BaseMultiChoice.CabDeletedListener() {
//            @Override
//            public void onCabDelete(List<String> selectedIds) {
//                listener.onExpListItemDeleted(updateList(selectedIds));
//            }
//        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                listener.onExpListItemSelected();
                ResultsExpListParent parent = adapter.getParent().get(groupPosition);
                if (parent.getChild() == null) {
                    parent.setChild(new ResultView(getActivity(), parent.getSqlId()));
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

    private ResultsExpListListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ResultsExpListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ResultsExpListListener");
        }
    }

    private Object[] getListEntries() {
        SparseArray<ResultsExpListParent> posItems = new SparseArray<>();
        SparseIntArray idPositions = new SparseIntArray();
        int pos = 0;

        DbHelper dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder = DbScheme.ResultsTable.COLUMN_DATE + " DESC";
        Cursor c = db.query(DbScheme.ResultsTable.TABLE_NAME,
                            null, null, null, null, null, sortOrder);
        c.moveToFirst();
        if (c.getCount() == 0) { return new Object[]{posItems, idPositions}; }
        String str;
        do {
            int id = c.getInt(c.getColumnIndex(DbScheme.ResultsTable._ID));
            long date = c.getLong(c.getColumnIndex(DbScheme.ResultsTable.COLUMN_DATE));
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
            String hName = c.getString(c.getColumnIndex(DbScheme.ResultsTable.COLUMN_HOME_TEAM));
            String gName = c.getString(c.getColumnIndex(DbScheme.ResultsTable.COLUMN_GUEST_TEAM));
            str = dateFormat.format(new Date(date)) + "\n" + hName + " - " + gName;
            posItems.put(pos, new ResultsExpListParent(str, id));
            idPositions.put(id, pos++);
        } while (c.moveToNext());
        c.close();
        dbHelper.close();

        return new Object[]{posItems, idPositions};
    }
}