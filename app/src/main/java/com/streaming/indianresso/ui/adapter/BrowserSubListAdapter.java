package com.streaming.indianresso.ui.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.fragment.app.FragmentTransaction;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.andraskindler.quickscroll.Scrollable;
import com.streaming.indianresso.Common;
import com.streaming.indianresso.R;
import com.streaming.indianresso.asynctask.AsyncAddToQueueTask;
import com.streaming.indianresso.helper.TypefaceHelper;
import com.streaming.indianresso.helper.UIElementsHelper;
import com.streaming.indianresso.ui.activity.BrowserSubListActivity;
import com.streaming.indianresso.ui.dialog.AddToPlaylistDialog2;
import com.streaming.indianresso.ui.dialog.CautionEditSongsDialog;
import com.streaming.indianresso.ui.dialog.CustomDialogFragment;
import com.streaming.indianresso.ui.dialog.ID3sSongEditorDialog;
import com.streaming.indianresso.utility.AppConstants;
import com.streaming.indianresso.utility.Utils;

import java.util.HashMap;

/**
 * Generic ListView adapter for ListViewFragment.
 */
public class BrowserSubListAdapter extends SimpleCursorAdapter implements Scrollable {

    private Context mContext;
    private Common mApp;
    private BrowserSubListActivity mActivity;
    public static ListViewHolder mHolder = null;
    private String mFilePath = "", mSongTitle = "", mArtist = "";

    //HashMap for DB column names.
    private HashMap<Integer, String> mDBColumnsMap;
    public static final int TITLE_TEXT = 0;
    public static final int SOURCE = 1;
    public static final int FILE_PATH = 2;
    public static final int ARTWORK_PATH = 3;
    public static final int FIELD_1 = 4; //Empty fields for other
    public static final int FIELD_2 = 5;
    public static final int FIELD_3 = 6;
    public static final int FIELD_4 = 7;
    public static final int FIELD_5 = 8;

    public BrowserSubListAdapter(Context context, BrowserSubListActivity activity,
                                 HashMap<Integer, String> dbColumnsMap) {

        super(context, -1, activity.getCursor(), new String[]{}, new int[]{}, 0);
        mContext = context;
        mActivity = activity;
        mApp = (Common) mContext.getApplicationContext();
        mDBColumnsMap = dbColumnsMap;

    }

    /**
     * Quick scroll indicator implementation.
     */
    @Override
    public String getIndicatorForPosition(int childPosition, int groupPosition) {
        Cursor c = (Cursor) getItem(childPosition);
        String title = c.getString(c.getColumnIndex(mDBColumnsMap.get(TITLE_TEXT)));
        if (title != null && title.length() > 1)
            return "  " + title.substring(0, 1) + "  ";
        else
            return "  N/A  ";
    }

    /**
     * Returns the current position of the top view in the list/grid.
     */
    @Override
    public int getScrollPosition(int childPosition, int groupPosition) {
        // TODO Auto-generated method stub
        return childPosition;
    }

