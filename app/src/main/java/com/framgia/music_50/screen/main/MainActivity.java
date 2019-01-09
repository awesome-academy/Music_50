package com.framgia.music_50.screen.main;

import android.os.Bundle;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import com.framgia.music_50.screen.BaseActivity;
import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View, OnFetchDataListener<Track> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onSuccess(List<Track> data) {
    }

    @Override
    public void onError(Exception e) {
    }
}
