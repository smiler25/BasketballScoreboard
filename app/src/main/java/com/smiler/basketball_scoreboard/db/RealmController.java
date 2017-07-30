package com.smiler.basketball_scoreboard.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;


public class RealmController {

    private static RealmController instance;
    private final Realm realm;
    public static String realmName = "main.realm";
    private final int dbVersion = 1;

    private RealmController() {
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

//    public void deleteTmpResult() {
//        deleteResult(-1);
//    }

    private Results createTmpResult() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.createObject(Results.class, -1);
            }
        });
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                result.deleteFromRealm();
            }
        });
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
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                data.deleteAllFromRealm();
            }
        });
    }

    public void deleteResults(final Integer[] ids) {
        final RealmResults<Results> results = realm.where(Results.class).in("id", ids).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    public void deleteTeams(final Integer[] ids) {
        final RealmResults<Team> teams = realm.where(Team.class).in("id", ids).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                teams.deleteAllFromRealm();
            }
        });
    }

    public void deletePlayerResults(final int gameId) {
        final RealmResults<Results> results = realm.where(Results.class).equalTo("id", gameId).findAll();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                results.deleteAllFromRealm();
            }
        });
    }

    public String getShareString(int id) {
        return realm.where(Results.class).equalTo("id", id).findFirst().getShareString();
    }

    public RealmResults<Team> getTeams() {
        return realm.where(Team.class).findAll().sort("name", Sort.ASCENDING);
    }

    public Team getTeam(String name) {
        return realm.where(Team.class).equalTo("name", name).findFirst();
    }

}