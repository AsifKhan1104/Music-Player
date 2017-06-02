package com.squad.music.ui.activity;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.animations.TranslateAnimation;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.PauseOnScrollHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.adapter.ListViewCardsAdapter;
import com.squad.music.ui.adapter.PlayListDetailAdapter;
import com.squad.music.ui.fragment.NavigationDrawerFragment;
import com.squad.music.ui.fragment.QueueDrawerFragment;
import com.squad.music.utility.AppConstants;

import java.util.HashMap;

public class PlayListDetailActivity extends AppCompatActivity {

    //Context and common objects.
    private Context mContext;
    private Common mApp;
    private Handler mHandler;
    private QueueDrawerFragment mQueueDrawerFragment;

    //UI elements
    private ImageView mHeaderImage;
    public ListView mListView;
    private RelativeLayout mDrawerParentLayout;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mNavDrawerLayout;
    private RelativeLayout mCurrentQueueDrawerLayout;

    //Data adapter objects/vars.
    private HashMap<Integer, String> mDBColumnsMap;
    private PlayListDetailAdapter mListViewAdapter;
    private Cursor mCursor;
    private String mQuerySelection;

    //Arguments passed in from the calling activity.
    private String mPlaylistId;
    private String mPlaylistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = getApplicationContext();
        mApp = (Common) mContext;
        mHandler = new Handler();
        mDBColumnsMap = new HashMap<Integer, String>();

        //Set the theme and inflate the layout.
        //setTheme();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);

        mPlaylistId = getIntent().getExtras().getString(AppConstants.PLAYLIST_ID);
        mPlaylistName = getIntent().getExtras().getString(AppConstants.PLAYLIST_NAME);

