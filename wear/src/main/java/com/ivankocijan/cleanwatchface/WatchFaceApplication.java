package com.ivankocijan.cleanwatchface;

import net.danlew.android.joda.JodaTimeAndroid;

import android.app.Application;

/**
 * @author Koc
 *         ivan.kocijan@infinum.hr
 * @since 16.03.15.
 */
public class WatchFaceApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

    }

    private static WatchFaceApplication application;

    public WatchFaceApplication() {
        application = this;
    }

    public static Application getInstance() {
        return application;
    }

}
