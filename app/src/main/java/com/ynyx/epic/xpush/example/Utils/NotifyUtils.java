package com.ynyx.epic.xpush.example.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.ynyx.epic.xpush.example.R;
import com.ynyx.epic.xpush.example.activity.MainActivity;

/**
 * Created by Dong on 2016/4/20.
 */
public class NotifyUtils {


    public static  final int NOTIFICATION_BASE_NUMBER=110;
    /**
     * Show a notification while this service is running.
     */
    public static void showNotification(Context context, CharSequence title,
                                        CharSequence content) {


        NotificationManager mNM = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

     
        Notification notification ;
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("message",content.toString());
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder1 = new NotificationCompat.Builder(context);
        builder1.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)// 　　　　　　　　　　　　　　　　　　　　
                .setWhen(System.currentTimeMillis())//
                .setAutoCancel(true)//
                .setContentTitle(title)//
                .setContentText(content);//
        notification = builder1.build();//
        notification.defaults =Notification.DEFAULT_SOUND;//

        mNM.notify(NOTIFICATION_BASE_NUMBER, notification);
    }

    public static void cancelNotification(Context context) {
        NotificationManager mNM = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        mNM.cancel(NOTIFICATION_BASE_NUMBER);
    }
}
