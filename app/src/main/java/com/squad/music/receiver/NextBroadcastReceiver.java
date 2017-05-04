package com.squad.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squad.music.Common;

public class NextBroadcastReceiver extends BroadcastReceiver {
	
	private Common mApp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mApp = (Common) context.getApplicationContext();
		
		if (mApp.isServiceRunning())
			mApp.getService().skipToNextTrack();

	}
	  
}
