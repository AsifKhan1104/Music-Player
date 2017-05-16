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

import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.ui.fragment.GridViewFragment;
import com.squad.music.ui.fragment.ListViewFragment;
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

public class ID3sArtistEditorDialog extends DialogFragment {

    private Context mContext;
    private static Activity parentActivity;
    private DialogFragment dialogFragment;
    private View rootView;

    private TextView artistText;
    private EditText artistEditText;
    private CheckBox artistCheckbox;
    private boolean artistEdited = false;

    private String ARTIST;

    private ArrayList<String> artistsList = new ArrayList<String>();
    private ArrayList<String> songURIsList = new ArrayList<String>();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mContext = getActivity();
        parentActivity = getActivity();
        dialogFragment = this;

        //Get the artist name.
        ARTIST = getArguments().getString(AppConstants.KEY_ARTIST);

        rootView = (View) parentActivity.getLayoutInflater().inflate(R.layout.fragment_edit_id3_artist_dialog, null);

        artistText = (TextView) rootView.findViewById(R.id.edit_artist_text);
        artistEditText = (EditText) rootView.findViewById(R.id.edit_artist_field);
        artistCheckbox = (CheckBox) rootView.findViewById(R.id.artist_checkbox);
        artistText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        artistText.setPaintFlags(artistText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
        artistEditText.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        artistEditText.setPaintFlags(artistText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Keep all the fields locked by default.
        artistCheckbox.setChecked(false);

        //Disable all EditTexts by default.
        artistEditText.setEnabled(false);

        //Register click registers on each checkbox.
        artistCheckbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton checkbox, boolean checked) {

                if (checked == true) {
                    artistEdited = true;
                    artistEditText.setEnabled(true);
                    ;
                } else {
                    artistEdited = false;
                    artistEditText.setEnabled(false);
                }

            }

        });

        if (ARTIST != null) {
            songURIsList = getAllSongsByArtist(ARTIST);

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
            boolean artistsSame = checkIfAllElementsEqual(artistsList);

            //Populate the EditTexts.
            setEditorFields(artistsSame, artistsList, artistEditText);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set the dialog title.
        builder.setTitle(R.string.edit_tags);
        builder.setView(rootView);
        builder.setPositiveButton(R.string.save, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialogFragment.dismiss();
                AsyncSaveArtistTagsTask asyncSaveArtistTagsTask = new AsyncSaveArtistTagsTask(getActivity(), getActivity());
                asyncSaveArtistTagsTask.execute();

            }

        });

        builder.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });

        return builder.create();

    }

    public static ArrayList<String> getAllSongsInAlbum(String albumName, String artistName) {
        ArrayList<String> songURIsList = new ArrayList<String>();

        DBAccessHelper dbHelper = new DBAccessHelper(parentActivity);

        //Escape any rogue apostrophes.
        if (albumName.contains("'")) {
            albumName = albumName.replace("'", "''");
        }

        if (artistName.contains("'")) {
            artistName = artistName.replace("'", "''");
        }

        String selection = DBAccessHelper.SONG_ALBUM + "=" + "'" + albumName + "'" + " AND "
                + DBAccessHelper.SONG_ARTIST + "=" + "'" + artistName + "'" + " AND "
                + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'";

        String[] projection = {DBAccessHelper._ID, DBAccessHelper.SONG_FILE_PATH};

        Cursor cursor = dbHelper.getWritableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                projection,
                selection,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        if (cursor.getCount() != 0) {

            songURIsList.add(cursor.getString(1));

            while (cursor.moveToNext()) {
                songURIsList.add(cursor.getString(1));

            }

        }

        cursor.close();

        return songURIsList;

    }

    public static ArrayList<String> getAllSongsByArtist(String artistName) {
        ArrayList<String> songURIsList = new ArrayList<String>();

        DBAccessHelper dbHelper = new DBAccessHelper(parentActivity);

        //Escape any rogue apostrophes.
        if (artistName.contains("'")) {
            artistName = artistName.replace("'", "''");
        }

        String selection = DBAccessHelper.SONG_ARTIST + "=" + "'" + artistName + "'"
                /*+ " AND " + DBAccessHelper.SONG_SOURCE + "<>" + "'GOOGLE_PLAY_MUSIC'"*/;

        String[] projection = {DBAccessHelper._ID, DBAccessHelper.SONG_FILE_PATH};

        Cursor cursor = dbHelper.getWritableDatabase().query(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                projection,
                selection,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();

        if (cursor.getCount() != 0) {

            songURIsList.add(cursor.getString(1));

            while (cursor.moveToNext()) {
                songURIsList.add(cursor.getString(1));

            }

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

        for (int i = 0; i < dataURIsList.size(); i++) {

            try {
                File file = new File(dataURIsList.get(i));
                AudioFile audioFile = AudioFileIO.read(file);

                artistsList.add(audioFile.getTag().getFirst(FieldKey.ARTIST));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

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

    class AsyncSaveArtistTagsTask extends AsyncTask<String, String, String> {

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

        public AsyncSaveArtistTagsTask(Context context, Activity activity) {
            mContext = context;
            mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(mActivity);
            pd.setTitle(R.string.saving_artist_info);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setIndeterminate(false);
            pd.setMax(songURIsList.size());
            pd.show();

            //Retrieve the strings from the EditText fields.
            if (artistEdited) {
                songArtist = artistEditText.getText().toString();
                songArtist = songArtist.replace("'", "''");
            } else {
                songArtist = null;
            }

        }

        @Override
        protected String doInBackground(String... arg0) {

            //Create DB instances.
            DBAccessHelper dbHelper = new DBAccessHelper(mContext.getApplicationContext());

            for (i = 0; i < songURIsList.size(); i++) {
                publishProgress(new String[]{});
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

                //Escape any rogue apostrophes.
                String uri = songURIsList.get(i);

                if (uri != null) {
                    if (uri.contains("'")) {
                        uri = uri.replace("'", "''");
                    }

                } else {
                    continue;
                }

                Tag tag = audioFile.getTag();

                if (tag != null) {
                    String whereClause = DBAccessHelper.SONG_FILE_PATH + "=" + "'" + uri + "'";
                    ContentValues values = new ContentValues();

                    if (artistEdited == false) {
                        //Don't do anything here. The user didn't change the artist.
                    } else {
                        try {
                            tag.setField(FieldKey.ARTIST, songArtist);
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

                        values.put(DBAccessHelper.SONG_ARTIST, songArtist);

                    }

                    try {
                        audioFile.commit();
                    } catch (CannotWriteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //Write the values to the DB.
                    try {
                        dbHelper.getWritableDatabase().update(DBAccessHelper.MUSIC_LIBRARY_TABLE,
                                values,
                                whereClause,
                                null);

                        dbHelper.close();
                        dbHelper = null;

                        // Now refresh the fragment list
                        ((GridViewFragment) dialogFragment.getTargetFragment()).startRunnable();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(mContext, R.string.unable_to_edit_artist_tags, Toast.LENGTH_SHORT).show();
                }

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pd.setProgress(i);
            //String message = mContext.getResources().getString(R.string.saving_song_info_for) + " " + artistsList.get(i) + ".";
            //pd.setMessage(String.valueOf(i));
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pd.dismiss();

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Delete all the contents of the ArrayLists.
            clearArrayLists();
            dialogFragment.dismiss();
            Toast.makeText(parentActivity, R.string.changes_saved, Toast.LENGTH_SHORT).show();

        }

    }


    public void clearArrayLists() {
        if (artistsList != null) {
            artistsList.clear();
            artistsList = null;
        }

        if (songURIsList != null) {
            songURIsList.clear();
            songURIsList = null;
        }
    }

}
