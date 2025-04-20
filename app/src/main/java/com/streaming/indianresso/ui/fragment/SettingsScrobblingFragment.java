package com.streaming.indianresso.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.UIElementsHelper;
import com.streaming.indianresso.ui.activity.AppCompatPreferenceActivity;
import com.streaming.indianresso.utility.AppConstants;
import com.streaming.indianresso.utility.Utils;

public class SettingsScrobblingFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;

    private View mRootView;
    private ListView mListView;
    private Toolbar mToolbar;

    private Preference mScrobblerDownloadPreference;
    private CheckBoxPreference mScrobblingControlsPreference;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.settings_scrobbling);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_settings, container, false);
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(getActivity()));

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        setToolbar();

        mScrobblerDownloadPreference = getPreferenceManager().findPreference("preference_key_scrobbler_download");
        mScrobblingControlsPreference = (CheckBoxPreference) getPreferenceManager().findPreference("preference_key_scrobbling_controls");

        //Apply the click listeners.
        mScrobblerDownloadPreference.setOnPreferenceClickListener(scrobblerDownloadClickListener);
        mScrobblingControlsPreference.setOnPreferenceChangeListener(scrobblingControlsClickListener);

        return mRootView;
    }

    private void setToolbar() {
        if (mRootView != null) {
            AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();
            mToolbar = (Toolbar) mRootView.findViewById(R.id.toolbar);
            activity.setSupportActionBar(mToolbar);

            ActionBar bar = activity.getSupportActionBar();
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(getActivity().getTitle());

            // to change the color of back arrow in toolbar
            final Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.ic_arrow_back);
            if (upArrow != null) {
                upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            bar.setHomeAsUpIndicator(upArrow);

            // to change the title text color of toolbar
            mToolbar.setTitleTextColor(Color.WHITE);
        }
    }

    /**
     * Click listener for the scrobbler app download preference.
     */
    private Preference.OnPreferenceClickListener scrobblerDownloadClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Utils.openAppInGooglePlay(getActivity(), AppConstants.SCROBBLER_APP_PACKAGE_NAME);
            return false;
        }
    };

    /**
     * Checkbox click listener for the scrobbler controls preference.
     */
    private Preference.OnPreferenceChangeListener scrobblingControlsClickListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean value = (Boolean) newValue;
            mApp.getSharedPreferences().edit().putBoolean(AppConstants.SCROBBLER_FLAG, value).commit();
            ((CheckBoxPreference) preference).setChecked(value);
            return false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            View frame = (View) getView().getParent();
            if (frame != null)
                frame.setPadding(0, 0, 0, 0);
        }

    }
}
