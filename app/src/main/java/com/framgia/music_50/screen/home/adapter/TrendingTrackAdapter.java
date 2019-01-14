package com.framgia.music_50.screen.home.adapter;

import android.content.Context;
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

public class TrendingTrackAdapter extends RecyclerView.Adapter<TrendingTrackAdapter.ViewHolder> {
    private Context mContext;
    private List<Track> mTrackList;
    private OnItemRecyclerViewClickListener<Object> mOnItemRecyclerViewClickListener;

    public TrendingTrackAdapter(Context context) {
        mContext = context;
        mTrackList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_trending, viewGroup, false);
        return new ViewHolder(mContext, view, mTrackList, mOnItemRecyclerViewClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bindViewData(mTrackList.get(i));
    }

    @Override
    public int getItemCount() {
        return mTrackList != null ? mTrackList.size() : 0;
    }

    public void setOnItemRecyclerViewClickListener(
            OnItemRecyclerViewClickListener<Object> onItemRecyclerViewClickListener) {
        mOnItemRecyclerViewClickListener = onItemRecyclerViewClickListener;
    }

    public void updateData(List<Track> tracks) {
        mTrackList.clear();
        mTrackList.addAll(tracks);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context mContext;
        private ImageView mArtworkImageView;
        private TextView mTrackTitleTextView;
        private TextView mArtistNameTextView;
        private List<Track> mTrackList;
        private OnItemRecyclerViewClickListener<Object> mListener;

        ViewHolder(Context context, View itemView, List<Track> trackList,
                OnItemRecyclerViewClickListener<Object> listener) {
            super(itemView);
            mContext = context;
            mTrackList = trackList;
            mListener = listener;
            mArtistNameTextView = itemView.findViewById(R.id.textViewArtistName);
            mArtworkImageView = itemView.findViewById(R.id.imageViewArtwork);
            mTrackTitleTextView = itemView.findViewById(R.id.textViewTrackTitle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClickListener(mTrackList.get(getAdapterPosition()));
            }
        }

        void bindViewData(Track track) {
            Glide.with(mContext)
                    .load(Common.getBigImageUrl(track.getArtworkUrl()))
                    .into(mArtworkImageView);
            mArtistNameTextView.setText(track.getArtistName());
            mTrackTitleTextView.setText(track.getTitle());
        }
    }
}
