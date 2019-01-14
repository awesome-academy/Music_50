package com.framgia.music_50.screen.playlist.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.utils.Common;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemTrackViewHolder> {
    private List<Track> mTracks;
    private OnItemRecyclerViewClickListener<Track> mOnItemClickListener;

    public PlaylistAdapter() {
        mTracks = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemTrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_playlist, viewGroup, false);
        return new ItemTrackViewHolder(view, mTracks, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemTrackViewHolder itemTrackViewHolder, int i) {
        itemTrackViewHolder.bindViewData(mTracks.get(i));
    }

    @Override
    public int getItemCount() {
        return mTracks != null ? mTracks.size() : 0;
    }

    public void setOnItemClickListener(OnItemRecyclerViewClickListener<Track> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void updateData(List<Track> tracks) {
        if (mTracks != null) {
            mTracks.clear();
            mTracks.addAll(tracks);
            notifyDataSetChanged();
        }
    }

    static class ItemTrackViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView mArtworkImageView;
        private TextView mTrackTitleTextView;
        private TextView mArtistNameTextView;
        private TextView mDurationTextView;
        private List<Track> mTracks;
        private OnItemRecyclerViewClickListener<Track> mOnItemClickListener;

        ItemTrackViewHolder(@NonNull View itemView, List<Track> tracks,
                OnItemRecyclerViewClickListener<Track> onItemClickListener) {
            super(itemView);
            mTracks = tracks;
            mOnItemClickListener = onItemClickListener;
            mArtistNameTextView = itemView.findViewById(R.id.textViewArtistNameItem);
            mArtworkImageView = itemView.findViewById(R.id.imageViewArtworkItem);
            mTrackTitleTextView = itemView.findViewById(R.id.textViewTrackTitleItem);
            mDurationTextView = itemView.findViewById(R.id.textViewTrackDurationItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClickListener(mTracks.get(getAdapterPosition()));
            }
        }

        void bindViewData(Track track) {
            Glide.with(itemView.getContext()).load(track.getArtworkUrl()).into(mArtworkImageView);
            mTrackTitleTextView.setText(track.getTitle());
            mArtistNameTextView.setText(track.getArtistName());
            mDurationTextView.setText(Common.convertTime(track.getDuration()));
        }
    }
}
