package com.squad.music.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squad.music.R;
import com.squad.music.helper.TypefaceHelper;
import com.squad.music.helper.UIElementsHelper;
import com.squad.music.model.Playlist;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

    private List<Playlist> mItems;
    private Context mContext;

    public PlaylistAdapter(Context context, List<Playlist> items) {
        mItems = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_playlist, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.mTextViewText.setText(mItems.get(i).getPlaylistName());
        viewHolder.mTextViewSubText.setText(mItems.get(i).getNoOfSongs());

        viewHolder.mTextViewText.setTextColor(UIElementsHelper.getThemeBasedTextColor(mContext));
        viewHolder.mTextViewSubText.setTextColor(UIElementsHelper.getSmallTextColor(mContext));

        viewHolder.mTextViewText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));
        viewHolder.mTextViewSubText.setTypeface(TypefaceHelper.getTypeface(mContext, "Roboto-Regular"));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound("A", Color.RED);
        viewHolder.mImageView.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout mLinearLayout;
        private final ImageView mImageView;
        private final TextView mTextViewText, mTextViewSubText;

        ViewHolder(View v) {
            super(v);
            mLinearLayout = (LinearLayout) v.findViewById(R.id.linear_layout_dialog);
            mTextViewText = (TextView) v.findViewById(R.id.listViewTitleText);
            mTextViewSubText = (TextView) v.findViewById(R.id.listViewSubText);
            mImageView = (ImageView) v.findViewById(R.id.listViewLeftIcon);
        }
    }

}
