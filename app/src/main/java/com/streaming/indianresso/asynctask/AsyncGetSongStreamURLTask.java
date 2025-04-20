package com.streaming.indianresso.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.gmusic.GMusicClientCalls;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class AsyncGetSongStreamURLTask extends AsyncTask<String, Void, Boolean> {
    private Context mContext;
    private Common mApp;
    private String mSongID;
    
    public AsyncGetSongStreamURLTask(Context context, String songID) {
    	mContext = context;
    	mApp = (Common) mContext;
    	mSongID = songID;
    	
    }
 
    @Override
    protected Boolean doInBackground(String... params) {
    	
    	if (mSongID.equals(mApp.getService().getCurrentSong().getId())) {
    		try {
    			mApp.getService().getCurrentSong().setFilePath(GMusicClientCalls.getSongStream(mSongID).toURL().toString());
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    			return false;
    		} catch (JSONException e) {
    			e.printStackTrace();
    			return false;
    		} catch (URISyntaxException e) {
    			e.printStackTrace();
    			return false;
    		} catch (Exception e) {
    			e.printStackTrace();
    			return false;
    		}
        	
        	if (mApp.getService().getCurrentSong().getId()==null) {
        		return false;
        	} else {
        		return true;
        	}
        	
    	} else {
    		this.cancel(true);
    		return false;
    	}
    	
    }

    @Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		try {
			if (result==true && mApp.getService().getCurrentSong().getId().equals(mSongID)) {
				//We got the right URL, so go ahead and prepare the media player.
				mApp.getService().startPlayback();
			} else if (result==false && mApp.getService().getCurrentSong().getId().equals(mSongID)) {
				//We were unable to get the url, so skip to the next song.
				mApp.getService().skipToNextTrack();
				Toast.makeText(mContext, R.string.song_failed_to_load, Toast.LENGTH_LONG).show();
			} else {
				//The song has been changed, so the URL is now useless. Exit this AsyncTask.
				return;
			}
			
		} catch (Exception e) {
			return;
		}

	}

}
