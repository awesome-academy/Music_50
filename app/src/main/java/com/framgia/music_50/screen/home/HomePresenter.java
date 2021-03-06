package com.framgia.music_50.screen.home;

import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import java.util.List;

public class HomePresenter implements HomeContract.Presenter {
    private HomeContract.View mView;
    private TrackRepository mTrackRepository;

    HomePresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(HomeContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void getTrendingTracks() {
        mTrackRepository.getTrendingTracks(new OnFetchDataListener<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.onGetTrendingTracksSuccess(tracks);
            }

            @Override
            public void onError(Exception e) {
                mView.onGetTrendingTrackError(e);
            }
        });
    }

    @Override
    public void getMusicGenres() {
        mTrackRepository.getMusicGenres(new OnFetchDataListener<Genre>() {
            @Override
            public void onSuccess(List<Genre> genres) {
                mView.onGetMusicGenreSuccess(genres);
            }

            @Override
            public void onError(Exception e) {
                mView.onGetMusicGenreError(e);
            }
        });
    }
}
