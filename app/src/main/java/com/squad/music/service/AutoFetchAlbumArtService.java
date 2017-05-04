package com.squad.music.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.squad.music.R;
import com.squad.music.asynctask.AsyncAutoGetAlbumArtTask;

public class AutoFetchAlbumArtService extends Service {
	
	private Context mContext;
	private SharedPreferences sharedPreferences;
	public static NotificationCompat.Builder builder;
	public static Notification notification;
	public static final int NOTIFICATION_ID = 2; //NOTE: Using 0 as a notification ID causes the Android to ignore the notification call.
	
	//Prepare the media player for first use.
	@Override
	public void onCreate() {
		
		//Initialize SharedPreferences.
        sharedPreferences = this.getSharedPreferences("com.squad.music", Context.MODE_PRIVATE);
        mContext = this;
		super.onCreate();
		
	}
	
	//This method is called when the service is created.
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {

		//Launch a notification to set the service as a foreground service.
		builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(getResources().getString(R.string.downloading_missing_cover_art));
        builder.setTicker(getResources().getString(R.string.downloading_missing_cover_art));
        builder.setContentText(null);
        
        notification = builder.build();
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        startForeground(NOTIFICATION_ID, notification);
		
        //Call the AsyncTask that checks for missing art and downloads them.

        /*UNCOMMENT BELOW LINE*/

        /*AsyncAutoGetAlbumArtTask task = new AsyncAutoGetAlbumArtTask(mContext, SettingsActivity____.mSettingsActivity);
        task.execute();*/
        
        return START_STICKY;
    }
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

}
