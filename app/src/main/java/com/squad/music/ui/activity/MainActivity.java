package com.squad.music.ui.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.helper.UIElementsHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Context and Common object(s).
    private Context mContext;
    private Common mApp;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //Context and Common object(s).
        mContext = getApplicationContext();
        mApp = (Common) getApplicationContext();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Check if this is the first time the app is being started.
        if (mApp.getSharedPreferences().getBoolean(Common.FIRST_RUN, true) == true) {
            //showAlbumArtScanningDialog();
            mApp.getSharedPreferences().edit().putBoolean(Common.FIRST_RUN, false).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setLogo(0);
        getActionBar().setIcon(0);

        if (showPaste) {
            //Change the ActionBar's background and show the Paste Here option.
            menu.findItem(R.id.action_paste).setVisible(true);
            menu.findItem(R.id.action_cancel).setVisible(true);
            getActionBar().setBackgroundDrawable(this.getResources()
                    .getDrawable(R.drawable.cab_background_top_apptheme));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(new ColorDrawable(0xFF002E3E));

        } else {
            //Hide the Paste Here option and set the default ActionBar background.
            menu.findItem(R.id.action_paste).setVisible(false);
            menu.findItem(R.id.action_cancel).setVisible(false);
            getActionBar().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(this));

            //Change the KitKat system bar color.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
                getWindow().setBackgroundDrawable(UIElementsHelper.getGeneralActionBarBackground(this));

        }

        LayoutInflater inflator = LayoutInflater.from(this);
        View view = inflator.inflate(R.layout.custom_actionbar_layout, null);

        TextView titleText = (TextView) view.findViewById(R.id.custom_actionbar_title);
        titleText.setText(filePath);
        titleText.setSelected(true);
        titleText.setTextColor(0xFFFFFFFF);

        //Inject the custom view into the ActionBar.
        getActionBar().setCustomView(view);

    }

    public Menu getMenu() {
        return mMenu;
    }
}
