package com.streaming.indianresso.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.streaming.indianresso.Common;

public class PlayPauseBroadcastReceiver extends BroadcastReceiver {

	private Common mApp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mApp = (Common) context.getApplicationContext();
		
		if (mApp.isServiceRunning())
			mApp.getService().togglePlaybackState();
		
	}

}
