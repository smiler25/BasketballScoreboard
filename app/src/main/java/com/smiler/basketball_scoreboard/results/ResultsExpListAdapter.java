package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

import java.util.HashMap;
import java.util.List;

public class ResultsExpListAdapter extends BaseExpandableListAdapter{

    private LayoutInflater inflater;
    private HashMap<Integer, ResultsExpListParent> posObjects;
    private HashMap<Integer, Integer> idPositions;
    private SparseBooleanArray selectedIds;

    public ResultsExpListAdapter(Context context, HashMap<Integer, ResultsExpListParent> posObjects, HashMap<Integer, Integer> idPositions) {
        this.posObjects = posObjects;
        this.idPositions = idPositions;
        this.inflater = LayoutInflater.from(context);
        selectedIds = new SparseBooleanArray();
    }

    public void deleteItem(int id) {
        posObjects.remove(idPositions.get(id));
        idPositions.remove(id);
    }

    public void deleteItems(List<String> selectedIds) {
        for (String id:selectedIds) {
            deleteItem(Integer.valueOf(id));
        }
    }

    public int getGroupCount() {
        return posObjects.size();
    }

    public int getChildrenCount(int parentPosition) {
        return 1;
    }

    public Object getGroup(int i) {
        return posObjects.get(i).getParent();
    }

    public HashMap<Integer, ResultsExpListParent> getParent() {
        return posObjects;
    }

    public Object getChild(int parentPosition, int childPosition) {
        return posObjects.get(parentPosition).getChild();
    }

    public long getGroupId(int parentPosition) {
        try {
            return posObjects.get(parentPosition).getSqlId();
        } catch (NullPointerException e) {
            return -1;
        }
    }

    public long getChildId(int i, int childPosition) {
        return childPosition;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getGroupView(int parentPosition, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.results_list_item, viewGroup, false);
        }
        ResultsExpListParent obj = posObjects.get(parentPosition);
        ((TextView) view).setText(obj.getParent());
        view.setTag(obj.getSqlId());
        return view;
    }

    public View getChildView(int parentPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        return (ResultView) getChild(parentPosition, childPosition);
    }

    public boolean isChildSelectable(int i, int i1) {
        return true;
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