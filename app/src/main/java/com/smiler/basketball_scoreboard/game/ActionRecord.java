package com.smiler.basketball_scoreboard.game;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
public class ActionRecord {
    private long time;
    private int type;
    private int team;
    @Setter
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

    public ActionRecord(long time, int type, int team, int value) {
        this.time = time;
        this.type = type;
        this.team = team;
        number = -1;
        this.value = value;
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