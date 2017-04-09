package ru.illarionovroman.yandexmobilizationhomework.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Prefs {

    private static final String KEY_LAST_USED_ITEM_ID = "KEY_LAST_USED_ITEM_ID";

    public static void putLastUsedItemId(Context context, long itemId) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .putLong(KEY_LAST_USED_ITEM_ID, itemId)
                .apply();
    }

    public static long getLastUsedItemId(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getLong(KEY_LAST_USED_ITEM_ID, -1);
    }
}
