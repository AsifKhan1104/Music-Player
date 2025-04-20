package com.streaming.indianresso.utility;

/**
 * Created by mdasifkhan on 5/5/17.
 */

public interface AppConstants {

    public interface NAV_ACTIONS {
        String ARTISTS = "Artists";
        String ALBUM_ARTISTS = "Album Artists";
        String ALBUMS = "Albums";
        String SONGS = "Songs";
        String PLAYLISTS = "Playlists";
        String GENRES = "Genres";
        String FOLDERS = "Folders";
        String SETTINGS = "Settings";
    }

    String KEY_SONG = "SONG";
    String KEY_ARTIST = "ARTIST";
    String KEY_ALBUM_ARTIST = "ALBUM_ARTIST";
    String KEY_ALBUM = "ALBUM";
    String KEY_GENRE = "GENRE";
    String KEY_ALBUM_BY_ALBUM_ARTIST = "ALBUM_BY_ALBUM_ARTIST";
    String KEY_PLAYLIST = "PLAYLIST";

    String EDIT_TYPE = "EditType";
    String ADD_TYPE = "AddType";

    String PLAYLIST_ID = "PlaylistId";
    String PLAYLIST_NAME = "PlaylistName";

    String SEARCH_QUERY = "searchQuery";

    String SCROBBLER_APP_PACKAGE_NAME = "com.adam.aslfms";
    String SCROBBLER_FLAG = "scrobblerFlag";
}
