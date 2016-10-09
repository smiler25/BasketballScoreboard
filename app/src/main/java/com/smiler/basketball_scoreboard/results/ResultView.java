package com.smiler.basketball_scoreboard.results;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smiler.basketball_scoreboard.R;
import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.PlayersResults;
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

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

class ResultView extends LinearLayout {
    public static String TAG = "BS-ResultView";
    private TextView title;
    private int sqlId;
    private Results game;
    private RealmConfiguration realmConfig;

    ResultView(Context context, int sqlId) {
        super(context);
        Realm.init(context);
        realmConfig = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();

        if(!isInEditMode()) {
            this.sqlId = sqlId;
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.result_view, this);
        title = (TextView)findViewById(R.id.result_view_title);

        Result result = getResult();
        System.out.println("game = " + game);
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
                    SparseArray<Player> players = (entry.getKey().equals(result.getHomeName())) ? hPlayers : gPlayers;
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

    private Result getResult() {
        Realm realm = Realm.getInstance(realmConfig);
        Results realmResults = realm.where(Results.class).equalTo("id", sqlId).findFirst();

        ArrayList<Integer> hPeriods = new ArrayList<>();
        ArrayList<Integer> gPeriods = new ArrayList<>();
        String hPeriodsString = realmResults.getHomePeriods();
        String gPeriodsString = realmResults.getGuestPeriods();
        if (!hPeriodsString.equals("")) {
            for (String periodString : hPeriodsString.split("-")) {
                try {
                    hPeriods.add(Integer.parseInt(periodString));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getResult (h): " + hPeriodsString + " - " + periodString);
                }
            }
        }
        if (!gPeriodsString.equals("")) {
            for (String periodString : gPeriodsString.split("-")) {
                try {
                    gPeriods.add(Integer.parseInt(periodString));
                } catch (NumberFormatException e) {
                    Log.d(TAG, "getResult (g): " + gPeriodsString + " - " + periodString);
                }
            }
        }

        Result res = new Result(
                realmResults.getHomeTeam(),
                realmResults.getGuestTeam(),
                realmResults.getHomeScore(),
                realmResults.getGuestScore(),
                hPeriods,
                gPeriods,
                realmResults.getComplete(),
                realmResults.getDate().getTime(),
                realmResults.getRegularPeriods());

        game = realmResults;
        realm.close();
        return res;
    }

    private TreeMap<String, ArrayList<Player>> getPlayersContent() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();
        Realm realm = Realm.getInstance(config);
        RealmResults<PlayersResults> results = realm.where(PlayersResults.class)
                .equalTo("game.id", sqlId)
                .findAll();

        TreeMap<String, ArrayList<Player>> result = new TreeMap<>();

        for (PlayersResults r : results) {
            String team = r.getPlayerTeam();
            if (result.get(team) == null) { result.put(team, new ArrayList<Player>()); }
            result.get(team).add(new Player(
                    r.getPlayerNumber(),
                    r.getPlayerName(),
                    r.getPlayerPoints(),
                    r.getPlayerFouls(),
                    r.getCaptain()));
        }
        realm.close();
        return result;
    }

    private TreeMap<String, Integer> getDetailContent() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();
        Realm realm = Realm.getInstance(config);
        GameDetails details = realm.where(GameDetails.class).equalTo("game.id", sqlId).findFirst();
        TreeMap<String, Integer> result = new TreeMap<>();
        result.put(ResultGameDetails.LEADER_CHANGED, details.getLeadChanged());
        result.put(ResultGameDetails.TIE, details.getHomeMaxLead());
        result.put(ResultGameDetails.HOME_MAX_LEAD, details.getGuestMaxLead());
        result.put(ResultGameDetails.GUEST_MAX_LEAD, details.getTie());
        realm.close();
        return result;
    }

    private JSONArray getPlayByPlay() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("main.realm")
                .schemaVersion(0)
                .build();
        Realm realm = Realm.getInstance(config);
        GameDetails details = realm.where(GameDetails.class).equalTo("game.id", sqlId).findFirst();
        JSONArray result = null;
        try {
            String val = details.getPlayByPlay();
            if (val != null) {
                result = new JSONArray(val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        realm.close();
        return result;
    }
}
