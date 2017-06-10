package com.smiler.basketball_scoreboard.layout;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import com.smiler.basketball_scoreboard.elements.OverlayFragment;
import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.panels.SidePanelFragment;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;
import com.smiler.basketball_scoreboard.preferences.Preferences;

import java.util.TreeMap;
import java.util.TreeSet;

import static com.smiler.basketball_scoreboard.Constants.LEFT;
import static com.smiler.basketball_scoreboard.Constants.OVERLAY_PANELS;
import static com.smiler.basketball_scoreboard.Constants.PANEL_DELETE_TYPE_FULL;
import static com.smiler.basketball_scoreboard.Constants.RIGHT;


public class PlayersPanels {
    public static final String TAG = "BS-PlayersPanels";
    private final Preferences preferences;
    private SidePanelFragment leftPanel, rightPanel;
    private OverlayFragment overlayPanels;
    private FragmentManager fragmentManager;
    private Context context;

    public PlayersPanels(Context context, Preferences preferences) {
        this.context = context;
        this.preferences = preferences;
        fragmentManager = ((Activity) context).getFragmentManager();
        init();
    }

    private PlayersPanels init() {
        leftPanel = SidePanelFragment.newInstance(LEFT);
        rightPanel = SidePanelFragment.newInstance(RIGHT);
        overlayPanels = OverlayFragment.newInstance(OVERLAY_PANELS);
        leftPanel.setRetainInstance(true);
        rightPanel.setRetainInstance(true);
        overlayPanels.setRetainInstance(true);
        preferences.spStateChanged = false;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft
          .add(R.id.left_panel_full, leftPanel, SidePanelFragment.TAG_LEFT_PANEL)
          .add(R.id.right_panel_full, rightPanel, SidePanelFragment.TAG_RIGHT_PANEL)
          .hide(leftPanel)
          .hide(rightPanel)
          .addToBackStack(null)
          .commit();
        return this;
    }

    public void saveInstanceState(Bundle outState){
        if (overlayPanels != null && overlayPanels.isAdded()) {
            fragmentManager.putFragment(outState, OverlayFragment.TAG_PANELS, overlayPanels);
        }
        if (leftPanel != null && leftPanel.isAdded()) {
            fragmentManager.putFragment(outState, SidePanelFragment.TAG_LEFT_PANEL, leftPanel);
        }
        if (rightPanel != null && rightPanel.isAdded()) {
            fragmentManager.putFragment(outState, SidePanelFragment.TAG_RIGHT_PANEL, rightPanel);
        }
    }

    public void restoreInstanceState(Bundle inState) {
        Fragment overlayPanels_ = fragmentManager.getFragment(inState, OverlayFragment.TAG_PANELS);
        if (overlayPanels_ != null) {
            overlayPanels = (OverlayFragment) overlayPanels_;
        }
        Fragment leftPanel_ = fragmentManager.getFragment(inState, SidePanelFragment.TAG_LEFT_PANEL);
        if (leftPanel_ != null) {
            leftPanel = (SidePanelFragment) leftPanel_;
        }
        Fragment rightPanel_ = fragmentManager.getFragment(inState, SidePanelFragment.TAG_RIGHT_PANEL);
        if (rightPanel_ != null) {
            rightPanel = (SidePanelFragment) rightPanel_;
        }
        fragmentManager.popBackStack();
    }

    public void switchSides() {
        leftPanel.changeRowsSide();
        rightPanel.changeRowsSide();
        leftPanel.clearTable();
        rightPanel.clearTable();
        TreeMap<Integer, SidePanelRow> leftRows = leftPanel.getAllPlayers();
        TreeSet<SidePanelRow> leftActivePlayers = leftPanel.getActivePlayers();
        SidePanelRow leftCaptainPlayer = leftPanel.getCaptainPlayer();
        leftPanel.replaceRows(rightPanel.getAllPlayers(), rightPanel.getActivePlayers(), rightPanel.getCaptainPlayer());
        rightPanel.replaceRows(leftRows, leftActivePlayers, leftCaptainPlayer);
    }

