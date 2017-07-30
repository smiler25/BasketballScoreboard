package com.smiler.basketball_scoreboard.profiles;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;
import com.smiler.basketball_scoreboard.elements.CABListener;
import com.smiler.basketball_scoreboard.elements.lists.BaseMultiChoice;
import com.smiler.basketball_scoreboard.elements.lists.ExpandableListParent;
import com.smiler.basketball_scoreboard.elements.lists.ListListener;
import com.smiler.basketball_scoreboard.elements.lists.RealmListFragment;

import io.realm.RealmResults;

public class ProfilesListFragment extends ListFragment implements RealmListFragment {
    public static String TAG = "BS-ProfilesListFragment";
    private RealmResults<Team> realmData;
    private ProfilesRealmRecyclerAdapter adapter;
    private SparseArray<ExpandableListParent> items = new SparseArray<>();
    private SparseIntArray idPositions = new SparseIntArray();
    private ListListener listener;
    private BaseMultiChoice cab;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ProfilesRealmRecyclerAdapter(realmData);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.help_activity_values, R.layout.results_list_item);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        listener.onListElementClick(position);
        v.setSelected(true);
    }

    @Override
    public void initData() {
        realmData = RealmController.with().getTeams();
    }

    @Override
    public void setListener(ListListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean updateList() {
//        adapter.deleteItems(selectedIds);
//        adapter.notifyDataSetChanged();
//        adapter.notifyDataSetInvalidated();
        return true;
    }

    @Override
    public void clearSelection() {

    }

    @Override
    public void deleteSelection() {
        RealmController.with().deleteResults(adapter.selectedIds.toArray(new Integer[adapter.selectedIds.size()]));
        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.cab_success), Toast.LENGTH_LONG).show();
        adapter.deleteSelection();
    }

    @Override
    public void setMode(CABListener listener) {
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            listener = (com.smiler.basketball_scoreboard.help.HelpListFragment.ProfilesListFragmentListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement ProfilesListFragmentListener");
//        }
//    }

}