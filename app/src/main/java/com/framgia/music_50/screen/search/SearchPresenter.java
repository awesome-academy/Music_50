package com.framgia.music_50.screen.search;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import java.util.List;

public class SearchPresenter implements SearchContract.Presenter {
    private SearchContract.View mView;
    private TrackRepository mTrackRepository;

    SearchPresenter(TrackRepository trackRepository) {
        mTrackRepository = trackRepository;
    }

    @Override
    public void setView(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void querySearch(String keyword) {
        mTrackRepository.querySearch(keyword, new OnFetchDataListener<Track>() {
            @Override
            public void onSuccess(List<Track> tracks) {
                mView.onSearchTrackSuccess(tracks);
            }

            @Override
            public void onError(Exception e) {
                mView.onSearchTrackError(e);
            }
        });
    }
}
