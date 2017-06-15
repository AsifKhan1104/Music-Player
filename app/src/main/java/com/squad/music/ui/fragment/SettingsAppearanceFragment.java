package com.squad.music.ui.fragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.AppCompatPreferenceActivity;
import com.squad.music.ui.dialog.ApplicationThemeDialog;

import petrov.kristiyan.colorpicker.ColorPicker;

public class SettingsAppearanceFragment extends PreferenceFragment {

    private Context mContext;
    private Common mApp;

    private View mRootView;
    private ListView mListView;
    private Toolbar mToolbar;

    private Preference mAppThemePreference;
    private Preference mColorPreference;
    private Preference mDefaultScreenPreference;
    private CheckBoxPreference mLockscreenControlsPreference;

    private static final String NOW_PLAYING_COLOR = "NOW_PLAYING_COLOR";
    private static final String BLUE = "BLUE";
    private static final String RED = "RED";
    private static final String GREEN = "GREEN";
    private static final String ORANGE = "ORANGE";
    private static final String PURPLE = "PURPLE";
    private static final String MAGENTA = "MAGENTA";
    private static final String GRAY = "GRAY";
    private static final String BLACK = "BLACK";

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        addPreferencesFromResource(R.xml.settings_appearance);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSavedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_settings, container, false);
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(getActivity()));

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        setToolbar();

        mAppThemePreference = getPreferenceManager().findPreference("preference_key_app_theme");
        mColorPreference = getPreferenceManager().findPreference("preference_key_player_color_scheme");
        mDefaultScreenPreference = getPreferenceManager().findPreference("preference_key_startup_screen");
        mLockscreenControlsPreference = (CheckBoxPreference) getPreferenceManager().findPreference("preference_key_lockscreen_controls");

        //Apply the click listeners.
        mAppThemePreference.setOnPreferenceClickListener(appThemeClickListener);
        mColorPreference.setOnPreferenceClickListener(colorClickListener);
        mDefaultScreenPreference.setOnPreferenceClickListener(defaultScreenClickListener);
        mLockscreenControlsPreference.setOnPreferenceChangeListener(lockscreenControlsClickListener);

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
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            bar.setHomeAsUpIndicator(upArrow);

            // to change the title text color of toolbar
            mToolbar.setTitleTextColor(Color.WHITE);
        }
    }

    /**
     * Click listener for the app theme preference.
     */
    private Preference.OnPreferenceClickListener appThemeClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ApplicationThemeDialog appThemeDialog = new ApplicationThemeDialog();
            appThemeDialog.show(ft, "appThemeDialog");

            return false;
        }

    };

    /**
     * Click listener for the color preference.
     */
    private Preference.OnPreferenceClickListener colorClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            /*FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            NowPlayingColorSchemesDialog appThemeDialog = new NowPlayingColorSchemesDialog();
            appThemeDialog.show(ft, "colorSchemesDialog");*/

            final SharedPreferences sharedPreferences = getActivity().
                    getSharedPreferences("com.squad.music", Context.MODE_PRIVATE);

            ColorPicker colorPicker = new ColorPicker(getActivity());
            colorPicker.setColors(getResources().getIntArray(R.array.toolbar_bgs))
                    .setRoundColorButton(true)
                    .setTitle(getString(R.string.choose_accent_color))
                    .disableDefaultButtons(true)
                    .setDefaultColorButton(R.color.colorPrimary)
                    .setColumns(4)
                    .setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                        @Override
                        public void setOnFastChooseColorListener(int position, int color) {
                            changeColor(position, sharedPreferences);
                        }

                        @Override
                        public void onCancel() {

                        }
                    }).show();

            return false;
        }

    };

    private void changeColor(int position, SharedPreferences sharedPreferences) {
        switch (position) {
            case 0:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, BLUE).commit();
                break;
            case 1:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, GRAY).commit();
                break;
            case 2:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, RED).commit();
                break;
            case 3:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, GREEN).commit();
                break;
            case 4:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, ORANGE).commit();
                break;
            case 5:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, PURPLE).commit();
                break;
            case 6:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, MAGENTA).commit();
                break;
            case 7:
                sharedPreferences.edit().putString(NOW_PLAYING_COLOR, BLACK).commit();
                break;
        }

        mToolbar.setBackground(UIElementsHelper.getGeneralActionBarBackground(getActivity()));
        UIElementsHelper.setStatusBarColor(getActivity());
    }

    /**
     * Click listener for the default browser preference.
     */
    private Preference.OnPreferenceClickListener defaultScreenClickListener = new Preference.OnPreferenceClickListener() {

        @Override
        public boolean onPreferenceClick(Preference preference) {
            //Get the current preference.
            int currentPreference = mApp.getSharedPreferences().getInt(Common.STARTUP_BROWSER, 0);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.default_browser);
            builder.setSingleChoiceItems(R.array.startup_screen_items, currentPreference, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mApp.getSharedPreferences().edit().putInt(Common.STARTUP_BROWSER, which).commit();

                    dialog.dismiss();
                    Toast.makeText(mContext, R.string.changes_saved, Toast.LENGTH_SHORT).show();

                }

            });

            builder.create().show();
            return false;
        }

    };

    /**
     * Checkbox click listener for the lockscreen controls preference.
     */
    private Preference.OnPreferenceChangeListener lockscreenControlsClickListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            boolean value = (Boolean) newValue;
            mApp.getSharedPreferences().edit().putBoolean(Common.SHOW_LOCKSCREEN_CONTROLS, value).commit();
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
