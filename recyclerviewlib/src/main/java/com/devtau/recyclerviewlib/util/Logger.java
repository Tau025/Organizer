package com.devtau.recyclerviewlib.util;

import android.util.Log;

public abstract class Logger {
    public static final String TAG = "MY_LOG";
    private static boolean isDebug = false;

    public static void d(String message) {
        if (isDebug) {
            Log.d(TAG, message);
        }
    }
}
