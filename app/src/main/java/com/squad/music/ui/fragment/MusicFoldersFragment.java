package com.squad.music.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.utility.MarshMallowUtils;

public class MusicFoldersFragment extends Fragment {

    private Context mContext;
    private Common mApp;
    private FragmentManager mChildFragmentManager;
    private MusicFoldersSelectionFragment mMusicFoldersSelectionFragment = null;
    private TextView mWelcomeHeader;
    private RadioGroup mMusicFoldersOptions;
    private RadioButton mMusicFoldersRadioButton;
    private TranslateAnimation mSlideInAnimation;
    private TranslateAnimation mSlideOutAnimation;
    private RelativeLayout mFoldersLayout;
    private static final int REQUEST_READ_PERMISSION = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        View rootView = (View) getActivity().getLayoutInflater().inflate(R.layout.fragment_welcome_screen_2, null);

        mFoldersLayout = (RelativeLayout) rootView.findViewById(R.id.folders_fragment_holder);
        if (mApp.getSharedPreferences().getInt("MUSIC_FOLDERS_SELECTION", 0) == 0) {
            mFoldersLayout.setVisibility(View.INVISIBLE);
            mFoldersLayout.setEnabled(false);
        } else {
            mFoldersLayout.setVisibility(View.VISIBLE);
            mFoldersLayout.setEnabled(true);
        }

        mSlideInAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        mSlideInAnimation.setDuration(600);
        mSlideInAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mSlideInAnimation.setAnimationListener(slideInListener);

        mSlideOutAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f);
        mSlideOutAnimation.setDuration(600);
        mSlideOutAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mSlideOutAnimation.setAnimationListener(slideOutListener);

        mChildFragmentManager = this.getChildFragmentManager();
        mChildFragmentManager.beginTransaction()
                .add(R.id.folders_fragment_holder, getMusicFoldersSelectionFragment())
                .commit();

        mWelcomeHeader = (TextView) rootView.findViewById(R.id.permission_header);
        mWelcomeHeader.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));

        mMusicFoldersOptions = (RadioGroup) rootView.findViewById(R.id.music_library_welcome_radio_group);
        mMusicFoldersRadioButton = (RadioButton) rootView.findViewById(R.id.pick_folders_radio);
        RadioButton getAllSongsRadioButton = (RadioButton) mMusicFoldersOptions.findViewById(R.id.get_all_songs_radio);
        RadioButton letMePickFoldersRadioButton = (RadioButton) mMusicFoldersOptions.findViewById(R.id.pick_folders_radio);

        getAllSongsRadioButton.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        letMePickFoldersRadioButton.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        mMusicFoldersOptions.setOnCheckedChangeListener(onCheckedChangeListener);
        return rootView;
    }

    /**
     * RadioButton selection listener.
     */
    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int radioButtonId) {
            switch (radioButtonId) {
                case R.id.get_all_songs_radio:
                    mFoldersLayout.startAnimation(mSlideOutAnimation);
                    mFoldersLayout.setEnabled(false);
                    break;
                case R.id.pick_folders_radio:
                    mFoldersLayout.startAnimation(mSlideInAnimation);
                    mFoldersLayout.setEnabled(true);

                    // permission dialog
                    checkForPermissions();
                    break;
            }

        }

    };

    // To check a single permission call (for marshmallow permission system)
    private void checkForPermissions() {
        if (!MarshMallowUtils.isPermissionGranted(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            MarshMallowUtils.requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_READ_PERMISSION, getString(R.string.permission_read_external_storage));
        }
    }

    //put the callback(if needed) on request for permission (allowed or denied)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION && grantResults.length == 1) {
            //do your code here
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMusicFoldersRadioButton.setChecked(true);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                MarshMallowUtils.showDialog(getActivity(), getString(R.string.permission_denial_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }
    }

    /**
     * Slide out animation listener.
     */
    private AnimationListener slideOutListener = new AnimationListener() {

        @Override
        public void onAnimationEnd(Animation arg0) {
            mFoldersLayout.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationStart(Animation arg0) {
            mFoldersLayout.setVisibility(View.VISIBLE);

        }

    };

    /**
     * Slide in animation listener.
     */
    private AnimationListener slideInListener = new AnimationListener() {

        @Override
        public void onAnimationEnd(Animation arg0) {
            mFoldersLayout.setVisibility(View.VISIBLE);

        }

        @Override
        public void onAnimationRepeat(Animation arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationStart(Animation arg0) {
            mFoldersLayout.setVisibility(View.VISIBLE);

        }

    };

    /**
     * Instantiates a new fragment if mMusicFoldersSelectionFragment is null.
     * Returns the current fragment, otherwise.
     */
    public MusicFoldersSelectionFragment getMusicFoldersSelectionFragment() {
        if (mMusicFoldersSelectionFragment == null) {
            mMusicFoldersSelectionFragment = new MusicFoldersSelectionFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("com.squad.music.WELCOME", true);
            mMusicFoldersSelectionFragment.setArguments(bundle);
        }

        return mMusicFoldersSelectionFragment;
    }

}

