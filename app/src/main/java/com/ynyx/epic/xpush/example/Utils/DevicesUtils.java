package com.ynyx.epic.xpush.example.Utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by Dong on 2016/4/20.
 */
public class DevicesUtils {

    public static String getAndroidDeviceId(Context context) {
        String deviceId = getDeviceId(context);
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        String androidId = getAndroidId(context);
        if (!TextUtils.isEmpty(deviceId)) {
            return androidId;
        }
        return "Anonymous";
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return telephonyManager.getDeviceId();
    }

    public static String getAndroidId(Context context) {
        @SuppressWarnings("deprecation")
        String androidId = android.provider.Settings.System.getString(
                context.getContentResolver(),
                android.provider.Settings.System.ANDROID_ID);
        return androidId;
    }
}
