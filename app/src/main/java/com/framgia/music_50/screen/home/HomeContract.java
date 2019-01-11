package com.framgia.music_50.screen.home;

import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BasePresenter;
import java.util.List;

public interface HomeContract {
    interface View {
        void onGetTrendingTracksSuccess(List<Track> tracks);

        void onGetTrendingTrackError(Exception e);

        void onGetMusicGenreSuccess(List<Genre> genres);

        void onGetMusicGenreError(Exception e);
    }

    interface Presenter extends BasePresenter<View> {
        void getTrendingTracks();

        void getMusicGenres();
    }
}
