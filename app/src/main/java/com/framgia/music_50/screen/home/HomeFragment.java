package com.framgia.music_50.screen.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;
import com.framgia.music_50.screen.home.adapter.GenreAdapter;
import com.framgia.music_50.screen.home.adapter.TrendingTrackAdapter;
import com.framgia.music_50.screen.play.PlayActivity;
import com.framgia.music_50.screen.playlist.PlaylistFragment;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Navigator;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.List;

public class HomeFragment extends Fragment
        implements HomeContract.View, OnItemRecyclerViewClickListener<Object> {
    public static final String TAG = HomeFragment.class.getSimpleName();
    private TrendingTrackAdapter mTrendingTrackAdapter;
    private GenreAdapter mGenreAdapter;
    private List<Track> mTrendingTracks;
    private Navigator mNavigator;

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void onGetTrendingTracksSuccess(List<Track> tracks) {
        if (tracks != null) {
            mTrendingTracks = tracks;
            mTrendingTrackAdapter.updateData(tracks);
        }
    }

    @Override
    public void onGetTrendingTrackError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMusicGenreSuccess(List<Genre> genres) {
        if (genres != null) {
            mGenreAdapter.updateData(genres);
        }
    }

    @Override
    public void onGetMusicGenreError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClickListener(Object item) {
        if (item instanceof Track) {
            startActivity(PlayActivity.getIntent(getContext(), (Track) item));
            if (getActivity() != null) {
                Intent intent = TrackService.getServiceIntent(getContext(), mTrendingTracks,
                        mTrendingTracks.indexOf(item));
                getActivity().startService(intent);
            }
        } else if (item instanceof Genre) {
            mNavigator.goNextChildFragment(getFragmentManager(), R.id.layoutContainer,
                    PlaylistFragment.newInstance((Genre) item), true, HomeFragment.TAG);
        }
    }

    private void initView(View view) {
        RecyclerView trendingMusicRecyclerView = view.findViewById(R.id.recyclerViewTrendingMusic);
        trendingMusicRecyclerView.setHasFixedSize(true);
        trendingMusicRecyclerView.setNestedScrollingEnabled(false);
        mTrendingTrackAdapter = new TrendingTrackAdapter(getContext());
        trendingMusicRecyclerView.setAdapter(mTrendingTrackAdapter);
        mTrendingTrackAdapter.setOnItemRecyclerViewClickListener(this);
        RecyclerView genresRecyclerView = view.findViewById(R.id.recyclerViewMusicGenres);
        genresRecyclerView.setHasFixedSize(true);
        genresRecyclerView.setNestedScrollingEnabled(false);
        mGenreAdapter = new GenreAdapter();
        genresRecyclerView.setAdapter(mGenreAdapter);
        mGenreAdapter.setOnItemRecyclerViewClickListener(this);
    }

    private void initData() {
        mNavigator = new Navigator(this);
        TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
        TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
        TrackRepository repository =
                TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
        HomeContract.Presenter presenter = new HomePresenter(repository);
        presenter.setView(this);
        presenter.getTrendingTracks();
        presenter.getMusicGenres();
    }

    private void initListener() {
    }
}
