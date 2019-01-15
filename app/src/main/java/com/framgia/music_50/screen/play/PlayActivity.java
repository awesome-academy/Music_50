package com.framgia.music_50.screen.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BaseActivity;
import com.framgia.music_50.service.ServiceContract;
import com.framgia.music_50.service.TrackService;

public class PlayActivity extends BaseActivity implements ServiceContract.OnMediaPlayerChange {
    private final static String EXTRA_TRACK = "EXTRA_TRACK";
    private boolean mIsBound;
    private TrackService mTrackService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TrackService.TrackBinder trackBinder = (TrackService.TrackBinder) binder;
            mTrackService = trackBinder.getService();
            mTrackService.setOnMediaChangeListener(PlayActivity.this);
            mTrackService.play();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    public static Intent getIntent(Context context, Track track) {
        Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(EXTRA_TRACK, track);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(PlayActivity.this, TrackService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {
    }
}
