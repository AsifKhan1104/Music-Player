package com.squad.music.asynctask;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.utility.AppConstants;

public class AsyncAddSongsToPlaylistTask2 extends AsyncTask<String, Integer, Boolean> {
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

    public AsyncAddSongsToPlaylistTask2(Context context,
                                        String playlistName,
                                        String playlistId,
                                        String ARTIST,
                                        String ALBUM,
                                        String SONG,
                                        String GENRE,
                                        String ALBUM_ARTIST,
                                        String ADD_TYPE) {

        mContext = context;
        mPlaylistName = playlistName;
        mPlaylistId = playlistId;
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
        assignCursor();
        for (int i = 0; i < mCursor.getCount(); i++) {
            try {
                mCursor.moveToPosition(i);

                //Add the song to Android's MediaStore database.
                String artist = mCursor.getString(mCursor.getColumnIndex(DBAccessHelper.SONG_ARTIST));
                String album = mCursor.getString(mCursor.getColumnIndex(DBAccessHelper.SONG_ALBUM));
                String title = mCursor.getString(mCursor.getColumnIndex(DBAccessHelper.SONG_TITLE));

                try {
                    long audioId = getSongAudioId(artist, album, title);
                    if (audioId != -1) {
                        addSongToPlaylist(mContext.getContentResolver(), audioId, Long.parseLong(mPlaylistId));
                    }

                } catch (Exception e) {
                    //Just fail silently if this song can't be inserted into the playlist.
                    e.printStackTrace();
                    publishProgress(new Integer[]{1});
                }

            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(new Integer[]{1});
            }
        }
        //Show a confirmation toast message.
        publishProgress(new Integer[]{0});

        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                if (mCursor.getCount() == 1)
                    Toast.makeText(mContext, R.string.song_added_to_playlist, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(mContext, R.string.songs_added_to_playlist, Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(mContext, R.string.some_error_occurred, Toast.LENGTH_SHORT).show();
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

    //Retrieves and assigns the cursor based on the set of song(s) that are being enqueued.
    private void assignCursor() {
        DBAccessHelper dbHelper = new DBAccessHelper(mContext);

        if (mAddType.equals(AppConstants.KEY_SONG)) {
            String selection = DBAccessHelper.SONG_TITLE + "=" + "'" + mSong + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'0'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_TITLE + " ASC");

        } else if (mAddType.equals(AppConstants.KEY_ARTIST)) {
            String selection = DBAccessHelper.SONG_ARTIST + "=" + "'" + mArtist + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'0'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_ALBUM + " ASC" + ", " + DBAccessHelper.SONG_TRACK_NUMBER + "*1 ASC");

        } else if (mAddType.equals("ALBUM")) {

            String selection = DBAccessHelper.SONG_ARTIST + "=" + "'" + mArtist + "'" + " AND "
                    + DBAccessHelper.SONG_ALBUM + "=" + "'" + mAlbum + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'" + " AND "
                    + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_TRACK_NUMBER + "*1 ASC");

        } else if (mAddType.equals("ALBUM_BY_ALBUM_ARTIST")) {
            String selection = DBAccessHelper.SONG_ALBUM_ARTIST + "=" + "'" + mAlbumArtist + "'" + " AND "
                    + DBAccessHelper.SONG_ALBUM + "=" + "'" + mAlbum + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'" + " AND "
                    + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_TRACK_NUMBER + "*1 ASC");

        } else if (mAddType.equals("ALBUM_ARTIST")) {
            String selection = DBAccessHelper.SONG_ALBUM_ARTIST + "=" + "'" + mAlbumArtist + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'" + " AND "
                    + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_ALBUM + " ASC, " + DBAccessHelper.SONG_TRACK_NUMBER + "*1 ASC");

        } else if (mAddType.equals("TOP_25_PLAYED")) {

            String selection = DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'";

            mCursor = dbHelper.getTop25PlayedTracks(selection);
        } else if (mAddType.equals("RECENTLY_ADDED")) {
            String selection = DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'";

            mCursor = dbHelper.getRecentlyAddedSongs(selection);
        } else if (mAddType.equals("TOP_RATED")) {
            String selection = DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'";

            mCursor = dbHelper.getTopRatedSongs(selection);
        } else if (mAddType.equals("RECENTLY_PLAYED")) {
            String selection = DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'";

            mCursor = dbHelper.getRecentlyPlayedSongs(selection);
        } else if (mAddType.equals("PLAYLIST")) {
           /* String selection = " AND " + DBAccessHelper.MUSIC_LIBRARY_PLAYLISTS_NAME + "."
                             + DBAccessHelper.PLAYLIST_ID + "=" + "'" + mPlaylistId + "'";

            if (mApp.isGooglePlayMusicEnabled()) {
            	mCursor = dbHelper.getAllSongsInPlaylistSearchable(selection);
            } else {
            	mCursor = dbHelper.getLocalSongsInPlaylistSearchable(selection);
            }

            mPlayingNext = mPlaylistName;*/
        } else if (mAddType.equals("GENRE")) {

            String selection = DBAccessHelper.SONG_GENRE + "=" + "'" + mGenre + "'" + " AND "
                    + DBAccessHelper.BLACKLIST_STATUS + "=" + "'FALSE'" + " AND "
                    + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

            mCursor = dbHelper.getReadableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                    null,
                    selection,
                    null,
                    null,
                    null,
                    DBAccessHelper.SONG_ALBUM + " ASC, " +
                            DBAccessHelper.SONG_TRACK_NUMBER + "*1 ASC");

        }
    }

    private void addSongToPlaylist(ContentResolver resolver, long audioId, long playlistId) {

        String[] cols = new String[]{
                "count(*)"
        };

        Uri uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        Cursor cur = resolver.query(uri, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, Long.valueOf(base + audioId));
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(uri, values);
    }

    private long getSongAudioId(String artist, String album, String title) {
        artist = artist.replace("'", "''");
        album = album.replace("'", "''");
        title = title.replace("'", "''");

        String selection = MediaStore.Audio.AudioColumns.ALBUM + "=" + "'" + album + "'" + " AND "
                + MediaStore.Audio.AudioColumns.ARTIST + "=" + "'" + artist + "'" + " AND "
                + MediaStore.Audio.AudioColumns.TITLE + "=" + "'" + title + "'";

        Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                selection,
                null,
                null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        } else {
            return -1;
        }

    }
}
