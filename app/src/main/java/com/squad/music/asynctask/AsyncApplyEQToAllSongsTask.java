package com.squad.music.asynctask;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.widget.Toast;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.ui.activity.EqualizerActivity;

public class AsyncApplyEQToAllSongsTask extends AsyncTask<String, Void, Void> {

    private Context mContext;
    private Common mApp;
    private EqualizerActivity mEqualizerFragment;

    public AsyncApplyEQToAllSongsTask(Context context, EqualizerActivity fragment) {
        mContext = context;
        mEqualizerFragment = fragment;
        mApp = (Common) context.getApplicationContext();
    }

    protected void onPreExecute() {
        Toast.makeText(mContext, R.string.applying_equalizer_to_all_songs, Toast.LENGTH_SHORT).show();

    }

    @Override
    protected Void doInBackground(String... params) {

        //Get a cursor with all the songs in the library.
        Cursor songsCursor = mApp.getDBAccessHelper().getAllSongs();

        //Loop through the songs and add them to the EQ settings DB with the current EQ settings.
        if (songsCursor != null && songsCursor.getCount() > 0) {

            for (int j = 0; j < songsCursor.getCount(); j++) {
                songsCursor.moveToPosition(j);

                String songId = songsCursor.getString(songsCursor.getColumnIndex(DBAccessHelper.SONG_ID));

                mEqualizerFragment.setEQValuesForSong(songId);

            }

        }

        if (songsCursor != null)
            songsCursor.close();

        return null;

    }

    @Override
    protected void onPostExecute(Void arg0) {
        Toast.makeText(mContext, R.string.equalizer_applied_to_all_songs, Toast.LENGTH_SHORT).show();

    }

}
