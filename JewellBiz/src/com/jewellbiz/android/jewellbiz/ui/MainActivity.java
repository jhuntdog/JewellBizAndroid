package com.jewellbiz.android.jewellbiz.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.service.JbzDownloaderService;

public class MainActivity extends BaseActivity implements 
		HeadlinesFragment.OnItemSelectedListener {
	
	private String[] mToggleLabels = {"Show Titles", "Hide Titles"};
	private boolean mTitlesHidden = false;
	
	private boolean mDualPane = false;
	HeadlinesFragment mHeadlinesFragment;
	ArticleFragment mArticleFragment;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(savedInstanceState != null) {
        	mTitlesHidden = savedInstanceState.getBoolean("titlesHidden");
        }
        
        setContentView(R.layout.main);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        
        // find our fragments
        mHeadlinesFragment = (HeadlinesFragment) getSupportFragmentManager().findFragmentById(R.id.headlines_frag);
        mArticleFragment = (ArticleFragment) getSupportFragmentManager().findFragmentById(R.id.article_frag);
        
        if (mArticleFragment != null) mDualPane = true;
        
        if (mTitlesHidden) {
        	getSupportFragmentManager().beginTransaction().hide(mHeadlinesFragment);
        }
        
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
		
		if (!mDualPane) {
			menu.removeItem(R.id.menu_toggleList);
		} else {
			menu.findItem(R.id.menu_toggleList).setTitle(mToggleLabels[mTitlesHidden ? 0 : 1]);
		}
		
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
			
		case R.id.menu_testhelper:
			Intent newTestIntent = new Intent(getApplicationContext(), TestActivity.class);
			startActivity(newTestIntent);
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
