package com.framgia.music_50.data.source.local;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.TrackDataSource;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import com.framgia.music_50.utils.Genres;
import java.util.ArrayList;
import java.util.List;

public class TrackLocalDataSource implements TrackDataSource.LocalDataSource {
    private static TrackLocalDataSource sInstance;

    public static TrackLocalDataSource getInstance() {
        if (sInstance == null) {
            sInstance = new TrackLocalDataSource();
        }
        return sInstance;
    }

    @Override
    public void getMusicGenres(OnFetchDataListener<Genre> listener) {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(Genres.ALTERNATIVE_ROCK_TITLE, R.drawable.alternative_rock_genre,
                Genres.ALTERNATIVE_ROCK));
        genres.add(new Genre(Genres.AMBIENT_TITLE, R.drawable.ambient_genre, Genres.AMBIENT));
        genres.add(new Genre(Genres.CLASSICAL_TITLE, R.drawable.classical_genre, Genres.CLASSICAL));
        genres.add(new Genre(Genres.COUNTRY_TITLE, R.drawable.country_genre, Genres.COUNTRY));
        listener.onSuccess(genres);
    }

    @Override
    public void getLocalTracks(ContentResolver contentResolver,
            OnFetchDataListener<Track> listener) {
        List<Track> tracks = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Track track = new Track.TrackBuilder().setTitle(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)))
                        .setArtistName(cursor.getString(
                                cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
                        .setDuration(cursor.getInt(
                                cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)))
                        .setStreamUrl(cursor.getString(
                                cursor.getColumnIndex(MediaStore.Audio.Media.DATA)))
                        .build();
                tracks.add(track);
            }
            if (listener != null) {
                listener.onSuccess(tracks);
            }
            cursor.close();
        }
    }
}
