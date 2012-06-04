package com.jewellbiz.android.jewellbiztoday;

import com.jewellbiz.android.jewellbiztoday.ui.JbzPrefsActivity;
import com.jewellbiz.android.jewellbiztoday.util.Utils;

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
