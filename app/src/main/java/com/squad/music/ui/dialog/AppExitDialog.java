package com.squad.music.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.squad.music.R;

/**
 * Created by mdasifkhan on 28/5/17.
 */

public class AppExitDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                // Set Dialog Icon
                // .setIcon(R.drawable.androidhappy)
                // Set Dialog Title
                // .setTitle("Alert DialogFragment")
                // Set Dialog Message
                .setMessage(R.string.app_exit_msg)

                // Positive button
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        getActivity().finish();
                        getActivity().finishAffinity();
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
