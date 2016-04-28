package com.ynyx.epic.xpush.recevier;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ynyx.epic.xpush.service.PushService;
import com.ynyx.epic.xpush.utils.L;
import com.ynyx.epic.xpush.utils.NetWorkUtils;

import java.util.Calendar;

public class PushAlarmReceiver extends BroadcastReceiver{

	private static final String TAG = PushAlarmReceiver.class.getSimpleName();
	
//	public static String ACTION_NAME = "PushAlarmReceiver";
	
	public static void alarm(Context context) {
		// When the alarm goes off, we want to broadcast an Intent to our
	    // BroadcastReceiver.  Here we make an Intent with an explicit class
	    // name to have our own receiver (which has been published in
	    // AndroidManifest.xml) instantiated and called, and then create an
	    // IntentSender to have the intent executed as a broadcast.
	    Intent intent = new Intent(context, PushAlarmReceiver.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context,
	            0, intent, 0);
	
	    // We want the alarm to go off 30 seconds from now.
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(System.currentTimeMillis());
	    calendar.add(Calendar.SECOND, 30);
	
	    // Schedule the alarm!
	    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//	    am.cancel(sender);
	    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
	}

	public static void cancel(Context context) {
		// When the alarm goes off, we want to broadcast an Intent to our
	    // BroadcastReceiver.  Here we make an Intent with an explicit class
	    // name to have our own receiver (which has been published in
	    // AndroidManifest.xml) instantiated and called, and then create an
	    // IntentSender to have the intent executed as a broadcast.
	    Intent intent = new Intent(context, PushAlarmReceiver.class);
	    PendingIntent sender = PendingIntent.getBroadcast(context,
	            0, intent, 0);
	    
	    // Schedule the alarm!
	    AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	    am.cancel(sender);
	}

	public static Intent getIntent(Context context) {
		Intent intent = new Intent(context, PushAlarmReceiver.class);
		return intent;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		alarm(context);
        String action = intent.getAction();
        L.d("onReceive action:"+action);
        if (!TextUtils.isEmpty(action)&&action.equalsIgnoreCase("android.net.conn" +
                ".CONNECTIVITY_CHANGE")){
            if (!NetWorkUtils.isNetworkAvailable(context)){
                PushService.stopService(context.getApplicationContext());
            } else{
                PushService.startService(context.getApplicationContext());
            }
        }else{
            PushService.startService(context.getApplicationContext());
        }
	}
}
