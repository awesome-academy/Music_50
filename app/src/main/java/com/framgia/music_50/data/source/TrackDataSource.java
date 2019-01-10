package com.framgia.music_50.data.source;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;

public interface TrackDataSource {
    interface LocalDataSource {
    }

    interface RemoteDataSource {
        void getTrendingTracks(OnFetchDataListener<Track> listener);
    }
}
