package com.squad.music.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andraskindler.quickscroll.QuickScroll;
import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.PauseOnScrollHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.MainActivity;
import com.squad.music.ui.adapter.SearchSongsAdapter;
import com.squad.music.utility.AppConstants;

import java.util.HashMap;

public class SearchSongsFragment extends Fragment {

    private Context mContext;
    private Activity mActivity;
    private SearchSongsFragment mFragment;
    private Common mApp;
    private View mRootView;
    private String mSearchQuery;

    private QuickScroll mQuickScroll;
    private SearchSongsAdapter mSearchSongsAdapter;
    private HashMap<Integer, String> mDBColumnsMap;
    private ListView mListView;
    private TextView mEmptyTextView;

    private RelativeLayout mSearchLayout;
    private EditText mSearchEditText;

    public Handler mHandler = new Handler();
    private Cursor mCursor;
    private String mQuerySelection = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_list_view, container, false);
        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mFragment = this;
        mActivity = getActivity();

        //Set the background. We're using getGridViewBackground() since the list doesn't have card items.
        mRootView.setBackgroundColor(UIElementsHelper.getGridViewBackground(mContext));

        //Grab the fragment. This will determine which data to load into the cursor.
        mSearchQuery = getArguments().getString(AppConstants.SEARCH_QUERY);
        mDBColumnsMap = new HashMap<Integer, String>();

        //Init the search fields.
        mSearchLayout = (RelativeLayout) mRootView.findViewById(R.id.search_layout);
        mSearchEditText = (EditText) mRootView.findViewById(R.id.search_field);

        mSearchEditText.setTypeface(TypefaceHelper.getTypeface(mContext, "RobotoCondensed-Regular"));
        mSearchEditText.setPaintFlags(mSearchEditText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        mSearchEditText.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
        mSearchEditText.setFocusable(true);
        mSearchEditText.setCursorVisible(true);

        mQuickScroll = (QuickScroll) mRootView.findViewById(R.id.quickscroll);

        mListView = (ListView) mRootView.findViewById(R.id.generalListView);
        mListView.setVerticalScrollBarEnabled(false);

        //Apply the ListViews' dividers.
        if (mApp.getCurrentTheme() == Common.DARK_THEME) {
            mListView.setDivider(mContext.getResources().getDrawable(R.drawable.icon_list_divider));
        } else {
            mListView.setDivider(mContext.getResources().getDrawable(R.drawable.icon_list_divider_light));
        }

        mListView.setDividerHeight(1);

        //Set the empty views.
        mEmptyTextView = (TextView) mRootView.findViewById(R.id.empty_view_text);
        mEmptyTextView.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Light"));
        mEmptyTextView.setPaintFlags(mEmptyTextView.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        //Create a set of options to optimize the bitmap memory usage.
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;

        new AsyncRunQuery().execute();
        return mRootView;
    }

    /**
     * Item click listener for the ListView.
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {
            mApp.getPlaybackKickstarter()

                    .initPlayback(mContext,
                            mQuerySelection,
                            Common.PLAY_ALL_SONGS,
                            index,
                            true,
                            false);

        }

    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRootView = null;

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        onItemClickListener = null;
        mListView = null;
        mListView = null;
        mSearchSongsAdapter = null;
        mContext = null;
        mHandler = null;

    }

    public class AsyncRunQuery extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //String selection = "AND " + DBAccessHelper.SONG_TITLE + "LIKE '%" + mSearchQuery + "%'";
            mCursor = mApp.getDBAccessHelper().getSongsSearch(mSearchQuery);
            mCursor.getCount();

            loadDBColumnNames();
            return null;
        }

        /**
         * Populates the DB column names based on the specifed fragment id.
         */
        private void loadDBColumnNames() {
            mDBColumnsMap.put(SearchSongsAdapter.TITLE_TEXT, DBAccessHelper.SONG_TITLE);
            mDBColumnsMap.put(SearchSongsAdapter.SOURCE, DBAccessHelper.SONG_SOURCE);
            mDBColumnsMap.put(SearchSongsAdapter.FILE_PATH, DBAccessHelper.SONG_FILE_PATH);
            mDBColumnsMap.put(SearchSongsAdapter.ARTWORK_PATH, DBAccessHelper.SONG_ALBUM_ART_PATH);
            mDBColumnsMap.put(SearchSongsAdapter.FIELD_1, DBAccessHelper.SONG_DURATION);
            mDBColumnsMap.put(SearchSongsAdapter.FIELD_2, DBAccessHelper.SONG_ARTIST);
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

            mSearchSongsAdapter = new SearchSongsAdapter(mContext, mActivity, mFragment, mDBColumnsMap);
            mListView.setAdapter(mSearchSongsAdapter);
            mListView.setOnItemClickListener(onItemClickListener);

	     /* SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(mSearchSongsAdapter);
            animationAdapter.setShouldAnimate(true);
	        animationAdapter.setShouldAnimateFromPosition(0);
	        animationAdapter.setAbsListView(mListView);
	        mListView.setAdapter(animationAdapter); */

            //Init the quick scroll widget.
            mQuickScroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE,
                    mListView,
                    (SearchSongsAdapter) mSearchSongsAdapter,
                    QuickScroll.STYLE_HOLO);

            int[] quickScrollColors = UIElementsHelper.getQuickScrollColors(mContext);
            PauseOnScrollHelper scrollListener = new PauseOnScrollHelper(mApp.getPicasso(), null, true, true);

            mQuickScroll.setOnScrollListener(scrollListener);
            mQuickScroll.setPicassoInstance(mApp.getPicasso());
            mQuickScroll.setHandlebarColor(quickScrollColors[0], quickScrollColors[0], quickScrollColors[1]);
            mQuickScroll.setIndicatorColor(quickScrollColors[1], quickScrollColors[0], quickScrollColors[2]);
            mQuickScroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);

            animation.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationEnd(Animation arg0) {
                    mQuickScroll.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationStart(Animation arg0) {
                    mListView.setVisibility(View.VISIBLE);

                }

            });

            mListView.startAnimation(animation);

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     * Getter methods.
     */

    public SearchSongsAdapter getListViewAdapter() {
        return (SearchSongsAdapter) mSearchSongsAdapter;
    }

    public ListView getListView() {
        return mListView;
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
