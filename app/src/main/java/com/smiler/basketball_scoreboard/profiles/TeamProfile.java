package com.smiler.basketball_scoreboard.profiles;

import com.smiler.basketball_scoreboard.db.Player;
import com.smiler.basketball_scoreboard.db.Team;

import java.util.ArrayList;

import io.realm.Realm;

public class TeamProfile {

    private Team team;
    private String name;
    private ArrayList<Player> players = new ArrayList<>();
    private boolean active;

    public TeamProfile(Team team) {
        this.team = team;
    }

    TeamProfile(String name, ArrayList<Player> players, boolean active) {
        this.name = name;
        this.players = players;
        this.active = active;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isActive() {
        return active;
    }

    public void saveTeam(Realm realm) {
        Number lastId = realm.where(Team.class).max("id");
        final long nextID = lastId != null ? (long) lastId + 1 : 0;

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Team team = realm.createObject(Team.class, nextID);
                team.setName(name).setActive(true);
            }
        });
    }
}