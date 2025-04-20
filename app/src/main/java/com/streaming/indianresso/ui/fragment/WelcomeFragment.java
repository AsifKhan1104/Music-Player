package com.streaming.indianresso.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.streaming.indianresso.R;
import com.streaming.indianresso.helper.TypefaceHelper;

public class WelcomeFragment extends Fragment {

    private Context mContext;
    private TextView welcomeHeader;
    private TextView welcomeText1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        View rootView = (View) getActivity().getLayoutInflater().inflate(R.layout.fragment_welcome_screen, null);

        welcomeHeader = (TextView) rootView.findViewById(R.id.permission_header);
        welcomeHeader.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));

        welcomeText1 = (TextView) rootView.findViewById(R.id.welcome_text_1);
        welcomeText1.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        welcomeText1.setText(getString(R.string.welcome_text_1) + " " + getString(R.string.app_name) + ". " + getString(R.string.welcome_text_1_half));

        return rootView;
    }

}

