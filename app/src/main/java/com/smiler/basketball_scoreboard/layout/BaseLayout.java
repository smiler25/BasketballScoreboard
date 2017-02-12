package com.smiler.basketball_scoreboard.layout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeSet;

public class BaseLayout extends LinearLayout {
    public BaseLayout(Context context) {
        super(context);
    }

    public void handleArrowsVisibility() {
    }

    public void hideArrows() {
    }

    public void showArrows() {
    }

    public void zeroState() {
    }

    public void switchSides() {
    }

    public void setBlockLongClick(boolean state) {
    }

    public void setColors() {
    }

    public void setColor(TextView v, int color) {
        v.setTextColor(color);
    }

    public void setColorRed(TextView v) {
        setColor(v, getResources().getColor(R.color.red));
    }

    public void setColorGreen(TextView v) {
        setColor(v, getResources().getColor(R.color.green));
    }


    // scores
    public void setScores(CharSequence home, CharSequence guest) {
    }

    public void setGuestScore(int value) {
    }

    public void setHomeScore(int value) {
    }

    public void handleScoresSize() {
    }


    // fouls
    public void setFouls(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
    }

    public void setHomeFoul(String value, boolean limit) {
    }

    public void setGuestFoul(String value, boolean limit) {
    }

    public void nullFouls() {
    }

    public void nullHomeFouls() {
    }

    public void nullGuestFouls() {
    }

    public void setHomeFoulsGreen() {
    }

    public void setGuestFoulsGreen() {
    }


    // timeouts
    public void setTimeouts(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
    }

    public void setTimeouts20(CharSequence hValue, CharSequence gValue, int hColor, int gColor) {
    }

    public void setHomeTimeouts(String value, boolean limit) {
    }

    public void setGuestTimeouts(String value, boolean limit) {
    }

    public void setHomeTimeouts20(String value, boolean limit) {
    }

    public void setGuestTimeouts20(String value, boolean limit) {
    }

    public void setHomeTimeoutsGreen() {
    }

    public void setGuestTimeoutsGreen() {
    }

    public void setHomeTimeouts20Green() {
    }

    public void setGuestTimeouts20Green() {
    }

    public void nullTimeouts() {
    }

    public void nullTimeouts20() {
    }

    public void nullHomeTimeouts(String value) {
    }

    public void nullGuestTimeouts(String value) {
    }

    public void nullHomeTimeouts20(String value) {
    }

    public void nullGuestTimeouts20(String value) {
    }


    // possession
    public void toggleArrow(boolean left) {
    }

    public void clearPossession() {
    }


    // times
    public void setMainTimeFormat(SimpleDateFormat value) {
    }

    public void setMainTimeText(long millis) {
    }

    public void setShotTimeText(long value) {
    }

    public void setShotTimeSwitchText(long value) {
    }

    public void hideShotTime() {
    }

    public void showShotTime() {
    }

    public void blinkShotTime() {

    }

    public boolean shotTimeVisible() {
        return false;
    }

    public boolean shotTimeSwitchVisible() {
        return false;
    }

    public void hideShotTimeSwitch() {
    }

    public void showShotTimeSwitch() {
    }


    // period
    public void setPeriod(String value, boolean regular) {
    }


    // names
    public void setTeamNames(CharSequence home, CharSequence guest) {
    }

    public void setHomeName(CharSequence value) {
    }

    public void setGuestName(CharSequence value) {
    }


    // players
    private ArrayList<View> getAllButtons(ViewGroup group) {
        return null;
    }

    private void attachLeftButton(View button) {
    }

    private void attachRightButton(View button) {
    }

    public boolean playersButtonsInitiated() {
        return false;
    }

    public void hidePlayersButtons() {
    }

    public void showPlayersButtons() {
    }

    public void setPlayersButtons(boolean left, TreeSet<SidePanelRow> rows) {
    }

    public void setPlayersButtonsEmpty(boolean left) {
    }

    public void updatePlayerButton(boolean left, int id, int number) {
    }

    public void clearPlayerButton(boolean left, int id) {
    }

    public Button getSelectedPlayerButton() {
        return null;
    }

}
