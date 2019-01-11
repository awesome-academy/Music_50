package com.framgia.music_50.screen.home.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ItemMusicGenreViewHolder> {
    private List<Genre> mGenres;
    private OnItemRecyclerViewClickListener<Object> mOnItemClickListener;

    public GenreAdapter() {
        mGenres = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemMusicGenreViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_layout_genre, viewGroup, false);
        return new ItemMusicGenreViewHolder(view, mGenres, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemMusicGenreViewHolder viewHolder, int i) {
        viewHolder.bindViewData(mGenres.get(i));
    }

    @Override
    public int getItemCount() {
        return mGenres != null ? mGenres.size() : 0;
    }

    public void setOnItemRecyclerViewClickListener(
            OnItemRecyclerViewClickListener<Object> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void updateData(List<Genre> genres) {
        mGenres.clear();
        mGenres.addAll(genres);
        notifyDataSetChanged();
    }

    static class ItemMusicGenreViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ImageView mGenreImageView;
        private TextView mGenreTitleTextView;
        private List<Genre> mGenres;
        private OnItemRecyclerViewClickListener<Object> mListener;

        ItemMusicGenreViewHolder(@NonNull View itemView, List<Genre> genreList,
                OnItemRecyclerViewClickListener<Object> listener) {
            super(itemView);
            mGenres = genreList;
            mListener = listener;
            mGenreImageView = itemView.findViewById(R.id.imageViewMusicGenreItem);
            mGenreTitleTextView = itemView.findViewById(R.id.textViewMusicGenreItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClickListener(mGenres.get(getAdapterPosition()));
            }
        }

        void bindViewData(Genre genre) {
            mGenreTitleTextView.setText(genre.getTitle());
            Glide.with(itemView.getContext()).load(genre.getImage()).into(mGenreImageView);
        }
    }
}
