package com.smiler.basketball_scoreboard.elements.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.adapters.TeamsListAdapter;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Team;

import java.util.ArrayList;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;


public class ListDialog extends DialogFragment {

    public static final String TAG = "ListDialog";
    private boolean left;
    private int team;
    private ListDialogListener listener;

    public interface ListDialogListener {
        void onTimeoutDialogItemClick(int which);
        void onNewPeriodDialogItemClick(int which);
        void onClearPanelDialogItemClick(int which, boolean left);
        void onSubstituteListSelect(boolean left, int newNumber);
        void onSelectAddPlayers(int which, boolean left);
        void onSelectTeam(int type, Team team);
    }

    public static ListDialog newInstance(DialogTypes type) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(DialogTypes type, boolean left) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putBoolean("left", left);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(DialogTypes type, ArrayList<String> values,
                                         boolean left, int number) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putStringArrayList("values", values);
        args.putBoolean("left", left);
        args.putInt("number", number);
        f.setArguments(args);
        return f;
    }

    public static ListDialog newInstance(DialogTypes type, int team) {
        ListDialog f = new ListDialog();
        Bundle args = new Bundle();
        args.putSerializable("type", type);
        args.putInt("team", team);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        DialogTypes type = (DialogTypes) args.get("type");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (type != null) {
            switch (type) {
                case TIMEOUT:
                    initTimeout(builder);
                    break;
                case NEW_PERIOD:
                    initNewPeriod(builder);
                    break;
                case PANEL_CLEAR:
                    initPanelClear(builder, args);
                    break;
                case SUBSTITUTE:
                    initSubstitute(builder, args);
                    break;
                case SELECT_TEAM:
                    initTeamSelect(builder, args);
                    break;
                case SELECT_ADD_PLAYERS:
                    initAddPlayersSelect(builder, args);
                    break;
            }
        }
        builder.setCancelable(true);
        return builder.create();
    }

    private void initTimeout(AlertDialog.Builder builder) {
        builder.setItems(R.array.timeout_variants, (dialog, which) -> listener.onTimeoutDialogItemClick(which));
    }

    private void initNewPeriod(AlertDialog.Builder builder) {
        builder.setItems(R.array.new_period_variants, (dialog, which) -> listener.onNewPeriodDialogItemClick(which));
    }

    private void initPanelClear(AlertDialog.Builder builder, Bundle args) {
        left = args.getBoolean("left", true);
        builder.setItems(R.array.sp_clear_titles, (dialog, which) -> listener.onClearPanelDialogItemClick(which, left));
    }

    private void initSubstitute(AlertDialog.Builder builder, Bundle args) {
        left = args.getBoolean("left", true);
        ArrayList<String> values = args.getStringArrayList("values");
        if (values == null) { values = new ArrayList<>(); }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_list_item, values);
        builder.setAdapter(adapter, listClickListener);
        int number = args.getInt("number", -1);
        String title;
        if (left) {
            title = number == -1
                    ? getResources().getString(R.string.substitute_dialog_title_home0)
                    : String.format(getResources().getString(R.string.substitute_dialog_title_home), number);
        } else {
            title = number == -1
                    ? getResources().getString(R.string.substitute_dialog_title_guest0)
                    : String.format(getResources().getString(R.string.substitute_dialog_title_guest), number);
        }
        builder.setTitle(title);
    }

    private void initTeamSelect(AlertDialog.Builder builder, Bundle args) {
        team = args.getInt("team", -1);
        int titleResId;
        if (team == HOME) {
            titleResId = R.string.select_home_team;
        } else if (team == GUEST) {
            titleResId = R.string.select_guest_team;
        } else {
            titleResId = R.string.select_team;
        }
        TeamsListAdapter adapter = new TeamsListAdapter(getActivity(), RealmController.with().getTeams());
        builder.setAdapter(adapter, selectTeamListener);
        String title = getResources().getString(titleResId);
        builder.setTitle(title);
    }

    private void initAddPlayersSelect(AlertDialog.Builder builder, Bundle args) {
        left = args.getBoolean("left", true);
        builder.setItems(R.array.select_add_players, (dialog, which) -> listener.onSelectAddPlayers(which, left));
    }

    DialogInterface.OnClickListener listClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            listener.onSubstituteListSelect(left,
                    Integer.parseInt(((AlertDialog) dialog).getListView().getAdapter().getItem(which).toString().split(":")[0]));
        }
    };

    DialogInterface.OnClickListener selectTeamListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            listener.onSelectTeam(team, (Team) ((AlertDialog) dialog).getListView().getAdapter().getItem(which));
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ListDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ListDialogListener");
        }
    }
}