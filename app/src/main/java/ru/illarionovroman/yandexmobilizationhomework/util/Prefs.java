package ru.illarionovroman.yandexmobilizationhomework.util;


import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    public static final String PREF_NAME = "LAST_USED_ITEM_ID_PREF";
    public static final String KEY_LAST_USED_ITEM_ID = "KEY_LAST_USED_ITEM_ID";

    public static void putLastUsedItemId(Context context, long itemId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putLong(KEY_LAST_USED_ITEM_ID, itemId)
                .apply();
    }

    public static long getLastUsedItemId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_LAST_USED_ITEM_ID, -1);
    }
}
