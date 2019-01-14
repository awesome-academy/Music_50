package com.framgia.music_50.data.source;

import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;

public interface TrackDataSource {
    interface LocalDataSource {
        void getMusicGenres(OnFetchDataListener<Genre> listener);
    }

    interface RemoteDataSource {
        void getTrendingTracks(OnFetchDataListener<Track> listener);

        void getTracksByGenre(String genreType, OnFetchDataListener<Track> listener);
    }
}
