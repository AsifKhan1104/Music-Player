package com.streaming.indianresso.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.SpannableString;
import android.view.ContextThemeWrapper;
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

import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.TypefaceHelper;
import com.streaming.indianresso.helper.UIElementsHelper;
import com.streaming.indianresso.ui.activity.MainActivity;
import com.streaming.indianresso.ui.activity.SettingsActivity;
import com.streaming.indianresso.ui.adapter.NavigationDrawerAdapter;
import com.streaming.indianresso.ui.adapter.NavigationDrawerLibrariesAdapter;

import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_OK;

public class NavigationDrawerFragment extends Fragment implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE_TIMER_DIALOG = 421;
    private static final int REQUEST_CODE_SETTINGS = 422;
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
    private int mSelectedFragmentId;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = (Common) mContext.getApplicationContext();
        mHandler = new Handler();

        //Set the UI theme.
        Context contextThemeWrapper = null;
        if (mApp.getCurrentTheme() == Common.LIGHT_THEME) {
            // create ContextThemeWrapper from the original Activity Context with the custom theme
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);
            // clone the inflater using the ContextThemeWrapper
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

            rootView = localInflater.inflate(R.layout.navigation_drawer_layout, null);
            mNavigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
            mNavigationView.setItemTextColor(ContextCompat.getColorStateList(mContext, R.color.drawer_item_light));
            mNavigationView.setItemIconTintList(ContextCompat.getColorStateList(mContext, R.color.drawer_item_light));
            rootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));
        } else {
            // create ContextThemeWrapper from the original Activity Context with the custom theme
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppThemeDark);
            // clone the inflater using the ContextThemeWrapper
            LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

            rootView = localInflater.inflate(R.layout.navigation_drawer_layout, null);
            mNavigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
            mNavigationView.setItemTextColor(ContextCompat.getColorStateList(mContext, R.color.drawer_item_dark));
            mNavigationView.setItemIconTintList(ContextCompat.getColorStateList(mContext, R.color.drawer_item_dark));
            rootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));
        }

        mNavigationView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));
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

            // get deafult nav item
            if (getActivity() instanceof MainActivity) {
                int id = ((MainActivity) getActivity()).getCurrentFragmentId();
                MenuItem menuItem = mNavigationView.getMenu().getItem(0);
                switch (id) {
                    case Common.ARTISTS_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(0);
                        break;
                    case Common.ALBUMS_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(1);
                        break;
                    case Common.SONGS_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(2);
                        break;
                    case Common.GENRES_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(3);
                        break;
                    case Common.FOLDERS_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(4);
                        break;
                    case Common.PLAYLISTS_FRAGMENT:
                        menuItem = mNavigationView.getMenu().getItem(5);
                        break;
                }

                menuItem.setChecked(true);
            }
        }
    }

    private void changeTypefaceAndColor(MenuItem item) {
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"), 0, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(UIElementsHelper.getThemeBasedTextColor(mContext)), 0, s.length(), 0);
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
        if (v.getId() == R.id.imageview_timer_edit) {
            DialogFragment fragment = new TimePickerDialogFragment();
            fragment.setTargetFragment(this, REQUEST_CODE_TIMER_DIALOG);
            fragment.show(getFragmentManager(), "timePicker");

        } else if (v.getId() == R.id.imageview_timer_cancel) {
            mCountDownTimer.cancel();
            mTextViewTimerText.setText("00:00");
            mImageViewTimerCancel.setVisibility(View.GONE);
            mImageViewTimerEditor.setVisibility(View.VISIBLE);
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
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            mSelectedFragmentId = ((MainActivity) getActivity()).getCurrentFragmentId();
            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SETTINGS && resultCode == RESULT_OK) {
            Menu menu = mNavigationView.getMenu();
            switch (mSelectedFragmentId) {
                case 0:
                    menu.getItem(0).setChecked(true);
                    break;
                case 2:
                    menu.getItem(1).setChecked(true);
                    break;
                case 3:
                    menu.getItem(2).setChecked(true);
                    break;
                case 4:
                    menu.getItem(5).setChecked(true);
                    break;
                case 5:
                    menu.getItem(3).setChecked(true);
                    break;
                case 6:
                    menu.getItem(4).setChecked(true);
                    break;
            }
        }
    }
}
