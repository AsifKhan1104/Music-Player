package com.squad.music.asynctask;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.PlaylistHelper;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

public class AsyncCreateNewPlaylistTask2 extends AsyncTask<String, Integer, Boolean> {
    private Context mContext;
    private Cursor mCursor;
    private String mPlaylistName;
    private String mPlaylistId;
    private SharedPreferences sharedPreferences;
    private DBAccessHelper musicLibraryDBHelper;

    private String mArtist;
    private String mAlbum;
    private String mSong;
    private String mGenre;
    private String mAlbumArtist;
    private String mAddType;

    private final String[] PROJECTION_PLAYLIST = new String[]{
            MediaStore.Audio.Playlists._ID,
            MediaStore.Audio.Playlists.NAME,
            MediaStore.Audio.Playlists.DATA
    };

    public AsyncCreateNewPlaylistTask2(Context context,
                                       String playlistName,
                                       String ARTIST,
                                       String ALBUM,
                                       String SONG,
                                       String GENRE,
                                       String ALBUM_ARTIST,
                                       String ADD_TYPE) {

        mContext = context;
        mPlaylistName = playlistName;
        sharedPreferences = context.getSharedPreferences("com.squad.music", Context.MODE_PRIVATE);
        musicLibraryDBHelper = new DBAccessHelper(mContext);

        mArtist = ARTIST;
        mAlbum = ALBUM;
        mSong = SONG;
        mGenre = GENRE;
        mAlbumArtist = ALBUM_ARTIST;
        mAddType = ADD_TYPE;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        //Replace illegal characters in the playlistName.
        if (mPlaylistName.contains("/")) {
            mPlaylistName = mPlaylistName.replace("/", "_");
        }

        if (mPlaylistName.contains("\\")) {
            mPlaylistName = mPlaylistName.replace("\\", "_");
        }

        boolean flag = PlaylistHelper.createPlaylist(mContext, mPlaylistName);
        if (flag)
            publishProgress(new Integer[]{0});
        else
            publishProgress(new Integer[]{1});

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                Toast.makeText(mContext, R.string.playlist_created, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(mContext, R.string.playlist_could_not_be_created, Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }
}
