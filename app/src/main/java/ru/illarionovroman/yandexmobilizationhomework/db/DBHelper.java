package ru.illarionovroman.yandexmobilizationhomework.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract.*;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mobilization.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TRANSLATIONS_HISTORY_TABLE =
                "CREATE TABLE" + TranslationHistoryEntry.TABLE_NAME +
                " (" +
                TranslationHistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TranslationHistoryEntry.WORD + " TEXT NOT NULL, " +
                TranslationHistoryEntry.TRANSLATION + " TEXT NOT NULL, " +
                TranslationHistoryEntry.LANGUAGE_FROM + " CHARACTER(2) NOT NULL, " +
                TranslationHistoryEntry.LANGUAGE_TO + " CHARACTER(2) NOT NULL, " +
                TranslationHistoryEntry.DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_TRANSLATIONS_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Update this method after version increment
        db.execSQL("DROP TABLE IF EXISTS " + TranslationHistoryEntry.TABLE_NAME);
        onCreate(db);
    }
}
