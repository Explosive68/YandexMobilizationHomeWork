package ru.illarionovroman.yandexmobilizationhomework.util;


import java.util.Date;

public class Utils {

    public static String getCurrentFormattedDateUtc() {
        return AppConstants.DATE_FORMAT_UTC.format(new Date());
    }
}
