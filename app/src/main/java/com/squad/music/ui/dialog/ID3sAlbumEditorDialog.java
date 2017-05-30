package com.squad.music.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.ui.fragment.GridViewFragment;
import com.squad.music.utility.AppConstants;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ID3sAlbumEditorDialog extends DialogFragment {

    private static Activity parentActivity;
    private static Common mApp;
    private DialogFragment dialogFragment;
    private View rootView;

    private TextView albumText;
    private EditText albumEditText;
    private CheckBox albumCheckbox;
    private boolean albumEdited = false;

    private String ALBUM;
    private String CALLING_FRAGMENT;

    private ArrayList<String> albumsList;
    private ArrayList<String> songURIsList;
    private static ArrayList<String> songSourcesList;
    private static ArrayList<String> songIdsList;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mApp = (Common) getActivity().getApplicationContext();
        parentActivity = getActivity();
        dialogFragment = this;

        albumsList = new ArrayList<String>();
        songURIsList = new ArrayList<String>();
        songSourcesList = new ArrayList<String>();
        songIdsList = new ArrayList<String>();

        rootView = (View) parentActivity.getLayoutInflater().inflate(R.layout.fragment_edit_id3_album_dialog, null);

        albumText = (TextView) rootView.findViewById(R.id.edit_album_text);
        albumEditText = (EditText) rootView.findViewById(R.id.edit_album_field);
        albumCheckbox = (CheckBox) rootView.findViewById(R.id.album_checkbox);

        albumText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        albumText.setPaintFlags(albumText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        albumEditText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        albumEditText.setPaintFlags(albumText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Keep all the fields locked by default.
        albumCheckbox.setChecked(false);

        //Disable all EditTexts by default.
        albumEditText.setEnabled(false);

        //Register click registers on each checkbox.
        albumCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton checkbox, boolean checked) {

                if (checked == true) {
                    albumEdited = true;
                    albumEditText.setEnabled(true);
                    ;
                } else {
                    albumEdited = false;
                    albumEditText.setEnabled(false);
                }

            }

        });

        //Get the album and artist name.
        ALBUM = getArguments().getString(AppConstants.KEY_ALBUM);
        CALLING_FRAGMENT = getArguments().getString("CALLING_FRAGMENT");

        if (ALBUM != null) {
            songURIsList = getAllSongsInAlbum(ALBUM);

            //Populate the ArrayLists with the song tags.
            try {
                getSongTags(songURIsList);
            } catch (CannotReadException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (TagException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ReadOnlyFileException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvalidAudioFrameException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //Now check if any of the ArrayLists contain the same exact elements.
            boolean albumsSame = checkIfAllElementsEqual(albumsList);

            //Populate the EditTexts.
            setEditorFields(albumsSame, albumsList, albumEditText);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set the dialog title.
        builder.setTitle(R.string.edit_tags);
        builder.setView(rootView);
        builder.setPositiveButton(R.string.save, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                AsyncSaveAlbumTagsTask asyncSaveAlbumTagsTask = new AsyncSaveAlbumTagsTask(getActivity(), getActivity());
                asyncSaveAlbumTagsTask.execute();

            }

        });

        builder.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearArrayLists();
                dialogFragment.dismiss();

            }

        });

        return builder.create();

    }

    public static ArrayList<String> getAllSongsInAlbum(String albumName) {
        ArrayList<String> songURIsList = new ArrayList<String>();

        //Escape any rogue apostrophes.
        if (albumName.contains("'")) {
            albumName = albumName.replace("'", "''");
        }

        String selection = " AND " + DBAccessHelper.SONG_ALBUM + "=" + "'" + albumName + "'" + " AND "
                + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

        Cursor cursor = mApp.getDBAccessHelper().getAllSongsByAlbumArtist(selection);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {

            do {
                songURIsList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_FILE_PATH)));
                songSourcesList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_SOURCE)));
                songIdsList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ID)));
            } while (cursor.moveToNext());

        }

        cursor.close();
        return songURIsList;
    }

    public static ArrayList<String> getAllSongsByArtist(String artistName) {
        ArrayList<String> songURIsList = new ArrayList<String>();

        //Escape any rogue apostrophes.
        if (artistName.contains("'")) {
            artistName = artistName.replace("'", "''");
        }

        String selection = DBAccessHelper.SONG_ARTIST + "=" + "'" + artistName + "'"
                + " AND " + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

        String[] projection = {DBAccessHelper._ID, DBAccessHelper.SONG_FILE_PATH,
                DBAccessHelper.SONG_SOURCE, DBAccessHelper.SONG_ID};

        Cursor cursor = mApp.getDBAccessHelper().getWritableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                projection,
                selection,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {

            do {
                songURIsList.add(cursor.getString(1));
                songSourcesList.add(cursor.getString(2));
                songIdsList.add(cursor.getString(3));
            } while (cursor.moveToNext());

        }

        cursor.close();
        return songURIsList;
    }

    //This method loops through all the songs and saves their tags into ArrayLists.
    public void getSongTags(ArrayList<String> dataURIsList) throws CannotReadException,
            IOException,
            TagException,
            ReadOnlyFileException,
            InvalidAudioFrameException {

        Cursor cursor = null;
        for (int i = 0; i < dataURIsList.size(); i++) {

            //Check if the song is from Google Play Music.
            if (songSourcesList.get(i).equals(DBAccessHelper.GMUSIC)) {

                String songId = songIdsList.get(i);
                cursor = mApp.getDBAccessHelper().getSongById(songId);
                cursor.moveToFirst();

                albumsList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ALBUM)));
            } else {
                File file = null;
                try {
                    file = new File(dataURIsList.get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                AudioFile audioFile = AudioFileIO.read(file);

                albumsList.add(audioFile.getTag().getFirst(FieldKey.ALBUM));
            }

        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

    }

    //This method goes through the inputted ArrayList and checks if all its elements are the same.
    public static boolean checkIfAllElementsEqual(ArrayList<String> list) {

        if (list.size() != 0) {
            String firstElement = list.get(0);

            for (int i = 0; i < list.size(); i++) {
                if (!firstElement.equals(list.get(i))) {
                    return false;
                }

            }

            return true;
        }

        return false;
    }

    //This method sets the specified EditText values based on the boolean parameter.
    public static void setEditorFields(boolean allElementsSame, ArrayList<String> list, EditText editText) {

        if (allElementsSame == true) {
            editText.setText(list.get(0));
        } else {
            editText.setText(R.string.varies_by_song);
        }

    }

    //This AsyncTask gathers all the tags, iterates through each song, and saves them.
    class AsyncSaveAlbumTagsTask extends AsyncTask<String, String, String> {

        private Context mContext;
        private Activity mActivity;
        private ProgressDialog pd;
        private int i = 0;

        private String songTitle;
        private String songArtist;
        private String songAlbum;
        private String songAlbumArtist;
        private String songComposer;
        private String songProducer;
        private String songTrackNumber;
        private String songTrackTotals;
        private String songComments;
        private String songYear;

        public AsyncSaveAlbumTagsTask(Activity activity, Context context) {
            mContext = context;
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mActivity);
            pd.setTitle(R.string.saving_album_info);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setIndeterminate(false);
            pd.setMax(songURIsList.size());
            pd.show();

            //Retrieve the strings from the EditText fields.
            if (albumEdited) {
                songAlbum = albumEditText.getText().toString();
                songAlbum = songAlbum.replace("'", "''");
            } else {
                songAlbum = null;
            }
        }

        @Override
        protected String doInBackground(String... arg0) {

            for (i = 0; i < songURIsList.size(); i++) {
                publishProgress(new String[]{});

                //Check if the current song is from Google Play Music.
                if (songSourcesList.get(i).equals(DBAccessHelper.GMUSIC)) {
                    continue;
/*					String songId = songIdsList.get(i);

					try {
						String result = GMusicClientCalls.editTrackInfo(4, mContext, songId, songTitle, 
																   songAlbum, songArtist, songAlbumArtist, 
																   null, songComposer, songYear, 
																   songTrackNumber, songTrackTotals);
						Log.e("DEBUG", ">>>RESULT: " + result);
						
						 ==================================================
						 * ==================================================
						 * ==================================================
						 * MUST UPDATE THE DATABASE =========================
						 * ==================================================
						 * ==================================================
						 
						
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}*/

                } else {
                    File file = null;
                    try {
                        file = new File(songURIsList.get(i));
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    AudioFile audioFile = null;

                    try {
                        audioFile = AudioFileIO.read(file);
                    } catch (CannotReadException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (TagException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (ReadOnlyFileException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (InvalidAudioFrameException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Tag tag = audioFile.getTag();

                    //Update the DB entries.
                    if (tag != null) {
                        //Escape any rogue apostrophes.
                        String uri = songURIsList.get(i);

                        if (uri.contains("'")) {
                            uri = uri.replace("'", "''");
                        }

                        String whereClause = DBAccessHelper.SONG_FILE_PATH + "=" + "'" + uri + "'";
                        ContentValues values = new ContentValues();

                        if (albumEdited == false) {
                            //Don't do anything here. The user didn't change the album.
                        } else {
                            try {
                                tag.setField(FieldKey.ALBUM, songAlbum);
                            } catch (KeyNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (FieldDataInvalidException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (NoSuchElementException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            values.put(DBAccessHelper.SONG_ALBUM, songAlbum);
                        }

                        try {
                            audioFile.commit();
                        } catch (CannotWriteException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        //Write the values to the DB.
                        try {
                            mApp.getDBAccessHelper().getWritableDatabase().update(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                                    values,
                                    whereClause,
                                    null);

                            // Now refresh the fragment list
                            ((GridViewFragment) dialogFragment.getTargetFragment()).startRunnable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(mContext, R.string.unable_to_edit_album_tags, Toast.LENGTH_SHORT).show();
                    }

                }

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setProgress(i);
            //String message = mContext.getResources().getString(R.string.saving_song_info_for) + " " + titlesList.get(i) + ".";
            //pd.setMessage(message);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

/*			if (CALLING_FRAGMENT.equals("ALBUMS_FRAGMENT")) {

			} else if (CALLING_FRAGMENT.equals("ALBUM_ARTISTS_FLIPPED_FRAGMENT")) {
				AlbumArtistsFlippedFragment.getCursor();
				AlbumArtistsFlippedFragment.albumArtistsFlippedListViewAdapter.notifyDataSetChanged();
			} else if (CALLING_FRAGMENT.equals("GENRES_FLIPPED_FRAGMENT")) {
				GenresFlippedFragment.getCursor();
				GenresFlippedFragment.genresFlippedListViewAdapter.notifyDataSetChanged();
			} else {
				ArtistsFlippedFragment.artistsFlippedListView.setAdapter(null);
				ArtistsFlippedFragment.getCursor();
				ArtistsFlippedFragment.artistsFlippedListViewAdapter = new ArtistsFlippedListViewAdapter(mContext, ArtistsFlippedFragment.cursor);
				ArtistsFlippedFragment.artistsFlippedListView.setAdapter(ArtistsFlippedFragment.artistsFlippedListViewAdapter);
				ArtistsFlippedFragment.artistsFlippedListView.invalidate();
			}*/

            //Delete all the contents of the ArrayLists.
            clearArrayLists();
            dialogFragment.dismiss();
            Toast.makeText(parentActivity, R.string.changes_saved, Toast.LENGTH_SHORT).show();

        }

    }

    public void clearArrayLists() {
        if (albumsList != null) {
            albumsList.clear();
            albumsList = null;
        }

        if (songURIsList != null) {
            songURIsList.clear();
            songURIsList = null;
        }

        if (songSourcesList != null) {
            songSourcesList.clear();
            songSourcesList = null;
        }

        if (songIdsList != null) {
            songIdsList.clear();
            songIdsList = null;
        }

    }
}
