package ru.illarionovroman.yandexmobilizationhomework.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.illarionovroman.yandexmobilizationhomework.BuildConfig;


public class ApiManager {

    private static final String TRANSLATOR_END_POINT = "https://translate.yandex.net/api/v1.5/tr.json/";

    private static TranslatorInterface sTranslatorInterface;
    private static Retrofit sRetrofit;

    public static TranslatorInterface getApiInterfaceInstance() {
        if (sTranslatorInterface == null) {
            sTranslatorInterface = getRetrofitInstance().create(TranslatorInterface.class);
        }
        return sTranslatorInterface;
    }

    public static Retrofit getRetrofitInstance() {
        if (sRetrofit == null) {
            sRetrofit = createRetrofit();
        }
        return sRetrofit;
    }

    private static Retrofit createRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpBuilder = new OkHttpClient().newBuilder();
        if (BuildConfig.DEBUG) {
            okHttpBuilder.addNetworkInterceptor(new StethoInterceptor());
        }
        OkHttpClient httpClient = okHttpBuilder
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(new ApiKeyInsertInterceptor())
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(TRANSLATOR_END_POINT)
                .client(httpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());

        return builder.build();
    }
}
