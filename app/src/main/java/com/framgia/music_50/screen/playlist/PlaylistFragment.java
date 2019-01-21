package com.framgia.music_50.screen.playlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;
import com.framgia.music_50.screen.BaseFragment;
import com.framgia.music_50.screen.home.HomeFragment;
import com.framgia.music_50.screen.home.adapter.OnAttachedPlaylistFragment;
import com.framgia.music_50.screen.play.PlayActivity;
import com.framgia.music_50.screen.playlist.adapter.PlaylistAdapter;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Navigator;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.List;
import java.util.Objects;

public class PlaylistFragment extends BaseFragment
        implements PlaylistContract.View, OnItemRecyclerViewClickListener<Track>,
        AppBarLayout.OnOffsetChangedListener {
    public static final String TAG = PlaylistFragment.class.getSimpleName();
    private static final String EXTRA_GENRE_KEY = "EXTRA_GENRE_KEY";
    private static final int DEFAULT_ALPHA = 1;
    private TextView mGenreTitleTextView;
    private ImageView mGenreArtworkImageView;
    private AppBarLayout mPlaylistAppBar;
    private PlaylistAdapter mPlaylistAdapter;
    private Navigator mNavigator;
    private List<Track> mTracks;
    private OnAttachedPlaylistFragment mOnAttachedPlaylistFragment;

    public static PlaylistFragment newInstance(Genre genre) {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_GENRE_KEY, genre);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOnAttachedPlaylistFragment != null) {
            mOnAttachedPlaylistFragment.setBottomNavigationVisibility(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mOnAttachedPlaylistFragment != null) {
            mOnAttachedPlaylistFragment.setBottomNavigationVisibility(true);
        }
    }

    @Override
    public void initView(View view) {
        mPlaylistAppBar = view.findViewById(R.id.appBarLayoutPlaylist);
        mGenreTitleTextView = view.findViewById(R.id.textViewGenreTitle);
        mGenreArtworkImageView = view.findViewById(R.id.imageViewGenreArtwork);
        RecyclerView playlistRecyclerView = view.findViewById(R.id.recyclerViewPlaylist);
        playlistRecyclerView.setHasFixedSize(true);
        playlistRecyclerView.setNestedScrollingEnabled(false);
        mPlaylistAdapter = new PlaylistAdapter();
        mPlaylistAdapter.setOnItemClickListener(this);
        playlistRecyclerView.setAdapter(mPlaylistAdapter);
        Toolbar playlistToolbar = view.findViewById(R.id.toolbarPlaylist);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).setSupportActionBar(
                playlistToolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar())
                .setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar())
                .setDisplayShowTitleEnabled(false);
    }

    @Override
    public void initData() {
        mNavigator = new Navigator(this);
        if (getArguments() != null) {
            Genre genre = getArguments().getParcelable(EXTRA_GENRE_KEY);
            TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
            TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
            TrackRepository repository =
                    TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
            PlaylistPresenter playlistPresenter = new PlaylistPresenter(repository);
            playlistPresenter.setView(this);
            playlistPresenter.getTracksByGenre(genre != null ? genre.getGenreType() : null);
            if (genre != null) {
                mGenreTitleTextView.setText(genre.getTitle());
                Glide.with(this).load(genre.getImage()).into(mGenreArtworkImageView);
            }
        }
    }

    @Override
    public void initListener() {
        mPlaylistAppBar.addOnOffsetChangedListener(this);
    }

    @Override
    public void onGetTracksByGenreSuccess(List<Track> tracks) {
        if (tracks != null) {
            mTracks = tracks;
            mPlaylistAdapter.updateData(tracks);
        }
    }

    @Override
    public void onGetTracksByGenreError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickListener(Track track) {
        startActivity(PlayActivity.getIntent(getContext(), track));
        if (getActivity() != null) {
            Intent intent =
                    TrackService.getServiceIntent(getContext(), mTracks, mTracks.indexOf(track));
            getActivity().startService(intent);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        float offsetAlpha = (appBarLayout.getY() / appBarLayout.getTotalScrollRange());
        mGenreArtworkImageView.setAlpha(DEFAULT_ALPHA - (offsetAlpha * -DEFAULT_ALPHA));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mNavigator.removeFragment(getFragmentManager(), HomeFragment.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setOnAttachedPlaylistFragment(OnAttachedPlaylistFragment onAttach) {
        mOnAttachedPlaylistFragment = onAttach;
    }
}
