package com.framgia.music_50.screen.home;

import com.framgia.music_50.screen.BasePresenter;

public interface HomeContract {
    interface View {
    }

    interface Presenter extends BasePresenter<View> {
        void getTrendingTracks();
    }
}
