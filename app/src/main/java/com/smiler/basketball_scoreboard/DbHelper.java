package com.smiler.basketball_scoreboard;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.util.Date;

public class DbHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private static DbHelper instance;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "scoreboard_results.db";
    private static final String COMMA = ",";
    private static final String INT_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String TEXT_TYPE = " TEXT";

    private static final String TABLE_CREATE_GAME =
            "CREATE TABLE " + DbScheme.ResultsTable.TABLE_NAME_GAME + " (" +
                    DbScheme.ResultsTable._ID + " INTEGER PRIMARY KEY," +
                    DbScheme.ResultsTable.COLUMN_NAME_DATE + LONG_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_TEAM + TEXT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_TEAM + TEXT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_SCORE + INT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_SCORE + INT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_HOME_PERIODS + TEXT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_GUEST_PERIODS + TEXT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_COMPLETE + INT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_REGULAR_PERIODS + INT_TYPE + COMMA +
                    DbScheme.ResultsTable.COLUMN_NAME_SHARE_STRING + TEXT_TYPE +
                    " )";

    private static final String TABLE_CREATE_GAME_PLAYERS =
            "CREATE TABLE " + DbScheme.ResultsPlayersTable.TABLE_NAME_GAME_PLAYERS + " (" +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_GAME_ID + " INTEGER, " +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_TEAM + TEXT_TYPE + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_NUMBER + INT_TYPE + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_NAME + TEXT_TYPE + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_POINTS + INT_TYPE + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_FOULS + INT_TYPE + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_CAPTAIN + INT_TYPE + COMMA +
                    "PRIMARY KEY (" + DbScheme.ResultsPlayersTable.COLUMN_NAME_GAME_ID + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_NUMBER + COMMA +
                    DbScheme.ResultsPlayersTable.COLUMN_NAME_PLAYER_TEAM + "))";

    public static final String TABLE_GAME_DELETE = "DROP TABLE IF EXISTS " + DbScheme.ResultsTable.TABLE_NAME_GAME;
    public static final String TABLE_GAME_PLAYERS_DELETE = "DROP TABLE IF EXISTS " + DbScheme.ResultsPlayersTable.TABLE_NAME_GAME_PLAYERS;

    public static synchronized DbHelper getInstance(Context context) {
        if (instance == null) { instance = new DbHelper(context.getApplicationContext()); }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_GAME);
        db.execSQL(TABLE_CREATE_GAME_PLAYERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(TABLE_CREATE_GAME_PLAYERS);
        }
    }

    public int delete(String[] ids) {
        this.open();
        String stringIds = new String(new char[ids.length - 1]).replace("\0", "?, ");
        int deleted = db.delete(DbScheme.ResultsTable.TABLE_NAME_GAME,
                DbScheme.ResultsTable._ID + " IN (" + stringIds + "?)",
                ids);
        db.delete(DbScheme.ResultsPlayersTable.TABLE_NAME_GAME_PLAYERS,
                DbScheme.ResultsPlayersTable._ID + " IN (" + stringIds + "?)",
                ids);
        return deleted;
    }

    public String getShareString(int id) {
        this.open();
        String[] columns = new String[] {
                DbScheme.ResultsTable.COLUMN_NAME_DATE,
                DbScheme.ResultsTable.COLUMN_NAME_SHARE_STRING
        };
        String query = "_id = ?";
        Cursor c = db.query(
                DbScheme.ResultsTable.TABLE_NAME_GAME,
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

//    public String getShareStringWithTopScorers(int id) {
//    }

    public SQLiteDatabase open() {
        if (db == null || !db.isOpen()) db = this.getWritableDatabase();
        return db;
    }

    public void dropDb() {
        db.execSQL(TABLE_GAME_DELETE);
        db.execSQL(TABLE_GAME_PLAYERS_DELETE);
    }

}