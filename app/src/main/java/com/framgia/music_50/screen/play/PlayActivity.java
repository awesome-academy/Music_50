package com.framgia.music_50.screen.play;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BaseActivity;
import com.framgia.music_50.service.ServiceContract;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Common;
import com.framgia.music_50.utils.LoopType;

public class PlayActivity extends BaseActivity
        implements ServiceContract.OnMediaPlayerChange, View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private final static String EXTRA_TRACK = "EXTRA_TRACK";
    private final static String MP3_EXTENSION = ".mp3";
    private final int TIME_UPDATE_SEEK_BAR = 100;
    private final int DOWNLOAD_REQUEST_CODE = 2019;
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
    private TextView mLoopOneTextView;
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
            updateSeekBar();
            onMediaPlayerStateChange(mTrackService.isPlaying());
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
        if (!mIsBound) {
            Intent intent = new Intent(PlayActivity.this, TrackService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        if (mIsBound) {
            unbindService(mServiceConnection);
            mIsBound = false;
        }
        mTrackService.setOnMediaChangeListener(null);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DOWNLOAD_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadTrack();
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
        mLoopOneTextView = findViewById(R.id.textViewLoopOne);
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
        mSkipPreviousImageView.setOnClickListener(this);
        mSkipNextImageView.setOnClickListener(this);
        mLoopImageView.setOnClickListener(this);
        mSkipPreviousImageView.setOnClickListener(this);
        mSkipNextImageView.setOnClickListener(this);
        mShuffleImageView.setOnClickListener(this);
        mDownloadImageView.setOnClickListener(this);
    }

    private void updateUI(Track track) {
        mTrackTitleTextView.setText(track.getTitle());
        mArtistNameTextView.setText(track.getArtistName());
        Glide.with(getApplicationContext())
                .load(Common.getBigImageUrl(track.getArtworkUrl()))
                .apply(new RequestOptions().error(R.drawable.ic_music_player))
                .into(mArtworkImageView);
        mTotalDurationTextView.setText(Common.convertTime(track.getDuration()));
        mSeekBar.setMax(track.getDuration());
        if (track.isDownloadable()) {
            mDownloadImageView.setVisibility(View.VISIBLE);
        } else {
            mDownloadImageView.setVisibility(View.INVISIBLE);
        }
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
    public void setLoopType(int loopType) {
        switch (loopType) {
            case LoopType.LOOP_ALL:
                mLoopImageView.setImageDrawable(getDrawable(R.drawable.ic_loop));
                mLoopOneTextView.setVisibility(View.INVISIBLE);
                break;
            case LoopType.LOOP_ONE:
                mLoopImageView.setImageDrawable(getDrawable(R.drawable.ic_loop));
                mLoopOneTextView.setVisibility(View.VISIBLE);
                break;
            case LoopType.NO_LOOP:
                mLoopImageView.setImageDrawable(getDrawable(R.drawable.ic_no_loop));
                mLoopOneTextView.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onShuffleStateChange(boolean isShuffle) {
        if (isShuffle) {
            mShuffleImageView.setImageDrawable(getDrawable(R.drawable.ic_shuffle));
        } else {
            mShuffleImageView.setImageDrawable(getDrawable(R.drawable.ic_shuffle_disable));
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
            case R.id.imageViewSkipPrevious:
                onMediaPlayerStateChange(false);
                mTrackService.skipPrevious();
                break;
            case R.id.imageViewSkipNext:
                onMediaPlayerStateChange(false);
                mTrackService.skipNext();
                break;
            case R.id.imageViewLoop:
                mTrackService.changeLoopType();
                break;
            case R.id.imageViewShuffle:
                mTrackService.shuffleTracks();
                break;
            case R.id.imageViewDownloadTrack:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                DOWNLOAD_REQUEST_CODE);
                    } else {
                        downloadTrack();
                    }
                } else {
                    downloadTrack();
                }
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

    private void downloadTrack() {
        Track track = mTrackService.getCurrentTrack();
        if (track == null) {
            return;
        }
        DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(track.getStreamUrl());
        manager.enqueue(new DownloadManager.Request(uri).setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,
                        track.getTitle() + MP3_EXTENSION)
                .setDescription(track.getTitle()));
    }
}
