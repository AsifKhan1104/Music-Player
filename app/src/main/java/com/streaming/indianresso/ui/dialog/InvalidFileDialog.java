package com.streaming.indianresso.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import com.streaming.indianresso.R;

public class InvalidFileDialog extends DialogFragment {

	private String fileName;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

		fileName = getArguments().getString("FILE_NAME");
		
		final DialogFragment dialog = this;
		
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Set the dialog title.
        builder.setTitle(R.string.error);
        builder.setMessage(fileName + " " + getResources().getString(R.string.invalid_file_message));
        builder.setNegativeButton(R.string.ok, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dialog.dismiss();
				
			}
        	
        });
        
        return builder.create();
    }
	
}
