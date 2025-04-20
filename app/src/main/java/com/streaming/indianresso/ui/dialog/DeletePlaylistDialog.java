package com.streaming.indianresso.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.PlaylistHelper;

/**
 * Created by mdasifkhan on 2/6/17.
 */

public class DeletePlaylistDialog extends DialogFragment {

    public static DeletePlaylistDialog newInstance(String id) {
        DeletePlaylistDialog f = new DeletePlaylistDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                // .setIcon(R.drawable.androidhappy)
                // Set Dialog Title
                // .setTitle("Alert DialogFragment")
                // Set Dialog Message
                .setMessage(R.string.delete_playlist_warning)

                // Positive button
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PlaylistHelper.deletePlaylist(getContext(), getArguments().getString("id"));
                        /*for (int i = 0; i < PlaylistAdapter.mItems.size(); i++) {
                            if (PlaylistAdapter.mItems.get(i).getPlaylistId() == Long.parseLong(getArguments().getString("id")))
                                PlaylistAdapter.mItems.remove(i);
                        }*/
                        Toast.makeText(getContext(), getActivity().getString(R.string.playlist_deleted), Toast.LENGTH_SHORT).show();
                    }
                })

                // Negative Button
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).create();
    }
}
