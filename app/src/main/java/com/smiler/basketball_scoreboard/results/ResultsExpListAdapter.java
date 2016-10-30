package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

import java.util.ArrayList;
import java.util.List;

public class ResultsExpListAdapter extends BaseExpandableListAdapter{

    private LayoutInflater inflater;
    private SparseArray<ResultsExpListParent> objects;

    private SparseIntArray idPositions;
    ArrayList<Integer> selectedIds = new ArrayList<>();

    ResultsExpListAdapter(Context context, SparseArray<ResultsExpListParent> objects, SparseIntArray idPositions) {
        this.objects = objects;
        this.idPositions = idPositions;
        this.inflater = LayoutInflater.from(context);
    }

    private void deleteItem(int id) {
        objects.remove(idPositions.get(id));
        idPositions.delete(id);
    }

    void deleteItems(List<String> selectedIds) {
        for (String id : selectedIds) {
            deleteItem(Integer.valueOf(id));
        }
    }

    public int getGroupCount() {
        return objects.size();
    }

    public int getChildrenCount(int parentPosition) {
        return 1;
    }

    public Object getGroup(int i) {
        return objects.get(i).getParent();
    }

    SparseArray<ResultsExpListParent> getParent() {
        return objects;
    }

    public Object getChild(int parentPosition, int childPosition) {
        return objects.get(parentPosition).getChild();
    }

    public long getGroupId(int parentPosition) {
        try {
            return objects.get(parentPosition).getItemId();
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
        ResultsExpListParent obj = objects.get(parentPosition);
        ((TextView) view).setText(obj.getParent());
        view.setTag(obj.getItemId());
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
            selectedIds.add((Integer) position);

        } else {
            selectedIds.remove((Integer) position);

        }
        notifyDataSetChanged();
    }

    public void deleteSelection() {
//        for (Integer id : selectedIds) {
//            idPositions.delete(id);
//            objects.delete(id);
//        }
        selectedIds.clear();
        notifyDataSetChanged();
    }

}