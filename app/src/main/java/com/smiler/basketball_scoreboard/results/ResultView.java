package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;
import com.smiler.basketball_scoreboard.models.Player;
import com.smiler.basketball_scoreboard.models.ResultGameDetails;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import io.realm.RealmResults;

class ResultView extends LinearLayout {
    public static String TAG = "BS-ResultView";
    private TextView title;
    private int itemId;
    private Results gameResult;
    private RealmResults<PlayersResults> gamePlayers;
    private RealmController realmController;

    ResultView(Context context, int itemId) {
        super(context);
        realmController = RealmController.with();
        if(!isInEditMode()) {
            this.itemId = itemId;
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.result_view, this);
        title = (TextView)findViewById(R.id.result_view_title);

        getData();
        Result result = getResult();
        TreeMap<String, ArrayList<Player>> playersData = getPlayersContent();
        TreeMap<String, Integer> detailData = getDetailContent();
        JSONArray playByPlay = getPlayByPlay();

        long date = result.getDate();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT);
        String dateStr = dateFormat.format(new Date(date));
        title.setText(dateStr);

        LinearLayout layout = (LinearLayout) findViewById(R.id.result_view);
        layout.addView(new ResultViewScoreTable(getContext(), result));

        if (!detailData.isEmpty()) {
            layout.addView(new ResultViewDetail(getContext(), detailData));
        }

        if (!playersData.isEmpty()) {
            layout.addView(new ResultViewBoxscore(getContext(), playersData));
        }

        if (playByPlay != null && playByPlay.length() > 0) {
            SparseArray<Player> hPlayers = new SparseArray<>();
            SparseArray<Player> gPlayers = new SparseArray<>();
            if (!playersData.isEmpty()) {
                for (Map.Entry<String, ArrayList<Player>> entry : playersData.entrySet()) {
                    SparseArray<Player> players = entry.getKey().equals(result.getHomeName()) ? hPlayers : gPlayers;
                    for (Player player : entry.getValue()) {
                        players.put(player.getNumber(), player);
                    }
                }
            }
            layout.addView(new ResultViewPlayByPlay(getContext(), playByPlay, hPlayers, gPlayers, result.getHomeName(), result.getGuestName()));
        }
    }

    public void setTitle(String value) {
        title.setText(value);
    }

    private void getData() {
        gameResult = realmController.getResult(itemId);
        gamePlayers = realmController.getPlayers(itemId);
    }

    private Result getResult() {
        ArrayList<Integer> hPeriods = new ArrayList<>();
        ArrayList<Integer> gPeriods = new ArrayList<>();
        String hPeriodsString = gameResult.getHomePeriods();
        String gPeriodsString = gameResult.getGuestPeriods();
        if (hPeriodsString != null && !hPeriodsString.equals("")) {
            for (String periodString : hPeriodsString.split("-")) {
                try {
                    hPeriods.add(Integer.parseInt(periodString));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getResult (h): " + hPeriodsString + " - " + periodString);
                }
            }
        }
        if (gPeriodsString != null && !gPeriodsString.equals("")) {
            for (String periodString : gPeriodsString.split("-")) {
                try {
                    gPeriods.add(Integer.parseInt(periodString));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getResult (g): " + gPeriodsString + " - " + periodString);
                }
            }
        }

        return new Result(
                gameResult.getHomeTeam(),
                gameResult.getGuestTeam(),
                gameResult.getHomeScore(),
                gameResult.getGuestScore(),
                hPeriods,
                gPeriods,
                gameResult.getComplete(),
                gameResult.getDate().getTime(),
                gameResult.getRegularPeriods());
    }

    private TreeMap<String, ArrayList<Player>> getPlayersContent() {
        TreeMap<String, ArrayList<Player>> result = new TreeMap<>();
        for (PlayersResults r : gamePlayers) {
            String team = r.getTeam();
            if (result.get(team) == null) { result.put(team, new ArrayList<Player>()); }
            result.get(team).add(new Player(
                    r.getNumber(),
                    r.getPlayerName(),
                    r.getPoints(),
                    r.getFouls(),
                    r.getCaptain()));
        }
        return result;
    }

    private TreeMap<String, Integer> getDetailContent() {
        GameDetails details = gameResult.getDetails();
        TreeMap<String, Integer> result = new TreeMap<>();
        if (details != null) {
            result.put(ResultGameDetails.LEADER_CHANGED, details.getLeadChanged());
            result.put(ResultGameDetails.TIE, details.getHomeMaxLead());
            result.put(ResultGameDetails.HOME_MAX_LEAD, details.getGuestMaxLead());
            result.put(ResultGameDetails.GUEST_MAX_LEAD, details.getTie());
        }
        return result;
    }

    private JSONArray getPlayByPlay() {
        JSONArray result = null;
        GameDetails details = gameResult.getDetails();
        if (details != null) {
            try {
                String val = details.getPlayByPlay();
                if (val != null) {
                    result = new JSONArray(val);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
