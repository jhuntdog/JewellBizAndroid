package com.jewellbiz.android.jewellbiz.ui;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.ArticleSearch;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.Articles;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SearchFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {
	
	private static final int LOADER_ID = R.string.search_title;
	
	private SimpleCursorAdapter mAdapter;

    private String mArticleTitle;

    interface InitBundle {
        String QUERY = "query";

        String ARTICLE_TITLE = "title";
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_fragment, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	String[] from = new String[] {
    			Articles.ARTICLE_TITLE,  Articles.OVERVIEW, Articles.READ
    	};
    	
    	int[] to = new int[] {
    			R.id.TextViewSearchRow, R.id.TextViewSearchSnippet, R.id.TextViewSearchArticleReadState
    	};
    	
    	mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.search_row, null, from, to, 0);
    	mAdapter.setViewBinder(new ViewBinder() {
    		
    		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
    			if (columnIndex == SearchQuery.TITLE) {
    				TextView titles = (TextView) view;
    				String artTitle = getString(R.string.article_title) + " " + cursor.getString(columnIndex);
    				titles.setText(artTitle);
    				return true;
    			}
				return false;
    		}
    	});
    	setListAdapter(mAdapter);
    	
    	getLoaderManager().initLoader(LOADER_ID, getArguments(), this);
    	
    }
    
    public void onPerformSearch(Bundle args) {
        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    	Intent i = new Intent(getActivity(), ArticleActivity.class);
    	i.putExtra(ArticleActivity.InitBundle.ARTICLE_ID, (int) id);
    }
	

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String selection = null;
        final String query = args.getString(SearchManager.QUERY);
        String[] selectionArgs = new String[] {
                query
            };
        
        Bundle appData = args.getBundle(SearchManager.APP_DATA);
        if (appData != null) {
            String articletitle = appData.getString(InitBundle.ARTICLE_TITLE);
            if (articletitle != null) {
                // preserve show filter as long as this fragment is alive
                mArticleTitle = articletitle;
            }
        }
        
     // set show filter
        if (mArticleTitle != null) {
            selection = Articles.ARTICLE_TITLE + "=?";
            selectionArgs = new String[] {
                    query, mArticleTitle
            };
        }
        
        
        return new CursorLoader(getActivity(), ArticleSearch.CONTENT_URI_SEARCH, 
        		SearchQuery.PROJECTION, selection, selectionArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        mAdapter.swapCursor(data);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        mAdapter.swapCursor(null);
		
	}
	
	interface SearchQuery {
		String[] PROJECTION = new String[] {
				Articles._ID, Articles.ARTICLE_TITLE,  Articles.OVERVIEW, Articles.READ
		};
		
		int _ID = 0;

        int TITLE = 1;

        int OVERVIEW = 2;
        
        int READ = 3;
	}
	
	

}
