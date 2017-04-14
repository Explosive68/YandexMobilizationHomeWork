package ru.illarionovroman.yandexmobilizationhomework;

import android.app.Application;

import com.facebook.stetho.Stetho;

import ru.illarionovroman.yandexmobilizationhomework.dagger.component.AppComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.component.DaggerAppComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.AppContextModule;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.NetworkModule;
import timber.log.Timber;


public class MobilizationApp extends Application {

    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppComponent = DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(getApplicationContext()))
                .networkModule(new NetworkModule())
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        }
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}
