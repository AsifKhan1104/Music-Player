package com.squad.music.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.squad.music.R;

/**
 * Created by Navkrishna on December 10, 2014
 */
public class CustomDialogFragment extends DialogFragment {
    public static final String LABEL_POSITIVE = "labelPositive";
    public static final String LABEL_NEGATIVE = "labelNegative";
    public static final String LABEL_NEUTRAL = "labelNeutral";
    private ACTION_TYPE mActionType;
    private ButtonClickListener mButtonClickListener;
    private String mTitle;
    private String mMessage;
    private String mNeutralButtonLabel;
    private String mNegativeButtonLabel;
    private String mPositiveButtonLabel;

    public static CustomDialogFragment getInstance(Activity activity, String title, String message,
                                                   ACTION_TYPE actionType, ButtonClickListener buttonClickListener,
                                                   Bundle labelBundle, boolean cancelable) {
        CustomDialogFragment customDialogFragment = new CustomDialogFragment();
        customDialogFragment.mTitle = title;
        customDialogFragment.mMessage = message;
        customDialogFragment.mActionType = actionType;
        customDialogFragment.mButtonClickListener = buttonClickListener;
        if (labelBundle != null) {
            customDialogFragment.mNeutralButtonLabel = labelBundle.getString(LABEL_NEUTRAL, activity.getString(R.string.neutral));
            customDialogFragment.mNegativeButtonLabel = labelBundle.getString(LABEL_NEGATIVE, activity.getString(R.string.no));
            customDialogFragment.mPositiveButtonLabel = labelBundle.getString(LABEL_POSITIVE, activity.getString(R.string.yes));
        }
        customDialogFragment.setCancelable(cancelable);
        return customDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle(mTitle);
        if (mMessage != null)
            dialogBuilder.setMessage(Html.fromHtml(mMessage));
        // for crash fix: Null pointer exception in switch(mActionType)
        if (mActionType != null) {
            switch (mActionType) {
                case POSITIVE_NEGATIVE_NEUTRAL:
                    dialogBuilder.setNeutralButton(mNeutralButtonLabel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mButtonClickListener.neutralClicked(dialog);
                        }
                    });
                case POSITIVE_NEGATIVE:
                    dialogBuilder.setNegativeButton(mNegativeButtonLabel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mButtonClickListener.negativeClicked(dialog);
                        }
                    });
                case POSITIVE:
                    dialogBuilder.setPositiveButton(mPositiveButtonLabel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mButtonClickListener.positiveClicked(dialog);
                        }
                    });
            }
        }
        return dialogBuilder.create();
    }


    public enum ACTION_TYPE {
        POSITIVE,
        POSITIVE_NEGATIVE,
        POSITIVE_NEGATIVE_NEUTRAL
    }

    public static abstract class ButtonClickListener {
        public void positiveClicked(DialogInterface dialog) {
        }

        public void negativeClicked(DialogInterface dialog) {
        }

        public void neutralClicked(DialogInterface dialog) {
        }


    }
}
