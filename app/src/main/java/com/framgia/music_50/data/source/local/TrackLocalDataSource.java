package com.framgia.music_50.data.source.local;

import com.framgia.music_50.data.source.TrackDataSource;

public class TrackLocalDataSource implements TrackDataSource.LocalDataSource {
    private static TrackLocalDataSource sInstance;

    public static TrackLocalDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new TrackLocalDataSource();
        }
        return sInstance;
    }
}
