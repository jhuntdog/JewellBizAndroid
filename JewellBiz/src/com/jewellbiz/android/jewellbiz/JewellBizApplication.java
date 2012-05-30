package com.jewellbiz.android.jewellbiz;

import android.app.Application;

public class JewellBizApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		/*String theme = PreferenceManager.getDefaultSharedPreferences(this).getString(
				JbzPrefsActivity.KEY_THEME, "0");
		Utils.updateTheme(theme);*/
	}

}
