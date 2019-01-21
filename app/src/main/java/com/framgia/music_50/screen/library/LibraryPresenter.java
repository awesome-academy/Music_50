package com.framgia.music_50.screen.library;

import android.content.ContentResolver;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import java.util.List;

public class LibraryPresenter implements LibraryContract.Presenter {
    private LibraryContract.View mView;
    private TrackRepository mTrackRepository;

    LibraryPresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(LibraryContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void getLocalTracks(ContentResolver contentResolver) {
        mTrackRepository.getLocalTracks(contentResolver, new OnFetchDataListener<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.onGetLocalTracksSuccess(tracks);
            }

            @Override
            public void onError(Exception e) {
                mView.onGetLocalTracksError(e);
            }
        });
    }
}
