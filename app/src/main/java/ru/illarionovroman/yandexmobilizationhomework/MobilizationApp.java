package ru.illarionovroman.yandexmobilizationhomework;

import android.app.Application;

import com.facebook.stetho.Stetho;

import ru.illarionovroman.yandexmobilizationhomework.dagger.component.DaggerNetworkComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.component.NetworkComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.NetworkModule;
import timber.log.Timber;


public class MobilizationApp extends Application {

    private NetworkComponent mNetworkComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mNetworkComponent = DaggerNetworkComponent.builder()
                .networkModule(new NetworkModule())
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            Stetho.initializeWithDefaults(this);
        }
    }

    public NetworkComponent getNetworkComponent() {
        return mNetworkComponent;
    }
}
