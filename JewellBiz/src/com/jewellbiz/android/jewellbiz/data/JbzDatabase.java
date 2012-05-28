package com.jewellbiz.android.jewellbiz.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class JbzDatabase extends SQLiteOpenHelper {
	
	private static final String DEBUG_TAG = "JbzDatabase";
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "jbz_database";
	
	public static final String TABLE_ARTICLES = "articles";
	public static final String ID = "_id";
	public static final String COL_TITLE = "title";
	public static final String COL_URL = "url";
	
	public static final String COL_DATE = "article_date";
	private static final String ALTER_ADD_COL_DATE = "ALTER TABLE "
			+ TABLE_ARTICLES + " ADD COLUMN " + COL_DATE 
			+ " INTEGER NOT NULL DEFAULT '1297728000' ";
	
	public static final String COL_READ = "read";
	private static final String ALTER_ADD_COL_READ = "ALTER TABLE "
			+ TABLE_ARTICLES + " ADD COLUMN " + COL_READ
			+ " INTEGER NOT NULL DEFAULT 0";
	
	private static final String CREATE_TABLE_ARTICLES = "CREATE TABLE " 
			+ TABLE_ARTICLES + " (" + ID 
			+ " integer PRIMARY KEY AUTOINCREMENT, " + COL_TITLE 
			+ " text NOT NULL, " + COL_URL + " text UNIQUE NOT NULL, "
			+ COL_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "	
			+ COL_READ + " INTEGER NOT NULL default 0" + ");";
	
	private static final String DB_SCHEMA = CREATE_TABLE_ARTICLES;
	
	public JbzDatabase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DB_SCHEMA);
		seedData(db);
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DEBUG_TAG, "Upgrading database");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
		onCreate(db);
		
		// example of how to check for when upgrading database
		// for new app versions
		/*if (newVersion == 3) {
			// try to keep the date, using alter tables
			if (oldVersion == 2) {
				db.execSQL(ALTER_ADD_COL_READ);
			} else if (oldVersion == 1) {
				db.execSQL(ALTER_ADD_COL_DATE);
				db.execSQL(ALTER_ADD_COL_READ);
			}
		} else {
			Log.w(DEBUG_TAG, "Upgrading database. Existing contents will be lost. ["
	                + oldVersion + "]->[" + newVersion + "]");
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POSTS);
	        onCreate(db);
		} */
		
	}
	
	private void seedData(SQLiteDatabase db) {
        db.execSQL("insert into articles (title, url, article_date) values ('Welcome to the Spring Edition of Jewell Business Today', 'http://jewellbiz.com/2012/03/15/welcome-to-the-spring-edition-of-jewell-business-today/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Scott A. McCormick to Deliver 2012 Distinguished Executive Lecture', 'http://jewellbiz.com/2012/03/15/scott-a-mccormick-to-deliver-2012-distinguished-executive-lecture/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Career Spotlight – Hallmark', 'http://jewellbiz.com/2012/03/15/career-spotlight-hallmark/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Jewell Welcomes New Professor of Business and Nonprofit Leadership', 'http://jewellbiz.com/2012/03/15/jewell-welcomes-new-professor-of-business-and-nonprofit-leadership/', (strftime('%s', '2012-03-15')));");

    }
	
    
    public Cursor getWordMatches(String query, String[] columns) {
    	String selection = COL_TITLE + " MATCH ?";
    	String[] selectionArgs = new String[] {query+"*"};
    	
		return query(selection, selectionArgs, columns);
    }

	private Cursor query(String selection, String[] selectionArgs, String[] columns) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(TABLE_ARTICLES);
		
		Cursor cursor = builder.query(getReadableDatabase(), columns, selection, selectionArgs, null, null, null);
		
		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		
		return cursor;
	}

}
