package ru.illarionovroman.yandexmobilizationhomework.db;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ru.illarionovroman.yandexmobilizationhomework.model.HistoryItem;

/**
 * Helper class for centralized DB management
 */
public class DBManager {
    private static final String WHERE_ARG_PLACEHOLDER = "=?";

    public static CursorLoader getHistoryCursorLoader(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
                context,
                Contract.HistoryEntry.CONTENT_URI_HISTORY,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE + " DESC");
        return cursorLoader;
    }

    public static CursorLoader getFavoriteCursorLoader(Context context) {
        CursorLoader cursorLoader = new CursorLoader(
                context,
                Contract.HistoryEntry.CONTENT_URI_FAVORITES,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE + " DESC");
        return cursorLoader;
    }

    public static Cursor getAllHistoryItemsCursor(Context context) {
        return context.getContentResolver().query(
                Contract.HistoryEntry.CONTENT_URI_HISTORY,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE + " DESC");
    }

    public static Cursor getFavoriteHistoryItemsCursor(Context context) {
        return context.getContentResolver().query(
                Contract.HistoryEntry.CONTENT_URI_FAVORITES,
                null,
                null,
                null,
                Contract.HistoryEntry.DATE + " DESC");
    }

    public static HistoryItem getHistoryItemByWordAndLangs(Context context, String word,
                                                           String languageFrom, String languageTo) {
        HistoryItem item = null;
        String selection = Contract.HistoryEntry.WORD + WHERE_ARG_PLACEHOLDER + " AND " +
                Contract.HistoryEntry.LANGUAGE_FROM + WHERE_ARG_PLACEHOLDER + " AND " +
                Contract.HistoryEntry.LANGUAGE_TO + WHERE_ARG_PLACEHOLDER;
        String[] selectionArgs = new String[]{word, languageFrom, languageTo};

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

    public static int updateHistoryItemWithId(Context context, HistoryItem item) {
        Uri idUri = Contract.HistoryEntry.CONTENT_URI_HISTORY.buildUpon()
                .appendPath(String.valueOf(item.getId()))
                .build();
        int updatedCount = context.getContentResolver()
                .update(idUri, item.toContentValues(), null, null);
        return updatedCount;
    }
}