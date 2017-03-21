package ru.illarionovroman.yandexmobilizationhomework.network.responses;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({ResponseCodes.SUCCESS, ResponseCodes.API_KEY_INVALID, ResponseCodes.API_KEY_BLOCKED,
        ResponseCodes.DAY_LIMIT_EXCEED, ResponseCodes.TEXT_SIZE_EXCEED,
        ResponseCodes.TEXT_UNTRANSLATABLE, ResponseCodes.TRANSLATION_DIRECTION_UNSUPPORTED})
@Retention(RetentionPolicy.SOURCE)
public @interface ResponseCodes {
    int SUCCESS = 200;
    int API_KEY_INVALID = 401;
    int API_KEY_BLOCKED = 402;
    int DAY_LIMIT_EXCEED = 404;
    int TEXT_SIZE_EXCEED = 413;
    int TEXT_UNTRANSLATABLE = 422;
    int TRANSLATION_DIRECTION_UNSUPPORTED = 501;
}
