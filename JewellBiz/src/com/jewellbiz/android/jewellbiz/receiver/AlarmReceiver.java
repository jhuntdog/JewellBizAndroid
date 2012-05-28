package com.jewellbiz.android.jewellbiz.receiver;

import java.util.Calendar;
import java.util.TimeZone;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.service.JbzDownloaderService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	
	private static final String DEBUG_TAG = "AlarmReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(DEBUG_TAG, "Recurring alarm; requesting download service.");
		
		//start the download
		Intent downloader = new Intent(context, JbzDownloaderService.class);
		downloader.setData(Uri.parse(context.getString(R.string.default_url)));
		context.startService(downloader);
		
	}
	
	// cancel alarms pointing at receiver
	public static void cancelRecurringAlarm(Context context) {
		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(recurringDownload);
	}
		
	public static void setRecurringAlarm(Context context) {
		Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        updateTime.set(Calendar.HOUR_OF_DAY, 11);
        updateTime.set(Calendar.MINUTE, 45);
        
        Intent downloader = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
                0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                recurringDownload);
	}

}
