package com.framgia.music_50.data.repository;

import android.content.ContentResolver;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.TrackDataSource;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;

public class TrackRepository {
    private static TrackRepository sInstance;
    private TrackDataSource.RemoteDataSource mRemoteDataSource;
    private TrackDataSource.LocalDataSource mLocalDataSource;

    private TrackRepository(TrackDataSource.RemoteDataSource remoteDataSource,
            TrackDataSource.LocalDataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    public static TrackRepository getInstance(TrackDataSource.RemoteDataSource remoteDataSource,
            TrackDataSource.LocalDataSource localDataSource) {
        if (sInstance == null) {
            sInstance = new TrackRepository(remoteDataSource, localDataSource);
        }
        return sInstance;
    }

    public void getTrendingTracks(OnFetchDataListener<Track> listener) {
        mRemoteDataSource.getTrendingTracks(listener);
    }

    public void getMusicGenres(OnFetchDataListener<Genre> listener) {
        mLocalDataSource.getMusicGenres(listener);
    }

    public void getTracksByGenre(String genreType, OnFetchDataListener<Track> listener) {
        mRemoteDataSource.getTracksByGenre(genreType, listener);
    }

    public void getLocalTracks(ContentResolver contentResolver,
            OnFetchDataListener<Track> listener) {
        mLocalDataSource.getLocalTracks(contentResolver, listener);
    }

    public void querySearch(String keyword, OnFetchDataListener<Track> listener) {
        mRemoteDataSource.querySearch(keyword, listener);
    }
}
