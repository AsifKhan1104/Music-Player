package com.streaming.indianresso.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.streaming.indianresso.asynctask.AsyncBuildLibraryTask;
import com.streaming.indianresso.R;
import com.streaming.indianresso.ui.activity.WelcomeActivity;

public class BuildMusicLibraryService extends Service implements AsyncBuildLibraryTask.OnBuildLibraryProgressUpdate {

	private Context mContext;
	private Notification mNotification;
	private NotificationManager mNotifyManager;
	public static int mNotificationId = 92713;
	private final String CHANNEL_ID = "channel_007";

	@Override
	public void onCreate() {
		mContext = this.getApplicationContext();
	}

	@Override
	public int onStartCommand(Intent intent, int startId, int flags) {

		//Create a persistent notification that keeps this service running and displays the scan progress.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			createNotificationChannel(CHANNEL_ID);
			mNotification = new Notification.Builder(mContext, CHANNEL_ID)
					.setSmallIcon(R.drawable.notif_icon)
					.setContentTitle(getResources().getString(R.string.building_music_library))
					.setTicker(getResources().getString(R.string.building_music_library))
					.setContentText("")
					.setProgress(0, 0, true)
					.build();
		} else {
			mNotification = new NotificationCompat.Builder(mContext)
					.setSmallIcon(R.drawable.notif_icon)
					.setContentTitle(getResources().getString(R.string.building_music_library))
					.setTicker(getResources().getString(R.string.building_music_library))
					.setContentText("")
					.setProgress(0, 0, true)
					.build();

			mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;
		}

		startForeground(mNotificationId, mNotification);

		//Go crazy with a full-on scan.
		AsyncBuildLibraryTask task = new AsyncBuildLibraryTask(mContext, this);
		task.setOnBuildLibraryProgressUpdate(WelcomeActivity.mBuildingLibraryProgressFragment);
		task.setOnBuildLibraryProgressUpdate(this);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		return START_STICKY;
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private void createNotificationChannel(String channelId) {
		NotificationChannel channel = new NotificationChannel(channelId,
				getResources().getString(R.string.building_music_library),
				NotificationManager.IMPORTANCE_DEFAULT);

		((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStartBuildingLibrary() {

	}

	@Override
	public void onProgressUpdate(AsyncBuildLibraryTask task, String mCurrentTask, int overallProgress,
								 int maxProgress, boolean mediaStoreTransferDone) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			//createNotificationChannel(CHANNEL_ID);
			mNotification = new Notification.Builder(mContext, CHANNEL_ID)
					.setSmallIcon(R.drawable.notif_icon)
					.setContentTitle(mCurrentTask)
					.setTicker(mCurrentTask)
					.setContentText("")
					.setProgress(maxProgress, overallProgress, false)
					.build();
		} else {
			mNotification = new NotificationCompat.Builder(mContext)
					.setSmallIcon(R.drawable.notif_icon)
					.setContentTitle(mCurrentTask)
					.setTicker(mCurrentTask)
					.setContentText("")
					.setProgress(maxProgress, overallProgress, false)
					.build();

			mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;
		}

		mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification.flags |= Notification.FLAG_INSISTENT | Notification.FLAG_NO_CLEAR;
		mNotifyManager.notify(mNotificationId, mNotification);

	}

	@Override
	public void onFinishBuildingLibrary(AsyncBuildLibraryTask task) {
		mNotifyManager.cancel(mNotificationId);
		stopSelf();

		Toast.makeText(mContext, R.string.finished_scanning_album_art, Toast.LENGTH_LONG).show();

	}

}
