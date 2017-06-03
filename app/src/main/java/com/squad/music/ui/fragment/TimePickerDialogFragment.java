package com.squad.music.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squad.music.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mdasifkhan on 2/5/17.
 */

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /*onSelectListener mListener;

    public interface onSelectListener {
        void setOnSelectListener(long millis, boolean flag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (onSelectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        boolean hourChanged = true;
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        long currentMillis = calendar.getTime().getTime();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        long setMillis = calendar.getTime().getTime();
        int setHour = calendar.get(Calendar.HOUR_OF_DAY);

        if (setMillis > currentMillis) {
            if (setHour == currentHour) {
                hourChanged = false;
            }
            long diff = setMillis - currentMillis;
            ((NavigationDrawerFragment) getTargetFragment()).updateTimerText(diff, hourChanged);
        } else {
            Toast.makeText(getActivity(), getString(R.string.timer_error_message), Toast.LENGTH_SHORT).show();
        }
    }
}
