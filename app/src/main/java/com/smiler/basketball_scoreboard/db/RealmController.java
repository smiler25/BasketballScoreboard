package com.smiler.basketball_scoreboard.db;

import com.smiler.basketball_scoreboard.exceptions.CaptainAlreadyAssignedException;
import com.smiler.basketball_scoreboard.panels.SidePanelRow;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmController {

    private static RealmController instance;
    protected final Realm realm;
    public static String realmName = "main.realm";
    private static final int dbVersion = 3;

    protected RealmController() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .name(realmName)
                .schemaVersion(dbVersion)
                .migration(new Migration())
                .build();
        realm = Realm.getInstance(realmConfig);
    }

    public static RealmController with() {
        if (instance == null) {
            instance = new RealmController();
        }
        return instance;
    }

    public static void close() {
        if (instance != null) {
            instance.realm.close();
            instance = null;
        }
    }

    public Realm getRealm() {
        return realm;
    }

    public RealmResults<Results> getResults() {
        return realm.where(Results.class).notEqualTo("id", -1).findAll().sort("date", Sort.DESCENDING);
    }

    public Results getResult(int id) {
        return realm.where(Results.class).equalTo("id", id).findFirst();
    }

    public Results getTmpResult() {
        Results res = getResult(-1);
        if (res != null) {
            return res;
        }
        return createTmpResult();
    }

    private Results createTmpResult() {
        realm.executeTransaction(realm -> realm.createObject(Results.class, -1));
        return getResult(-1);
    }

    public RealmResults<PlayersResults> getPlayers(int gameId) {
        return realm.where(PlayersResults.class).equalTo("game.id", gameId).findAll();
    }

    public RealmResults<PlayersResults> getPlayers(int gameId, String team) {
        return realm.where(PlayersResults.class).equalTo("game.id", gameId).equalTo("team", team).findAll();
    }

    public boolean deleteResult(final int id) {
        final Results result = realm.where(Results.class).equalTo("id", id).findFirst();
        if (result == null){
            return false;
        }
        realm.executeTransaction(realm -> result.deleteFromRealm());
        return true;
    }

    public void deleteTmpPlayers(String team) {
        final RealmResults<PlayersResults> data = realm.where(PlayersResults.class)
                .equalTo("team", team)
                .beginGroup()
                    .isNull("game")
                    .or()
                    .equalTo("game.id", -1)
                .endGroup()
                .findAll();
        realm.executeTransaction(realm -> data.deleteAllFromRealm());
    }

    public void deleteResults(final Integer[] ids) {
        final RealmResults<Results> results = realm.where(Results.class).in("id", ids).findAll();
        realm.executeTransaction(realm -> results.deleteAllFromRealm());
    }

    public void deleteTeams(final Integer[] ids) {
        final RealmResults<Team> teams = realm.where(Team.class).in("id", ids).findAll();
        realm.executeTransaction(realm -> teams.deleteAllFromRealm());
    }

    public void deletePlayerResults(final int gameId) {
        final RealmResults<Results> results = realm.where(Results.class).equalTo("id", gameId).findAll();
        realm.executeTransaction(realm -> results.deleteAllFromRealm());
    }

    public String getShareString(int id) {
        return realm.where(Results.class).equalTo("id", id).findFirst().getShareString();
    }

    public RealmResults<Team> getTeams() {
        return realm.where(Team.class).findAll().sort("name", Sort.ASCENDING);
    }

    public Team getTeam(int id) {
        return realm.where(Team.class).equalTo("id", id).findFirst();
    }

    public int createTeam(final String name, final boolean active) {
        Number lastId = realm.where(Team.class).max("id");
        final long nextId = lastId != null ? (long) lastId + 1 : 0;
        realm.executeTransaction(realm -> {
            Team result = realm.createObject(Team.class, nextId);
            result.setName(name)
                  .setActive(active);
        });
        return (int) nextId;
    }

    public Team createTeamAndGet(String name, boolean active) {
        return getTeam(createTeam(name, active));
    }

    public Player createPlayer(int teamId, final int number, final String name,
                               final boolean isCaptain, final boolean replaceCaptain) throws CaptainAlreadyAssignedException {
        final Team team = realm.where(Team.class).equalTo("id", teamId).findFirst();
        if (isCaptain) {
            Player captain = team.getCaptain();
            if (captain != null) {
                if (!replaceCaptain) {
                    throw new CaptainAlreadyAssignedException();
                }
            }
        }
        Number lastId = realm.where(Player.class).max("id");
        final long nextId = lastId != null ? (long) lastId + 1 : 0;
        realm.executeTransaction(realm -> {
            if (replaceCaptain) {
                team.cancelCaptain();
            }
            Player player = realm.createObject(Player.class, nextId);
            player.setName(name).setNumber(number).setCaptain(isCaptain).setTeam(team);
            team.addPlayer(player);
        });
        return realm.where(Player.class).equalTo("id", nextId).findFirst();
    }

    // TODO SidePanelRow -> PlayerEntry
    public void createPlayers(final Team team, final Iterable<SidePanelRow> players) {
        Number lastId = realm.where(Player.class).max("id");
        final long nextId = lastId != null ? (long) lastId + 1 : 0;
        realm.executeTransaction(realm -> {
            long playerId = nextId;

            for (SidePanelRow entry : players) {
                Player player = realm.createObject(Player.class, playerId);
                player.setName(entry.getName())
                        .setNumber(entry.getNumber())
                        .setCaptain(entry.isCaptain())
                        .setTeam(team);
                team.addPlayer(player);
                playerId++;
            }
        });
    }

    public void editPlayer(int playerId, final int number, final String name, final boolean captain) {
        final Player player = realm.where(Player.class).equalTo("id", playerId).findFirst();
        realm.executeTransaction(realm -> player.setName(name).setNumber(number).setCaptain(captain));
    }

    public void deletePlayer(int playerId) {
        final Player player = realm.where(Player.class).equalTo("id", playerId).findFirst();
        realm.executeTransaction(realm -> player.deleteFromRealm());
    }

    public Player getPlayer(int id) {
        return realm.where(Player.class).equalTo("id", id).findFirst();
    }

}