package com.jewellbiz.android.jewellbiz.ui;

import com.actionbarsherlock.app.ActionBar;
import com.jewellbiz.android.jewellbiz.R;

import android.os.Bundle;

public class MainActivity extends BaseActivity {
	
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		
	}
	
	

}
