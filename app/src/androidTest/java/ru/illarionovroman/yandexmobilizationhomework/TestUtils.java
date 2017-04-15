package ru.illarionovroman.yandexmobilizationhomework;


import android.content.ContentValues;
import android.support.annotation.NonNull;

import ru.illarionovroman.yandexmobilizationhomework.db.Contract;


class TestUtils {

    static final String TEST_VALUE_WORD = "Example";
    static final String TEST_VALUE_TRANSLATION = "Пример";
    static final String TEST_VALUE_LANG_FROM = "en";
    static final String TEST_VALUE_LANG_TO = "ru";
    static final String TEST_VALUE_DATE = "2017-04-15 00:00:00";
    static final String TEST_VALUE_IS_FAVORITE = "0";

    @NonNull
    static ContentValues createTestContentValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(Contract.HistoryEntry.WORD, TEST_VALUE_WORD);
        testValues.put(Contract.HistoryEntry.TRANSLATION, TEST_VALUE_TRANSLATION);
        testValues.put(Contract.HistoryEntry.LANGUAGE_FROM, TEST_VALUE_LANG_FROM);
        testValues.put(Contract.HistoryEntry.LANGUAGE_TO, TEST_VALUE_LANG_TO);
        testValues.put(Contract.HistoryEntry.DATE, TEST_VALUE_DATE);
        testValues.put(Contract.HistoryEntry.IS_FAVORITE, TEST_VALUE_IS_FAVORITE);
        return testValues;
    }
}
