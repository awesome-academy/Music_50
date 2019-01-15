package com.framgia.music_50.screen.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BaseActivity;
import com.framgia.music_50.service.ServiceContract;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Common;

public class PlayActivity extends BaseActivity implements ServiceContract.OnMediaPlayerChange {
    private final static String EXTRA_TRACK = "EXTRA_TRACK";
    private ImageView mBackImageView;
    private ImageView mDownloadImageView;
    private ImageView mArtworkImageView;
    private ImageView mShuffleImageView;
    private ImageView mSkipPreviousImageView;
    private ImageView mSkipNextImageView;
    private ImageView mPlayPauseImageView;
    private ImageView mLoopImageView;
    private TextView mTrackTitleTextView;
    private TextView mArtistNameTextView;
    private TextView mCurrentDurationText;
    private TextView mTotalDurationText;
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
        mBackImageView = findViewById(R.id.imageViewBack);
        mDownloadImageView = findViewById(R.id.imageViewDownloadTrack);
        mArtworkImageView = findViewById(R.id.imageViewArtwork);
        mShuffleImageView = findViewById(R.id.imageViewShuffle);
        mSkipPreviousImageView = findViewById(R.id.imageViewSkipPrevious);
        mSkipNextImageView = findViewById(R.id.imageViewSkipNext);
        mPlayPauseImageView = findViewById(R.id.imageViewPlayPause);
        mLoopImageView = findViewById(R.id.imageViewLoop);
        mTrackTitleTextView = findViewById(R.id.textViewTrackTitle);
        mArtistNameTextView = findViewById(R.id.textViewArtistName);
        mCurrentDurationText = findViewById(R.id.textViewCurrentDuration);
        mTotalDurationText = findViewById(R.id.textViewTotalDuration);
    }

    @Override
    public void initData() {
        Track track = getIntent().getParcelableExtra(EXTRA_TRACK);
        if (track != null) {
            updateUI(track);
        }
    }

    @Override
    public void initListener() {
    }

    private void updateUI(Track track) {
        mTrackTitleTextView.setText(track.getTitle());
        mArtistNameTextView.setText(track.getArtistName());
        Glide.with(this).load(Common.getBigImageUrl(track.getArtworkUrl())).into(mArtworkImageView);
        mTotalDurationText.setText(Common.convertTime(track.getDuration()));
    }
}
