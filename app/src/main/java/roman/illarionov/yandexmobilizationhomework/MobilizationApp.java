package roman.illarionov.yandexmobilizationhomework;

import android.app.Application;

import timber.log.Timber;

/**
 * Created by WakeUp on 17.03.2017.
 */

public class MobilizationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