        mListView = (ListView) findViewById(R.id.browser_sub_list_view);
        mDrawerParentLayout = (RelativeLayout) findViewById(R.id.browser_sub_drawer_parent);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.browser_sub_drawer_root);
        mNavDrawerLayout = (RelativeLayout) findViewById(R.id.nav_drawer_container);
        mCurrentQueueDrawerLayout = (RelativeLayout) findViewById(R.id.current_queue_drawer_container);

        //Apply the ListViews' dividers.
        if (mApp.getCurrentTheme() == Common.DARK_THEME) {
            mListView.setDivider(mContext.getResources().getDrawable(R.drawable.list_divider));
        } else {
            mListView.setDivider(mContext.getResources().getDrawable(R.drawable.list_divider_light));
        }
        mListView.setDividerHeight(1);

        mDrawerParentLayout.setBackgroundColor(UIElementsHelper.getBackgroundColor(mContext));

        //Load the drawer fragments.
        loadDrawerFragments();

        //Start loading the cursor.
        startLoading();
    }

    public void startLoading() {
        AsyncRunQuery task = new AsyncRunQuery();
        task.execute();
    }

    /**
     * Sets the entire activity-wide theme.
     */
    /*private void setTheme() {
        if (mApp.getCurrentTheme()==Common.DARK_THEME) {
            setTheme(R.style.AppThemeNoActionBar);
        } else {
            setTheme(R.style.AppThemeLightNoActionBar);
        }

    }*/

    /**
     * Loads the drawer fragments.
     */
    private void loadDrawerFragments() {
        //Load the navigation drawer.
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_drawer_container, new NavigationDrawerFragment())
                .commit();

        //Load the current queue drawer.
        mQueueDrawerFragment = new QueueDrawerFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.current_queue_drawer_container, mQueueDrawerFragment)
                .commit();

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
            mCursor = mApp.getDBAccessHelper().getFragmentCursor(mContext, mPlaylistId, Common.PLAYLISTS_FRAGMENT);
            loadDBColumnNames();

            return null;
        }

        /**
         * Populates the DB column names based on the specifed fragment id.
         */
        private void loadDBColumnNames() {

            /*switch (mFragmentId) {
                case Common.ARTISTS_FLIPPED_SONGS_FRAGMENT:
                case Common.ALBUM_ARTISTS_FLIPPED_SONGS_FRAGMENT:
                case Common.ALBUMS_FLIPPED_FRAGMENT:
                case Common.GENRES_FLIPPED_SONGS_FRAGMENT:
                    mDBColumnsMap.put(ListViewCardsAdapter.TITLE_TEXT, DBAccessHelper.SONG_TITLE);
                    mDBColumnsMap.put(ListViewCardsAdapter.SOURCE, DBAccessHelper.SONG_SOURCE);
                    mDBColumnsMap.put(ListViewCardsAdapter.FILE_PATH, DBAccessHelper.SONG_FILE_PATH);
                    mDBColumnsMap.put(ListViewCardsAdapter.ARTWORK_PATH, DBAccessHelper.SONG_ALBUM_ART_PATH);
                    mDBColumnsMap.put(ListViewCardsAdapter.FIELD_1, DBAccessHelper.SONG_DURATION);
                    mDBColumnsMap.put(ListViewCardsAdapter.FIELD_2, DBAccessHelper.SONG_ARTIST);
                    mDBColumnsMap.put(ListViewCardsAdapter.FIELD_3, DBAccessHelper.SONG_TRACK_NUMBER);
                    break;
            }*/

        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            mHandler.postDelayed(initGridView, 200);

        }

    }

    /**
     * Item click listener for the ListView.
     */
    /*private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int index, long id) {
            int playbackRouteId = Common.PLAY_ALL_SONGS;
            switch (mFragmentId) {
                case Common.ARTISTS_FLIPPED_SONGS_FRAGMENT:
                case Common.ALBUM_ARTISTS_FLIPPED_SONGS_FRAGMENT:
                case Common.GENRES_FLIPPED_FRAGMENT:
                case Common.ALBUMS_FLIPPED_FRAGMENT:
                    playbackRouteId = Common.PLAY_ALL_BY_ALBUM;
                    break;
            }

            mApp.getPlaybackKickstarter()
                    .initPlayback(mContext,
                            mQuerySelection,
                            playbackRouteId,
                            index,
                            true,
                            false);

        }

    };*/

    /**
     * Runnable that loads the GridView after a set interval.
     */
    private Runnable initGridView = new Runnable() {

        @Override
        public void run() {
            android.view.animation.TranslateAnimation animation = new
                    android.view.animation.TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 2.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(150);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());

            //mListViewAdapter = new PlayListDetailAdapter(mContext, PlayListDetailActivity.this, mDBColumnsMap);
            //mListView.setAdapter(mListViewAdapter);

            //GridView animation adapter.
            final SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(mListViewAdapter, 100, 150);
            animationAdapter.setShouldAnimate(true);
            animationAdapter.setShouldAnimateFromPosition(0);
            animationAdapter.setAbsListView(mListView);
            mListView.setAdapter(animationAdapter);
            //mListView.setOnItemClickListener(onItemClickListener);

            PauseOnScrollHelper scrollHelper = new PauseOnScrollHelper(mApp.getPicasso(), onScrollListener, false, true);
            mListView.setOnScrollListener(scrollHelper);

            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationEnd(Animation arg0) {

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

    };

    /**
     * Slides away the header layout.
     */
    private void slideAwayHeader() {
        TranslateAnimation slideDown = new TranslateAnimation(mListView, 400, new AccelerateInterpolator(2.0f),
                View.INVISIBLE, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -2.0f);

        slideDown.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mListView.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mListView.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        });

        slideDown.animate();
    }

    /**
     * Slides away the GridView.
     */
    private void slideAwayGridView() {
        android.view.animation.TranslateAnimation animation = new
                android.view.animation.TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 2.0f);

        animation.setDuration(400);
        animation.setInterpolator(new AccelerateInterpolator(2.0f));
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                mListView.setVisibility(View.INVISIBLE);
                PlayListDetailActivity.super.onBackPressed();

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationStart(Animation arg0) {

            }

        });

        mListView.startAnimation(animation);
    }

    /**
     * Scroll listener to calculate the ListView's scroll offset and adjust
     * the header view accordingly.
     */
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            try {
                View topChild = view.getChildAt(0);
                int scrollY = -(topChild.getTop()) + view.getFirstVisiblePosition() * topChild.getHeight();
                int adjustedScrollY = (int) ((-scrollY) - mApp.convertDpToPixels(340.0f, mContext));

                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mListView.getLayoutParams();
                params.topMargin = adjustedScrollY / 3;
                mListView.setLayoutParams(params);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public void onBackPressed() {
        slideAwayHeader();
        slideAwayGridView();

    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

}
