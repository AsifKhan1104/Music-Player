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
        boolean flag = createPlaylist(mPlaylistName);
        if (flag)
            publishProgress(0);
        else
            publishProgress(1);

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                //Common.displayToast(R.string.playlist_created, Toast.LENGTH_SHORT);
                Toast.makeText(mContext, R.string.playlist_created, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                //Common.displayToast(R.string.playlist_could_not_be_created, Toast.LENGTH_SHORT);
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

    // create new playlist
    private boolean createPlaylist(String pname) {

        boolean flag = false;
        Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = mContext.getContentResolver();
        Cursor c = resolver.query(playlists, new String[]{"*"}, null, null,
                null);
        long playlistId = 0;
        c.moveToFirst();
        do {
            String plname = c.getString(c
                    .getColumnIndex(MediaStore.Audio.Playlists.NAME));
            if (plname.equalsIgnoreCase(pname)) {
                playlistId = c.getLong(c
                        .getColumnIndex(MediaStore.Audio.Playlists._ID));
                break;
            }
        } while (c.moveToNext());
        c.close();

        if (playlistId != 0) {
            Uri deleteUri = ContentUris.withAppendedId(playlists, playlistId);
            Log.d(TAG, "REMOVING Existing PlaylistQueries: " + playlistId);

            // delete the playlist
            resolver.delete(deleteUri, null, null);
        }

        Log.d(TAG, "CREATING PLAYLIST: " + pname);
        ContentValues v1 = new ContentValues();
        v1.put(MediaStore.Audio.Playlists.NAME, pname);
        v1.put(MediaStore.Audio.Playlists.DATE_MODIFIED,
                System.currentTimeMillis());
        Uri newpl = resolver.insert(playlists, v1);
        Log.d(TAG, "Added PlayLIst: " + newpl);

        flag = true;
        return flag;
    }
}
