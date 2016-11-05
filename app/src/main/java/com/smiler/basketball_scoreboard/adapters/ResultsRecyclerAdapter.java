package com.smiler.basketball_scoreboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.results.ListListener;

import java.text.DateFormat;
import java.util.ArrayList;

import io.realm.RealmResults;

public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.ViewHolder> {
    private static final String TAG = "BS-ResultsAdapter";
    private ListListener listener;
    private RealmResults<Results> dataSet;
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    private boolean multiSelection = false;
    private View selectedItem;
    private ItemsCallback callback;
    public ArrayList<Integer> selectedIds = new ArrayList<>();

    private interface ItemsCallback {
        void onElementClick(View view);
        void onElementLongClick(View view);
    }

    public ResultsRecyclerAdapter(RealmResults<Results> dataSet) {
        this.dataSet = dataSet;
        callback = new ItemsCallback() {
            @Override
            public void onElementLongClick(View view) {
                if (!multiSelection) {
                    multiSelection = true;
                    selectedIds.clear();
                    if (selectedItem != null) {
                        selectedItem.setSelected(false);
                    }
                    // TODO проверить, что onClick происходит всегда после onLongClick
//                    selectedIds.add((Integer) view.getTag());
                    listener.onListElementLongClick(0);
//                    view.setSelected(!view.isSelected());
                }
            }

            @Override
            public void onElementClick(View view) {
                view.setSelected(!view.isSelected());
                if (multiSelection) {
                    if (view.isSelected()) {
                        selectedIds.add((Integer) view.getTag());
                    } else {
                        selectedIds.remove((Integer) view.getTag());
                    }
                    listener.onListElementClick(selectedIds.size());
                } else {
                    selectedIds.clear();
                    if (selectedItem != null) {
                        selectedItem.setSelected(false);
                    }
                    selectedItem = view;
                    listener.onListElementClick((int) view.getTag());
                }
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Results results = dataSet.get(position);
        viewHolder.setTextView(String.format("%s\n%s - %s", dateFormat.format(results.getDate()),
                results.getHomeTeam(), results.getGuestTeam()));
        viewHolder.setId(results.getId());
        viewHolder.setSelected(selectedIds.indexOf(results.getId()) != -1);
        viewHolder.setCallback(callback);
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final View root;
        private ItemsCallback callback;

        ViewHolder(View v) {
            super(v);
            root = v;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onElementClick(root);
                    }
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (callback != null) {
                        callback.onElementLongClick(root);
                    }
                    return false;
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        void setTextView(String text) { textView.setText(text); }
        void setId(int id) { root.setTag(id); }
        void setCallback(ItemsCallback callback) { this.callback = callback; }
        void setSelected(boolean selected) {
            root.setSelected(selected);
        }
    }

    public void clearSelection() {
        if (selectedItem != null) {
            selectedItem.setSelected(false);
            selectedItem = null;
        }
        selectedIds.clear();
        multiSelection = false;
        notifyDataSetChanged();
    }

    public void deleteSelection() {
        selectedItem = null;
        selectedIds.clear();
        multiSelection = false;
        notifyDataSetChanged();
    }
}
