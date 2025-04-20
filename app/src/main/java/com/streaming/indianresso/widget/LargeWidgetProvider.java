package com.streaming.indianresso.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.streaming.indianresso.asynctask.AsyncUpdateLargeWidgetTask;

public class LargeWidgetProvider extends AppWidgetProvider {

	private Context mContext;
	
	public static final String PREVIOUS_ACTION = "com.streaming.indianresso.PREVIOUS_ACTION";
	public static final String PLAY_PAUSE_ACTION = "com.streaming.indianresso.PLAY_PAUSE_ACTION";
	public static final String NEXT_ACTION = "com.streaming.indianresso.NEXT_ACTION";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent); 
		
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName thisAppWidget = new ComponentName(context.getPackageName(), LargeWidgetProvider.class.getName());
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

		onUpdate(context, appWidgetManager, appWidgetIds);

	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	mContext = context;
        final int N = appWidgetIds.length;
        
        AsyncUpdateLargeWidgetTask task = new AsyncUpdateLargeWidgetTask(mContext, N, appWidgetIds, appWidgetManager);
        task.execute();
        
    }
    
}
