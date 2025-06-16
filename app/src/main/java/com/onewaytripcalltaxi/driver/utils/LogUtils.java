package com.onewaytripcalltaxi.driver.utils;


import android.util.Log;

import com.onewaytripcalltaxi.driver.BuildConfig;

/**
 * Created by developer on 26/4/18.
 */

public class LogUtils {
    public static void debug(final String tag, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void debug(String message) {
        if (BuildConfig.DEBUG) {
            Log.d("mylogggg", message);
        }
    }
}
