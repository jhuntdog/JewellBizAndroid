package com.jewellbiz.android.jewellbiz.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class JewellContracts {
	
	interface ArticlesColumns {
		
		String REF_ARTICLE_ID = "article_id";
		
		String ARTICLE_TITLE = "articletitle";
		
		String ARTICLE_URL = "articleurl";
		
		String ARTICLE_DATE = "articledate";
		
		String READ = "read";
		
		String OVERVIEW = "articledescription";
		
	}
	
	interface ArticleSearchColumns {
		
		String _DOCID = "docid";
		
		String TITLE = Articles.ARTICLE_TITLE;
		
		String DESC = Articles.OVERVIEW;
		
	}
	
	
	public static final String CONTENT_AUTHORITY = "com.jewellbiz.android.jewellbiz";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    
    public static final String PATH_ARTICLES = "articles";
        
    public static final String PATH_ARTICLESEARCH = "articlesearch";

    public static final String PATH_RENEWFTSTABLE = "renewftstable";

    public static final String PATH_SEARCH = "search";
    
    
    public static class Articles implements ArticlesColumns, BaseColumns {
    	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ARTICLES).build();

        /** Use if a single item is returned */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/jbz-articles";
    
        /** Use if multiple items get returned */
    	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/jbz-articles";
    	
    	public static Uri buildArticleUri(String articleId) {
    		return CONTENT_URI.buildUpon().appendPath(articleId).build();
    	}
    	
    	public static String getArticleId(Uri uri) {
            return uri.getLastPathSegment();
        }
    	
    }
    
    public static class ArticleSearch implements ArticleSearchColumns {
    	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ARTICLESEARCH).build();
    	
    	public static final Uri CONTENT_URI_SEARCH = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ARTICLESEARCH).appendPath(PATH_SEARCH).build();

        public static final Uri CONTENT_URI_RENEWFTSTABLE = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RENEWFTSTABLE).build();
        
        public static Uri buildDocIdUri(String rowId) {
            return CONTENT_URI.buildUpon().appendPath(rowId).build();
        }
        
        public static String getDocId(Uri uri) {
            return uri.getLastPathSegment();
        }
    	
    }
    
    private JewellContracts() {
    	
    }
    
    
    
    

}
