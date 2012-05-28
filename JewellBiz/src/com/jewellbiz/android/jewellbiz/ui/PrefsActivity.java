package com.jewellbiz.android.jewellbiz.ui;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.receiver.AlarmReceiver;

public class PrefsActivity extends SherlockPreferenceActivity {
	//implements OnSharedPreferenceChangeListener
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getPreferenceManager().setSharedPreferencesName(JbzSharedPrefs.PREFS_NAME);
		addPreferencesFromResource(R.xml.prefs);
		
	}
	
	private void cancelRecurringAlarm(Context context) {
		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(recurringDownload);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Context context = getApplicationContext();
		if (JbzSharedPrefs.getBackgroundUpdateFlag(getApplicationContext())) {
			setRecurringAlarm(context);
		} else {
			cancelRecurringAlarm(context);
		}
	}
	
	private void setRecurringAlarm(Context context) {
		// when does jewell biz update?
		// Do not know.
		// Let's grab new stuff around 11:45 GMT, inexactly
		Calendar updateTime = Calendar.getInstance();
		updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		updateTime.set(Calendar.HOUR_OF_DAY, 11);
		updateTime.set(Calendar.MINUTE, 45);
		
		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) getSystemService(
				Context.ALARM_SERVICE);
		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
				recurringDownload);
		
	}

}
