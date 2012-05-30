/*
 * Copyright (C) 2012 Jewell Biz Today, jewellbiz.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.jewellbiz.android.jewellbiz.data.JbzSharedPrefs;
import com.jewellbiz.android.jewellbiz.receiver.AlarmReceiver;

public class JbzPrefsActivity extends SherlockPreferenceActivity {
	
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
