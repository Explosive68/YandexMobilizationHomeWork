package ru.illarionovroman.yandexmobilizationhomework.network;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.DetectLanguageResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.SupportedLanguagesResponse;
import ru.illarionovroman.yandexmobilizationhomework.network.responses.TranslationResponse;


public interface TranslatorInterface {

    @GET("getLangs")
    Observable<SupportedLanguagesResponse> getSupportedLanguages(@Query("ui") String languageCode);

    @GET("detect")
    Observable<DetectLanguageResponse> detectLanguage(@Query("text") String languageCode,
                                                      @Query("hint") String hintList);

    @GET("translate")
    Observable<TranslationResponse> getTranslation(@Query("text") String text,
                                                   @Query("lang") String langFromTo,
                                                   @Query("format") String format);
}