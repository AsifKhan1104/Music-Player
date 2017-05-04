package com.squad.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squad.music.Common;
import com.squad.music.ui.activity.NowPlayingActivity;

public class LaunchNowPlayingReceiver extends BroadcastReceiver {
	
	private Common mApp;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		mApp = (Common) context.getApplicationContext();

		if (mApp.isServiceRunning()) {
            Intent activityIntent = new Intent(context, NowPlayingActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(activityIntent);

        }

	}
	  
}
