package com.ynyx.epic.xpush.smack;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.ynyx.epic.xpush.exception.XPushException;
import com.ynyx.epic.xpush.service.PushService;
import com.ynyx.epic.xpush.utils.L;

import org.jivesoftware.smack.SmackConfiguration;

import java.util.Arrays;
import java.util.List;

/**
 * push  initial 
 * Created by Dong on 2016/4/20.
 */
public class XPushInterface {

    public static final String XPUSH_APPKEY = "XPUSH_APPKEY";
    

    private XPushInterface() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * @param flag
     */
    public static void setDebugMode(boolean flag) {
        L.isPrintDebug = flag;
        SmackConfiguration.DEBUG = flag;
    }

    /**
     *
     * @param context
     */
    public static void init(Context context) {
        if (!checkAppKey(context)) {
            throw new XPushException("appkey cannot be instantiated! ");
        }
        //check  use permision 
        if (!checkUsePermission(context)) {
            throw new XPushException("Please apply sufficient permissions! ");
        }
        //init  XPush service
        PushService.startService(context);
    }

    /**
     * exit XPush
     */
    public static void logout(Context context) {
        PushService.stopService(context);
        XmppManager.getInstance().closeConnection();
        
    }

    /**
     * check  use  appkey
     * @param context
     * @return return true if appkey not null and avalible
     */
    private static boolean checkAppKey(Context context) {
        L.d("checkAppKey()");
        // get  meta-data  element in  manifest.XML on  application dot 
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            String appkey = applicationInfo.metaData.getString(XPUSH_APPKEY);
            if (!TextUtils.isEmpty(appkey)) {
                L.d("appkey:" + appkey);
            }

            return !TextUtils.isEmpty(appkey);

            // TODO: 2016/4/20  check  appkey 

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * check  use permission 
     *
     * @param context
     * @return true has grant use  permission 
     */
    private static boolean checkUsePermission(Context context) {
        L.d("checkUsePermission()");

        String[] permissions = {"android.permission.INTERNET",
                "android.permission.ACCESS_NETWORK_STATE"};
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            String[] lists = packageInfo.requestedPermissions;

            if (lists == null || lists.length == 0) {
                return false;
            }

            List<String> listA = Arrays.asList(permissions);
            List<String> listB = Arrays.asList(lists);
            L.d("checkUsePermission result:" + listB.containsAll(listA));
            return listB.containsAll(listA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

}
