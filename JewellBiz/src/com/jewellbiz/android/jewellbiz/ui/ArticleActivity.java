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

import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jewellbiz.android.jewellbiz.R;


public class ArticleActivity extends BaseActivity {
	//private ShareActionProvider mShareActionProvider;
	//private WebView mWebView = null;
	private int mThemeId = 0;
	
	public static final String DEBUG_TAG = "ContentActivity";
	
	
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState); 
		 
		 setContentView(R.layout.content); 
		 
		 Intent launchingIntent = getIntent();
		 String content = launchingIntent.getData().toString();
		 
		 ArticleFragment mWebView = (ArticleFragment) getSupportFragmentManager()
				 .findFragmentById(R.id.article_frag);
		 
		 mWebView.updateUrl(content);
		 
		 ActionBar actionBar = getSupportActionBar();
		 actionBar.setDisplayShowTitleEnabled(false);
		 
	 }
	 
	 
	 @Override
	  protected void onSaveInstanceState(Bundle outState) {
	      super.onSaveInstanceState(outState);
	      outState.putInt("theme", mThemeId);
	  }

	

}
