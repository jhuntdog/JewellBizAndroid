package com.jewellbiz.android.jewellbiztoday.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

public class JbzDatabase extends SQLiteOpenHelper {
	
	private static final String DEBUG_TAG = "JbzDatabase";
	public static final int DB_VERSION = 4;
	private static final String DB_NAME = "jbz_database";
	
	public static final String TABLE_ARTICLES = "articles";
	public static final String ID = "_id";
	public static final String COL_TITLE = "title";
	public static final String COL_URL = "url";
	
	public static final String COL_DATE = "article_date";
	
	public static final String COL_READ = "read";
	
	public static final String COL_DESC = "desc";
	
	private static final String CREATE_TABLE_ARTICLES = "CREATE TABLE " 
			+ TABLE_ARTICLES + " (" + ID 
			+ " integer PRIMARY KEY AUTOINCREMENT, " + COL_TITLE 
			+ " text NOT NULL, " + COL_URL + " text UNIQUE NOT NULL, "
			+ COL_DATE + " INTEGER NOT NULL DEFAULT (strftime('%s','now')), "
			+ COL_DESC + " text, "
			+ COL_READ + " INTEGER NOT NULL default 0" + ");";
	
	private static final String DB_SCHEMA = CREATE_TABLE_ARTICLES;
	
	// Searchable Database
	private static final String FTS_VIRTUAL_SEARCH_TABLE = "searchtable";
	
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
		Log.d(DEBUG_TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
		
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
        Log.d(DEBUG_TAG, "after upgrade logic, at version " + version);
        if (version != DB_VERSION) {
        	Log.w(DEBUG_TAG, "Database has incompatible version, starting from scratch");
        	db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
    		onCreate(db);
        }
	}
	
	/**
	 * Add a {@link Read} column for marking articles as read
	 */
	private void upgradeToFour(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + TABLE_ARTICLES + " ADD COLUMN " + COL_DESC + " text");
	}
	
	/**
	 * Add a {@link Read} column for marking articles as read
	 */
	private void upgradeToThree(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + TABLE_ARTICLES + " ADD COLUMN " + COL_READ + " INTEGER NOT NULL DEFAULT 0");
	}
	
	/**
	 * Add a {@link Date} column for storing article date
	 */
	private void upgradeToTwo(SQLiteDatabase db) {
		db.execSQL("ALTER TABLE " + TABLE_ARTICLES + " ADD COLUMN " + COL_DATE + " INTEGER NOT NULL DEFAULT '1297728000' ");
	}
	
	
	private void seedData(SQLiteDatabase db) {
        db.execSQL("insert into articles (title, url, article_date) values ('Welcome to the Spring Edition of Jewell Business Today', 'http://jewellbiz.com/2012/03/15/welcome-to-the-spring-edition-of-jewell-business-today/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Scott A. McCormick to Deliver 2012 Distinguished Executive Lecture', 'http://jewellbiz.com/2012/03/15/scott-a-mccormick-to-deliver-2012-distinguished-executive-lecture/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Career Spotlight – Hallmark', 'http://jewellbiz.com/2012/03/15/career-spotlight-hallmark/', (strftime('%s', '2012-03-15')));");
        db.execSQL("insert into articles (title, url, article_date) values ('Jewell Welcomes New Professor of Business and Nonprofit Leadership', 'http://jewellbiz.com/2012/03/15/jewell-welcomes-new-professor-of-business-and-nonprofit-leadership/', (strftime('%s', '2012-03-15')));");

    }
	

}
