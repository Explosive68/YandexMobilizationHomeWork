package ru.illarionovroman.yandexmobilizationhomework.utils;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;
import ru.illarionovroman.yandexmobilizationhomework.models.HistoryItem;

/**
 * Created by WakeUp on 20.03.2017.
 */

public class Utils {

    public static class DB {

        private static final String WHERE_ARG_PLACEHOLDER = "=?";

        public static ArrayList<HistoryItem> getAllHistoryItems(Context context) {
            ArrayList<HistoryItem> historyItems = new ArrayList<>();

            Cursor cursor = context.getContentResolver().query(
                    Contract.TranslationHistoryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    Contract.TranslationHistoryEntry.DATE);

            if (cursor == null) {
                return historyItems;
            }

            while (cursor.moveToNext()) {
                HistoryItem item = new HistoryItem();
                // TODO: fill all HistoryItem fields
                historyItems.add(item);
            }
            cursor.close();

            return historyItems;
        }

        public static Cursor getAllHistoryItemsCursor(Context context) {
            return context.getContentResolver().query(
                    Contract.TranslationHistoryEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    Contract.TranslationHistoryEntry.DATE);
        }

        public static ArrayList<HistoryItem> getFavoriteHistoryItems(Context context) {
            ArrayList<HistoryItem> favoriteItems = new ArrayList<>();

            String selection = Contract.TranslationHistoryEntry.IS_FAVORITE + WHERE_ARG_PLACEHOLDER;
            // TODO: set positive value for IS_FAVORITE
            String selectionArgs[] = new String[]{};
            Cursor cursor = context.getContentResolver().query(
                    Contract.TranslationHistoryEntry.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    Contract.TranslationHistoryEntry.DATE);

            if (cursor == null) {
                return favoriteItems;
            }

            while (cursor.moveToNext()) {
                HistoryItem item = new HistoryItem();
                // TODO: fill all HistoryItem fields
                favoriteItems.add(item);
            }
            cursor.close();

            return favoriteItems;
        }

        public static Cursor getFavoriteHistoryItemsCursor(Context context) {

            String selection = Contract.TranslationHistoryEntry.IS_FAVORITE + WHERE_ARG_PLACEHOLDER;
            // TODO: set positive value for IS_FAVORITE
            String selectionArgs[] = new String[]{};

            return context.getContentResolver().query(
                    Contract.TranslationHistoryEntry.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    Contract.TranslationHistoryEntry.DATE);
        }
    }


}
