package com.squad.music.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.model.Playlist;
import com.squad.music.ui.adapter.PlaylistAdapter;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private Context mContext;
    private Common mApp;
    private RecyclerView mRecyclerView;
    private View mRootView;
    public Handler mHandler = new Handler();

    private ArrayList<Playlist> mItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;

        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_playlist_view, container, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recyclerview);

        mHandler.postDelayed(queryRunnable, 400);

        return mRootView;
    }

    /**
     * Query runnable.
     */
    public Runnable queryRunnable = new Runnable() {

        @Override
        public void run() {
            new AsyncRunQuery().execute();

        }

    };

    /**
     * Runs the correct DB query based on the passed in fragment id and
     * displays the ListView.
     *
     * @author Saravan Pantham
     */
    public class AsyncRunQuery extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mItems = DBAccessHelper.getAllPlaylists(mContext);

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 2.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(600);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setAdapter(new PlaylistAdapter(mContext, mItems, PlaylistFragment.this));

            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation arg0) {
                    //mQuickScroll.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationStart(Animation arg0) {
                    mRecyclerView.setVisibility(View.VISIBLE);

                }

            });

            mRecyclerView.startAnimation(animation);

        }
    }
}