    /**
     * Returns the individual row/child in the list/grid.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Cursor c = (Cursor) getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_sub_browser_item, parent, false);

            mHolder = new ListViewHolder();
            mHolder.trackNumber = (TextView) convertView.findViewById(R.id.listViewTrackNumber);
            mHolder.titleText = (TextView) convertView.findViewById(R.id.listViewTitleText);
            mHolder.subText = (TextView) convertView.findViewById(R.id.listViewSubText);
            mHolder.rightSubText = (TextView) convertView.findViewById(R.id.listViewRightSubText);
            mHolder.overflowIcon = (ImageButton) convertView.findViewById(R.id.listViewOverflow);

            mHolder.titleText.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
            mHolder.subText.setTextColor(UIElementsHelper.getSmallTextColor(mContext));
            mHolder.rightSubText.setTextColor(UIElementsHelper.getSmallTextColor(mContext));
            mHolder.trackNumber.setTextColor(UIElementsHelper.getSmallTextColor(mContext));

            mHolder.titleText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
            mHolder.subText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
            mHolder.rightSubText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
            mHolder.trackNumber.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

            mHolder.overflowIcon.setImageResource(UIElementsHelper.getIcon(mContext, "ic_action_overflow"));
            mHolder.overflowIcon.setOnClickListener(overflowClickListener);
            mHolder.overflowIcon.setFocusable(false);
            mHolder.overflowIcon.setFocusableInTouchMode(false);

            convertView.setTag(mHolder);
        } else {
            mHolder = (ListViewHolder) convertView.getTag();
        }

        //Retrieve data from the cursor.
        String titleText = "";
        String source = "";
        String filePath = "";
        String artworkPath = "";
        String field1 = "";
        String field2 = "";
        String field3 = "";
        String field4 = "";
        String field5 = "";
        try {
            titleText = c.getString(c.getColumnIndex(mDBColumnsMap.get(TITLE_TEXT)));
            source = c.getString(c.getColumnIndex(mDBColumnsMap.get(SOURCE)));
            filePath = c.getString(c.getColumnIndex(mDBColumnsMap.get(FILE_PATH)));
            artworkPath = c.getString(c.getColumnIndex(mDBColumnsMap.get(ARTWORK_PATH)));
            field1 = c.getString(c.getColumnIndex(mDBColumnsMap.get(FIELD_1)));
            field2 = c.getString(c.getColumnIndex(mDBColumnsMap.get(FIELD_2)));
            field3 = c.getString(c.getColumnIndex(mDBColumnsMap.get(FIELD_3)));
            field4 = c.getString(c.getColumnIndex(mDBColumnsMap.get(FIELD_4)));
            field5 = c.getString(c.getColumnIndex(mDBColumnsMap.get(FIELD_5)));

        } catch (NullPointerException e) {
            //e.printStackTrace();
        }

        //Set the tags for this grid item.
        convertView.setTag(R.string.title_text, titleText);
        convertView.setTag(R.string.song_source, source);
        convertView.setTag(R.string.song_file_path, filePath);
        convertView.setTag(R.string.album_art, artworkPath);
        convertView.setTag(R.string.field_1, field1);
        convertView.setTag(R.string.field_2, field2);
        convertView.setTag(R.string.field_3, field3);
        convertView.setTag(R.string.field_4, field4);
        convertView.setTag(R.string.field_5, field5);

        //Set the tags for this list item's overflow button.
        mHolder.overflowIcon.setTag(R.string.title_text, titleText);
        mHolder.overflowIcon.setTag(R.string.source, source);
        mHolder.overflowIcon.setTag(R.string.file_path, filePath);
        mHolder.overflowIcon.setTag(R.string.field_1, field1);
        mHolder.overflowIcon.setTag(R.string.field_2, field2);
        mHolder.overflowIcon.setTag(R.string.field_3, field3);
        mHolder.overflowIcon.setTag(R.string.field_4, field4);
        mHolder.overflowIcon.setTag(R.string.field_5, field5);

        //Set the title text in the ListView.
        mHolder.titleText.setText(titleText);
        mHolder.subText.setText(field2);
        mHolder.rightSubText.setText(field1);
        //mHolder.trackNumber.setText(field3);

        return convertView;
    }


    /**
     * Click listener for overflow button.
     */
    private OnClickListener overflowClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(mActivity, v);
            menu.inflate(R.menu.list_overflow_menu);
            menu.setOnMenuItemClickListener(popupMenuItemClickListener);
            mFilePath = (String) v.getTag(R.string.file_path);
            mSongTitle = (String) v.getTag(R.string.title_text);
            mArtist = (String) v.getTag(R.string.field_2);
            menu.show();

        }

    };

    /**
     * Menu item click listener for the pop up menu.
     */
    private PopupMenu.OnMenuItemClickListener popupMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int id = item.getItemId();
            if (id == R.id.edit_artist_tags) {
                //Edit Artist Tags.
                if (mApp.getSharedPreferences().getBoolean("SHOW_ARTIST_EDIT_CAUTION", true)) {
                    FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EDIT_TYPE, AppConstants.KEY_SONG);
                    bundle.putString(AppConstants.KEY_SONG, mFilePath);
                    CautionEditSongsDialog dialog = new CautionEditSongsDialog();
                    dialog.setArguments(bundle);
                    dialog.show(transaction, "cautionSongsDialog");
                } else {
                    FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                    Bundle bundle = new Bundle();
                    bundle.putString(AppConstants.EDIT_TYPE, AppConstants.KEY_SONG);
                    bundle.putString(AppConstants.KEY_SONG, mFilePath);
                    ID3sSongEditorDialog dialog = new ID3sSongEditorDialog();
                    dialog.setArguments(bundle);
                    dialog.show(ft, "id3SongEditorDialog");
                }
            } else if (id == R.id.add_to_queue) {
                //Add to Queue.
                AsyncAddToQueueTask task = new AsyncAddToQueueTask(mContext,
                        mActivity,
                        AppConstants.KEY_SONG,
                        null,
                        null,
                        mSongTitle,
                        null,
                        null,
                        null,
                        null);
                task.execute();
            } else if (id == R.id.play_next) {
                AsyncAddToQueueTask playNextTask = new AsyncAddToQueueTask(mContext,
                        mActivity,
                        AppConstants.KEY_SONG,
                        null,
                        null,
                        mSongTitle,
                        null,
                        null,
                        null,
                        null);
                playNextTask.execute(new Boolean[]{true});
            } else if (id == R.id.set_as_ringtone) {
                checkSystemWritePermission();
            } else if (id == R.id.add_to_playlist) {
                //Add to Playlist
                AddToPlaylistDialog2 dialog = new AddToPlaylistDialog2().newInstance(AppConstants.KEY_SONG, mSongTitle);
                dialog.show(mActivity.getSupportFragmentManager(), "AddToPlaylistDialog");
            } else if (id == R.id.blacklist_artist) {
                //Blacklist Artist
                mApp.getDBAccessHelper().setBlacklistForArtist(mArtist, true);
                Toast.makeText(mContext, R.string.artist_blacklisted, Toast.LENGTH_SHORT).show();

                //Update the ListView.
                mActivity.startLoading();
            } else if (id == R.id.share) {
                Utils.shareSong(mActivity, mFilePath);
            } else if (id == R.id.delete) {
                showDeleteDialog();
            }
            return false;
        }

    };

    /**
     * Holder subclass for ListViewCardsAdapter.
     *
     * @author Saravan Pantham
     */
    static class ListViewHolder {
        public TextView trackNumber;
        public TextView titleText;
        public TextView subText;
        public TextView rightSubText;
        public ImageButton overflowIcon;

    }

    /**
     * Sets the {@link Uri} of the default sound for a given sound type.
     */
    public void setAsDefaultRingtone() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DATA, mFilePath);
            values.put(MediaStore.MediaColumns.TITLE, mSongTitle);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "audio*//*");
            values.put(MediaStore.Audio.Media.ARTIST, mArtist);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, true);
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);

            Uri uri1 = MediaStore.Audio.Media.getContentUriForPath(mFilePath);
            mContext.getContentResolver().delete(uri1,
                    MediaStore.MediaColumns.DATA + "=\""
                            + mFilePath + "\"", null);
            Uri uri2 = mContext.getContentResolver().insert(uri1, values);

            RingtoneManager.setActualDefaultRingtoneUri(mContext, RingtoneManager.TYPE_RINGTONE, uri2);
            Toast.makeText(mContext, mContext.getString(R.string.ringtone_set), Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(mContext, mContext.getString(R.string.some_error_occurred), Toast.LENGTH_SHORT).show();
        }
    }

    // To check write settings permission
    private boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(mContext);
            if (retVal) {
                setAsDefaultRingtone();
            } else {
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    // Request modify settings permission
    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    private void showDeleteDialog() {
        String title = "";
        String message = mContext.getString(R.string.delete_song_warning);
        Bundle labelBundle = new Bundle();
        labelBundle.putString(CustomDialogFragment.LABEL_POSITIVE, mContext.getString(R.string.yes));
        labelBundle.putString(CustomDialogFragment.LABEL_NEGATIVE, mContext.getString(R.string.no));
        CustomDialogFragment customDialogFragment = CustomDialogFragment.getInstance(mActivity, title, message, CustomDialogFragment.ACTION_TYPE.POSITIVE_NEGATIVE, mEventClickListener, labelBundle, false);
        customDialogFragment.show(mActivity.getSupportFragmentManager(), "DeleteSongDialog");
    }

    private CustomDialogFragment.ButtonClickListener mEventClickListener = new CustomDialogFragment.ButtonClickListener() {
        @Override
        public void positiveClicked(DialogInterface dialog) {
            boolean flag = Utils.deleteSong(mActivity, mFilePath);
            // if deleted successfully then delete from our database and refresh the list
            if (flag) {
                mApp.getDBAccessHelper().deleteSongByFilePath(mFilePath);

                //Update the ListView.
                mActivity.startLoading();
            }
        }

        @Override
        public void negativeClicked(DialogInterface dialog) {
            dialog.dismiss();
        }
    };
}
