package com.jewellbiz.android.jewellbiz.data;

import com.jewellbiz.android.jewellbiz.data.JewellContracts.ArticleSearch;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.ArticleSearchColumns;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.Articles;
import com.jewellbiz.android.jewellbiz.data.JewellContracts.ArticlesColumns;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class JewellBizDatabase extends SQLiteOpenHelper {
	
	private static final String TAG = "JewellBizDatabase";
	
	private static String DATABASE_NAME = "jewelldatabase";
	
	private static int DATABASE_VERSION = 4;
	
	public interface Tables {
		String ARTICLES = "articles";
		
		String ARTICLES_SEARCH = "searchtable";
	}
	
	interface References {
		String ARTICLE_ID = "REFERENCES " + Tables.ARTICLES + "(" + BaseColumns._ID + ")";
	}
	
	private static final String CREATE_ARTICLES_TABLE = "CREATE TABLE " + Tables.ARTICLES + " ("
			+ BaseColumns._ID + " INTEGER PRIMARY KEY," + ArticlesColumns.ARTICLE_TITLE + " TEXT NOT NULL,"
			+ ArticlesColumns.ARTICLE_URL + " TEXT UNIQUE NOT NULL," 
			+ ArticlesColumns.ARTICLE_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')),"
			+ ArticlesColumns.OVERVIEW + " TEXT," 
			+ ArticlesColumns.READ + " INTEGER NOT NULL default 0" + ");";
	
	private static final String CREATE_SEARCH_TABLE = "CREATE VIRTUAL TABLE "
            + Tables.ARTICLES_SEARCH + " USING FTS3(" + ArticleSearchColumns.TITLE + " TEXT,"
            + ArticleSearchColumns.DESC + " TEXT" + ");";
	
	public JewellBizDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ARTICLES_TABLE);
		
		db.execSQL(CREATE_SEARCH_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		
		// run necessary upgrades
        int version = oldVersion;
        switch (version) {
	        case 1:
	        	upgradeToTwo(db);
	        	version = 2;
	        case 2:
	        	upgradeToThree(db);
	        	version = 3;
	        case 3:
	        	upgradeToFour(db);
	        	version = 4;
        }
        
        // drop all tables if version is not right
        Log.d(TAG, "after upgrade logic, at version " + version);
        if (version != DATABASE_VERSION) {
        	Log.w(TAG, "Database has incompatible version, starting from scratch");
        	db.execSQL("DROP TABLE IF EXISTS " + Tables.ARTICLES);

            db.execSQL("DROP TABLE IF EXISTS " + Tables.ARTICLES_SEARCH);
        }
		
	}
	
	/**
	 * Add a {@link Read} column for marking articles as read
	 */
	private void upgradeToFour(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + Tables.ARTICLES + " ADD COLUMN " + ArticlesColumns.OVERVIEW + " text");
	}
	
	/**
	 * Add a {@link Read} column for marking articles as read
	 */
	private void upgradeToThree(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + Tables.ARTICLES + " ADD COLUMN " + ArticlesColumns.READ + " INTEGER NOT NULL DEFAULT 0");
	}
	
	/**
	 * Add a {@link Date} column for storing article date
	 */
	private void upgradeToTwo(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + Tables.ARTICLES + " ADD COLUMN " + ArticlesColumns.ARTICLE_DATE + " INTEGER NOT NULL DEFAULT '1297728000' ");
	}
	
	public static void onRenewFTSTable(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL("drop table if exists " + Tables.ARTICLES_SEARCH);
			db.execSQL(CREATE_SEARCH_TABLE);
			db.execSQL("INSERT INTO " + Tables.ARTICLES_SEARCH + "(docid," + Articles.ARTICLE_TITLE + ","
					+ Articles.OVERVIEW + ")" + " select " + Articles._ID + "," + Articles.ARTICLE_TITLE
					+ "," + Articles.OVERVIEW + " from " + Tables.ARTICLES + ";");
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public static Cursor search(String selection, String[] selectionArgs, SQLiteDatabase db) {
		
		StringBuilder query = new StringBuilder();
		
		// select final result columns
        query.append("SELECT ");
        query.append(Articles._ID).append(",");
        query.append(Articles.ARTICLE_TITLE).append(",");
        query.append(Articles.ARTICLE_URL).append(",");
        query.append(Articles.ARTICLE_DATE).append(",");
        query.append(Articles.OVERVIEW).append(",");
        query.append(Articles.REF_ARTICLE_ID);
        
        query.append(" FROM ");
        query.append("(");
        
        query.append("(");
        query.append("SELECT ");
        query.append(Articles._ID).append(",");
        query.append(Articles.ARTICLE_TITLE).append(",");
        query.append(Articles.OVERVIEW);
        query.append(" FROM ");
        
        // join searchtable results...
        query.append("(");
        query.append("SELECT ");
        query.append(ArticleSearch._DOCID).append(",");
        query.append("snippet(" + Tables.ARTICLES_SEARCH + ",'<b>','</b>','...')").append(" AS ")
        	.append(Articles.OVERVIEW);
        query.append(" FROM ").append(Tables.ARTICLES_SEARCH);
        query.append(" WHERE ").append(Tables.ARTICLES_SEARCH).append(" MATCH ?");
        query.append(")");
        query.append(" JOIN ");
        // ...with articles table
        query.append("(");
        query.append("SELECT ");
        query.append(Articles._ID).append(",");
        query.append(Articles.ARTICLE_TITLE).append(",");
        query.append(Articles.OVERVIEW);
        query.append(" FROM ").append(Tables.ARTICLES);
        query.append(")");
        query.append(" ON ").append(Articles._ID).append("=").append(ArticleSearch._DOCID);
        
        query.append(")");
        query.append(" ON ").append("sid=").append(Articles.REF_ARTICLE_ID);
        query.append(")");
        
        // append given selection
        if (selection != null) {
            query.append(" WHERE ");
            query.append("(").append(selection).append(")");
        }
        
        // ordering
        query.append(" ORDER BY ");
        query.append(Articles.ARTICLE_TITLE).append(" ASC,");
        query.append(Articles.ARTICLE_DATE).append(" ASC,");
        
        // search for anything starting with the given search term
        selectionArgs[0] = "\"" + selectionArgs[0] + "*\"";

        return db.rawQuery(query.toString(), selectionArgs);

	}

	public static Cursor getSuggestions(String searchterm, SQLiteDatabase db) {
		StringBuilder query = new StringBuilder("select _id," + Articles.ARTICLE_TITLE + " as "
				+ SearchManager.SUGGEST_COLUMN_TEXT_1 + "," + Articles.OVERVIEW + " as "
				+ SearchManager.SUGGEST_COLUMN_TEXT_2 + "," + "_id as "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID + " from ((select _id as sid,"
				+ Articles.ARTICLE_TITLE + " from " + Tables.ARTICLES + ")" + " join " + "(select _id,"
				+ Articles.ARTICLE_TITLE + "," + Articles.REF_ARTICLE_ID + " from " + "(select docid" + " from "
				+ Tables.ARTICLES_SEARCH + " where " + Tables.ARTICLES_SEARCH + " match " + "?)"
				+ " join " + "(select _id," + Articles.ARTICLE_TITLE + "," + Articles.REF_ARTICLE_ID
				+ " from articles" + "on _id=docid)" + "on sid=" + Articles.REF_ARTICLE_ID + ")");
		// search for anything starting with the given search term
        return db.rawQuery(query.toString(), new String[] {
            "\"" + searchterm + "*\""
        });
	}
	
	

}
