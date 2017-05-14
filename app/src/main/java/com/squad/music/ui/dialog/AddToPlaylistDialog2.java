package com.squad.music.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squad.music.R;
import com.squad.music.database.DBAccessHelper;
import com.squad.music.model.Playlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mdasifkhan on 21/3/17.
 */

public class AddToPlaylistDialog2 extends DialogFragment {

    private LinearLayout mLinearLayoutCreatePlaylist;
    private Context mContext;
    private String ADD_TYPE;
    private String ARTIST;
    private String ALBUM;
    private String ALBUM_ARTIST;
    private String SONG;
    private String GENRE;

    public AddToPlaylistDialog2() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddToPlaylistDialog2 newInstance(String addType, String artist) {
        AddToPlaylistDialog2 frag = new AddToPlaylistDialog2();
        Bundle args = new Bundle();
        args.putString("ADD_TYPE", addType);
        args.putString("ARTIST", artist);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.custom_playlist_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContext = getActivity().getApplicationContext();
        //Retrieve the arguments.
        ADD_TYPE = getArguments().getString("ADD_TYPE");
        setData();

        // Get field from view
        mLinearLayoutCreatePlaylist = (LinearLayout) view.findViewById(R.id.linear_layout_create_playlist);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        showPlaylistsInList(view);

        mLinearLayoutCreatePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FragmentManager fm = getActivity().getFragmentManager();
                /*CreatePlaylistDialogFragment fragment = CreatePlaylistDialogFragment.newInstance("dialog");
                fragment.show(fm, "create_playlist");*/

                // dismiss current dialog
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
        } else if (ADD_TYPE.equals("SONG")) {
            ARTIST = getArguments().getString("ARTIST");
            ALBUM = getArguments().getString("ALBUM");
            SONG = getArguments().getString("SONG");
        } else if (ADD_TYPE.equals("GENRE")) {
            GENRE = getArguments().getString("GENRE");
        } else if (ADD_TYPE.equals("ALBUM_BY_ALBUM_ARTIST")) {
            ALBUM = getArguments().getString("ALBUM");
            ALBUM_ARTIST = getArguments().getString("ALBUM_ARTIST");
        }
    }

    private void showPlaylistsInList(View view) {
        ArrayList<Playlist> items = DBAccessHelper.getAllPlaylists(mContext);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new RecyclerAdapter_PlaylistDialog(items));
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
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            String item = mItems.get(i).getPlaylistName();
            viewHolder.mTextView.setText(item);
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
}