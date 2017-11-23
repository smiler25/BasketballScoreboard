package com.smiler.basketball_scoreboard.db.deprecated;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.RealmController;
import com.smiler.basketball_scoreboard.db.Results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.realm.Realm;

public class DbHelper extends SQLiteOpenHelper {

    private String TAG = "BS-DbHelper";
    private SQLiteDatabase db;
    private static DbHelper instance;

    private static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "scoreboard_results.db";
    private Realm realm;

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) { instance = new DbHelper(context.getApplicationContext()); }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbScheme.ResultsTable.CREATE_TABLE);
        db.execSQL(DbScheme.ResultsPlayersTable.CREATE_TABLE);
        db.execSQL(DbScheme.GameDetailsTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);
        realm = RealmController.with().getRealm();
        switch (oldVersion) {
            case 1:
                // db.execSQL(DbScheme.ResultsPlayersTable.CREATE_TABLE);
                toRealmResults(db);
                break;
            case 2:
                // db.execSQL(DbScheme.GameDetailsTable.CREATE_TABLE);
                toRealmResultsPlayers(db, toRealmResults(db));
                break;
            case 3:
                ArrayList<String> gameIds = toRealmResults(db);
                toRealmGameDetails(db, gameIds);
                toRealmResultsPlayers(db, gameIds);
                break;
        }
    }

    public SQLiteDatabase open() {
        if (db == null || !db.isOpen()) db = getWritableDatabase();
        return db;
    }

    public void dropDb() {
        db.execSQL(DbScheme.ResultsTable.DELETE_TABLE);
        db.execSQL(DbScheme.ResultsPlayersTable.DELETE_TABLE);
        db.execSQL(DbScheme.GameDetailsTable.DELETE_TABLE);
    }

    private ArrayList<String> toRealmResults(SQLiteDatabase db) {
        final ArrayList<String> ids = new ArrayList<>();
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsTable.COLUMN_DATE,
                    DbScheme.ResultsTable.COLUMN_HOME_TEAM,
                    DbScheme.ResultsTable.COLUMN_GUEST_TEAM,
                    DbScheme.ResultsTable.COLUMN_HOME_SCORE,
                    DbScheme.ResultsTable.COLUMN_GUEST_SCORE,
                    DbScheme.ResultsTable.COLUMN_HOME_PERIODS,
                    DbScheme.ResultsTable.COLUMN_GUEST_PERIODS,
                    DbScheme.ResultsTable.COLUMN_REGULAR_PERIODS,
                    DbScheme.ResultsTable.COLUMN_COMPLETE,
                    DbScheme.ResultsTable.COLUMN_SHARE_STRING,
                    DbScheme.ResultsTable._ID,
            };
            final Cursor c = db.query(
                    DbScheme.ResultsTable.TABLE_NAME, columns,
                    null, null, null, null, null
            );

            if (c.getCount() > 0) {
                c.moveToFirst();
                realm.executeTransaction(realm -> {
                    do {
                        Results result = realm.createObject(Results.class, c.getInt(10));
                        result.setDate(new Date(c.getLong(0)))
                              .setFirstTeamName(c.getString(1))
                              .setSecondTeamName(c.getString(2))
                              .setFirstScore(c.getInt(3))
                              .setSecondScore(c.getInt(4))
                              .setFirstPeriods(c.getString(5))
                              .setSecondPeriods(c.getString(6))
                              .setShareString(c.getString(9))
                              .setRegularPeriods(c.getInt(7))
                              .setComplete(c.getInt(8) > 0);
                        ids.add(c.getString(10));
                    } while (c.moveToNext());
                });
            }
            c.close();
        } finally {
//            this.close();
        }
        return ids;
    }

    private void toRealmResultsPlayers(SQLiteDatabase db, ArrayList<String> ids) {
        try {
            String[] columns = new String[] {
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_TEAM,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NUMBER,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_NAME,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_POINTS,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_FOULS,
                    DbScheme.ResultsPlayersTable.COLUMN_PLAYER_CAPTAIN,
                    DbScheme.ResultsPlayersTable.COLUMN_GAME_ID,
            };

            String query = DbScheme.ResultsPlayersTable.COLUMN_GAME_ID +
                    " IN (" + TextUtils.join(",", Collections.nCopies(ids.size(), "?")) + ")";
            final Cursor c = db.query(
                    DbScheme.ResultsPlayersTable.TABLE_NAME,
                    columns, query, ids.toArray(new String[0]), null, null, null
            );

            if (c.getCount() > 0) {
                c.moveToFirst();
                realm.executeTransaction(realm -> {
                    do {
                        Results game = realm.where(Results.class).equalTo("id", c.getInt(5)).findFirst();

                        PlayersResults playersResults = realm.createObject(PlayersResults.class);
                        playersResults.setGame(game)
                                      .setTeam(c.getString(0))
                                      .setNumber(c.getInt(1))
                                      .setName(c.getString(2))
                                      .setPoints(c.getInt(3))
                                      .setFouls(c.getInt(4))
                                      .setCaptain(c.getInt(5) == 1);
                    } while (c.moveToNext());
                });
            }
            c.close();
        } finally {
        }
    }

    private void toRealmGameDetails(SQLiteDatabase db, ArrayList<String> ids) {
        try {
            String[] columns = new String[] {
                    DbScheme.GameDetailsTable.COLUMN_LEADER_CHANGED,
                    DbScheme.GameDetailsTable.COLUMN_TIE,
                    DbScheme.GameDetailsTable.COLUMN_HOME_MAX_LEAD,
                    DbScheme.GameDetailsTable.COLUMN_GUEST_MAX_LEAD,
                    DbScheme.GameDetailsTable.COLUMN_PLAY_BY_PLAY,
                    DbScheme.GameDetailsTable.COLUMN_GAME_ID,
            };

            String query = DbScheme.GameDetailsTable.COLUMN_GAME_ID +
                    " IN (" + TextUtils.join(",", Collections.nCopies(ids.size(), "?")) + ")";
            final Cursor c = db.query(
                    DbScheme.GameDetailsTable.TABLE_NAME,
                    columns, query, ids.toArray(new String[0]), null, null, null
            );
            if (c.getCount() > 0) {
                c.moveToFirst();
                realm.executeTransaction(realm -> {
                    do {
                        Results game = realm.where(Results.class).equalTo("id", c.getInt(5)).findFirst();
                        GameDetails details = realm.createObject(GameDetails.class);
                        details.setLeadChanged(c.getInt(0))
                               .setHomeMaxLead(c.getInt(2))
                               .setGuestMaxLead(c.getInt(3))
                               .setTie(c.getInt(1));
                        if (c.getString(4) != null) {
                            details.setPlayByPlay(c.getString(4));
                        }
                        game.setDetails(details);
                    } while (c.moveToNext());
                });
            }
            c.close();
        } finally {
        }
    }
}