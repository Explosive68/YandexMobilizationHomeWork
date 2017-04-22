package ru.illarionovroman.yandexmobilizationhomework.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract.*;

/**
 * Application specific extension of SQLiteOpenHelper
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mobilization.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_HISTORY_TABLE =
                "CREATE TABLE " + HistoryEntry.TABLE_NAME +
                " (" +
                HistoryEntry._ID           + " INTEGER PRIMARY KEY AUTOINCREMENT, "   +
                HistoryEntry.WORD          + " TEXT NOT NULL, "                       +
                HistoryEntry.TRANSLATION   + " TEXT NOT NULL, "                       +
                HistoryEntry.LANGUAGE_FROM + " CHARACTER(2) NOT NULL, "               +
                HistoryEntry.LANGUAGE_TO   + " CHARACTER(2) NOT NULL, "               +
                HistoryEntry.DATE          + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                HistoryEntry.IS_FAVORITE   + " INTEGER DEFAULT 0, "                   +
                "UNIQUE (" +
                HistoryEntry.WORD + ", "          +
                HistoryEntry.LANGUAGE_FROM + ", " +
                HistoryEntry.LANGUAGE_TO          +
                ") ON CONFLICT REPLACE" +
                ");";
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Update this method after version increment
        db.execSQL("DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME);
        onCreate(db);
    }
}