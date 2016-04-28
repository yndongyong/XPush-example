package com.ynyx.epic.xpush.utils;


import android.util.Log;

/**
 * Log tools class 
 * Created by Dong on 2015/9/26.
 */
public class L {
    private L() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isPrintDebug =true; // whether or not to print bug log
    public static boolean isPrintError =true;// whether or not to print error  log 

    private static final String TAG = "XPush";

    public static void i(String msg) {
        if (isPrintDebug)
            Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (isPrintDebug)
            Log.d(TAG, msg);
    }

    public static void e(String msg) {
        if (isPrintError)
            Log.e(TAG, msg);
    }

    public static void v(String msg) {
        if (isPrintDebug)
            Log.v(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (isPrintDebug)
            Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isPrintDebug)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isPrintError)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isPrintDebug)
            Log.v(tag, msg);
    }
}
