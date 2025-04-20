package com.streaming.indianresso.helper;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.streaming.indianresso.model.Playlist;
import com.streaming.indianresso.model.Song;

import java.util.ArrayList;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

/**
 * Created by mdasifkhan on 2/6/17.
 */

public class PlaylistHelper {

    // create new playlist
    public static boolean createPlaylist(Context context, String pname) {

        boolean flag = false;
        Uri playlists = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
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

    /* Queries all the playlists known to the MediaLibrary.
            *
            * @param context the context to use
	 * @return The queried cursor.
	 */
    public static ArrayList<Playlist> getAllPlaylists(Context context) {
        ArrayList<Playlist> list = new ArrayList<>();
        Uri uri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
        ContentResolver resolver = context.getContentResolver();
        Cursor c = resolver.query(uri, new String[]{"*"}, null, null,
                null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Playlist playlist = new Playlist();
                String plname = c.getString(c
                        .getColumnIndex(MediaStore.Audio.Playlists.NAME));
                long playlistId = c.getLong(c
                        .getColumnIndex(MediaStore.Audio.Playlists._ID));
                playlist.setPlaylistName(plname);
                playlist.setPlaylistId(playlistId);
                list.add(playlist);

            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    /*Retrieve songs from playlist*/
    public static ArrayList<Song> getSongsFromPlaylist(Context context, long playlistId) {
        ArrayList<Song> list = new ArrayList<>();
        String[] proj =
                {
                        MediaStore.Audio.Playlists.Members.AUDIO_ID,
                        MediaStore.Audio.Playlists.Members.ARTIST,
                        MediaStore.Audio.Playlists.Members.TITLE,
                        MediaStore.Audio.Playlists.Members._ID
                };

        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                proj, null, null, null);

        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                Song song = new Song();
                long id = c.getLong(c.getColumnIndex(MediaStore.Audio.Playlists.Members.AUDIO_ID));
                String title = c.getString(c
                        .getColumnIndex(MediaStore.Audio.Playlists.Members.TITLE));
                String artist = c.getString(c
                        .getColumnIndex(MediaStore.Audio.Playlists.Members.ARTIST));
                /*String uri = c.getString(c
                        .getColumnIndex(String.valueOf(MediaStore.Audio.Playlists.Members.getContentUri(title, playlistId))));*/

                song.setId(id);
                song.setTitle(title);
                song.setArtist(artist);
                //song.setUri(uri);
                list.add(song);

            } while (c.moveToNext());
        }
        c.close();

        return list;
    }

    // delete playlist using MediaStore
    public static void deletePlaylist(Context context, String playlistId) {
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistId};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        return;
    }

    // rename playlist using MediaStore
    public static void renamePlaylist(Context context, String newplaylist, String playlist_id) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        String where = MediaStore.Audio.Playlists._ID + " =? ";
        String[] whereVal = {playlist_id};
        values.put(MediaStore.Audio.Playlists.NAME, newplaylist);
        resolver.update(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values, where, whereVal);
    }
}
