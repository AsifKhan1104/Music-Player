package com.squad.music.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.asynctask.AsyncApplyEQToGenreTask;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.ui.activity.EqualizerActivity;

public class EQGenresListDialog extends DialogFragment {

    private Common mApp;
    private EqualizerActivity mEqualizerFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mApp = (Common) getActivity().getApplicationContext();
        mEqualizerFragment = (EqualizerActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Get a cursor with the list of all the unique genres.
        final Cursor cursor = mApp.getDBAccessHelper().getAllUniqueGenres("");

        //Set the dialog title.
        builder.setTitle(R.string.apply_to);
        builder.setCursor(cursor, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                cursor.moveToPosition(which);
                String genreName = cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_GENRE));
                AsyncApplyEQToGenreTask task = new AsyncApplyEQToGenreTask(getActivity(),
                        genreName,
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

        }, DBAccessHelper.SONG_GENRE);

        return builder.create();
    }

}

