package com.jewellbiz.android.jewellbiz;

import com.jewellbiz.android.jewellbiz.ui.JbzPrefsActivity;
import com.jewellbiz.android.jewellbiz.util.Utils;

import android.app.Application;
import android.preference.PreferenceManager;

public class JewellBizApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(
				JbzPrefsActivity.KEY_THEME, "0");
		Utils.updateTheme(theme);
	}

}