    public void showLeft() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_in);
        Fragment o = fragmentManager.findFragmentByTag(OverlayFragment.TAG_PANELS);
        if (o != null) {
            if (!o.isVisible()) {
                ft.show(o);
            }
        } else {
            ft.add(R.id.overlay, overlayPanels, OverlayFragment.TAG_PANELS);
        }

        addLeftToTransaction(ft);
        if (preferences.spConnected && context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            addRightToTransaction(ft);
        }
        ft.addToBackStack(null).commit();
    }

    public void showRight() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_in);
        Fragment o = fragmentManager.findFragmentByTag(OverlayFragment.TAG_PANELS);
        if (o != null) {
            if (!o.isVisible()) {
                ft.show(o);
            }
        } else {
            ft.add(R.id.overlay, overlayPanels, OverlayFragment.TAG_PANELS);
        }

        addRightToTransaction(ft);
        if (preferences.spConnected && context.getResources().getConfiguration().orientation != Configuration.ORIENTATION_PORTRAIT) {
            addLeftToTransaction(ft);
        }
        ft.addToBackStack(null).commit();
    }

    public void closeLeft() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_left_side_hide, R.animator.slide_left_side_hide)
          .hide(leftPanel);
        if (!(leftPanel.isVisible() && rightPanel.isVisible())) {
            ft.setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out)
              .hide(overlayPanels);
        }
        ft.commit();
    }

    public void closeRight() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.setCustomAnimations(R.animator.slide_right_side_hide, R.animator.slide_right_side_hide)
          .hide(rightPanel);
        if (!(leftPanel.isVisible() && rightPanel.isVisible())) {
            ft.setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out)
              .hide(overlayPanels);
        }
        ft.commit();
    }

    public boolean closePanels() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        int toClose = 0;
        if (leftPanel.isVisible()) {
            toClose++;
            if (leftPanel.selectionConfirmed()) {
                ft.setCustomAnimations(R.animator.slide_left_side_hide, R.animator.slide_left_side_hide);
                ft.hide(leftPanel);
                toClose--;
            } else {
                return false;
            }
        }
        if (rightPanel.isVisible()) {
            toClose++;
            if (rightPanel.selectionConfirmed()) {
                ft.setCustomAnimations(R.animator.slide_right_side_hide, R.animator.slide_right_side_hide);
                ft.hide(rightPanel);
                toClose--;
            } else {
                return false;
            }
        }
        if (overlayPanels.isVisible() && toClose == 0) {
            ft.setCustomAnimations(R.animator.fragment_fade_out, R.animator.fragment_fade_out);
            ft.hide(overlayPanels);
        }
        ft.commit();
        return true;
    }

    public void addPlayer(boolean left, int number, String name, boolean captain) {
        (left ? leftPanel : rightPanel).addRow(number, name, captain);
    }

    public int validatePlayer(boolean left, int number, boolean captain) {
        return (left ? leftPanel : rightPanel).checkNewPlayer(number, captain);
    }

    public boolean deletePlayer(boolean left, int id) {
        return (left ? leftPanel : rightPanel).deleteRow(id);
    }

    public void deletePlayers(int type, boolean left) {
        (left ? leftPanel : rightPanel).clear(type == PANEL_DELETE_TYPE_FULL);
    }

    public void deletePlayers(boolean left) {
        (left ? leftPanel : rightPanel).clear(preferences.spClearDelete);
    }

    public boolean editPlayer(boolean left, int id, int number, String name, boolean captain) {
        return (left ? leftPanel : rightPanel).editRow(id, number, name, captain);
    }

    public SidePanelRow getPlayer(boolean left, int number) {
        SidePanelFragment panel = left? leftPanel : rightPanel ;
        return panel.getPlayer(number);
    }

    private FragmentTransaction addLeftToTransaction(FragmentTransaction ft) {
        ft.setCustomAnimations(R.animator.slide_left_side_show, R.animator.slide_left_side_show);
        Fragment panel = fragmentManager.findFragmentByTag(SidePanelFragment.TAG_LEFT_PANEL);
        if (panel != null) {
            ft.show(panel);
        } else {
            ft.add(R.id.left_panel_full, leftPanel, SidePanelFragment.TAG_LEFT_PANEL);
        }
        return ft;
    }

    private FragmentTransaction addRightToTransaction(FragmentTransaction ft) {
        ft.setCustomAnimations(R.animator.slide_right_side_show, R.animator.slide_right_side_show);
        Fragment panel = fragmentManager.findFragmentByTag(SidePanelFragment.TAG_RIGHT_PANEL);
        if (panel != null) {
            ft.show(panel);
        } else {
            ft.add(R.id.right_panel_full, rightPanel, SidePanelFragment.TAG_RIGHT_PANEL);
        }
        return ft;
    }

    public TreeMap<Integer, SidePanelRow> getLeftPlayers() {
        return leftPanel.getAllPlayers();
    }

    public TreeMap<Integer, SidePanelRow> getRightPlayers() {
        return rightPanel.getAllPlayers();
    }

    public TreeMap<Integer, SidePanelRow> getLeftInactivePlayers() {
        return leftPanel.getInactivePlayers();
    }

    public TreeMap<Integer, SidePanelRow> getRightInactivePlayers() {
        return rightPanel.getInactivePlayers();
    }

    public void saveState() {
        if (leftPanel != null) {
            leftPanel.saveCurrentData();
        }
        if (rightPanel != null) {
            rightPanel.saveCurrentData();
        }
    }

    public void clearSavedState() {
        SidePanelFragment.clearCurrentData();
    }

    public void substitute(boolean left, SidePanelRow in, SidePanelRow out) {
        (left ? leftPanel : rightPanel).substitute(in, out);
    }
}
