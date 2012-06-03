package com.jewellbiz.android.jewellbiz.util;

import com.jewellbiz.android.jewellbiz.data.JewellContracts.Articles;
import com.jewellbiz.android.jewellbiz.items.JbzArticle;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DBUtils {
	
	static final String Tag = "JewellBizDatabase";
	
	public static final String UNKNOWN_ARTICLE_PUB_DATE = "9223372036854775807";
	
	 /**
     * Fetches the row to a given article id and returns the results an JewellBlog
     * object. Returns {@code null} if there is no article with that id.
     * 
     * @param article id
     * @return
     */
	public static JbzArticle getArticle(Context context, String articleId) {
		JbzArticle article = new JbzArticle();
		Cursor details = context.getContentResolver().query(Articles.buildArticleUri(articleId), null, null, null, null);
		if (details.moveToFirst()) {
			article.setJbzArticleName(details.getString(details.getColumnIndexOrThrow(Articles.ARTICLE_TITLE)));
			article.setJbzArticleUrl(details.getString(details.getColumnIndexOrThrow(Articles.ARTICLE_URL)));
			article.setJbzArticleDateInt(details.getColumnIndexOrThrow(Articles.ARTICLE_DATE));
			article.setId(details.getString(details.getColumnIndexOrThrow(Articles._ID)));
			article.setJbzArticleDescription(details.getString(details.getColumnIndexOrThrow(Articles.OVERVIEW)));
		} else {
			article = null;
		}
		details.close();
		return article;
		
	}
	
	public static boolean isArticleExists(String articleId, Context context) {
		Cursor testsearch = context.getContentResolver().query(Articles.buildArticleUri(articleId), 
				new String[] {
			Articles._ID
		}, null, null, null);
		boolean isArticleExists = testsearch.getCount() !=0 ? true : false;
		testsearch.close();
		return isArticleExists;
	}
	
	public static ContentProviderOperation buildArticleOp(JbzArticle article, Context context, boolean isNew) {
		ContentValues values = new ContentValues();
		values = putCommonArticleValues(article, values);
		
		if (isNew) {
			values.put(Articles._ID, article.getId());
			return ContentProviderOperation.newInsert(Articles.CONTENT_URI).withValues(values).build();
		} else {
			return ContentProviderOperation.newUpdate(Articles.buildArticleUri(article.getId()))
                    .withValues(values).build();
		}
	}
	
	 private static ContentValues putCommonArticleValues(JbzArticle article, ContentValues values) {
		 values.put(Articles.ARTICLE_TITLE, article.getJbzArticleName());
		 values.put(Articles.ARTICLE_URL, article.getJbzArticleUrl());
		 values.put(Articles.ARTICLE_DATE, article.getJbzArticleDateInt());
		 values.put(Articles.OVERVIEW, article.getOverview());
		 return values; 
	 }

}
