package com.squad.music.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class SettingsScrobblingFragment extends Fragment {

    private Context mContext;
    private Common mApp;
    private View mRootView;
    private ListView mListView;
    private Toolbar mToolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_settings, container, false);
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(getActivity()));

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        setToolbar();

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
