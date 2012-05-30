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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jewellbiz.android.jewellbiz.R;

public class ArticleFragment extends SherlockFragment {
	
	public static final String DEBUG_TAG = "ContentFragment";

	private ShareActionProvider contentShareActionProvider;
	
	private WebView mWebView = null;
	private boolean mSoloFragment = false;
	private int mCurPosition = 0;

	//private String currentUrl = null;
	
	public ArticleFragment() {
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
		Fragment listFragment = getSherlockActivity().getSupportFragmentManager().findFragmentById(R.id.headlines_frag);
		mSoloFragment = listFragment == null ? true : false;
		
		
		/*if (mSoloFragment) {
			// The fragment is alone, so enable up navigation
			ActionBar bar = getSherlockActivity().getSupportActionBar();
			bar.setDisplayHomeAsUpEnabled(true);
			bar.setDisplayShowTitleEnabled(false);
			// Must call in order to get callback to onOptionsItemSelected()
			setHasOptionsMenu(true);
		}
		
		if (mSoloFragment) {
			ViewTreeObserver observer = getView().getViewTreeObserver();
			observer.addOnGlobalLayoutListener(layoutListener);
		}*/
		
		
		
			
	}
	
	/*@Override
	public void onDestroyView() {
	  super.onDestroyView();
	  // Always detach ViewTreeObserver listeners when the view tears down
	  getView().getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(R.menu.content_menu, menu);
		
		MenuItem actionItem = menu.findItem(R.id.content_menu_share);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		actionProvider.setShareIntent(createShareIntent());
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	

	private Intent createShareIntent() {
		String jbzUrl = getResources().getString(R.string.jewellbiz_url);

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		
		shareIntent.putExtra(Intent.EXTRA_TEXT, jbzUrl);
		
		
		return shareIntent;
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		
	}*/
	
	public void updateUrl(String newUrl) {
		if (mWebView !=null) {
			WebSettings webSettings = mWebView.getSettings();
			webSettings.setJavaScriptEnabled(true);
			mWebView.loadUrl(newUrl);
			
		}
	}
	

	
	@Override
	public void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("listPosition", mCurPosition);
	}
	
	/*ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		public void onGlobalLayout() {
			int barHeight = getActivity().getActionBar().getHeight();
			View contentView = getView();
			FrameLayout.LayoutParams params = (LayoutParams) contentView.getLayoutParams();
			// The list view top-margin should always match the action bar height
			if (params.topMargin != barHeight) {
				params.topMargin = barHeight;
				contentView.setLayoutParams(params);
			}
			// The action bar doesn't update its height when hidden, so make top-margin zero
			if (!getActivity().getActionBar().isShowing()) {
			  params.topMargin = 0;
			  contentView.setLayoutParams(params);
			}
		}
	};*/
	

}
