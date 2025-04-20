package com.streaming.indianresso.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.PlaylistHelper;
import com.streaming.indianresso.helper.TypefaceHelper;
import com.streaming.indianresso.utility.AppConstants;

public class RenamePlaylistDialog extends DialogFragment {

    private Activity parentActivity;
    private RenamePlaylistDialog dialog;
    private View dialogView;
    private TextView playlistInfo;
    private EditText playlistNameField;
    private String mPlaylistId, mPlaylistName;

    private OnRenamePlaylist onRenamePlaylist;
    public interface OnRenamePlaylist {
        void stateChanged();
    }

    public void setOnRenamePlaylist(OnRenamePlaylist onRenamePlaylist) {
        this.onRenamePlaylist = onRenamePlaylist;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        parentActivity = getActivity();
        dialog = this;

        //Retrieve the arguments.
        /*final String PLAYLIST_NAME = getArguments().getString("PLAYLIST_NAME");
        final String PLAYLIST_FILE_PATH = getArguments().getString("PLAYLIST_FILE_PATH");
        final String PLAYLIST_FOLDER_PATH = getArguments().getString("PLAYLIST_FOLDER_PATH");*/
        mPlaylistId = getArguments().getString(AppConstants.PLAYLIST_ID);
        mPlaylistName = getArguments().getString(AppConstants.PLAYLIST_NAME);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = parentActivity.getLayoutInflater().inflate(R.layout.rename_playlist_dialog_layout, null);

        playlistInfo = (TextView) dialogView.findViewById(R.id.rename_playlist_info);
        playlistInfo.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        playlistInfo.setPaintFlags(playlistInfo.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        playlistNameField = (EditText) dialogView.findViewById(R.id.rename_playlist_text_field);
        playlistNameField.setTypeface(TypefaceHelper.getTypeface(parentActivity, "RobotoCondensed-Light"));
        playlistNameField.setPaintFlags(playlistNameField.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        playlistNameField.setHint(mPlaylistName);

        //Set the dialog title.
        builder.setTitle(R.string.rename_playlist);
        builder.setView(dialogView);
        builder.setNegativeButton(R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                dialog.dismiss();
            }

        });

        builder.setPositiveButton(R.string.done, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //Create a File that points to the playlist on the filesystem.
                // File file = new File(PLAYLIST_FILE_PATH);

                //Retrieve the name and file path for the playlist.
                String playlistName = playlistNameField.getText().toString();

                //Replace illegal characters in the playlistName.
                if (playlistName.contains("/")) {
                    playlistName = playlistName.replace("/", "_");
                }

                if (playlistName.contains("\\")) {
                    playlistName = playlistName.replace("\\", "_");
                }

                //Create another File that points to the renamed playlist file.
                /*File newFile = new File(PLAYLIST_FOLDER_PATH + "/" + playlistName + ".m3u");
                file.renameTo(newFile);*/

				/*//Update the database with the new file path and playlist name.
                DBAccessHelper playlistsDBHelper = new DBAccessHelper(parentActivity);
				playlistsDBHelper.renamePlaylist(PLAYLIST_FILE_PATH, playlistName, newFile.getAbsolutePath());
				
				//Delete any instances of the old DB entries.
				playlistsDBHelper.deleteSpecificPlaylist(PLAYLIST_NAME, PLAYLIST_FILE_PATH);*/

                PlaylistHelper.renamePlaylist(parentActivity, playlistName, mPlaylistId);
                Toast.makeText(parentActivity, R.string.playlist_renamed, Toast.LENGTH_SHORT).show();
                onRenamePlaylist.stateChanged();

                //Refresh the playlists list view.
                //PlaylistsFragment playlistsFragment = new PlaylistsFragment();
                //playlistsFragment.refreshPlaylists();

                //playlistsDBHelper.close();
                dialog.dismiss();
            }

        });

        return builder.create();
    }

}
