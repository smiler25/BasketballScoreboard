package com.smiler.basketball_scoreboard.elements.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.Team;

import static com.smiler.basketball_scoreboard.Constants.GUEST;
import static com.smiler.basketball_scoreboard.Constants.HOME;

public class NewGameDialog extends DialogFragment implements TeamSelector {

    public static String TAG = "BS-NewGameDialog";
    NewGameDialogListener listener;
    private CheckBox saveCheckBox;
    private Button hSelector, gSelector;
    private Team hSelectedTeam, gSelectedTeam;
    private View saveButtons, saveTeamsToggle;
    private static String argSaveEnabled = "saveEnabled";
    private static String argSaveSelected = "saveSelected";
    private static String askSaveHomeTeamKey = "askSaveHomeTeam";
    private static String askSaveGuestTeamKey = "askSaveGuestTeam";
    private boolean saveHomeAvail = true;
    private boolean saveGuestAvail = true;
    private String hName, gName;

    public static NewGameDialog newInstance(boolean saveEnabled, boolean saveSelected,
                                            boolean askSaveHomeTeam, boolean askSaveGuestTeam) {
        NewGameDialog f = new NewGameDialog();
        Bundle args = new Bundle();
        args.putBoolean(argSaveEnabled, saveEnabled);
        args.putBoolean(argSaveSelected, saveSelected);
        args.putBoolean(askSaveHomeTeamKey, askSaveHomeTeam);
        args.putBoolean(askSaveGuestTeamKey, askSaveGuestTeam);
        f.setArguments(args);
        return f;
    }

    public interface NewGameDialogListener {
        void onStartSameTeams(boolean saveResult);
        void onStartNewTeams(boolean saveResult, Team hTeam, Team gTeam);
        void onStartNoTeams(boolean saveResult);
        boolean onSaveTeam(int teamType);
    }

    public void setHomeName(String value) {
        hName = value;
    }

    public void setGuestName(String value) {
        gName = value;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (NewGameDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must NewGameDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        saveHomeAvail = args.getBoolean(askSaveHomeTeamKey, true);
        saveGuestAvail = args.getBoolean(askSaveGuestTeamKey, true);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_new_game, null);
        builder.setView(v).setCancelable(true);
        saveCheckBox = v.findViewById(R.id.new_game_save);
        if (args.getBoolean(argSaveEnabled, true)) {
            saveCheckBox.setChecked(args.getBoolean(argSaveSelected, true));
        } else {
            saveCheckBox.setVisibility(View.GONE);
        }
        saveButtons = v.findViewById(R.id.save_teams_buttons);
        saveTeamsToggle = v.findViewById(R.id.new_game_save_teams);

        if (saveHomeAvail || saveGuestAvail) {
            View saveHomeTeamBu = v.findViewById(R.id.new_game_save_home_team);
            View saveGuestTeamBu = v.findViewById(R.id.new_game_save_guest_team);
            if (saveHomeAvail) {
                if (hName != null && !hName.equals("")) {
                    ((Button) saveHomeTeamBu).setText(String.format(getResources().getString(R.string.save_home_team_with_name), hName));
                }
                saveHomeTeamBu.setOnClickListener(view -> {
                    if (listener.onSaveTeam(HOME)) {
                        view.setVisibility(View.GONE);
                        saveHomeAvail = false;
                    }
                    if (canHideSaveButtons()) {
                        hideSaveButtons();
                    }
                });
            } else {
                saveHomeTeamBu.setVisibility(View.GONE);
            }
            if (saveGuestAvail) {
                if (gName != null && !gName.equals("")) {
                    ((Button) saveGuestTeamBu).setText(String.format(getResources().getString(R.string.save_guest_team_with_name), gName));
                }
                saveGuestTeamBu.setOnClickListener(view -> {
                    if (listener.onSaveTeam(GUEST)) {
                        view.setVisibility(View.GONE);
                        saveGuestAvail = false;
                    }
                    if (canHideSaveButtons()) {
                        hideSaveButtons();
                    }
                });
            } else {
                saveGuestTeamBu.setVisibility(View.GONE);
            }
            saveTeamsToggle.setOnClickListener(v16 -> toggleSaveTeamsBlock(saveButtons));
        } else {
            saveTeamsToggle.setVisibility(View.GONE);
            saveButtons.setVisibility(View.GONE);
        }

        v.findViewById(R.id.new_game_same_teams).setOnClickListener(v15 -> selectSameTeams());
        v.findViewById(R.id.new_game_other_teams).setOnClickListener(v14 -> selectNewTeams());
        v.findViewById(R.id.new_game_no_teams).setOnClickListener(v13 -> selectNoTeams());

        hSelector = v.findViewById(R.id.new_game_select_first_team);
        gSelector = v.findViewById(R.id.new_game_select_second_team);
        hSelector.setOnClickListener(v12 -> showTeamsList(HOME));
        gSelector.setOnClickListener(v1 -> showTeamsList(GUEST));
        return builder.create();
    }

    private boolean canHideSaveButtons() {
        return !(saveHomeAvail || saveGuestAvail);
    }

    private void hideSaveButtons() {
        if (saveButtons != null) {
            saveButtons.setVisibility(View.GONE);
        }
        if (saveTeamsToggle != null) {
            saveTeamsToggle.setVisibility(View.GONE);
        }
    }

    private void showTeamsList(int team) {
        ListDialog.newInstance(DialogTypes.SELECT_TEAM, team).show(getFragmentManager(), ListDialog.TAG);
    }

    public void handleTeamSelect(int type, Team team) {
        if (type == HOME) {
            if (gSelectedTeam != null && gSelectedTeam.getId() == team.getId()) {
                showSelectedToast();
                return;
            }
            hSelectedTeam = team;
            hSelector.setText(String.format(getResources().getString(R.string.select_home_team_selected), team.getName()));
        } else if (type == GUEST) {
            if (hSelectedTeam != null && hSelectedTeam.getId() == team.getId()) {
                showSelectedToast();
                return;
            }
            gSelectedTeam = team;
            gSelector.setText(String.format(getResources().getString(R.string.select_guest_team_selected), team.getName()));
        }
    }

    private void selectNewTeams() {
        if (hSelectedTeam == null || gSelectedTeam == null) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_team_both_required), Toast.LENGTH_LONG).show();
            return;
        }
        listener.onStartNewTeams(saveCheckBox.isChecked(), hSelectedTeam, gSelectedTeam);
        dismiss();
    }

    private void selectNoTeams() {
        listener.onStartNoTeams(saveCheckBox.isChecked());
        dismiss();
    }

    private void selectSameTeams() {
        listener.onStartSameTeams(saveCheckBox.isChecked());
        dismiss();
    }

    private void toggleSaveTeamsBlock(final View view) {
        final int visibility, height;
        final float viewAlpha, alpha;
        if (view.getVisibility() == View.GONE) {
            visibility = View.VISIBLE;
            viewAlpha = 0.0f;
            alpha = 1.0f;
            height = view.getHeight();
        } else {
            visibility = View.GONE;
            viewAlpha = 1.0f;
            alpha = 0.0f;
            height = -view.getHeight();
        }
        view.animate()
            .translationY(height)
            .alpha(alpha)
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(visibility);
                    view.setAlpha(viewAlpha);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(visibility);
                }
            });
    }

    public void showSelectedToast() {
        Toast.makeText(getActivity(), getResources().getString(R.string.select_team_already_selected), Toast.LENGTH_LONG).show();
    }

}