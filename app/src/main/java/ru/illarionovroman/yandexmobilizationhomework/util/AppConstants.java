package ru.illarionovroman.yandexmobilizationhomework.util;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class AppConstants {

    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMAT_UTC = buildSimpleDateFormatUtc();

    private static SimpleDateFormat buildSimpleDateFormatUtc() {
        // We need this date format to write in DB, so probably it shouldn't be localized,
        // it depends from how SQLite database generates current date.
        SimpleDateFormat sdf = new SimpleDateFormat(AppConstants.DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf;
    }
}