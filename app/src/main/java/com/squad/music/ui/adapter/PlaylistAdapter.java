package com.squad.music.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squad.music.R;
import com.squad.music.asynctask.AsyncAddToQueueTask;
import com.squad.music.helper.PlaylistHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.model.Playlist;
import com.squad.music.model.Song;
import com.squad.music.ui.dialog.CustomDialogFragment;
import com.squad.music.ui.dialog.RenamePlaylistDialog;
import com.squad.music.ui.fragment.PlaylistFragment;
import com.squad.music.utility.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> implements RenamePlaylistDialog.OnRenamePlaylist {

    private List<Playlist> mItems;
    private Context mContext;
    private FragmentActivity mActivity;
    private String mPlaylistId, mPlaylistName;
    private PlaylistFragment mPlaylistFragment;

    public PlaylistAdapter(Context context, FragmentActivity activity, List<Playlist> items, PlaylistFragment fragment) {
        mItems = items;
        mContext = context;
        mActivity = activity;
        mPlaylistFragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_playlist, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextViewText.setText(mItems.get(i).getPlaylistName());

        ArrayList<Song> songs = PlaylistHelper.getSongsFromPlaylist(mContext, mItems.get(i).getPlaylistId());
        if (songs.size() == 1) {
            viewHolder.mTextViewSubText.setText(songs.size() + " " + mContext.getString(R.string.song));
        } else {
            viewHolder.mTextViewSubText.setText(songs.size() + " " + mContext.getString(R.string.songss));
        }

        viewHolder.mTextViewText.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
        viewHolder.mTextViewSubText.setTextColor(UIElementsHelper.getSmallTextColor(mContext));

        viewHolder.mTextViewText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        viewHolder.mTextViewSubText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int randomColor = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(mItems.get(i).getPlaylistName().charAt(0)), randomColor);
        viewHolder.mImageView.setImageDrawable(drawable);

        viewHolder.mOverflowIcon.setOnClickListener(overflowClickListener);
        viewHolder.mOverflowIcon.setFocusable(false);
        viewHolder.mOverflowIcon.setFocusableInTouchMode(false);

        //Set the tags for this list item's overflow button.
        viewHolder.mOverflowIcon.setTag(R.string.title_text, String.valueOf(mItems.get(i).getPlaylistId()));
        viewHolder.mOverflowIcon.setTag(R.string.field_1, String.valueOf(mItems.get(i).getPlaylistName()));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mLinearLayout;
        private final ImageView mImageView;
        private final TextView mTextViewText, mTextViewSubText;
        public ImageButton mOverflowIcon;

        ViewHolder(View v) {
            super(v);
            mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_layout_dialog);
            mTextViewText = (TextView) v.findViewById(R.id.listViewTitleText);
            mTextViewSubText = (TextView) v.findViewById(R.id.listViewSubText);
            mImageView = (ImageView) v.findViewById(R.id.listViewLeftIcon);
            mOverflowIcon = (ImageButton) v.findViewById(R.id.listViewOverflow);
        }
    }

    /**
     * Click listener for overflow button.
     */
    private View.OnClickListener overflowClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(mContext, v);
            menu.inflate(R.menu.playlist_overflow_menu);
            menu.setOnMenuItemClickListener(popupMenuItemClickListener);
            mPlaylistId = (String) v.getTag(R.string.title_text);
            mPlaylistName = (String) v.getTag(R.string.field_1);
            menu.show();

        }
    };

    /**
     * Menu item click listener for the pop up menu.
     */
    private PopupMenu.OnMenuItemClickListener popupMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.add_to_queue:
                    //Add to Queue.
                    AsyncAddToQueueTask task = new AsyncAddToQueueTask(mContext,
                            mPlaylistFragment,
                            AppConstants.KEY_PLAYLIST,
                            null,
                            null,
                            null,
                            null,
                            mPlaylistId,
                            null,
                            null);
                    task.execute();
                    break;
                case R.id.play_next:
                    AsyncAddToQueueTask playNextTask = new AsyncAddToQueueTask(mContext,
                            mPlaylistFragment,
                            AppConstants.KEY_PLAYLIST,
                            null,
                            null,
                            null,
                            null,
                            mPlaylistId,
                            null,
                            null);
                    playNextTask.execute(new Boolean[]{true});
                    break;
                case R.id.rename:
                    showRenameDialog();

                    break;
                case R.id.delete:
                    showDeleteDialog();

                    break;
            }
            return false;
        }

    };

    private void showDeleteDialog() {
        String title = mContext.getString(R.string.alert);
        String message = mContext.getString(R.string.delete_playlist_warning);
        Bundle labelBundle = new Bundle();
        labelBundle.putString(CustomDialogFragment.LABEL_POSITIVE, mContext.getString(R.string.yes));
        labelBundle.putString(CustomDialogFragment.LABEL_NEGATIVE, mContext.getString(R.string.no));
        CustomDialogFragment customDialogFragment = CustomDialogFragment.getInstance(mActivity, title, message, CustomDialogFragment.ACTION_TYPE.POSITIVE_NEGATIVE, mEventClickListener, labelBundle, false);
        customDialogFragment.show(mActivity.getSupportFragmentManager(), "DeletePlaylistDialog");
    }

    private CustomDialogFragment.ButtonClickListener mEventClickListener = new CustomDialogFragment.ButtonClickListener() {
        @Override
        public void positiveClicked(DialogInterface dialog) {
            PlaylistHelper.deletePlaylist(mContext, mPlaylistId);
            Toast.makeText(mContext, mContext.getString(R.string.playlist_deleted), Toast.LENGTH_SHORT).show();
            // remove playlist from list
            for (int i = 0; i < mItems.size(); i++) {
                if (mItems.get(i).getPlaylistId() == Long.parseLong(mPlaylistId))
                    mItems.remove(i);
            }
            notifyDataSetChanged();
        }

        @Override
        public void negativeClicked(DialogInterface dialog) {
            dialog.dismiss();
        }
    };

    private void showRenameDialog() {
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.PLAYLIST_ID, mPlaylistId);
        bundle.putString(AppConstants.PLAYLIST_NAME, mPlaylistName);
        RenamePlaylistDialog renamePlaylistDialog = new RenamePlaylistDialog();
        renamePlaylistDialog.setOnRenamePlaylist(this);
        renamePlaylistDialog.setArguments(bundle);
        renamePlaylistDialog.show(ft, "renamePlaylistDialog");
    }

    @Override
    public void stateChanged() {
        mItems = PlaylistHelper.getAllPlaylists(mContext);
        notifyDataSetChanged();
    }
}
