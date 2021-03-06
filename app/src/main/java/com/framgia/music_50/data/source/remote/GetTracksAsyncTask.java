package com.framgia.music_50.data.source.remote;

import android.os.AsyncTask;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.utils.Constant;
import com.framgia.music_50.utils.QueryType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.framgia.music_50.utils.Constant.COLLECTION;
import static com.framgia.music_50.utils.Constant.GET_METHOD;
import static com.framgia.music_50.utils.Constant.REQUEST_TIMEOUT;
import static com.framgia.music_50.utils.Constant.USER;

public class GetTracksAsyncTask extends AsyncTask<String, Void, List<Track>> {
    private OnFetchDataListener<Track> mListener;
    private Exception mException;
    private int mQueryType;

    GetTracksAsyncTask(int queryType, OnFetchDataListener<Track> listener) {
        mQueryType = queryType;
        mListener = listener;
    }

    @Override
    protected List<Track> doInBackground(String... strings) {
        List<Track> tracks = null;
        try {
            String json = getDataFromUrl(strings[0]);
            if (mQueryType == QueryType.GET_TRACKS) {
                tracks = readDataFromJson(json);
            } else if (mQueryType == QueryType.SEARCH_TRACK) {
                tracks = readDataFromSearchJson(json);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            mException = e;
        }
        return tracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);
        if (mException != null) {
            mListener.onError(mException);
        }
        mListener.onSuccess(tracks);
    }

    private String getDataFromUrl(String url) throws IOException {
        URL apiUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
        connection.setRequestMethod(GET_METHOD);
        connection.setReadTimeout(REQUEST_TIMEOUT);
        connection.setConnectTimeout(REQUEST_TIMEOUT);
        connection.connect();
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private List<Track> readDataFromJson(String json) throws JSONException {
        List<Track> tracks = new ArrayList<>();
        JSONObject rootObject = new JSONObject(json);
        JSONArray jsonArray = rootObject.getJSONArray(COLLECTION);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i).getJSONObject(Track.TrackEntry.TRACK);
            Track track = new Track.TrackBuilder().setId(object.getLong(Track.TrackEntry.ID))
                    .setTitle(object.getString(Track.TrackEntry.TITLE))
                    .setArtworkUrl(object.getString(Track.TrackEntry.ARTWORK_URL))
                    .setArtistName(
                            object.getJSONObject(USER).getString(Track.TrackEntry.ARTIST_NAME))
                    .setAvatarUrl(object.getJSONObject(USER).getString(Track.TrackEntry.AVATAR_URL))
                    .setDownloadable(object.getBoolean(Track.TrackEntry.DOWNLOADABLE))
                    .setDownloadUrl(object.getString(Track.TrackEntry.DOWNLOAD_URL))
                    .setDuration(object.getInt(Track.TrackEntry.DURATION))
                    .setStreamUrl(Constant.STREAM_BASE_URL
                            + object.getLong(Track.TrackEntry.ID)
                            + Constant.STREAM)
                    .build();
            tracks.add(track);
        }
        return tracks;
    }

    private List<Track> readDataFromSearchJson(String json) throws JSONException {
        List<Track> tracks = new ArrayList<>();
        JSONObject rootObject = new JSONObject(json);
        JSONArray jsonArray = rootObject.getJSONArray(COLLECTION);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Track track = new Track.TrackBuilder().setId(object.getLong(Track.TrackEntry.ID))
                    .setTitle(object.getString(Track.TrackEntry.TITLE))
                    .setArtworkUrl(object.getString(Track.TrackEntry.ARTWORK_URL))
                    .setArtistName(
                            object.getJSONObject(USER).getString(Track.TrackEntry.ARTIST_NAME))
                    .setAvatarUrl(object.getJSONObject(USER).getString(Track.TrackEntry.AVATAR_URL))
                    .setDownloadable(object.getBoolean(Track.TrackEntry.DOWNLOADABLE))
                    .setDownloadUrl(object.getString(Track.TrackEntry.DOWNLOAD_URL))
                    .setDuration(object.getInt(Track.TrackEntry.DURATION))
                    .setStreamUrl(Constant.STREAM_BASE_URL
                            + object.getLong(Track.TrackEntry.ID)
                            + Constant.STREAM)
                    .build();
            tracks.add(track);
        }
        return tracks;
    }
}
