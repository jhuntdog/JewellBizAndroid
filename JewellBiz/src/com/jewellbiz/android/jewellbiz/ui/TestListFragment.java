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

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.data.JbzDatabase;
import com.jewellbiz.android.jewellbiz.data.JbzProvider;
import com.jewellbiz.android.jewellbiz.data.JbzSharedPrefs;
import com.jewellbiz.android.jewellbiz.data.JewellBizDatabase;
import com.jewellbiz.android.jewellbiz.data.JewellBizProvider;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.Articles;
import com.jewellbiz.android.jewellbiz.service.JbzDownloaderService;

public class TestListFragment extends SherlockListFragment implements 
	LoaderManager.LoaderCallbacks<Cursor> {
	
	OnItemSelectedListener mListener;
	private boolean mDualFragments = false;	
	
	private static final String LAST_POSITION_KEY = "lastPosition";
	private static final String LAST_ITEM_CLICKED_KEY = "lastItemClicked";
	private static final String CUR_ARTICLE_URL_KEY = "curArticleUrl";
	
	public static final String DEBUG_TAG = "TestListFragment";
	
	// private OnArticleSelectedListener articleSelectedListener;
	private static final int TEST_LIST_LOADER = 0x01;
	
	private SimpleCursorAdapter adapter;
	
	private long lastItemClicked = -1;
	private String curArticleUrl = null;
	private int selectedPosition = -1;
	
	public interface OnItemSelectedListener {
		public void onItemSelected(String articleUrl);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnItemSelectedListener");
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		getLoaderManager().initLoader(TEST_LIST_LOADER, null, this);
		
		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(), R.layout.list_item_testing,
				null, UI_BINDING_FROM, UI_BINDING_TO,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		
		adapter.setViewBinder(new JbzViewBinder());
		setListAdapter(adapter);
		
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		 // Must call in order to get callback to onCreateOptionsMenu()
		 setHasOptionsMenu(true);
		
		 setEmptyText(getResources().getText(R.string.empty_list_label));
		 getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		 if (savedInstanceState != null) {
			lastItemClicked = savedInstanceState.getLong(LAST_ITEM_CLICKED_KEY, -1);
			if (selectedPosition != -1) {
				setSelection(selectedPosition);
				getListView().smoothScrollToPosition(selectedPosition);
				getListView().setItemChecked(selectedPosition, true);
			}
			
			curArticleUrl = savedInstanceState.getString(CUR_ARTICLE_URL_KEY);
			if (curArticleUrl != null) {
				mListener.onItemSelected(curArticleUrl);
			}
		 }
		
		// attach global layout listener to get callback when layout has finished loading
		// do this so that we can adapt the top margin and account for
		// actionbar being in "overlay" mode
		ViewTreeObserver observer = getListView().getViewTreeObserver();
		observer.addOnGlobalLayoutListener(layoutListener);
	}
	
	@Override
	public void onDestroyView() {
	  super.onDestroyView();
	  // Always detach ViewTreeObserver listeners when the view tears down
	  getListView().getViewTreeObserver().removeGlobalOnLayoutListener(layoutListener);
	}
	
	private boolean showReadFlag;
	
	@Override
	public void onPause() {
		showReadFlag = JbzSharedPrefs.getOnlyUnreadFlag(getActivity());
		Log.d(DEBUG_TAG, "onPause");
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(DEBUG_TAG, "onResume");
		if (showReadFlag !=JbzSharedPrefs.getOnlyUnreadFlag(getActivity())) {
			getLoaderManager().restartLoader(TEST_LIST_LOADER, null, this);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(LAST_ITEM_CLICKED_KEY, lastItemClicked);
		outState.putString(CUR_ARTICLE_URL_KEY, curArticleUrl);
		outState.putInt(LAST_POSITION_KEY, selectedPosition);
	}
	
	@Override
	public void onDestroy() {
		Log.d(DEBUG_TAG, "onDestroy");
		super.onDestroy();
	}
	

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (position == selectedPosition ) {
			// if not changing selection, do nothing
			return;
		}
		
		// get Url
		String projection[] = { JbzDatabase.COL_URL };
		Uri viewedArticle = Uri.withAppendedPath(JbzProvider.CONTENT_URI, String.valueOf(id));
		Cursor articleCursor = getActivity().getContentResolver().query(viewedArticle, projection, null, null, null);
		if (articleCursor.moveToFirst()) {
			curArticleUrl = articleCursor.getString(0);
			mListener.onItemSelected(curArticleUrl);
		}
		articleCursor.close();
		
		//mark last item as read
		if (lastItemClicked != -1) {
			JbzProvider.markItemRead(getActivity().getApplicationContext(), lastItemClicked);
			Log.d(DEBUG_TAG, "Marking " + lastItemClicked 
					+ " as read. Now showing " + id + ".");
		}
		lastItemClicked = id;
		
		selectedPosition = position;
		l.setItemChecked(position, true);
	}
	
	private static final String[] UI_BINDING_FROM = {
		JbzDatabase.COL_TITLE, JbzDatabase.COL_DATE,
		JbzDatabase.COL_DESC,
		JbzDatabase.COL_READ };
	private static final int[] UI_BINDING_TO = {R.id.title, R.id.date, R.id.desc, R.id.title };

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(DEBUG_TAG, "onCreate");  
		
	}
	
	// LoaderManager.LoaderCallbacks<Cursor> methods
	// ------------------------------------------------------------------------
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { JbzDatabase.ID, JbzDatabase.COL_TITLE, 
				JbzDatabase.COL_DATE, JbzDatabase.COL_DESC, JbzDatabase.COL_READ };

		Uri content = JbzProvider.CONTENT_URI;
		String selection = null;
		if (JbzSharedPrefs.getOnlyUnreadFlag(getActivity())) {
			selection = JbzDatabase.COL_READ + "='0'";		
		}
		CursorLoader cursorLoader = new CursorLoader(getActivity(), 
				content, projection, null, null, JbzDatabase.COL_DATE+" desc");
		
		return cursorLoader;
		
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.swapCursor(cursor);		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);		
	}
	
	

	// custom viewbinder
	// ------------------------------------------------------------------------
	private class JbzViewBinder implements SimpleCursorAdapter.ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int index) {
			if (index == cursor.getColumnIndex(JbzDatabase.COL_READ)) {
				boolean read = cursor.getInt(index) > 0 ? true : false;
				TextView title = (TextView) view;
				if (!read) {
					title.setTypeface(Typeface.DEFAULT_BOLD, 0);
				} else {
					title.setTypeface(Typeface.DEFAULT);
				}
				return true;
			} else if (index == cursor.getColumnIndex(JbzDatabase.COL_DATE)) {
				// get a locale based string for the date
				DateFormat formatter = android.text.format.DateFormat
						.getDateFormat(getActivity().getApplicationContext());
				long date = cursor.getLong(index);
				Date dateObj = new Date(date * 1000);
				((TextView) view).setText(formatter.format(dateObj));
				return true;
			} else {
				return false;
			}
		}			
	}
	
	ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			int barHeight = getSherlockActivity().getSupportActionBar().getHeight();
			ListView listView = getListView();
			FrameLayout.LayoutParams params = (LayoutParams) listView.getLayoutParams();
			// The list view top-margin should always match the action bar height
			if (params.topMargin != barHeight) {
				params.topMargin = barHeight;
				listView.setLayoutParams(params);
			}
			// The action bar doesn't update its height when hidden, so make top-margin zero
			if (!getSherlockActivity().getSupportActionBar().isShowing()) {
				params.topMargin = 0;
				listView.setLayoutParams(params);
			}
		}
	};
	
	

}
