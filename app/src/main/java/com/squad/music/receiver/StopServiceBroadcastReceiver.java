package com.squad.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.squad.music.Common;

public class StopServiceBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//Stop the service.
		Common app = (Common) context.getApplicationContext();
		app.getService().stopSelf();
		
	}

}
