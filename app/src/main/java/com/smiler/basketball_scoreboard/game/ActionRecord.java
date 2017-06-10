package com.smiler.basketball_scoreboard.game;

import org.json.JSONException;
import org.json.JSONObject;

public class ActionRecord {

    private long time;
    private int type;
    private int team;
    private int number;
    private int value;

    public ActionRecord(JSONObject obj) {
         try {
             time = obj.getLong("time");
             type = obj.getInt("type");
             team = obj.getInt("team");
             number = obj.getInt("number");
             value = obj.getInt("value");
         } catch (JSONException e) {
             e.printStackTrace();
         }
     }

    public ActionRecord(long time, int type, int team, int number, int value) {
        this.time = time;
        this.type = type;
        this.team = team;
        this.number = number;
        this.value = value;
    }

    public ActionRecord(long time, int type, int team, int value) {
        this.time = time;
        this.type = type;
        this.team = team;
        number = -1;
        this.value = value;
    }

    public int getNumber() {
        return number;
    }

    public int getTeam() {
        return team;
    }

    public long getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public void setNumber(int value) {
        number = value;
    }

    @Override
    public String toString() {
//        return String.format(ACTION_FORMAT, type, team, value);
        return getJson().toString();
    }

    public JSONObject getJson() {
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