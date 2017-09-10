package com.smiler.basketball_scoreboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;

import java.util.ArrayList;

abstract public class RealmRecyclerAdapter extends RecyclerView.Adapter<RealmRecyclerAdapter.ViewHolder> {
    private static final String TAG = "BS-RealmRecyclerAdapter";
    private ListListener listener;
    private boolean multiSelection = false;
    private View selectedItem;
    protected ItemsCallback callback;
    public ArrayList<Integer> selectedIds = new ArrayList<>();

    private interface ItemsCallback {
        void onElementClick(View view);
        void onElementLongClick(View view);
    }

    public RealmRecyclerAdapter() {
        callback = new ItemsCallback() {
            @Override
            public void onElementLongClick(View view) {
                if (!multiSelection) {
                    multiSelection = true;
                    selectedIds.clear();
                    if (selectedItem != null) {
                        selectedItem.setSelected(false);
                    }
                    listener.onListElementLongClick(0);
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
    abstract public void onBindViewHolder(ViewHolder viewHolder, final int position);

    @Override
    abstract public int getItemCount();

    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final View root;
        private ItemsCallback callback;

        public ViewHolder(View v) {
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

        public void setTextView(String text) { textView.setText(text); }
        public void setId(int id) { root.setTag(id); }
        public void setCallback(ItemsCallback callback) { this.callback = callback; }
        public void setSelected(boolean selected) {
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
        RealmController.with().deleteTeams(selectedIds.toArray(new Integer[selectedIds.size()]));
        selectedItem = null;
        selectedIds.clear();
        multiSelection = false;
        notifyDataSetChanged();
    }
}
