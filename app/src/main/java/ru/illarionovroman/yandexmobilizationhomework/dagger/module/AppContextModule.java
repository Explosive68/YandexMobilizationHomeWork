package ru.illarionovroman.yandexmobilizationhomework.dagger.module;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppContextModule {

    private Context mAppContext;

    public AppContextModule(Context appContext) {
        mAppContext = appContext;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mAppContext;
    }
}
