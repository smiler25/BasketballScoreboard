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

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "scoreboard_results.db";
    private static final String COMMA = ",";
    private static final String INT_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String TEXT_TYPE = " TEXT";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + DbScheme.ResultsTable.TABLE_NAME + " (" +
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

    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + DbScheme.ResultsTable.TABLE_NAME;

    public static synchronized DbHelper getInstance(Context context) {

        if (instance == null) {
            instance = new DbHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public int delete(String[] ids) {
        this.open();
        return db.delete(DbScheme.ResultsTable.TABLE_NAME,
                DbScheme.ResultsTable._ID + " IN (" + new String(new char[ids.length - 1]).replace("\0", "?, ") + "?)",
                ids);
    }
    public String getShareString(int id) {
        this.open();
        String[] columns = new String[] {
                DbScheme.ResultsTable.COLUMN_NAME_DATE,
                DbScheme.ResultsTable.COLUMN_NAME_SHARE_STRING
        };
        String query = "_id = ?";
        Cursor c = db.query(DbScheme.ResultsTable.TABLE_NAME,
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

    public SQLiteDatabase open() {
        if (db == null || !db.isOpen()) db = this.getWritableDatabase();
        return db;
    }

    public void dropDb() {
        db.execSQL(TABLE_DELETE);
    }

}