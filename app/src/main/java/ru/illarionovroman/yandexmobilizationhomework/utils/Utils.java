package ru.illarionovroman.yandexmobilizationhomework.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.models.HistoryItem;


public class Utils {

    public static class DB {

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


}
