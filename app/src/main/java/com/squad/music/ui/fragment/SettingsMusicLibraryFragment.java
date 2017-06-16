package com.squad.music.ui.fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.AppCompatPreferenceActivity;
import com.squad.music.ui.activity.WelcomeActivity;
import com.squad.music.ui.dialog.SettingsMusicFoldersDialog;

public class SettingsMusicLibraryFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;
    private Toolbar mToolbar;

    private View mRootView;
    private ListView mListView;

    private Preference mSelectMusicFoldersPreference;
    private Preference mRefreshMusicFoldersPreference;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.settings_music_library);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_settings, container, false);
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(getActivity()));

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        setToolbar();

        mSelectMusicFoldersPreference = getPreferenceManager().findPreference("preference_key_music_folders");
        mRefreshMusicFoldersPreference = getPreferenceManager().findPreference("preference_key_refresh_music_library");

        mSelectMusicFoldersPreference.setOnPreferenceClickListener(selectMusicFoldersClickListener);
        mRefreshMusicFoldersPreference.setOnPreferenceClickListener(refreshMusicFoldersClickListener);

        return mRootView;
    }

    /**
     * Click listener for "Select Music Folders".
     */
    private Preference.OnPreferenceClickListener selectMusicFoldersClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            SettingsMusicFoldersDialog foldersDialog = new SettingsMusicFoldersDialog();
            foldersDialog.setArguments(bundle);
            foldersDialog.show(ft, "foldersDialog");

            return false;
        }

    };

    /**
     * Click listener for "Refresh Music Folders".
     */
    private Preference.OnPreferenceClickListener refreshMusicFoldersClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            getActivity().finish();

            Intent intent = new Intent(mContext, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("REFRESH_MUSIC_LIBRARY", true);
            mContext.startActivity(intent);

            return false;
        }

    };

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
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            bar.setHomeAsUpIndicator(upArrow);

            // to change the title text color of toolbar
            mToolbar.setTitleTextColor(Color.WHITE);
        }
    }

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
