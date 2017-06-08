package com.squad.music.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.EqualizerActivity;
import com.squad.music.ui.activity.MainActivity;
import com.squad.music.ui.adapter.NavigationDrawerAdapter;
import com.squad.music.ui.adapter.NavigationDrawerLibrariesAdapter;

import java.util.concurrent.TimeUnit;

public class NavigationDrawerFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_TIMER_DIALOG = 421;
    private Context mContext;
    private Common mApp;

    private RelativeLayout mLibraryPickerLayout;
    private TextView mLibraryPickerHeaderText;
    private Spinner mLibraryPickerSpinner;
    private ListView browsersListView;
    private ImageView mImageViewTimerEditor, mImageViewTimerCancel;
    private TextView mTextViewTimerText;

    private Cursor cursor;
    private int mCurrentLibraryPosition;
    private NavigationDrawerLibrariesAdapter mLibrariesAdapter;
    private NavigationDrawerAdapter mBrowsersAdapter;
    private Handler mHandler;
    private CountDownTimer mCountDownTimer;
    private NavigationView mNavigationView;
    private View rootView;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = (Common) mContext.getApplicationContext();
        mHandler = new Handler();

        rootView = inflater.inflate(R.layout.navigation_drawer_layout, null);
        rootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));

        mNavigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        mLibraryPickerLayout = (RelativeLayout) headerView.findViewById(R.id.library_picker_layout);
        mLibraryPickerSpinner = (Spinner) headerView.findViewById(R.id.library_picker_spinner);
        mLibraryPickerHeaderText = (TextView) headerView.findViewById(R.id.library_picker_header_text);
        mTextViewTimerText = (TextView) headerView.findViewById(R.id.textview_timer_text);
        mImageViewTimerEditor = (ImageView) headerView.findViewById(R.id.imageview_timer_edit);
        mImageViewTimerCancel = (ImageView) headerView.findViewById(R.id.imageview_timer_cancel);
        mImageViewTimerEditor.setOnClickListener(this);
        mImageViewTimerCancel.setOnClickListener(this);
        mLibraryPickerHeaderText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        //Apply the Libraries ListView's adapter.
        cursor = mApp.getDBAccessHelper().getAllUniqueLibraries();
        mLibrariesAdapter = new NavigationDrawerLibrariesAdapter(getActivity(), cursor);
        mLibraryPickerSpinner.setAdapter(mLibrariesAdapter);
        mLibraryPickerSpinner.setSelection(mApp.getCurrentLibraryIndex());
        mLibraryPickerSpinner.setOnItemSelectedListener(librariesItemSelectedListener);

        customizeNavigationItems();

        return rootView;
    }

    private void customizeNavigationItems() {
        if (mNavigationView != null) {
            // For more customization, kindly view NavigationDrawerAdapter class
            Menu menu = mNavigationView.getMenu();
            for (int i = 0; i < menu.size(); i++) {
                changeTypefaceAndColor(menu.getItem(i));
            }
            mNavigationView.setNavigationItemSelectedListener(this);
        }
    }

    private void changeTypefaceAndColor(MenuItem item) {
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"), 0, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(UIElementsHelper.getThemeBasedTextColor(mContext)), 0, s.length(), 0);
        item.setTitle(s);
    }

    private AdapterView.OnItemSelectedListener librariesItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (mApp.getCurrentLibraryIndex() == position)
                return;

            mApp.getSharedPreferences().edit().putString(Common.CURRENT_LIBRARY,
                    (String) view.getTag(R.string.library_name)).commit();

            mApp.getSharedPreferences().edit().putInt(Common.CURRENT_LIBRARY_POSITION, position).commit();

            //Update the fragment.
            ((MainActivity) getActivity()).loadFragment(null);

            //Reset the ActionBar after 500ms.
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    getActivity().invalidateOptionsMenu();

                }

            }, 500);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageview_timer_edit:
                DialogFragment fragment = new TimePickerDialogFragment();
                fragment.setTargetFragment(this, REQUEST_CODE_TIMER_DIALOG);
                fragment.show(getFragmentManager(), "timePicker");

                break;
            case R.id.imageview_timer_cancel:
                mCountDownTimer.cancel();
                mTextViewTimerText.setText("00:00");
                mImageViewTimerCancel.setVisibility(View.GONE);
                mImageViewTimerEditor.setVisibility(View.VISIBLE);

                break;
        }
    }

    public void updateTimerText(long millis, final boolean flag) {
        mImageViewTimerEditor.setVisibility(View.GONE);
        mImageViewTimerCancel.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), getString(R.string.timer_set_success), Toast.LENGTH_SHORT).show();

        mCountDownTimer = new CountDownTimer(millis, 1000) {
            public void onTick(long millisUntilFinished) {
                mTextViewTimerText.setText(millisToTimeFormat(millisUntilFinished, flag));
            }

            public void onFinish() {
                mTextViewTimerText.setText("00:00");
                mImageViewTimerEditor.setVisibility(View.VISIBLE);
                mImageViewTimerCancel.setVisibility(View.GONE);

                //pause the current playing song
                mApp.getService().pausePlayback();
            }
        }.start();
    }

    public String millisToTimeFormat(long millis, boolean hourChanged) {
        String str = null;
        if (hourChanged) {
            str = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        } else {
            str = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        }
        return str;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_artists) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.ARTISTS_FRAGMENT);
        } else if (id == R.id.nav_albums) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.ALBUMS_FRAGMENT);
        } else if (id == R.id.nav_songs) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.SONGS_FRAGMENT);
        } else if (id == R.id.nav_genres) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.GENRES_FRAGMENT);
        } else if (id == R.id.nav_folders) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.FOLDERS_FRAGMENT);
        } else if (id == R.id.nav_playlists) {
            ((MainActivity) getActivity()).setCurrentFragmentId(Common.PLAYLISTS_FRAGMENT);
        } else if (id == R.id.nav_play_queue) {
            ((MainActivity) getActivity()).mQueueDrawerFragment.setIsDrawerOpen(true);
        } else if (id == R.id.nav_equalizer) {
            Intent intent = new Intent(getActivity(), EqualizerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            /*Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);*/
        }

        //Update the fragment.
        ((MainActivity) getActivity()).loadFragment(null);

        //Reset the ActionBar after 500ms.
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                getActivity().invalidateOptionsMenu();
            }

        }, 500);

        return true;
    }
}
