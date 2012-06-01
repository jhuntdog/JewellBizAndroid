package com.jewellbiz.android.jewellbiz.util;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.ui.JbzPrefsActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class Utils {
	
	
	public static String getVersion(Context context) {
        String version;
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA).versionName;
        } catch (NameNotFoundException e) {
            version = "UnknownVersion";
        }
        return version;
    }

	public static synchronized void updateTheme(String themeIndex) {
		int theme = Integer.valueOf((String) themeIndex);
		switch (theme) {
		case 1:
			JbzPrefsActivity.THEME = R.style.ThemeICSLight;
			break;
		case 2:
			JbzPrefsActivity.THEME = R.style.ThemeICSDark;
			break;
		default:
			JbzPrefsActivity.THEME = R.style.ThemeJewell;
			break;
		}
		
	}
	
	
	

}
