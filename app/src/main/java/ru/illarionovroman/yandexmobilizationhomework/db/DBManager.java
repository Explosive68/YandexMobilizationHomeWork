package ru.illarionovroman.yandexmobilizationhomework.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;

/**
 * Helper class for centralized DB management
 */
public class DBManager {
    private static final String WHERE_ARG_PLACEHOLDER = "=?";

    public static Cursor getAllHistoryItemsCursor(Context context) {
        return context.getContentResolver().query(
                Contract.HistoryEntry.CONTENT_URI_HISTORY,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE);
    }

    public static Cursor getFavoriteHistoryItemsCursor(Context context) {
        return context.getContentResolver().query(
                Contract.HistoryEntry.CONTENT_URI_FAVORITES,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE);
    }

    public static HistoryItem getHistoryItemByWord(Context context, String word) {
        HistoryItem item = null;
        String selection = Contract.HistoryEntry.WORD + WHERE_ARG_PLACEHOLDER;
        String[] selectionArgs = new String[]{word};

        /*Uri searchWordUri = Contract.HistoryEntry.CONTENT_URI_HISTORY.buildUpon()
                .appendPath(word).build();*/
        Cursor cursor =  context.getContentResolver().query(
                Contract.HistoryEntry.CONTENT_URI_HISTORY,
                null,
                selection,
                selectionArgs,
                null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                item = new HistoryItem(cursor);
            }
            cursor.close();
        }
        return item;
    }

    public static HistoryItem getHistoryItemById(Context context, long id) {
        HistoryItem item = null;

        String strId = String.valueOf(id);
        Uri searchIdUri = Contract.HistoryEntry.CONTENT_URI_HISTORY.buildUpon()
                .appendPath(strId).build();
        Cursor cursor =  context.getContentResolver().query(
                searchIdUri,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                item = new HistoryItem(cursor);
            }
            cursor.close();
        }
        return item;
    }

    public static long addHistoryItem(Context context, HistoryItem item) {
        Uri uri = context.getContentResolver().insert(
                Contract.HistoryEntry.CONTENT_URI_HISTORY, item.toContentValues());
        return ContentUris.parseId(uri);
    }

    public static int deleteAllHistory(Context context) {
        Uri uri = Contract.HistoryEntry.CONTENT_URI_HISTORY;
        int deletedCount = context.getContentResolver().delete(uri, null, null);
        return deletedCount;
    }

    public static int deleteAllFavorites(Context context) {
        Uri uri = Contract.HistoryEntry.CONTENT_URI_FAVORITES;
        int deletedCount = context.getContentResolver().delete(uri, null, null);
        return deletedCount;
    }

    public static int updateHistoryItem(Context context, HistoryItem item) {
        String where = Contract.HistoryEntry._ID + WHERE_ARG_PLACEHOLDER;
        String[] args = new String[]{String.valueOf(item.getId())};
        int updatedCount = context.getContentResolver()
                .update(Contract.HistoryEntry.CONTENT_URI_HISTORY, item.toContentValues(), where, args);
        return updatedCount;
    }
}
