package com.smiler.basketball_scoreboard.results;

import com.smiler.basketball_scoreboard.adapters.RealmRecyclerAdapter;
import com.smiler.basketball_scoreboard.db.Results;

import java.text.DateFormat;

import io.realm.RealmResults;

public class ResultsRealmRecyclerAdapter extends RealmRecyclerAdapter {
    private final RealmResults<Results> data;
    private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

    public ResultsRealmRecyclerAdapter(RealmResults<Results> data) {
        super();
        this.data = data;
    }

    @Override
    public void onBindViewHolder(RealmRecyclerAdapter.ViewHolder viewHolder, int position) {
        Results results = data.get(position);
        viewHolder.setTextView(String.format("%s\n%s - %s", dateFormat.format(results.getDate()),
                results.getFirstTeamName(), results.getSecondTeamName()));
        viewHolder.setId(results.getId());
        viewHolder.setSelected(selectedIds.indexOf(results.getId()) != -1);
        viewHolder.setCallback(callback);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
