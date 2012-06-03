package com.jewellbiz.android.jewellbiz.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jewellbiz.android.jewellbiz.R;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class SearchActivity extends BaseActivity {
	
	private static final String TAG = "SearchActivity";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		handleIntent(getIntent());
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		setTitle(R.string.search_title);
		actionBar.setTitle(R.string.search_title);
		actionBar.setDisplayShowTitleEnabled(true);
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }
	
	private void handleIntent(Intent intent) {
		if (intent == null) {
            return;
        }
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			 String query = intent.getStringExtra(SearchManager.QUERY);
	            getSupportActionBar().setSubtitle("\"" + query + "\"");
	            
	            // display results in a search fragment
	            SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager()
	                    .findFragmentById(R.id.search_fragment);
	            if (searchFragment == null) {
	            	SearchFragment newFragment = new SearchFragment();
	            	newFragment.setArguments(getIntent().getExtras());
	            	getSupportFragmentManager().beginTransaction().replace(R.id.search_fragment, newFragment).commit();
	            } else {
	            	searchFragment.onPerformSearch(getIntent().getExtras());
	            }
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri data = intent.getData();
			String articleUrl = data.getLastPathSegment();
			onShowArticle(articleUrl);
			finish();
		}
		
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	private void onShowArticle(String articleUrl) {
		Intent i = new Intent(this, ArticleActivity.class);
		i.setData(Uri.parse(articleUrl));
		startActivity(i);
	}
	
	

}
