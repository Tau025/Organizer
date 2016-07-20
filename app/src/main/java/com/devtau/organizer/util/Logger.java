package com.devtau.organizer.util;

import android.util.Log;

public abstract class Logger {
    public static final String MY_LOG_TAG = "MY_LOG";
    public static boolean isDebug = true;

    public static void d(String CLASS_NAME_TAG, String message) {
        if (isDebug) {
            Log.d(MY_LOG_TAG + ". Msg from " + CLASS_NAME_TAG, message);
        }
    }

    public static void e(String CLASS_NAME_TAG, String message) {
        if (isDebug) {
            Log.e(MY_LOG_TAG + ". Error in " + CLASS_NAME_TAG, message);
        }
    }

    public static void e(String CLASS_NAME_TAG, Exception e) {
        if (isDebug) {
            Log.e(MY_LOG_TAG + ". Error in " + CLASS_NAME_TAG, e.getMessage(), e);
        }
    }

    public static void e(String CLASS_NAME_TAG, String message, Exception e) {
        if (isDebug) {
            Log.e(MY_LOG_TAG + ". Error in " + CLASS_NAME_TAG, message, e);
        }
    }
}
