package com.squad.music.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.MainActivity;
import com.squad.music.ui.adapter.NavigationDrawerAdapter;
import com.squad.music.ui.adapter.NavigationDrawerLibrariesAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {

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

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        mApp = (Common) mContext.getApplicationContext();
        mHandler = new Handler();

        View rootView = inflater.inflate(R.layout.navigation_drawer_layout, null);
        rootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));

        browsersListView = (ListView) rootView.findViewById(R.id.browsers_list_view);
        mLibraryPickerLayout = (RelativeLayout) rootView.findViewById(R.id.library_picker_layout);
        mLibraryPickerSpinner = (Spinner) rootView.findViewById(R.id.library_picker_spinner);
        mLibraryPickerHeaderText = (TextView) rootView.findViewById(R.id.library_picker_header_text);
        mTextViewTimerText = (TextView) rootView.findViewById(R.id.textview_timer_text);
        mImageViewTimerEditor = (ImageView) rootView.findViewById(R.id.imageview_timer_edit);
        mImageViewTimerCancel = (ImageView) rootView.findViewById(R.id.imageview_timer_cancel);
        mImageViewTimerEditor.setOnClickListener(this);
        mImageViewTimerCancel.setOnClickListener(this);
        mLibraryPickerHeaderText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        //Apply the Browser ListView's adapter.
        List<String> titles = Arrays.asList(getActivity().getResources().getStringArray(R.array.sliding_menu_array));
        mBrowsersAdapter = new NavigationDrawerAdapter(getActivity(), new ArrayList<String>(titles));
        browsersListView.setAdapter(mBrowsersAdapter);
        browsersListView.setOnItemClickListener(browsersClickListener);
        setListViewHeightBasedOnChildren(browsersListView);

        //Apply the Libraries ListView's adapter.
        cursor = mApp.getDBAccessHelper().getAllUniqueLibraries();
        mLibrariesAdapter = new NavigationDrawerLibrariesAdapter(getActivity(), cursor);
        mLibraryPickerSpinner.setAdapter(mLibrariesAdapter);
        mLibraryPickerSpinner.setSelection(mApp.getCurrentLibraryIndex());
        mLibraryPickerSpinner.setOnItemSelectedListener(librariesItemSelectedListener);

        browsersListView.setDividerHeight(0);

        return rootView;
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

    private OnItemClickListener browsersClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long dbID) {
            switch (position) {
                case 0:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.ARTISTS_FRAGMENT);
                    break;
                /*case 1:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.ALBUM_ARTISTS_FRAGMENT);
                    break;*/
                case 1:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.ALBUMS_FRAGMENT);
                    break;
                case 2:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.SONGS_FRAGMENT);
                    break;
                case 3:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.PLAYLISTS_FRAGMENT);
                    break;
                case 4:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.GENRES_FRAGMENT);
                    break;
                case 5:
                    ((MainActivity) getActivity()).setCurrentFragmentId(Common.FOLDERS_FRAGMENT);
                    break;
                case 6:
                    /*Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);*/
                    break;
            }

            //Update the adapter to reflect the new fragment.
            List<String> titles = Arrays.asList(getActivity().getResources().getStringArray(R.array.sliding_menu_array));
            mBrowsersAdapter = new NavigationDrawerAdapter(getActivity(), new ArrayList<String>(titles));
            browsersListView.setAdapter(mBrowsersAdapter);

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

    };

    /**
     * Clips ListViews to fit within the drawer's boundaries.
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

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
}
