package ru.illarionovroman.yandexmobilizationhomework.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import ru.illarionovroman.yandexmobilizationhomework.BuildConfig;


public class ApiKeyInsertInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();

        HttpUrl urlWithApiKey = originalHttpUrl.newBuilder()
                .addQueryParameter("key", BuildConfig.TRANSLATOR_API_KEY)
                .build();

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .url(urlWithApiKey);

        Request requestWithApiKey = requestBuilder.build();
        return chain.proceed(requestWithApiKey);
    }
}
