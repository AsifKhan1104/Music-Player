package com.squad.music.ui.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.squad.music.Common;
import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.ui.activity.AppCompatPreferenceActivity;
import com.squad.music.ui.adapter.BlacklistedElementsAdapter;

import java.util.ArrayList;

public class SettingsBlacklistsFragment extends Fragment {

    private Context mContext;
    private Common mApp;
    private View mRootView;
    private ListView mListView;
    private Toolbar mToolbar;

    //Temp array that holds the checkbox statuses in the ListView.
    private static ArrayList<Boolean> checkboxStatuses = new ArrayList<Boolean>();
    private static String MANAGER_TYPE;
    private Cursor cursor;
    private BlacklistedElementsAdapter adapter;

    //Temp ArrayLists for the cursor data.
    private ArrayList<String> elementNameList = new ArrayList<String>();
    private ArrayList<String> artistNameList = new ArrayList<String>();
    private ArrayList<String> filePathList = new ArrayList<String>();
    private ArrayList<String> songIdsList = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_blacklist_view, container, false);
        mRootView.setBackgroundColor(UIElementsHelper.getBackgroundColor(getActivity()));

        mContext = getActivity().getApplicationContext();
        mApp = (Common) mContext;
        mListView = (ListView) mRootView.findViewById(android.R.id.list);

        setToolbar();

        ImageView blacklistImage = (ImageView) mRootView.findViewById(R.id.blacklist_image);
        blacklistImage.setImageResource(UIElementsHelper.getIcon(mContext, "manage_blacklists"));

        //MANAGER_TYPE = getArguments().getString("MANAGER_TYPE");
        MANAGER_TYPE = "ARTISTS";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //Get a cursor with a list of blacklisted elements.
        if (MANAGER_TYPE.equals("ARTISTS")) {
            builder.setTitle(R.string.blacklisted_artists);
            cursor = mApp.getDBAccessHelper().getBlacklistedArtists();

            //Finish the activity if there are no blacklisted elements.
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), R.string.no_blacklisted_items_found, Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {
                //Load the cursor data into temporary ArrayLists.
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    elementNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ARTIST)));
                    artistNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ARTIST)));
                }

            }

        } else if (MANAGER_TYPE.equals("ALBUMS")) {
            builder.setTitle(R.string.blacklisted_albums);
            cursor = mApp.getDBAccessHelper().getBlacklistedAlbums();

            //Finish the activity if there are no blacklisted elements.
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), R.string.no_blacklisted_items_found, Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {
                //Load the cursor data into temporary ArrayLists.
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    elementNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ALBUM)));
                    artistNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ARTIST)));
                }

            }

        } else if (MANAGER_TYPE.equals("SONGS")) {
            builder.setTitle(R.string.blacklisted_songs);
            cursor = mApp.getDBAccessHelper().getAllBlacklistedSongs();

            //Finish the activity if there are no blacklisted elements.
            if (cursor.getCount() == 0) {
                Toast.makeText(getActivity(), R.string.no_blacklisted_items_found, Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {
                //Load the cursor data into temporary ArrayLists.
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    elementNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_TITLE)));
                    artistNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ARTIST)));
                    filePathList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_FILE_PATH)));
                    songIdsList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.SONG_ID)));
                }

            }

        } else if (MANAGER_TYPE.equals("PLAYLISTS")) {
            /*builder.setTitle(R.string.blacklisted_playlists);
            DBAccessHelper playlistsDBHelper = new DBAccessHelper(getActivity());
        	cursor = playlistsDBHelper.getAllBlacklistedPlaylists();

        	//Finish the activity if there are no blacklisted elements.
            if (cursor.getCount()==0) {
            	Toast.makeText(getActivity(), R.string.no_blacklisted_items_found, Toast.LENGTH_LONG).show();
            	getActivity().finish();
            } else {
                //Load the cursor data into temporary ArrayLists.
            	for (int i=0; i < cursor.getCount(); i++) {
            		cursor.moveToPosition(i);
            		elementNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.PLAYLIST_NAME)));
            		artistNameList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.NUMBER_OF_SONGS)));
            		filePathList.add(cursor.getString(cursor.getColumnIndex(DBAccessHelper.PLAYLIST_FILE_PATH)));
            	}

            }*/

        } else {
            Toast.makeText(getActivity(), R.string.error_occurred, Toast.LENGTH_LONG).show();
            getActivity().finish();
        }


        TextView blacklistManagerInfoText = (TextView) mRootView.findViewById(R.id.blacklist_manager_info_text);
        DragSortListView blacklistManagerListView = (DragSortListView) mRootView.findViewById(R.id.blacklist_manager_list);
        blacklistManagerListView.setRemoveListener(onRemove);

        blacklistManagerInfoText.setTypeface(TypefaceHelper.getTypeface(getActivity(), "RobotoCondensed-Light"));
        blacklistManagerInfoText.setPaintFlags(blacklistManagerInfoText.getPaintFlags()
                | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        blacklistManagerListView.setFastScrollEnabled(true);
        adapter = new BlacklistedElementsAdapter(getActivity(), elementNameList, artistNameList, MANAGER_TYPE);
        blacklistManagerListView.setAdapter(adapter);

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

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {

        @Override
        public void remove(int which) {

            //Before we remove the item, retrieve its parameters so we can form a where clause.
            String element = elementNameList.get(which);

            String artist = "";
            if (artistNameList.size() != 0) {
                artist = artistNameList.get(which);
            }

            String filePath = "";
            if (filePathList.size() != 0) {
                filePath = filePathList.get(which);
            }

            //Escape any rogue apostrophes.
            if (element.contains("'")) {
                element = element.replace("'", "''");
            }

            if (artist.contains("'")) {
                artist = artist.replace("'", "''");
            }

            if (filePath.contains("'")) {
                filePath = filePath.replace("'", "''");
            }

            adapter.remove(adapter.getItem(which));

            //Remove the elements in the actual ArrayLists.
            artistNameList.remove(which);

            //Unblacklist the element based on the MANAGER_TYPE.
            String where = "";
            if (MANAGER_TYPE.equals("ARTISTS")) {
                where = DBAccessHelper.SONG_ARTIST + "=" + "'" + element + "'";
                mApp.getDBAccessHelper().setBlacklistForArtist(element, false);

            } else if (MANAGER_TYPE.equals("ALBUMS")) {
                where = DBAccessHelper.SONG_ARTIST + "=" + "'" + element + "'";
                mApp.getDBAccessHelper().setBlacklistForAlbum(element, artist, false);

            } else if (MANAGER_TYPE.equals("SONGS")) {
                where = DBAccessHelper.SONG_ARTIST + "=" + "'" + element + "'";
                mApp.getDBAccessHelper().setBlacklistForSong(songIdsList.get(which), false);

            } else if (MANAGER_TYPE.equals("PLAYLISTS")) {
                /*DBAccessHelper playlistsDBHelper = new DBAccessHelper(mContext);
                where = DBAccessHelper.SONG_ARTIST + "=" + "'" + element + "'";
            	playlistsDBHelper.unBlacklistPlaylist(filePath);
            	playlistsDBHelper.close();*/

            }

            Toast.makeText(mContext, R.string.item_removed_from_blacklist, Toast.LENGTH_LONG).show();

        }

    };

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
