package com.framgia.music_50.screen.library;

import android.content.ContentResolver;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BasePresenter;
import java.util.List;

public interface LibraryContract {
    interface View {
        void onGetLocalTracksSuccess(List<Track> tracks);

        void onGetLocalTracksError(Exception e);
    }

    interface Presenter extends BasePresenter<View> {
        void getLocalTracks(ContentResolver contentResolver);
    }
}
