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

package com.jewellbiz.android.jewellbiz.data;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.R.string;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JbzSharedPrefs {
	
	public final static String PREFS_NAME = "jbz_prefs";
	
	public static boolean getBackgroundUpdateFlag(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		
		return prefs.getBoolean(
				context.getString(R.string.pref_key_flag_background_update),
				false);
	}
	
	public static void setBackgroundUpdateFlag(Context context, boolean newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putBoolean(
				context.getString(R.string.pref_key_flag_background_update),
				newValue);
		prefsEditor.commit();
	}
	
	public static boolean getOnlyUnreadFlag(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean(
				context.getString(R.string.pref_key_only_unread), false);
	}
	

}
