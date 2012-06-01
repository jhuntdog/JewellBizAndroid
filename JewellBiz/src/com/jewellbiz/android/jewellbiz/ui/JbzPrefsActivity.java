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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.Window;
import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.data.JbzDatabase;
import com.jewellbiz.android.jewellbiz.data.JbzSharedPrefs;
import com.jewellbiz.android.jewellbiz.receiver.AlarmReceiver;
import com.jewellbiz.android.jewellbiz.util.Utils;

public class JbzPrefsActivity extends SherlockPreferenceActivity {
	
	public static final String KEY_THEME = "com.jewellbiz.android.jewellbiz.theme";
	
	public static int THEME = R.style.ThemeJewell;
	
	protected static final int ABOUT_DIALOG = 0;
	
	private static final String TAG = "JbzPrefsActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(JbzPrefsActivity.THEME);
		super.onCreate(savedInstanceState);
		
		
		final JbzPrefsActivity activity = this;
		getPreferenceManager().setSharedPreferencesName(JbzSharedPrefs.PREFS_NAME);
		addPreferencesFromResource(R.xml.prefs);
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		//  final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		final String versionFinal = Utils.getVersion(this);
		
		// About dialog
		Preference aboutPref = (Preference) findPreference("aboutPref");
		aboutPref.setSummary("v" + versionFinal + " (dbver " + JbzDatabase.DB_VERSION + ")");
		aboutPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				showDialog(ABOUT_DIALOG);
				return true;
			}
		});
		
		// Theme Switcher
		findPreference(KEY_THEME).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				if (preference.getKey().equals(KEY_THEME)) {
					Utils.updateTheme((String) newValue);
				}
				return true;
			}
		});
		
		/*ViewTreeObserver observer = getListView().getViewTreeObserver();
		observer.addOnGlobalLayoutListener(layoutListener);*/
	}
	
	/*@Override
	public void onDestroy() {
	  super.onDestroy();
	  // Always detach ViewTreeObserver listeners when the view tears down
	  getListView().getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
	}*/
	
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
	
	
	@Override
    protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ABOUT_DIALOG: {
			final TextView message = new TextView(this);
			message.setAutoLinkMask(Linkify.ALL);
			message.setText(getString(R.string.about_message));
			message.setPadding(10, 5, 10, 5);
			message.setTextSize(16);
			final ScrollView aboutScroll = new ScrollView(this);
			aboutScroll.addView(message);
			return new AlertDialog.Builder(this).setTitle(getString(R.string.about_message_title))
				.setCancelable(true).setIcon(R.drawable.icon)
	            .setPositiveButton(getString(android.R.string.ok), null)
	            .setView(aboutScroll).create();
			}
		}
		return null;
		
	}
	
/*	ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

		@Override
		public void onGlobalLayout() {
			int barHeight = getSupportActionBar().getHeight();
			ListView listView = getListView();
			FrameLayout.LayoutParams params = (LayoutParams) listView.getLayoutParams();
			// The list view top-margin should always match the action bar height
			if (params.topMargin != barHeight) {
				params.topMargin = barHeight;
				listView.setLayoutParams(params);
			}
			// The action bar doesn't update its height when hidden, so make top-margin zero
			if (!getActivity().getActionBar().isShowing()) {
				params.topMargin = 0;
				listView.setLayoutParams(params);
			}
			
		}
		
	};*/
	
	
	
	

}
