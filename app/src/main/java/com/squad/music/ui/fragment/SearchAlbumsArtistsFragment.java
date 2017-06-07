package com.squad.music.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.BrowserSubGridActivity;
import com.squad.music.ui.activity.MainActivity;
import com.squad.music.ui.activity.SearchSubListActivity;
import com.squad.music.ui.adapter.SearchAlbumsArtitstsAdapter;
import com.squad.music.utility.AppConstants;

import java.util.HashMap;

public class SearchAlbumsArtistsFragment extends Fragment {

    private Context mContext;
    private SearchAlbumsArtistsFragment mFragment;
    private Common mApp;
    private View mRootView;
    private RelativeLayout mGridViewContainer;
    public int mFragmentId;
    private String mSearchQuery;

    private BaseAdapter mGridViewAdapter;
    private HashMap<Integer, String> mDBColumnsMap;
    private GridView mGridView;
    private TextView mEmptyTextView;

    public Handler mHandler = new Handler();
    private Cursor mCursor;
    private String mFragmentTitle;
    private String mQuerySelection = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_search_grid_view, container, false);
        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mFragment = this;

        //Set the background color and the partial color bleed.
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));

        //Grab the fragment. This will determine which data to load into the cursor.
        mFragmentId = getArguments().getInt(Common.FRAGMENT_ID);
        mSearchQuery = getArguments().getString(AppConstants.SEARCH_QUERY);
        mDBColumnsMap = new HashMap<Integer, String>();

        //Set the adapter for the outer gridview.
        mGridView = (GridView) mRootView.findViewById(R.id.generalGridView);
        mGridViewContainer = (RelativeLayout) mRootView.findViewById(R.id.fragment_grid_view_frontal_layout);
        mGridView.setVerticalScrollBarEnabled(false);

        //Set the number of gridview columns based on the screen density and orientation.
        if (mApp.isPhoneInLandscape() || mApp.isTabletInLandscape()) {
            mGridView.setNumColumns(4);
        } else if (mApp.isPhoneInPortrait()) {
            mGridView.setNumColumns(2);
        } else if (mApp.isTabletInPortrait()) {
            mGridView.setNumColumns(3);
        }

        //Set the empty views.
        mEmptyTextView = (TextView) mRootView.findViewById(R.id.empty_view_text);
        mEmptyTextView.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));
        mEmptyTextView.setPaintFlags(mEmptyTextView.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Create a set of options to optimize the bitmap memory usage.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;

        new AsyncRunQuery().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return mRootView;
    }

    /**
     * Query runnable.
     */
    public Runnable queryRunnable = new Runnable() {

        @Override
        public void run() {
            new AsyncRunQuery().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }

    };

    /**
     * Click listener for the "PLAY ALL" text.
     */
    private View.OnClickListener playAllClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mApp.isShuffleOn())
                ((MainActivity) getActivity()).playAll(true);
            else
                ((MainActivity) getActivity()).playAll(false);

        }

    };

    /**
     * Item click listener for the GridView/ListView.
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {

            //Determine the new activity's fragment id.
            int newFragmentId = getNewFragmentId();

            Intent intent;
            Bundle bundle = new Bundle();
            if (newFragmentId == Common.SEARCH_ALBUMS_FRAGMENT) {
                bundle.putInt("fragmentId", Common.ALBUMS_FLIPPED_FRAGMENT);
                intent = new Intent(mContext, SearchSubListActivity.class);
            } else {
                bundle.putInt("fragmentId", Common.ARTISTS_FLIPPED_FRAGMENT);
                intent = new Intent(mContext, BrowserSubGridActivity.class);
            }

            bundle.putString("headerImagePath", (String) view.getTag(R.string.album_art));
            bundle.putString("headerText", (String) view.getTag(R.string.title_text));
            bundle.putString("subText", (String) view.getTag(R.string.field_1));

            intent.putExtras(bundle);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }

    };

    /**
     * Determines the next activity's fragment id based on the
     * current activity's fragment id.
     */
    private int getNewFragmentId() {
        switch (mFragmentId) {
            case Common.SEARCH_ALBUMS_FRAGMENT:
                return Common.SEARCH_ALBUMS_FRAGMENT;
            case Common.SEARCH_ARTISTS_FRAGMENT:
                return Common.SEARCH_ARTISTS_FRAGMENT;
            default:
                return -1;
        }

    }

    /**
     * Runs the correct DB query based on the passed in fragment id and
     * displays the GridView.
     *
     * @author Saravan Pantham
     */
    public class AsyncRunQuery extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if (mFragmentId == Common.SEARCH_ALBUMS_FRAGMENT) {
                mCursor = mApp.getDBAccessHelper().getAlbumsViaSearch(mSearchQuery);
            } else {
                mCursor = mApp.getDBAccessHelper().getArtistsViaSearch(mSearchQuery);
            }
            loadDBColumnNames();

            return null;
        }

        /**
         * Populates the DB column names based on the specifed fragment id.
         */
        private void loadDBColumnNames() {

            switch (mFragmentId) {
                case Common.SEARCH_ARTISTS_FRAGMENT:
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.TITLE_TEXT, DBAccessHelper.SONG_ARTIST);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.SOURCE, DBAccessHelper.SONG_SOURCE);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.FILE_PATH, DBAccessHelper.SONG_FILE_PATH);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.ARTWORK_PATH, DBAccessHelper.SONG_ALBUM_ART_PATH);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.FIELD_1, DBAccessHelper.ALBUMS_COUNT);
                    break;
                case Common.SEARCH_ALBUMS_FRAGMENT:
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.TITLE_TEXT, DBAccessHelper.SONG_ALBUM);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.SOURCE, DBAccessHelper.SONG_SOURCE);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.FILE_PATH, DBAccessHelper.SONG_FILE_PATH);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.ARTWORK_PATH, DBAccessHelper.SONG_ALBUM_ART_PATH);
                    mDBColumnsMap.put(SearchAlbumsArtitstsAdapter.FIELD_1, DBAccessHelper.SONG_ARTIST);
                    break;
            }

        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            //mHandler.postDelayed(initGridView, 200);
            showGrid();

        }

    }

    public void showGrid() {
        // no data found text
        if (mCursor.getCount() == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        }

        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

        animation.setDuration(150);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        mGridViewAdapter = new SearchAlbumsArtitstsAdapter(mContext, mFragment, mDBColumnsMap);
        //mGridView.setAdapter(mGridViewAdapter);

        //GridView animation adapter.
        final SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(mGridViewAdapter, 100, 150);
        animationAdapter.setShouldAnimate(true);
        animationAdapter.setShouldAnimateFromPosition(0);
        animationAdapter.setAbsListView(mGridView);
        mGridView.setAdapter(animationAdapter);
        mGridView.setOnItemClickListener(onItemClickListener);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {
                mGridView.setVisibility(View.VISIBLE);

            }

        });

        mGridView.startAnimation(animation);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        onItemClickListener = null;
        mGridView = null;
        mGridViewAdapter = null;
        mContext = null;
        mHandler = null;

    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     * Getter methods.
     */

    public SearchAlbumsArtitstsAdapter getGridViewAdapter() {
        return (SearchAlbumsArtitstsAdapter) mGridViewAdapter;
    }

    public GridView getGridView() {
        return mGridView;
    }

    public Cursor getCursor() {
        return mCursor;
    }

	/*
     * Setter methods.
	 */

    public void setCursor(Cursor cursor) {
        this.mCursor = cursor;
    }

}
