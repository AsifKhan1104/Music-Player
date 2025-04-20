package com.streaming.indianresso.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.streaming.indianresso.Common;

/**
 * BroadcastReceiver that handles and processes all headset
 * unplug/plug actions and events.
 *
 * @author Saravan Pantham
 */
public class HeadsetPlugBroadcastReceiver extends BroadcastReceiver {

    private Common mApp;

    @Override
    public void onReceive(Context context, Intent intent) {

        mApp = (Common) context.getApplicationContext();

        if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    //Headset unplug event.
                    if (mApp != null && mApp.getService() != null && mApp.getService().isPlayingMusic())
                        mApp.getService().pausePlayback();
                    break;
                case 1:
                    //Headset plug-in event.
                    // Not doing anything when plugged in
                    // mApp.getService().startPlayback();
                    break;
                default:
                    //No idea what just happened.
            }

        }

    }

}
