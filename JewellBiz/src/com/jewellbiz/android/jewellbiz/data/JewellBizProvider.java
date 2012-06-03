package com.jewellbiz.android.jewellbiz.data;

import java.util.ArrayList;
import java.util.Arrays;

import com.jewellbiz.android.jewellbiz.data.JewellBizDatabase.Tables;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.ArticleSearch;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.Articles;
import com.jewellbiz.android.jewellbiz.util.SelectionBuilder;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class JewellBizProvider extends ContentProvider{
	
	private static final String TAG = "JewellBizProvider";

    private static final boolean LOGV = false;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    
    private static final int ARTICLES = 200;
    
    private static final int ARTICLES_ID = 201;
	
	private static final int ARTICLESEARCH = 300;
	
	private static final int ARTICLESEARCH_ID = 301;
	
	private static final int SEARCH_SUGGEST = 800;
	
	private static final int RENEW_FTSTABLE = 900;
    
	/**
     * Build and return a {@link UriMatcher} that catches all {@link Uri}
     * variations supported by this {@link ContentProvider}.
     */
    private static UriMatcher buildUriMatcher() {
    	final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    	final String authority = JewellContracts.CONTENT_AUTHORITY;
    	
    	// Articles
    	matcher.addURI(authority, JewellContracts.PATH_ARTICLES, ARTICLES);
    	matcher.addURI(authority, JewellContracts.PATH_ARTICLES + "/*", ARTICLES_ID);
    	
    	// Search
    	matcher.addURI(authority, JewellContracts.PATH_ARTICLESEARCH + "/" + JewellContracts.PATH_SEARCH, ARTICLESEARCH);
    	matcher.addURI(authority, JewellContracts.PATH_ARTICLESEARCH + "/*", ARTICLESEARCH_ID);
    	
    	// Suggestions
    	matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
    	matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
    	
    	// Ops
    	matcher.addURI(authority, JewellContracts.PATH_RENEWFTSTABLE, RENEW_FTSTABLE);
    	
		return matcher;
	}
    
    private JewellBizDatabase mOpenHelper;
    
    @Override
	public boolean onCreate() {
		final Context context = getContext();
		mOpenHelper = new JewellBizDatabase(context);
		
		return true;
	}
    
    @Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case ARTICLES:
			return Articles.CONTENT_TYPE;
		case ARTICLES_ID:
			return Articles.CONTENT_ITEM_TYPE;
		case SEARCH_SUGGEST:
            return SearchManager.SUGGEST_MIME_TYPE;
        case RENEW_FTSTABLE:
            // however there is nothing returned
            return Articles.CONTENT_TYPE;
        default:
           	throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
    
    @Override
	public Uri insert(Uri uri, ContentValues values) {
    	 if (LOGV)
             Log.v(TAG, "insert(uri=" + uri + ", values=" + values.toString() + ")");
    	 final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    	 final int match = sUriMatcher.match(uri);
    	 switch (match) {
	    	 case ARTICLES: {
	    		 db.insertOrThrow(Tables.ARTICLES, null, values);
	    		 getContext().getContentResolver().notifyChange(uri, null);
	    		 return Articles.buildArticleUri(values.getAsString(Articles._ID));
	    	 }
	    	 default: {
	    		 throw new UnsupportedOperationException("Unknown uri: " + uri);
	    	 }
    	 }
	}
    
    @Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
    	if (LOGV) {
            Log.v(TAG, "query(uri=" + uri + ", proj=" + Arrays.toString(projection) + ")");
        }
    	final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    	
    	final int match = sUriMatcher.match(uri);
    	switch (match) {
	    	case RENEW_FTSTABLE: {
	    		JewellBizDatabase.onRenewFTSTable(db);
	    		return null;
	    	}
	    	case ARTICLESEARCH: {
	    		if (selectionArgs == null) {
	    			throw new IllegalArgumentException(
	    					"selectionArgs must be provided for the Uri: " + uri);
	    		}
	    		return JewellBizDatabase.search(selection, selectionArgs, db);
	    	}
	    	case SEARCH_SUGGEST: {
	    		if (selectionArgs == null) {
                    throw new IllegalArgumentException(
                            "selectionArgs must be provided for the Uri: " + uri);
	    		}
	    		return JewellBizDatabase.getSuggestions(selectionArgs[0], db);
	    	}
	    	default: {
                // Most cases are handled with simple SelectionBuilder
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                Cursor query = builder.where(selection, selectionArgs).query(db, projection,
                        sortOrder);
                query.setNotificationUri(getContext().getContentResolver(), uri);
                return query;
	    	}
    	}
    	

	}
    
    @Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    	if (LOGV)
            Log.v(TAG, "update(uri=" + uri + ", values=" + values.toString() + ")");
    	final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
	}
    
    
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		if (LOGV)
            Log.v(TAG, "delete(uri=" + uri + ")");
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
	}


	/**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
    	
    	final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    	
    }
	
	
	/**
     * Build a simple {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually enough to support {@link #insert},
     * {@link #update}, and {@link #delete} operations.
     */
    private SelectionBuilder buildSimpleSelection(Uri uri) {
    	final SelectionBuilder builder = new SelectionBuilder();
    	final int match = sUriMatcher.match(uri);
        switch (match) {
	        case ARTICLES: {
	        	return builder.table(Tables.ARTICLES);
	        }
	        case ARTICLES_ID: {
	        	final String articleId = Articles.getArticleId(uri);
	        	return builder.table(Tables.ARTICLES).where(Articles.REF_ARTICLE_ID + "=?", articleId);
	        }
	        case ARTICLESEARCH_ID: {
	        	final String rowid = ArticleSearch.getDocId(uri);
	        	return builder.table(Tables.ARTICLES_SEARCH).where(ArticleSearch._DOCID + "=?", rowid);
	        }
	        default: {
	            throw new UnsupportedOperationException("Unknown uri: " + uri);
	        }
        }
    }
    
    /**
     * Build an advanced {@link SelectionBuilder} to match the requested
     * {@link Uri}. This is usually only used by {@link #query}, since it
     * performs table joins useful for {@link Cursor} data.
     */
	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
			case ARTICLES: {
	            return builder.table(Tables.ARTICLES);
	        }
	        case ARTICLES_ID: {
	            final String articleId = Articles.getArticleId(uri);
	            return builder.table(Tables.ARTICLES).where(Articles._ID + "=?", articleId);
	        }
	        default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
		}
	}

}
