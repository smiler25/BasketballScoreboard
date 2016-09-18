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

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "scoreboard_results.db";

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
        switch (oldVersion) {
            case 1:
                db.execSQL(DbScheme.ResultsPlayersTable.CREATE_TABLE);
            case 2:
                db.execSQL(DbScheme.GameDetailsTable.CREATE_TABLE);
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

//    public String getShareStringWithTopScorers(int id) {
//    }

}