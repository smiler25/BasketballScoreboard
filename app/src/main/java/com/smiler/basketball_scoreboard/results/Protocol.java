package com.smiler.basketball_scoreboard.results;

import com.smiler.basketball_scoreboard.game.Actions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import lombok.Data;

@Data
public class Protocol {
    private String homeName, guestName;
    private ArrayList<ArrayList<ProtocolRecord>> periods = new ArrayList<>();
    private ArrayList<ProtocolRecord> currentPeriod = new ArrayList<>();
    private long date;

    public Protocol(String homeName, String guestName) {
        this.homeName = homeName;
        this.guestName = guestName;
    }

    public Protocol(String protocolString) {
        try {
            JSONArray periodData;
            JSONArray periodsArray = new JSONArray(protocolString);
            int periodsSize = periodsArray.length();
            for (int i = 0; i < periodsSize; i++) {
                periodData = periodsArray.getJSONArray(i);
                currentPeriod.clear();
                for (int y = 0; y < periodData.length(); y++) {
                    currentPeriod.add(new ProtocolRecord(periodData.getJSONObject(y)));
                }
                periods.add(new ArrayList<>(currentPeriod));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addRecord(Actions action, int value, int team, int playerNumber, short period, long periodTime, long gameTime) {
        currentPeriod.add(new ProtocolRecord(action, value, team, playerNumber, period, periodTime, gameTime));
    }

    public void deleteLastRecord() {
        if (!currentPeriod.isEmpty()) {
            currentPeriod.remove(currentPeriod.size() - 1);
        }
    }

    public void completePeriod() {
        periods.add(new ArrayList<>(currentPeriod));
        currentPeriod.clear();
    }

    public String getString() {
        if (periods.isEmpty()) {
            return null;
        }
        JSONArray periodsArray = new JSONArray();
        for (ArrayList<ProtocolRecord> period : periods) {
            JSONArray array = new JSONArray();
            for (ProtocolRecord record : period) {
                array.put(record.getJson());
            }
            periodsArray.put(array);
        }
        return periodsArray.toString();
    }
}