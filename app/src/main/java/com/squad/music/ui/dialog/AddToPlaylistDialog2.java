package com.squad.music.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squad.music.R;
import com.squad.music.asynctask.AsyncAddSongsToPlaylistTask2;
import com.squad.music.asynctask.AsyncCreateNewPlaylistTask2;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.model.Playlist;
import com.squad.music.utility.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mdasifkhan on 21/3/17.
 */

public class AddToPlaylistDialog2 extends DialogFragment {

    private LinearLayout mLinearLayoutCreatePlaylist, mLinearLayoutPlaylist;
    private Context mContext;
    private String ADD_TYPE;
    private String ARTIST;
    private String ALBUM;
    private String ALBUM_ARTIST;
    private String SONG;
    private String GENRE;

    private TextView mTextViewAddToPlaylist, mTextViewCreatePlaylist, mTextViewPlaylistLabel;

    public AddToPlaylistDialog2() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddToPlaylistDialog2 newInstance(String addType, String songTitle) {
        AddToPlaylistDialog2 frag = new AddToPlaylistDialog2();
        Bundle args = new Bundle();
        args.putString(AppConstants.ADD_TYPE, addType);
        args.putString(AppConstants.KEY_SONG, songTitle);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.custom_playlist_dialog, container, false);
        linkViewId(view);

        return view;
    }

    private void linkViewId(View view) {
        mContext = getActivity().getApplicationContext();
        // Get field from view
        mLinearLayoutCreatePlaylist = (LinearLayout) view.findViewById(R.id.linear_layout_create_playlist);
        mLinearLayoutPlaylist = (LinearLayout) view.findViewById(R.id.linear_layout_playlist);
        mTextViewCreatePlaylist = (TextView) view.findViewById(R.id.text_view_create_playlist);
        mTextViewAddToPlaylist = (TextView) view.findViewById(R.id.text_view_add_to_playlist);
        mTextViewPlaylistLabel = (TextView) view.findViewById(R.id.text_view_playlist_label);

        // set text color
        mTextViewAddToPlaylist.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
        mTextViewCreatePlaylist.setTextColor(UIElementsHelper.getSmallTextColor(mContext));
        mTextViewPlaylistLabel.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));

        mTextViewAddToPlaylist.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        mTextViewCreatePlaylist.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        mTextViewPlaylistLabel.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        mLinearLayoutCreatePlaylist.setBackgroundResource(R.drawable.selectable_background_appthemelight);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Retrieve the arguments.
        ADD_TYPE = getArguments().getString(AppConstants.ADD_TYPE);

        setData();
        showPlaylistsInList(view);

        mLinearLayoutCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePlaylistDialog();
                getDialog().dismiss();
            }
        });
    }

    private void setData() {
        if (ADD_TYPE.equals("ARTIST")) {
            ARTIST = getArguments().getString("ARTIST");
        } else if (ADD_TYPE.equals("ALBUM_ARTIST")) {
            ALBUM_ARTIST = getArguments().getString("ALBUM_ARTIST");
        } else if (ADD_TYPE.equals("ALBUM")) {
            ARTIST = getArguments().getString("ARTIST");
            ALBUM = getArguments().getString("ALBUM");
        } else if (ADD_TYPE.equals(AppConstants.KEY_SONG)) {
            ARTIST = getArguments().getString("ARTIST");
            ALBUM = getArguments().getString("ALBUM");
            SONG = getArguments().getString(AppConstants.KEY_SONG);
        } else if (ADD_TYPE.equals("GENRE")) {
            GENRE = getArguments().getString("GENRE");
        } else if (ADD_TYPE.equals("ALBUM_BY_ALBUM_ARTIST")) {
            ALBUM = getArguments().getString("ALBUM");
            ALBUM_ARTIST = getArguments().getString("ALBUM_ARTIST");
        }
    }

    private void showPlaylistsInList(View view) {
        ArrayList<Playlist> items = DBAccessHelper.getAllPlaylists(mContext);
        if (items.size() > 0) {
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(new RecyclerAdapter_PlaylistDialog(items));
        } else {
            mLinearLayoutPlaylist.setVisibility(View.GONE);
        }
    }

    public class RecyclerAdapter_PlaylistDialog extends RecyclerView.Adapter<RecyclerAdapter_PlaylistDialog.ViewHolder> {
        private List<Playlist> mItems;

        public RecyclerAdapter_PlaylistDialog(List<Playlist> items) {
            mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_playlist_dialog, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            String item = mItems.get(i).getPlaylistName();
            viewHolder.mTextView.setText(item);

            viewHolder.mTextView.setTextColor(UIElementsHelper.getSmallTextColor(mContext));
            viewHolder.mTextView.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
            viewHolder.mTextView.setBackgroundResource(R.drawable.selectable_background_appthemelight);

            viewHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AsyncAddSongsToPlaylistTask2 task = new AsyncAddSongsToPlaylistTask2(mContext,
                            mItems.get(i).getPlaylistName(),
                            String.valueOf(mItems.get(i).getPlaylistId()),
                            ARTIST,
                            ALBUM,
                            SONG,
                            GENRE,
                            ALBUM_ARTIST,
                            ADD_TYPE);
                    task.execute();

                    getDialog().dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            private final LinearLayout mLinearLayout;
            private final TextView mTextView;

            ViewHolder(View v) {
                super(v);
                mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_layout_dialog);
                mTextView = (TextView) v.findViewById(R.id.item);
            }
        }
    }

    //Displays the "Add New Playlist" dialog.
    public void showCreatePlaylistDialog() {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.add_new_playlist_dialog_layout, null);
        final EditText newPlaylistEditText = (EditText) dialogView.findViewById(R.id.new_playlist_name_text_field);
        newPlaylistEditText.setTypeface(TypefaceHelper.getTypeface(getActivity(), "RobotoCondensed-Light"));
        newPlaylistEditText.setPaintFlags(newPlaylistEditText.getPaintFlags() | Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.new_playlist);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Retrieve the name of the new playlist.
                String playlistName = newPlaylistEditText.getText().toString();
                AsyncCreateNewPlaylistTask2 task = new AsyncCreateNewPlaylistTask2(mContext,
                        playlistName,
                        ARTIST,
                        ALBUM,
                        SONG,
                        GENRE,
                        ALBUM_ARTIST,
                        ADD_TYPE);
                task.execute();
                dialog.dismiss();

            }

        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }

        });

        builder.create().show();
    }
}