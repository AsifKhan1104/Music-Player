package com.squad.music.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.TypefaceHelper;

public class AlbumArtFragment extends Fragment {
	
	private Context mContext;
	private Common mApp;
	
	private TextView welcomeHeader;
	private TextView welcomeText1;
	
	private RadioGroup radioGroup;
	private RadioButton mPickWhatsBestRadioButton;
	private RadioButton mUseEmbeddedArtOnlyRadioButton;
	private RadioButton mUseFolderArtOnlyRadioButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mContext =  getActivity().getApplicationContext();
		mApp = (Common) mContext;
		View rootView = (View) getActivity().getLayoutInflater().inflate(R.layout.fragment_welcome_screen_4, null);
		
		welcomeHeader = (TextView) rootView.findViewById(R.id.permission_header);
		welcomeHeader.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Light"));
		
		welcomeText1 = (TextView) rootView.findViewById(R.id.welcome_text_1);
		welcomeText1.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Regular"));
        
        radioGroup = (RadioGroup) rootView.findViewById(R.id.album_art_radio_group);
        mPickWhatsBestRadioButton = (RadioButton) rootView.findViewById(R.id.pick_whats_best_for_me);
        mUseEmbeddedArtOnlyRadioButton = (RadioButton) rootView.findViewById(R.id.use_embedded_art_only);
        mUseFolderArtOnlyRadioButton = (RadioButton) rootView.findViewById(R.id.use_folder_art_only);
        
		mPickWhatsBestRadioButton.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Regular"));
		mUseEmbeddedArtOnlyRadioButton.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Regular"));
		mUseFolderArtOnlyRadioButton.setTypeface(TypefaceHelper.getTypeface(getActivity(), "Roboto-Regular"));
        
        //Check which album art source is selected and set the appropriate flag.
        if (mApp.getSharedPreferences().getInt("ALBUM_ART_SOURCE", 0)==0) {
        	mPickWhatsBestRadioButton.setChecked(true);
        } else if (mApp.getSharedPreferences().getInt("ALBUM_ART_SOURCE", 0)==1) {
        	mUseEmbeddedArtOnlyRadioButton.setChecked(true);
        } else if (mApp.getSharedPreferences().getInt("ALBUM_ART_SOURCE", 0)==2) {
        	mUseFolderArtOnlyRadioButton.setChecked(true);
        }
        
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.pick_whats_best_for_me:
					mApp.getSharedPreferences().edit().putInt("ALBUM_ART_SOURCE", 0).commit();
					break;
				case R.id.use_embedded_art_only:
					mApp.getSharedPreferences().edit().putInt("ALBUM_ART_SOURCE", 1).commit();
					break;
				case R.id.use_folder_art_only:
					mApp.getSharedPreferences().edit().putInt("ALBUM_ART_SOURCE", 2).commit();
					break;
				}
				
			}
        	
        });

        return rootView;
    }
	
}

