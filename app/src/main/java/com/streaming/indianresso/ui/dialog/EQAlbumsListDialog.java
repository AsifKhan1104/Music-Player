package com.streaming.indianresso.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.asynctask.AsyncApplyEQToAlbumTask;
import com.streaming.indianresso.database.DBAccessHelper;
import com.streaming.indianresso.ui.activity.EqualizerActivity;

public class EQAlbumsListDialog extends DialogFragment {

    private Common mApp;
    private EqualizerActivity mEqualizerFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mApp = (Common) getActivity().getApplicationContext();
        mEqualizerFragment = (EqualizerActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Get a cursor with the list of all albums.
        final Cursor cursor = mApp.getDBAccessHelper().getAllAlbumsOrderByName();

        //Set the dialog title.
        builder.setTitle(R.string.apply_to);
        builder.setCursor(cursor, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursor.moveToPosition(which);
                String albumName = cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ALBUM));
                AsyncApplyEQToAlbumTask task = new AsyncApplyEQToAlbumTask(getActivity(),
                        albumName,
                        mEqualizerFragment.getFiftyHertzLevel(),
                        mEqualizerFragment.getOneThirtyHertzLevel(),
                        mEqualizerFragment.getThreeTwentyHertzLevel(),
                        mEqualizerFragment.getEightHundredHertzLevel(),
                        mEqualizerFragment.getTwoKilohertzLevel(),
                        mEqualizerFragment.getFiveKilohertzLevel(),
                        mEqualizerFragment.getTwelvePointFiveKilohertzLevel(),
                        (short) mEqualizerFragment.getVirtualizerSeekBar().getProgress(),
                        (short) mEqualizerFragment.getBassBoostSeekBar().getProgress(),
                        (short) mEqualizerFragment.getReverbSpinner().getSelectedItemPosition());

                task.execute(new String[]{"" + which});

                if (cursor != null)
                    cursor.close();

                //Hide the equalizer fragment.
                getActivity().finish();

            }

        }, DBAccessHelper.SONG_ALBUM);

        return builder.create();
    }

}
