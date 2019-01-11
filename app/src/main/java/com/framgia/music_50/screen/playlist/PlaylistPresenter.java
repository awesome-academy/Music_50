package com.framgia.music_50.screen.playlist;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import java.util.List;

public class PlaylistPresenter implements PlaylistContract.Presenter {
    private PlaylistContract.View mView;
    private TrackRepository mTrackRepository;

    PlaylistPresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(PlaylistContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void getTracksByGenre(String genreType) {
        mTrackRepository.getTracksByGenre(genreType, new OnFetchDataListener<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.onGetTracksByGenreSuccess(tracks);
            }

            @Override
            public void onError(Exception e) {
                mView.onGetTracksByGenreError(e);
            }
        });
    }
}
