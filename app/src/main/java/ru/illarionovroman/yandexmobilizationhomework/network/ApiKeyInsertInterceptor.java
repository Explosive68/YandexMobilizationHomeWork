package ru.illarionovroman.yandexmobilizationhomework.network;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Interceptor which adds query parameter with private api key to every request.
 */
public class ApiKeyInsertInterceptor implements Interceptor {

    private static final String TRANSLATOR_API_KEY_KEY = "key";
    private static final String TRANSLATOR_API_KEY_VALUE = "trnsl.1.1.20170321T130741Z.f2f8ae81a3f923f1.57ed54ad0c9911c28b9615df505b77fe522d2f28";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        HttpUrl originalHttpUrl = originalRequest.url();

        HttpUrl urlWithApiKey = originalHttpUrl.newBuilder()
                .addQueryParameter(TRANSLATOR_API_KEY_KEY, TRANSLATOR_API_KEY_VALUE)
                .build();

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .url(urlWithApiKey);

        Request requestWithApiKey = requestBuilder.build();
        return chain.proceed(requestWithApiKey);
    }
}
