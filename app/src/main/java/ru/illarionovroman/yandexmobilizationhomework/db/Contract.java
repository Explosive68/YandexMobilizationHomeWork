package ru.illarionovroman.yandexmobilizationhomework.db;


import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    public static final String AUTHORITY = "ru.illarionovroman.yandexmobilizationhomework";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_TRANSLATIONS_HISTORY = "translations_history";

    private interface TranslationsHistoryColumns {
        String WORD = "word";
        String TRANSLATION = "translation";
        String LANGUAGE_FROM = "language_from";
        String LANGUAGE_TO = "language_to";
        String DATE = "date";
        String IS_FAVORITE = "is_favorite";
    }

    public static final class TranslationHistoryEntry implements BaseColumns,
            TranslationsHistoryColumns {

        public static final int CODE = 100;
        public static final String TABLE_NAME = "translations_history";
        public static final String CONTENT_PATH = "translations_history";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, CONTENT_PATH);
    }
}
