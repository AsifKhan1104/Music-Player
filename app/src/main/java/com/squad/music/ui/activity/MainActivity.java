package com.squad.music.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.dialog.AppExitDialog;
import com.squad.music.ui.dialog.TimePickerDialogFragment;
import com.squad.music.ui.fragment.FilesFoldersFragment;
import com.squad.music.ui.fragment.GridViewFragment;
import com.squad.music.ui.fragment.ListViewFragment;
import com.squad.music.ui.fragment.NavigationDrawerFragment;
import com.squad.music.ui.fragment.PlaylistFragment;
import com.squad.music.ui.fragment.QueueDrawerFragment;

public class MainActivity extends AppCompatActivity implements TimePickerDialogFragment.onSelectListener, View.OnClickListener {
    //Context and Common object(s).
    private Context mContext;
    private Common mApp;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mNavDrawerLayout;
    private RelativeLayout mCurrentQueueDrawerLayout;
    private Toolbar mToolbar;
    private QueueDrawerFragment mQueueDrawerFragment;
    private Menu mMenu;
    private FloatingActionButton mFloatingActionButton1, mFloatingActionButton2;

    //Current fragment params.
    private Fragment mCurrentFragment;
    public static int mCurrentFragmentId;
    public static int mCurrentFragmentLayout;

    //Layout flags.
    public static final String CURRENT_FRAGMENT = "CurrentFragment";
    public static final String ARTISTS_FRAGMENT_LAYOUT = "ArtistsFragmentLayout";
    public static final String ALBUM_ARTISTS_FRAGMENT_LAYOUT = "AlbumArtistsFragmentLayout";
    public static final String ALBUMS_FRAGMENT_LAYOUT = "AlbumsFragmentLayout";
    public static final String PLAYLISTS_FRAGMENT_LAYOUT = "PlaylistsFragmentLayout";
    public static final String GENRES_FRAGMENT_LAYOUT = "GenresFragmentLayout";
    public static final String FOLDERS_FRAGMENT_LAYOUT = "FoldersFragmentLayout";
    public static final String FRAGMENT_HEADER = "FragmentHeader";
    public static final int LIST_LAYOUT = 0;
    public static final int GRID_LAYOUT = 1;

    private boolean isAppLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkViewId();

        //Load the drawer fragments.
        loadDrawerFragments();

        //Load the fragment.
        loadFragment(savedInstanceState);

