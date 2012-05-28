package com.jewellbiz.android.jewellbiz.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.jewellbiz.android.jewellbiz.R;
import com.jewellbiz.android.jewellbiz.data.JbzDatabase;
import com.jewellbiz.android.jewellbiz.data.JbzProvider;
import com.jewellbiz.android.jewellbiz.ui.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class JbzDownloaderService extends Service{
	
	private static final String DEBUG_TAG = "JbzDownloaderService";
	private DownloaderTask jbzArticleDownloader;
	
	private static final int LIST_UPDATE_NOTIFICATION = 100;
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		URL articlePath;
		try {
			String url = intent.getDataString();
			if (url !=null && (url.length() > 0)) {
				articlePath = new URL(url);
				jbzArticleDownloader = new DownloaderTask();
				jbzArticleDownloader.execute(articlePath);
			}
		} catch (MalformedURLException e) {
			Log.e(DEBUG_TAG, "Bad URL", e);
		}
		return Service.START_FLAG_REDELIVERY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	private class DownloaderTask extends AsyncTask<URL, Void, Boolean> {
		
		private static final String DEBUG_TAG = "JbzDownloaderService$DownloaderTask";
		
		@Override
		protected Boolean doInBackground(URL... params) {
			boolean succeeded = false;
			URL downloadPath = params[0];
			
			if (downloadPath != null) {
				succeeded = xmlParse(downloadPath);
			}
			return succeeded;
		}
		
		private boolean xmlParse(URL downloadPath) {
			boolean succeeded = false;
			
			XmlPullParser articles;
			
			try {
				articles = XmlPullParserFactory.newInstance().newPullParser();
				articles.setInput(downloadPath.openStream(), null);
				int eventType = -1;
				// psuedo code--
                // for each found "item" tag, find "link" and "title" tags
                // before end tag "item"
				
				while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = articles.getName();
                        if (tagName.equals("item")) {

                            ContentValues articleData = new ContentValues();
                            // inner loop looking for link, title, and date
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                if (eventType == XmlPullParser.START_TAG) {
                                    
                                	if (articles.getName().equals("link")) {
                                    	articles.next();
                                        Log.d(DEBUG_TAG, "Link: " + articles.getText());
                                        articleData.put(JbzDatabase.COL_URL, articles.getText());
                                        
                                    } else if (articles.getName().equals("title")) {
                                    	articles.next();
                                    	articleData.put(JbzDatabase.COL_TITLE, articles.getText());
                                    	
                                    } else if (articles.getName().equals("pubDate")) {
                                    	articles.next();
                                    	DateFormat parser = new SimpleDateFormat("E, dd MMM yyyy");
                                    	try {
                                    		Date date = parser.parse(articles.getText());
                                    		articleData.put(JbzDatabase.COL_DATE, date.getTime() / 1000);
                                    		} catch (ParseException e) {
                                    			Log.e(DEBUG_TAG, "Error parsing date: " + articles.getText());
                                    		}
                                    }
                                    
                                } else if (eventType == XmlPullParser.END_TAG) {
                                    if (articles.getName().equals("item")) {
                                        // save the data, and then continue with
                                        // the outer loop
                                        getContentResolver().insert(JbzProvider.CONTENT_URI, articleData);
                                        break;
                                    }
                                }
                                eventType = articles.next();
                            }
                        }
                    }
                    eventType = articles.next();
                }
				// no exceptions during parsing
				succeeded = true;
				
			} catch (XmlPullParserException e) {
            	Log.e(DEBUG_TAG, "Error during parsing", e);
            } catch (IOException e) {
            	 Log.e(DEBUG_TAG, "IO Error during parsing", e);
            }
			
			return succeeded;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Context ctx = JbzDownloaderService.this.getApplicationContext();
			
			Intent notificationIntent = new Intent(ctx, MainActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
			
			Resources res = ctx.getResources();
			Notification.Builder builder = new Notification.Builder(ctx);
			
			builder.setContentIntent(contentIntent)
				.setSmallIcon(R.drawable.stat_notify_sync)
				.setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.wj_android_notification_icon))
				.setTicker(res.getString(R.string.notification_title))
				.setWhen(System.currentTimeMillis())
				.setAutoCancel(true)
				.setContentTitle(res.getString(R.string.notification_title))
				.setContentText(res.getString(R.string.notification_bar_text));
			Notification n = builder.getNotification();
	        
	        notificationManager.notify(LIST_UPDATE_NOTIFICATION, n);
	        
	        //update widget
	        //JbzWidgetProvider.updateWidgetContent(ctx, AppWidgetManager.getInstance(ctx));
	        
	        // all done
	        stopSelf();
			
		}
		
		// Original Notifcation Manager
		
		/*@Override
		protected void onPostExecute(Boolean result) {
			Context context = JbzDownloaderService.this.getApplicationContext();
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(NOTIFICATION_SERVICE);
			
			Notification updateComplete = new Notification();
			updateComplete.icon = R.drawable.stat_notify_sync;
			updateComplete.tickerText = context
					.getText(R.string.notification_title);
			updateComplete.when = System.currentTimeMillis();
			
			Intent notificationIntent = new Intent(context,
					JewellBizTodayActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
					notificationIntent, 0);
			
			String contentTitle = context.getText(R.string.notification_title).toString();
			
			String contentText;
			if (!result) {
	            Log.w(DEBUG_TAG, "XML download and parse had errors");
	            contentText = context.getText(R.string.notification_info_fail)
	                .toString();
	        } else {
	            contentText = context.getText(
	                R.string.notification_info_success).toString();
	        }
			updateComplete.setLatestEventInfo(context, contentTitle,
	                contentText, contentIntent);
	        
	        notificationManager
	                .notify(LIST_UPDATE_NOTIFICATION, updateComplete);
	        
	        //update widget
	        JbzWidgetProvider.updateWidgetContent(context, AppWidgetManager.getInstance(context));
	        
	        // all done
	        stopSelf();
			
		}*/

	}
	
	

}
