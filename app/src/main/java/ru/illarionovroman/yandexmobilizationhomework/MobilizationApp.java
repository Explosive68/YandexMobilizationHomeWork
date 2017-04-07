package ru.illarionovroman.yandexmobilizationhomework;

import android.app.Application;

import com.facebook.stetho.Stetho;

import timber.log.Timber;


public class MobilizationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        }
    }
}
