package com.smiler.basketball_scoreboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.smiler.basketball_scoreboard.db.GameDetails;
import com.smiler.basketball_scoreboard.db.PlayersResults;
import com.smiler.basketball_scoreboard.db.Results;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DbHelper extends SQLiteOpenHelper {

    private String TAG = "BS-DbHelper";
    private SQLiteDatabase db;
    private static DbHelper instance;

    private static final int DATABASE_VERSION = 13;
    private static final String DATABASE_NAME = "scoreboard_results.db";
    private Realm realm;
    private RealmConfiguration config;
    private Context context;

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) { instance = new DbHelper(context.getApplicationContext()); }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        Realm.init(this.context);
        config = new RealmConfiguration.Builder()
                .name("main.realm")
//                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();

        switch (oldVersion) {
            case 1:
//                db.execSQL(DbScheme.ResultsPlayersTable.CREATE_TABLE);
                toRealmResults(db);
                break;
            case 2:
//                db.execSQL(DbScheme.GameDetailsTable.CREATE_TABLE);
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
        if (db == null || !db.isOpen()) db = this.getWritableDatabase();
        return db;
    }

    public void dropDb() {
        db.execSQL(DbScheme.ResultsTable.DELETE_TABLE);
        db.execSQL(DbScheme.ResultsPlayersTable.DELETE_TABLE);
        db.execSQL(DbScheme.GameDetailsTable.DELETE_TABLE);
    }

    public int delete(String[] ids) {
        this.open();
        String stringIds = new String(new char[ids.length - 1]).replace("\0", "?, ");
        int deleted = db.delete(DbScheme.ResultsTable.TABLE_NAME,
                DbScheme.ResultsTable._ID + " IN (" + stringIds + "?)",
                ids);
        db.delete(DbScheme.ResultsPlayersTable.TABLE_NAME,
                DbScheme.ResultsPlayersTable.COLUMN_GAME_ID + " IN (" + stringIds + "?)",
                ids);
        return deleted;
    }

    public String getShareString(int id) {
        this.open();
        String[] columns = new String[] {
                DbScheme.ResultsTable.COLUMN_DATE,
                DbScheme.ResultsTable.COLUMN_SHARE_STRING
        };
        String query = "_id = ?";
        Cursor c = db.query(
                DbScheme.ResultsTable.TABLE_NAME,
                columns,
                query,
                new String[]{Integer.toString(id)},
                null,   // rows group
                null,   // filter by row groups
                null    // sort order
        );

        String result = "";
        if (c.getCount() == 1) {
            c.moveToFirst();
            DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
            String dateStr = dateFormat.format(new Date(c.getLong(0)));
            result = String.format("%1$s (%2$s)", c.getString(1), dateStr);
        }
        c.close();
        return result;
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
                realm = Realm.getInstance(config);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        do {
                            Results result = realm.createObject(Results.class);
                            result.setId(c.getInt(10))
                                  .setDate(new Date(c.getLong(0)))
                                  .setHomeTeam(c.getString(1))
                                  .setGuestTeam(c.getString(2))
                                  .setHomeScore(c.getInt(3))
                                  .setGuestScore(c.getInt(4))
                                  .setHomePeriods(c.getString(5))
                                  .setGuestPeriods(c.getString(6))
                                  .setShareString(c.getString(9))
                                  .setRegularPeriods(c.getInt(7))
                                  .setComplete((c.getInt(8) > 0));
                            ids.add(c.getString(10));
                        } while (c.moveToNext());
                    }
                });
                realm.close();
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
                realm = Realm.getInstance(config);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        do {
                            Results game = realm.where(Results.class).equalTo("id", c.getInt(5)).findFirst();

                            PlayersResults playersResults = realm.createObject(PlayersResults.class);
                            playersResults.setGame(game)
                                          .setPlayerTeam(c.getString(0))
                                          .setPlayerNumber(c.getInt(1))
                                          .setPlayerName(c.getString(2))
                                          .setPlayerPoints(c.getInt(3))
                                          .setPlayerFouls(c.getInt(4))
                                          .setCaptain(c.getInt(5) == 1);
                            System.out.println("game = " + game);
                            System.out.println("playersResults = " + playersResults);
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                        } while (c.moveToNext());
                    }
                });
                realm.close();
            }
            c.close();
        } finally {
        }
    }

    private void toRealmGameDetails(SQLiteDatabase db, ArrayList<String> ids) {
        System.out.println("===========toRealmGameDetails=============");
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
                realm = Realm.getInstance(config);
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        do {
                            System.out.println(
                                    "gameid = " + c.getInt(5) + ": " + c.getInt(0) + " - " + c.getInt(1) + " - " +
                                    c.getInt(2) + " - " + c.getInt(3) + " - " +
                                    c.getString(4));
                            Results game = realm.where(Results.class).equalTo("id", c.getInt(5)).findFirst();
                            System.out.println("game = " + game);
                            GameDetails details = realm.createObject(GameDetails.class);
                            details.setLeadChanged(c.getInt(0))
                                   .setHomeMaxLead(c.getInt(2))
                                   .setGuestMaxLead(c.getInt(3))
                                   .setTie(c.getInt(1));
                            if (c.getString(4) != null) {
                                details.setPlayByPlay(c.getString(4));
                            }
                            game.setDetails(details);
                            System.out.println("game = " + game);
                            System.out.println("details = " + details);
                            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

                        } while (c.moveToNext());
                    }
                });
                realm.close();
            }
            c.close();
        } finally {
        }
    }
}