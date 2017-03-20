package ru.illarionovroman.yandexmobilizationhomework.network;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface TranslatorInterface {

    @GET("/getLangs?ui={lang_code}")
    Flowable<String> getSupportedLanguages(@Path("lang_code") String languageCode);

    @GET("/detect?text={text}")
    Flowable<String> detectLanguage(@Path("text") String languageCode,
                                    @Query("hint") String hintList);

    @GET("/translate?text={text}&lang={lang}")
    Flowable<String> getTranslation(@Path("text") String text,
                                    @Path("lang") String langFromTo,
                                    @Query("format") String format);
}