        //Check if this is the first time the app is being started.
        if (mApp.getSharedPreferences().getBoolean(Common.FIRST_RUN, true) == true) {
            showAlbumArtScanningDialog();
            mApp.getSharedPreferences().edit().putBoolean(Common.FIRST_RUN, false).commit();
        }
    }

    private void linkViewId() {
        //Context and Common object(s).
        mContext = getApplicationContext();
        mApp = (Common) getApplicationContext();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_activity_drawer_root);
        mNavDrawerLayout = (RelativeLayout) findViewById(R.id.nav_drawer_container);
        mCurrentQueueDrawerLayout = (RelativeLayout) findViewById(R.id.current_queue_drawer_container);

        mFloatingActionButton1 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item1);
        mFloatingActionButton2 = (FloatingActionButton) findViewById(R.id.floating_action_menu_item2);

        // on click listeners
        mFloatingActionButton1.setOnClickListener(this);
        mFloatingActionButton2.setOnClickListener(this);

        setToolBar();
        setDrawer();
    }

    private void setDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View view) {
                if (mQueueDrawerFragment != null &&
                        view == mCurrentQueueDrawerLayout)
                    mQueueDrawerFragment.setIsDrawerOpen(false);
            }

            @Override
            public void onDrawerOpened(View view) {
                if (mQueueDrawerFragment != null &&
                        view == mCurrentQueueDrawerLayout)
                    mQueueDrawerFragment.setIsDrawerOpen(true);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setToolBar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

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
     * Loads the correct fragment based on the selected browser.
     */
    public void loadFragment(Bundle savedInstanceState) {
        //Get the target fragment from savedInstanceState if it's not null (orientation changes?).
        if (savedInstanceState != null) {
            mCurrentFragmentId = savedInstanceState.getInt(CURRENT_FRAGMENT);
            invalidateOptionsMenu();

        } else {
            //Set the current fragment based on the intent's extras.
            if (getIntent().hasExtra(CURRENT_FRAGMENT)) {
                mCurrentFragmentId = getIntent().getExtras().getInt(CURRENT_FRAGMENT);
            } else {
                // below code should run only once this class loads
                if (!isAppLaunched) {
                    // check for shared preferences from settings
                    mCurrentFragmentId = mApp.getSharedPreferences().getInt(Common.STARTUP_BROWSER, 0);
                    isAppLaunched = true;
                }
            }

            String[] array = getResources().getStringArray(R.array.startup_screen_items);
            switch (mCurrentFragmentId) {
                case Common.ARTISTS_FRAGMENT:
                    mCurrentFragment = getLayoutFragment(Common.ARTISTS_FRAGMENT);
                    getSupportActionBar().setTitle(array[0]);
                    break;
                case Common.ALBUM_ARTISTS_FRAGMENT:
                    mCurrentFragment = getLayoutFragment(Common.ALBUM_ARTISTS_FRAGMENT);
                    break;
                case Common.ALBUMS_FRAGMENT:
                    mCurrentFragment = getLayoutFragment(Common.ALBUMS_FRAGMENT);
                    getSupportActionBar().setTitle(array[2]);
                    break;
                case Common.SONGS_FRAGMENT:
                    mCurrentFragment = getLayoutFragment(Common.SONGS_FRAGMENT);
                    getSupportActionBar().setTitle(array[3]);
                    break;
                case Common.PLAYLISTS_FRAGMENT:
                    //mCurrentFragment = getLayoutFragment(Common.PLAYLISTS_FRAGMENT);
                    mCurrentFragment = new PlaylistFragment();
                    getSupportActionBar().setTitle(array[4]);
                    break;
                case Common.GENRES_FRAGMENT:
                    mCurrentFragment = getLayoutFragment(Common.GENRES_FRAGMENT);
                    getSupportActionBar().setTitle(array[5]);
                    break;
                case Common.FOLDERS_FRAGMENT:
                    mCurrentFragment = new FilesFoldersFragment();
                    break;
            }

            switchContent(mCurrentFragment);
        }

    }

    /**
     * Retrieves the correct fragment based on the saved layout preference.
     */
    private Fragment getLayoutFragment(int fragmentId) {

        //Instantiate a new bundle.
        Fragment fragment = null;
        Bundle bundle = new Bundle();

        //Retrieve layout preferences for the current fragment.
        switch (fragmentId) {
            case Common.ARTISTS_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ARTISTS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ARTISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.artists));
                break;
            case Common.ALBUM_ARTISTS_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ALBUM_ARTISTS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ALBUM_ARTISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.album_artists));
                break;
            case Common.ALBUMS_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(ALBUMS_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.ALBUMS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.albums));
                break;
            case Common.SONGS_FRAGMENT:
                mCurrentFragmentLayout = LIST_LAYOUT;
                bundle.putInt(Common.FRAGMENT_ID, Common.SONGS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.songs));
                break;
            case Common.PLAYLISTS_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(PLAYLISTS_FRAGMENT_LAYOUT, LIST_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.PLAYLISTS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.playlists));
                break;
            case Common.GENRES_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(GENRES_FRAGMENT_LAYOUT, GRID_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.GENRES_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.genres));
                break;
            case Common.FOLDERS_FRAGMENT:
                mCurrentFragmentLayout = mApp.getSharedPreferences().getInt(FOLDERS_FRAGMENT_LAYOUT, LIST_LAYOUT);
                bundle.putInt(Common.FRAGMENT_ID, Common.FOLDERS_FRAGMENT);
                bundle.putString(FRAGMENT_HEADER, mContext.getResources().getString(R.string.folders));
                break;
        }

        //Return the correct layout fragment.
        if (mCurrentFragmentLayout == GRID_LAYOUT) {
            fragment = new GridViewFragment();
            fragment.setArguments(bundle);
        } else {
            fragment = new ListViewFragment();
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    /**
     * Called when the user taps on the "Play all" or "Shuffle all" action button.
     */
    public void playAll(boolean shuffle) {
        //Start the playback sequence.
        mApp.getPlaybackKickstarter().initPlayback(mContext, "", Common.PLAY_ALL_SONGS, 0, true, true);
    }

    /**
     * Loads the specified fragment into the target layout.
     */
    public void switchContent(Fragment fragment) {
        // Reset action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainActivityContainer, fragment)
                .commit();

        //Close the drawer(s).
        mDrawerLayout.closeDrawer(Gravity.START);
        invalidateOptionsMenu();

    }

    /**
     * Displays the message dialog for album art processing.
     */
    private void showAlbumArtScanningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.album_art);
        builder.setMessage(R.string.scanning_for_album_art_details);
        builder.setPositiveButton(R.string.got_it, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        builder.create().show();
    }

    /**
     * Inflates the generic MainActivity ActionBar layout.
     *
     * @param inflater The ActionBar's menu inflater.
     * @param menu     The ActionBar menu to work with.
     */
    private void showMainActivityActionItems(MenuInflater inflater, Menu menu) {
        //Inflate the menu.
        getMenu().clear();
        inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);

        //Set the ActionBar background
        getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Set the ActionBar text color.
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(0xFFFFFFFF);
            }

        }

    }

    /**
     * Displays the folder fragment's action items.
     *
     * @param filePath  The file path to set as the ActionBar's title text.
     * @param inflater  The ActionBar's menu inflater.
     * @param menu      The ActionBar menu to work with.
     * @param showPaste Pass true if the ActionBar is being updated for a copy/move operation.
     */
    public void showFolderFragmentActionItems(String filePath,
                                              MenuInflater inflater,
                                              Menu menu,
                                              boolean showPaste) {
        getMenu().clear();
        inflater.inflate(R.menu.files_folders_fragment, menu);


        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setLogo(0);
        getSupportActionBar().setIcon(0);

        if (showPaste) {
            //Change the ActionBar's background and show the Paste Here option.
            menu.findItem(R.id.action_paste).setVisible(true);
            menu.findItem(R.id.action_cancel).setVisible(true);
            getSupportActionBar().setBackgroundDrawable(mContext.getResources()
                    .getDrawable(R.drawable.cab_background_top_apptheme));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(new ColorDrawable(0xFF002E3E));

        } else {
            //Hide the Paste Here option and set the default ActionBar background.
            menu.findItem(R.id.action_paste).setVisible(false);
            menu.findItem(R.id.action_cancel).setVisible(false);
            getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));

        }

        LayoutInflater inflator = LayoutInflater.from(this);
        View view = inflator.inflate(R.layout.custom_actionbar_layout, null);

        TextView titleText = (TextView) view.findViewById(R.id.custom_actionbar_title);
        titleText.setText(filePath);
        titleText.setSelected(true);
        titleText.setTextColor(0xFFFFFFFF);

        //Inject the custom view into the ActionBar.
        getSupportActionBar().setCustomView(view);

    }

    /**
     * Initializes the ActionBar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        if (mCurrentFragmentId == Common.FOLDERS_FRAGMENT)
            showFolderFragmentActionItems(FilesFoldersFragment.currentDir,
                    getMenuInflater(), menu, false);
        else
            showMainActivityActionItems(getMenuInflater(), menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * ActionBar item selection listener.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_search:
                //ArtistsFragment.showSearch();
                return true;
            case R.id.action_queue_drawer:
                if (mDrawerLayout != null && mCurrentQueueDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(mCurrentQueueDrawerLayout)) {
                        mDrawerLayout.closeDrawer(mCurrentQueueDrawerLayout);
                    } else {
                        mDrawerLayout.openDrawer(mCurrentQueueDrawerLayout);
                    }

                }
                return true;
            case R.id.action_up:
                ((FilesFoldersFragment) mCurrentFragment).getParentDir();
                return true;
            case R.id.action_paste:
                ((FilesFoldersFragment) mCurrentFragment).pasteIntoCurrentDir(((FilesFoldersFragment) mCurrentFragment).copyMoveSourceFile);
                showMainActivityActionItems(getMenuInflater(), getMenu());
                return true;
            case R.id.action_cancel:
                ((FilesFoldersFragment) mCurrentFragment).copyMoveSourceFile = null;
                if (((FilesFoldersFragment) mCurrentFragment).shouldMoveCopiedFile)
                    Toast.makeText(mContext, R.string.move_canceled, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mContext, R.string.copy_canceled, Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(Gravity.START)) { // Close left drawer if opened
            mDrawerLayout.closeDrawer(Gravity.START);

        } else if (getCurrentFragmentId() == Common.FOLDERS_FRAGMENT) {
            if (((FilesFoldersFragment) mCurrentFragment).getCurrentDir().equals("/"))
                super.onBackPressed();
            else
                ((FilesFoldersFragment) mCurrentFragment).getParentDir();

        } else {
            AppExitDialog dialogFragment = new AppExitDialog();
            dialogFragment.show(getSupportFragmentManager(), "AppExitDialog");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getSupportActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
            getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(mContext));
        }

    }

    /**
     * Getters/Setters.
     */

    public int getCurrentFragmentId() {
        return mCurrentFragmentId;
    }

    public void setCurrentFragmentId(int id) {
        mCurrentFragmentId = id;
    }

    public Menu getMenu() {
        return mMenu;
    }

    @Override
    public void setOnSelectListener(long millis, boolean flag) {
        NavigationDrawerFragment navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.nav_drawer_container);
        navigationDrawerFragment.updateTimerText(millis, flag);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_action_menu_item1:
                openAppInGooglePlay(this.getPackageName());

                break;
            case R.id.floating_action_menu_item2:
                String packageName = this.getPackageName();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Here is the link for this amazing app - " + "https://play.google.com/store/apps/details?id=" + packageName);

                try {
                    startActivity(Intent.createChooser(intent, "Share"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // (handle error)
                }

                break;
        }
    }

    private void openAppInGooglePlay(String packageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException e) { // if there is no Google Play on device
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }
}
