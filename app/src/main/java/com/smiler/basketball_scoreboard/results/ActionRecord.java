package com.smiler.basketball_scoreboard.results;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionRecord {

    private long time;
    private int type;
    private int team;
    private int number;
    private int value;

    ActionRecord(JSONObject obj) {
         try {
             this.time = obj.getLong("time");
             this.type = obj.getInt("type");
             this.team = obj.getInt("team");
             this.number = obj.getInt("number");
             this.value = obj.getInt("value");
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

    ActionRecord(long time, int type, int team, int number, int value) {
        this.time = time;
        this.type = type;
        this.team = team;
        this.number = number;
        this.value = value;
    }

    ActionRecord(long time, int type, int team, int value) {
        this.time = time;
        this.type = type;
        this.team = team;
        this.number = -1;
        this.value = value;
    }

    public int getNumber() {
        return this.number;
    }

    public int getTeam() {
        return this.team;
    }

    public long getTime() {
        return this.time;
    }

    public int getType() {
        return this.type;
    }

    public int getValue() {
        return this.value;
    }

    public void setNumber(int value) {
        this.number = value;
    }

    @Override
    public String toString() {
//        return String.format(ACTION_FORMAT, type, team, value);
        return getJson().toString();
    }

    JSONObject getJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("time", time);
            obj.put("type", type);
            obj.put("team", team);
            obj.put("value", value);
            obj.put("number", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }
}