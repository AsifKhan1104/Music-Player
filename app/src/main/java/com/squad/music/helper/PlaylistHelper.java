package com.squad.music.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.squad.music.model.Playlist;
import com.squad.music.model.Song;

import java.util.ArrayList;

/**
 * Created by mdasifkhan on 2/6/17.
 */

public class PlaylistHelper {

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

    public static void deletePlaylist(Context context, String playlistId) {
        ContentResolver resolver = context.getContentResolver();
        String where = MediaStore.Audio.Playlists._ID + "=?";
        String[] whereVal = {playlistId};
        resolver.delete(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, where, whereVal);
        return;
    }
}
