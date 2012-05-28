package com.jewellbiz.android.jewellbiz.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jewellbiz.android.jewellbiz.R;

public class ContentFragment extends SherlockFragment {
	
	public static final String DEBUG_TAG = "ContentFragment";
	
	private WebView mWebView = null;
	private boolean mSoloFragment = false;
	private int mCurPosition = 0;
	
	public ContentFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mWebView = (WebView) inflater.inflate(R.layout.webviewer, container, false);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		return mWebView;
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Set member variable for whether this fragment is the only one in the activity
		Fragment listFragment = getFragmentManager().findFragmentById(R.id.list_frag);
		mSoloFragment = listFragment == null ? true : false;
		
		if (mSoloFragment) {
			ActionBar actionBar = getSherlockActivity().getSupportActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			// Must call in order to get onOptionsItemSelected callback
			setHasOptionsMenu(true);
			
			/*ViewTreeObserver observer = getView().getViewTreeObserver();
			observer.addOnGlobalLayoutListener(layoutListener);*/
		}
		
	}
	
	/*@Override
	public void onDestroyView() {
		super.onDestroyView();
		// Always detach ViewTreeObserver listeners when the view tears down
		getView().getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
	}*/
	
	public void updateUrl(String newUrl) {
		if (mWebView !=null) {
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			mWebView.loadUrl(newUrl);
			
		}
	}
	
	
	// Options Menu
	// -----------------
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.content_menu, menu);
		
		//do stuff for sharing 
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(getSherlockActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("listPosition", mCurPosition);
	}

}
