package com.smiler.basketball_scoreboard.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Results;

import java.text.DateFormat;

import io.realm.RealmResults;

public class ResultsRecyclerAdapter extends RecyclerView.Adapter<ResultsRecyclerAdapter.ViewHolder> {
    private static final String TAG = "BS-ResultsRecyclerAdapter";

    private RealmResults<Results> dataSet;
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        ViewHolder(View v) {
            super(v);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });
            textView = (TextView) v.findViewById(R.id.textView);
        }

        TextView getTextView() {
            return textView;
        }
    }

    public ResultsRecyclerAdapter(RealmResults<Results> dataSet) {
        this.dataSet = dataSet;
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
        viewHolder.getTextView().setText(
                String.format("%s\n%s - %s", dateFormat.format(results.getDate()),
                        results.getHomeTeam(), results.getGuestTeam())
        );
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
