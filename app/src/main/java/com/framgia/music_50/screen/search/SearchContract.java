package com.framgia.music_50.screen.search;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BasePresenter;
import java.util.List;

public interface SearchContract {
    interface View {
        void onSearchTrackSuccess(List<Track> tracks);

        void onSearchTrackError(Exception e);
    }

    interface Presenter extends BasePresenter<View> {
        void querySearch(String keyword);
    }
}
