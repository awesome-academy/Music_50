package com.framgia.music_50.data.source.remote;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.TrackDataSource;
import com.framgia.music_50.utils.Constant;
import com.framgia.music_50.utils.QueryType;

public class TrackRemoteDataSource implements TrackDataSource.RemoteDataSource {
    private static TrackRemoteDataSource sInstance;

    public static TrackRemoteDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new TrackRemoteDataSource();
        }
        return sInstance;
    }

    @Override
    public void getTrendingTracks(OnFetchDataListener<Track> listener) {
        new GetTracksAsyncTask(QueryType.GET_TRACKS, listener).execute(Constant.TRENDING_MUSIC_URL);
    }

    @Override
    public void getTracksByGenre(String genreType, OnFetchDataListener<Track> listener) {
        new GetTracksAsyncTask(QueryType.GET_TRACKS, listener).execute(
                Constant.TRENDING_MUSIC_URL + Constant.GENRE + genreType);
    }

    @Override
    public void querySearch(String keyword, OnFetchDataListener<Track> listener) {
        new GetTracksAsyncTask(QueryType.SEARCH_TRACK, listener).execute(
                Constant.SEARCH_TRACK_URL + Constant.QUERY + keyword + Constant.CLIENT_ID);
    }
}
