package com.jewellbiz.android.jewellbiz.ui;

import com.jewellbiz.android.jewellbiz.R;

import android.content.Intent;
import android.os.Bundle;


public class ContentActivity extends BaseActivity {
	
	public static final String DEBUG_TAG = "ContentActivity";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.content); 
		 
		 Intent launchingIntent = getIntent();
		 String content = launchingIntent.getData().toString();
		 
		 ContentFragment mWebView = (ContentFragment) getSupportFragmentManager()
				 .findFragmentById(R.id.content_frag);
		 
		 mWebView.updateUrl(content);
		
	}
	
	

}
