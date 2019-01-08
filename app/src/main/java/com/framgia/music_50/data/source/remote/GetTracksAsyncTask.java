package com.framgia.music_50.data.source.remote;

import android.os.AsyncTask;
import com.framgia.music_50.data.model.Track;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static com.framgia.music_50.utils.Constant.COLLECTION;
import static com.framgia.music_50.utils.Constant.GET_METHOD;
import static com.framgia.music_50.utils.Constant.REQUEST_TIMEOUT;
import static com.framgia.music_50.utils.Constant.USER;

public class GetTracksAsyncTask extends AsyncTask<String, Void, List<Track>> {
    private OnFetchDataListener<Track> mListener;

    public GetTracksAsyncTask(OnFetchDataListener<Track> listener) {
        mListener = listener;
    }

    @Override
    protected List<Track> doInBackground(String... strings) {
        List<Track> tracks = null;
        try {
            String json = getDataFromUrl(strings[0]);
            tracks = readDataFromJson(json);
        } catch (IOException | JSONException e) {
            mListener.onError(e);
        }
        return tracks;
    }

    @Override
    protected void onPostExecute(List<Track> data) {
        super.onPostExecute(data);
        mListener.onSuccess(data);
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
            Track track = new Track.TrackBuilder()
                    .setTitle(object.getString(Track.TrackEntry.TITLE))
                    .setArtworkUrl(object.getString(Track.TrackEntry.ARTWORK_URL))
                    .setArtistName(object.getJSONObject(USER)
                            .getString(Track.TrackEntry.ARTIST_NAME))
                    .setAvatarUrl(object.getJSONObject(USER)
                            .getString(Track.TrackEntry.ARTWORK_URL))
                    .setDownloadable(object.getBoolean(Track.TrackEntry.DOWNLOADABLE))
                    .setDownloadUrl(object.getString(Track.TrackEntry.DOWNLOAD_URL))
                    .setDuration(object.getInt(Track.TrackEntry.DURATION))
                    .build();
            tracks.add(track);
        }
        return tracks;
    }
}
