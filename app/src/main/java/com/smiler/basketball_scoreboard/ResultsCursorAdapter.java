package com.smiler.basketball_scoreboard;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

public class ResultsCursorAdapter extends CursorAdapter {

    private SparseBooleanArray selectedIds;

    public ResultsCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        selectedIds = new SparseBooleanArray();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.results_list_item, parent, false);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DbScheme.ResultsTable._ID));
        long date = cursor.getLong(cursor.getColumnIndex(DbScheme.ResultsTable.COLUMN_NAME_DATE));
        String home_name = cursor.getString(cursor.getColumnIndex(DbScheme.ResultsTable.COLUMN_NAME_HOME_TEAM));
        String guest_name = cursor.getString(cursor.getColumnIndex(DbScheme.ResultsTable.COLUMN_NAME_GUEST_TEAM));
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        ((TextView)view).setText(dateFormat.format(new Date(date)) + "\n" + home_name + " - " + guest_name);
        view.setTag(id);
    }

    public void toggleSelection(int position, boolean selected) {
        if (selected) {
            selectedIds.put(position, true);
        } else {
            selectedIds.delete(position);
        }
        notifyDataSetChanged();
    }
}

