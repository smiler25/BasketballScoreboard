package com.smiler.basketball_scoreboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;

import io.realm.RealmBaseAdapter;
import io.realm.RealmObject;
import io.realm.RealmResults;

//public abstract class RealmRecyclerViewAdapter<T extends RealmObject> extends RecyclerView.Adapter {
public abstract class RealmRecyclerViewAdapter<T extends RealmObject>
        extends RecyclerView.Adapter<RealmRecyclerViewAdapter.ViewHolder> {

    private RealmBaseAdapter<T> realmBaseAdapter;
    protected RealmResults<T> realmResults;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(v1 -> {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
            });
            textView = v.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
        public void setTextView(String text) {
            textView.setText(text);
        }
    }


    public T getItem(int position) {
        return realmBaseAdapter.getItem(position);
    }

    public RealmBaseAdapter<T> getRealmAdapter() {
        return realmBaseAdapter;
    }

    public void setRealmAdapter(RealmBaseAdapter<T> realmAdapter) {
        realmBaseAdapter = realmAdapter;
    }

    @Override
    public RealmRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_item, viewGroup, false);
        return new RealmRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RealmRecyclerViewAdapter.ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");
//        viewHolder.setTextView(mDataSet[position]);
    }

//    @Override
//    public int getItemCount() {
//        return mDataSet.length;
//    }


}