package com.framgia.music_50.screen.main;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.BaseActivity;
import com.framgia.music_50.screen.audio.AudioFragment;
import com.framgia.music_50.screen.home.HomeFragment;
import com.framgia.music_50.screen.home.adapter.OnAttachedPlaylistFragment;
import com.framgia.music_50.screen.library.LibraryFragment;
import com.framgia.music_50.screen.play.PlayActivity;
import com.framgia.music_50.screen.playlist.PlaylistFragment;
import com.framgia.music_50.screen.search.SearchFragment;
import com.framgia.music_50.service.ServiceContract;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.Navigator;

public class MainActivity extends BaseActivity
        implements MainContract.View, BottomNavigationView.OnNavigationItemSelectedListener,
        OnAttachedPlaylistFragment, ServiceContract.OnMiniControllerChange, View.OnClickListener,
        View.OnTouchListener {
    private static final int TIME_UPDATE_SEEK_BAR = 100;
    private BottomNavigationView mBottomNavigationView;
    private ImageView mArtworkImageView;
    private ImageView mSkipPreviousImageView;
    private ImageView mSkipNextImageView;
    private ImageView mPlayPauseImageView;
    private TextView mArtistNameTextView;
    private TextView mTrackTitleTextView;
    private SeekBar mMiniSeekBar;
    private View mMiniControllerView;
    private Runnable mRunnable;
    private Handler mHandler;
    private Navigator mNavigator;
    private boolean mIsBound;
    private TrackService mTrackService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            TrackService.TrackBinder trackBinder = (TrackService.TrackBinder) binder;
            mTrackService = trackBinder.getService();
            mTrackService.setOnMiniControllerChange(MainActivity.this);
            if (mTrackService.isMediaPlayerPlaying()) {
                mMiniControllerView.setVisibility(View.VISIBLE);
            } else {
                mMiniControllerView.setVisibility(View.GONE);
            }
            onMediaPlayerStateChange(mTrackService.isPlaying());
            updateSeekBar();
            mIsBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsBound = false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initListener();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof PlaylistFragment) {
            PlaylistFragment playlistFragment = (PlaylistFragment) fragment;
            playlistFragment.setOnAttachedPlaylistFragment(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mIsBound) {
            Intent intent = new Intent(MainActivity.this, TrackService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        mTrackService.setOnMiniControllerChange(null);
    }

    @Override
    public void initView() {
        mBottomNavigationView = findViewById(R.id.bottomNavigationView);
        mArtistNameTextView = findViewById(R.id.textViewArtistNameMini);
        mTrackTitleTextView = findViewById(R.id.textViewTrackTitleMini);
        mArtworkImageView = findViewById(R.id.imageViewArtworkMini);
        mSkipPreviousImageView = findViewById(R.id.imageViewSkipPreviousMini);
        mSkipNextImageView = findViewById(R.id.imageViewSkipNextMini);
        mPlayPauseImageView = findViewById(R.id.imageViewPlayPauseMini);
        mMiniControllerView = findViewById(R.id.layoutMiniController);
        mMiniSeekBar = findViewById(R.id.seekBarMiniController);
        mTrackTitleTextView.setSelected(true);
    }

    @Override
    public void initData() {
        mHandler = new Handler();
        mNavigator = new Navigator(this);
        mNavigator.goNextChildFragment(getSupportFragmentManager(), R.id.layoutContainer,
                HomeFragment.newInstance(), false, HomeFragment.TAG);
    }

    @Override
    public void initListener() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
        mMiniSeekBar.setOnTouchListener(this);
        mSkipNextImageView.setOnClickListener(this);
        mSkipPreviousImageView.setOnClickListener(this);
        mPlayPauseImageView.setOnClickListener(this);
        mMiniControllerView.setOnClickListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actionHome:
                mNavigator.goNextChildFragment(getSupportFragmentManager(), R.id.layoutContainer,
                        HomeFragment.newInstance(), false, HomeFragment.TAG);
                return true;
            case R.id.actionAudio:
                mNavigator.goNextChildFragment(getSupportFragmentManager(), R.id.layoutContainer,
                        AudioFragment.newInstance(), false, AudioFragment.TAG);
                return true;
            case R.id.actionSearch:
                mNavigator.goNextChildFragment(getSupportFragmentManager(), R.id.layoutContainer,
                        SearchFragment.newInstance(), false, SearchFragment.TAG);
                return true;
            case R.id.actionLibrary:
                mNavigator.goNextChildFragment(getSupportFragmentManager(), R.id.layoutContainer,
                        LibraryFragment.newInstance(), false, LibraryFragment.TAG);
                return true;
        }
        return false;
    }

    @Override
    public void setBottomNavigationVisibility(boolean isVisible) {
        if (isVisible) {
            mBottomNavigationView.setVisibility(View.VISIBLE);
        } else {
            mBottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTrackChange(Track track) {
        if (track != null) {
            updateUI(track);
        }
    }

    @Override
    public void onMediaPlayerStateChange(boolean isPlaying) {
        if (isPlaying) {
            mPlayPauseImageView.setImageDrawable(getDrawable(R.drawable.ic_pause));
        } else {
            mPlayPauseImageView.setImageDrawable(getDrawable(R.drawable.ic_play));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewSkipNextMini:
                onMediaPlayerStateChange(false);
                mTrackService.skipNext();
                break;
            case R.id.imageViewPlayPauseMini:
                mTrackService.playPauseTrack();
                break;
            case R.id.imageViewSkipPreviousMini:
                onMediaPlayerStateChange(false);
                mTrackService.skipPrevious();
                break;
            case R.id.layoutMiniController:
                startActivity(
                        PlayActivity.getIntent(MainActivity.this, mTrackService.getCurrentTrack()));
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private void updateSeekBar() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mMiniSeekBar.setProgress(mTrackService.getCurrentDuration());
                mHandler.postDelayed(mRunnable, TIME_UPDATE_SEEK_BAR);
            }
        };
        mRunnable.run();
    }

    private void updateUI(Track track) {
        mTrackTitleTextView.setText(track.getTitle());
        mArtistNameTextView.setText(track.getArtistName());
        Glide.with(getApplicationContext())
                .load((track.getArtworkUrl()))
                .apply(new RequestOptions().error(R.drawable.ic_music_player))
                .into(mArtworkImageView);
        mMiniSeekBar.setMax(track.getDuration());
    }
}
