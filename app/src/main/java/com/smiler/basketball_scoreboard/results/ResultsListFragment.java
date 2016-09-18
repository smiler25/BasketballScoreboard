package com.smiler.basketball_scoreboard.results;

import android.app.Activity;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.smiler.basketball_scoreboard.BaseMultiChoice;
import com.smiler.basketball_scoreboard.DbHelper;
import com.smiler.basketball_scoreboard.DbScheme;
import com.smiler.basketball_scoreboard.ListMultiChoice;

import java.util.List;

public class ResultsListFragment extends ListFragment {

    private int selectedPos = -1;
    private ResultsCursorAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor = getDataCursor();
        if (cursor.getCount() == 0) {
            listener.onListEmpty();
            return;
        }
        adapter = new ResultsCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(adapter);
        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        final ListMultiChoice cab = new ListMultiChoice(listView, getActivity());
        listView.setMultiChoiceModeListener(cab);
        cab.setCabDeleteListener(new BaseMultiChoice.CabDeletedListener() {
            @Override
            public void onCabDelete(List<String> selectedIds) {
                listener.onListItemDeleted(updateList());
                cab.close();
            }
        });
    }

    boolean updateList() {
        adapter.notifyDataSetChanged();
        adapter.notifyDataSetInvalidated();
        adapter.swapCursor(getDataCursor());
        return adapter.getCount() == 0;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (position != selectedPos) {
            selectedPos = position;
            v.setSelected(true);
            listener.onListItemSelected((int) v.getTag());
        }
    }

    interface ResultsListListener {
        void onListItemSelected(int sqlId);
        void onListItemDeleted(boolean empty);
        void onListEmpty();

    }
    private ResultsListListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ResultsListListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ResultsListListener");
        }
    }

    private Cursor getDataCursor() {
        DbHelper dbHelper = DbHelper.getInstance(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sortOrder = DbScheme.ResultsTable.COLUMN_DATE + " DESC";
        return db.query(DbScheme.ResultsTable.TABLE_NAME,
                            null, null, null, null, null, sortOrder);
    }
}