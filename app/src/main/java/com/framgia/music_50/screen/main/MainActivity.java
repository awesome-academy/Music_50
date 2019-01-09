package com.framgia.music_50.screen.main;

import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.source.remote.OnFetchDataListener;
import com.framgia.music_50.screen.BaseActivity;
import java.util.List;

public class MainActivity extends BaseActivity implements MainContract.View,
        OnFetchDataListener<Track> {
    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }

    @Override
    public void onSuccess(List<Track> data) {
    }

    @Override
    public void onError(Exception e) {
    }
}
