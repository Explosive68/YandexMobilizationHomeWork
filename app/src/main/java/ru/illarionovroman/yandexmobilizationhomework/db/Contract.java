package ru.illarionovroman.yandexmobilizationhomework.db;

import android.net.Uri;
import android.provider.BaseColumns;


/**
 * Contract class to keep DB interactions consistent
 */
public class Contract {

    public static final String AUTHORITY = "ru.illarionovroman.yandexmobilizationhomework";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_HISTORY = "history";
    public static final String PATH_FAVORITES = "favorites";

    private interface TranslationColumns {
        String WORD = "word";
        String TRANSLATION = "translation";
        String LANGUAGE_FROM = "language_from";
        String LANGUAGE_TO = "language_to";
        String DATE = "date";
        String IS_FAVORITE = "is_favorite";
    }

    public static final class HistoryEntry implements BaseColumns, TranslationColumns {

        public static final String TABLE_NAME = "history";
        public static final Uri CONTENT_URI_HISTORY = Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_HISTORY);
        public static final Uri CONTENT_URI_FAVORITES= Uri.withAppendedPath(BASE_CONTENT_URI,
                PATH_FAVORITES);
    }
}
