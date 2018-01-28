package com.smiler.basketball_scoreboard.results;

import com.smiler.basketball_scoreboard.game.Actions;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class ProtocolRecord {
    private Actions action;
    private int value;
    private int team;
    private int playerNumber;
    private short period;
    private long periodTime;
    private long gameTime;

    public JSONObject getJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", action.name());
            obj.put("value", value);
            obj.put("team", team);
            obj.put("playerNumber", playerNumber);
            obj.put("period", period);
            obj.put("periodTime", periodTime);
            obj.put("gameTime", gameTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    ProtocolRecord(JSONObject obj) {
        try {
            action = Actions.valueOf(obj.getString("action"));
            team = obj.getInt("team");
            value = obj.getInt("value");
            playerNumber = obj.getInt("playerNumber");
            period = (short) obj.getInt("period");
            periodTime = obj.getInt("periodTime");
            gameTime = obj.getInt("gameTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}