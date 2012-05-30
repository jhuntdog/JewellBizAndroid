package com.jewellbiz.android.jewellbiz.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.service.JbzDownloaderService;

public class MainActivity extends BaseActivity implements 
		HeadlinesFragment.OnItemSelectedListener {
	
	private boolean mDualPane = false;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
	}
	
	// Options Menu
	// ----------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		
		// refresh menu item
		Intent refreshIntent = new Intent(getApplicationContext(), JbzDownloaderService.class);
		refreshIntent.setData(Uri.parse(getString(R.string.default_url)));
		MenuItem refresh = menu.findItem(R.id.menu_refresh);
		refresh.setIntent(refreshIntent);
		
		// prefs menu item
		Intent prefsIntent = new Intent(getApplicationContext(), JbzPrefsActivity.class);
		MenuItem preferences = menu.findItem(R.id.menu_prefs);
		preferences.setIntent(prefsIntent);
		
		return super.onPrepareOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
		case R.id.menu_refresh:
			startService(item.getIntent());
			return true;
			
		case R.id.menu_prefs:
			startActivity(item.getIntent());
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(String articleUrl) {
		ArticleFragment mArticleFragment = (ArticleFragment) getSupportFragmentManager()
				.findFragmentById(R.id.article_frag);
		
		if (mArticleFragment == null || !mArticleFragment.isInLayout()) {
			Intent showContent = new Intent(getApplicationContext(), ArticleActivity.class);
			showContent.setData(Uri.parse(articleUrl));
			startActivity(showContent);
		} else {
			mArticleFragment.updateUrl(articleUrl);
		}
		
	}

}
