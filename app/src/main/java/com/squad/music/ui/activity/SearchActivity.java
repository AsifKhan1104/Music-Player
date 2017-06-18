package com.squad.music.ui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.fragment.SearchAlbumsArtistsFragment;
import com.squad.music.ui.fragment.SearchSongsFragment;
import com.squad.music.utility.AppConstants;

public class SearchActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String mSearchQuery;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private Common mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mApp = (Common) getApplicationContext();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Set the theme and inflate the layout.
        setTheme();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        handleIntent(getIntent());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY);
            // set title of toolbar
            getSupportActionBar().setTitle(mSearchQuery);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Bundle bundle = new Bundle();
            bundle.putString(AppConstants.SEARCH_QUERY, mSearchQuery);

            switch (position) {
                case 0:
                    SearchSongsFragment fragment1 = new SearchSongsFragment();
                    fragment1.setArguments(bundle);

                    return fragment1;
                case 1:
                    SearchAlbumsArtistsFragment fragment2 = new SearchAlbumsArtistsFragment();
                    bundle.putInt(Common.FRAGMENT_ID, Common.SEARCH_ALBUMS_FRAGMENT);
                    fragment2.setArguments(bundle);

                    return fragment2;
                case 2:
                    SearchAlbumsArtistsFragment fragment3 = new SearchAlbumsArtistsFragment();
                    bundle.putInt(Common.FRAGMENT_ID, Common.SEARCH_ARTISTS_FRAGMENT);
                    fragment3.setArguments(bundle);

                    return fragment3;
                default:
                    break;
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Songs";
                case 1:
                    return "Albums";
                case 2:
                    return "Artists";
            }
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mToolbar.setBackground(UIElementsHelper.getGeneralActionBarBackground(this));
        mTabLayout.setBackground(UIElementsHelper.getGeneralActionBarBackground(this));
        UIElementsHelper.setStatusBarColor(this);
    }

    /**
     * Sets the entire activity-wide theme.
     */
    private void setTheme() {
        if (mApp.getCurrentTheme() == Common.LIGHT_THEME) {
            setTheme(R.style.AppTheme);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        } else {
            setTheme(R.style.AppThemeDark);
            mToolbar.setPopupTheme(R.style.AppTheme_PopupOverlay_Dark);
        }
    }
}
