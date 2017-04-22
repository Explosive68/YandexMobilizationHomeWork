package ru.illarionovroman.yandexmobilizationhomework;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import ru.illarionovroman.yandexmobilizationhomework.dagger.component.AppComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.component.DaggerAppComponent;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.AppContextModule;
import ru.illarionovroman.yandexmobilizationhomework.dagger.module.NetworkModule;
import timber.log.Timber;


public class MobilizationApp extends Application {

    private AppComponent mAppComponent;

    private RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();

        // Init LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mRefWatcher = LeakCanary.install(this);

        // Init Dagger modules
        mAppComponent = DaggerAppComponent.builder()
                .appContextModule(new AppContextModule(getApplicationContext()))
                .networkModule(new NetworkModule())
                .build();

        // Init Timber and Stetho in debug mode only
        if (BuildConfig.DEBUG) {
            Timber.plant(new MobilizationDebugTree());
            Stetho.initializeWithDefaults(this);
        }
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static MobilizationApp get(Context context) {
        return ((MobilizationApp)context.getApplicationContext());
    }

    /**
     * This is needed to monitor the fragment leaks
     */
    public static RefWatcher getRefWatcher(Context context) {
        MobilizationApp app = (MobilizationApp) context.getApplicationContext();
        return app.mRefWatcher;
    }

    /**
     * Customized Timber.DebugTree
     */
    private class MobilizationDebugTree extends Timber.DebugTree {
        @Override
        protected String createStackElementTag(StackTraceElement element) {
            // This adds line number to log message
            return super.createStackElementTag(element) + ":" + element.getLineNumber();
        }
    }
}
