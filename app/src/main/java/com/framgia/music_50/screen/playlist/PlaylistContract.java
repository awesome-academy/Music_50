package com.framgia.music_50.screen.playlist;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BasePresenter;
import java.util.List;

public interface PlaylistContract {
    interface View {
        void onGetTracksByGenreSuccess(List<Track> tracks);

        void onGetTracksByGenreError(Exception e);
    }

    interface Presenter extends BasePresenter<View> {
        void getTracksByGenre(String genreType);
    }
}
