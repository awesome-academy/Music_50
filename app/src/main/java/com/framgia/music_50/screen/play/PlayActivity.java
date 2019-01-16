package com.framgia.music_50.screen.play;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BaseActivity;
import com.framgia.music_50.service.ServiceContract;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Common;

public class PlayActivity extends BaseActivity
        implements ServiceContract.OnMediaPlayerChange, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private final static String EXTRA_TRACK = "EXTRA_TRACK";
    private final int TIME_UPDATE_SEEK_BAR = 100;
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
    private TextView mCurrentDurationTextView;
    private TextView mTotalDurationTextView;
    private SeekBar mSeekBar;
    private boolean mIsBound;
    private TrackService mTrackService;
    private Handler mHandler;
    private Runnable mRunnable;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TrackService.TrackBinder trackBinder = (TrackService.TrackBinder) binder;
            mTrackService = trackBinder.getService();
            mTrackService.setOnMediaChangeListener(PlayActivity.this);
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
        mCurrentDurationTextView = findViewById(R.id.textViewCurrentDuration);
        mTotalDurationTextView = findViewById(R.id.textViewTotalDuration);
        mSeekBar = findViewById(R.id.seekBarTrack);
    }

    @Override
    public void initData() {
        mHandler = new Handler();
        Track track = getIntent().getParcelableExtra(EXTRA_TRACK);
        if (track != null) {
            updateUI(track);
        }
    }

    @Override
    public void initListener() {
        mBackImageView.setOnClickListener(this);
        mPlayPauseImageView.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void updateUI(Track track) {
        mTrackTitleTextView.setText(track.getTitle());
        mArtistNameTextView.setText(track.getArtistName());
        Glide.with(this).load(Common.getBigImageUrl(track.getArtworkUrl())).into(mArtworkImageView);
        mTotalDurationTextView.setText(Common.convertTime(track.getDuration()));
        mSeekBar.setMax(track.getDuration());
    }

    @Override
    public void onTrackChange(Track track) {
        if (track != null) {
            updateUI(track);
        }
    }

    @Override
    public void onMediaPlayerStart() {
        updateSeekBar();
    }

    @Override
    public void onMediaPlayerStateChange(boolean isPlaying) {
        if (isPlaying) {
            mPlayPauseImageView.setImageDrawable(getDrawable(R.drawable.ic_rounded_pause));
        } else {
            mPlayPauseImageView.setImageDrawable(getDrawable(R.drawable.ic_rounded_play));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                finish();
                break;
            case R.id.imageViewPlayPause:
                mTrackService.playPauseTrack();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mTrackService.seekTo(seekBar.getProgress());
        mHandler.postDelayed(mRunnable, TIME_UPDATE_SEEK_BAR);
    }

    private void updateSeekBar() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mCurrentDurationTextView.setText(
                        Common.convertTime(mTrackService.getCurrentDuration()));
                mSeekBar.setProgress(mTrackService.getCurrentDuration());
                mHandler.postDelayed(mRunnable, TIME_UPDATE_SEEK_BAR);
            }
        };
        mRunnable.run();
    }

}
