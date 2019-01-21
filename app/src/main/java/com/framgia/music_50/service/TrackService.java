package com.framgia.music_50.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.screen.main.MainActivity;
import com.framgia.music_50.utils.LoopType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackService extends Service
        implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private final static String EXTRA_TRACK_LIST = "EXTRA_TRACK_LIST";
    private final static String EXTRA_TRACK_POSITION = "EXTRA_TRACK_POSITION";
    private final static String CHANNEL_NAME = "Y Music";
    private final static String CHANNEL_ID = "CHANNEL_ID";
    private final static String ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE";
    private final static String ACTION_SKIP_NEXT = "ACTION_SKIP_NEXT";
    private final static String ACTION_SKIP_PREVIOUS = "ACTION_SKIP_PREVIOUS";
    private final static int NOTIFICATION_ID = 2019;
    private final static int PENDING_REQUEST_CODE = 0;
    private final int DEFAULT_POSITION = 0;
    private final int DEFAULT_VALUE_ONE = 1;
    private List<Track> mTracks;
    private List<Track> mShuffleTracks;
    private List<Track> mTemporaryTracks;
    private int mPosition;
    private MediaPlayer mMediaPlayer;
    private ServiceContract.OnMediaPlayerChange mOnMediaPlayerChange;
    private ServiceContract.OnMiniControllerChange mOnMiniControllerChange;
    private TrackBinder mTrackBinder;
    private int mLoopType;
    private boolean mIsShuffle;
    private RemoteViews mRemoteViews;
    private NotificationTarget mArtworkTarget;
    private Notification mNotification;

    public static Intent getServiceIntent(Context context, List<Track> tracks, int position) {
        Intent intent = new Intent(context, TrackService.class);
        intent.putParcelableArrayListExtra(EXTRA_TRACK_LIST,
                (ArrayList<? extends Parcelable>) tracks);
        intent.putExtra(EXTRA_TRACK_POSITION, position);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTrackBinder = new TrackBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            List<Track> tracks = intent.getParcelableArrayListExtra(EXTRA_TRACK_LIST);
            if (tracks != null) {
                if (mNotification == null) {
                    createNotification();
                }
                mPosition = intent.getIntExtra(EXTRA_TRACK_POSITION, DEFAULT_POSITION);
                mTracks = tracks;
                mLoopType = LoopType.LOOP_ALL;
                mIsShuffle = false;
                mShuffleTracks = new ArrayList<>(mTracks);
                mTemporaryTracks = new ArrayList<>(mTracks);
                Collections.shuffle(mShuffleTracks);
                play();
            }
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_SKIP_PREVIOUS:
                        updatePlayPauseNotification(false);
                        skipPrevious();
                        break;
                    case ACTION_PLAY_PAUSE:
                        playPauseTrack();
                        break;
                    case ACTION_SKIP_NEXT:
                        updatePlayPauseNotification(false);
                        skipNext();
                        break;
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTrackBinder;
    }

    public void setOnMediaChangeListener(ServiceContract.OnMediaPlayerChange mediaChangeListener) {
        mOnMediaPlayerChange = mediaChangeListener;
    }

    public void setOnMiniControllerChange(
            ServiceContract.OnMiniControllerChange miniControllerChange) {
        mOnMiniControllerChange = miniControllerChange;
    }

    public void play() {
        Track track = mTracks.get(mPosition);
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(track.getStreamUrl());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(this);
        if (mOnMediaPlayerChange != null) {
            mOnMediaPlayerChange.onTrackChange(track);
        }
        if (mOnMiniControllerChange != null) {
            mOnMiniControllerChange.onTrackChange(track);
        }
        updateNotificationUI(track);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.setOnCompletionListener(this);
        if (mOnMediaPlayerChange != null) {
            mOnMediaPlayerChange.onMediaPlayerStart();
            mOnMediaPlayerChange.onMediaPlayerStateChange(true);
        }
        if (mOnMiniControllerChange != null) {
            mOnMiniControllerChange.onMediaPlayerStateChange(true);
        }
        updatePlayPauseNotification(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mOnMediaPlayerChange != null) {
            mOnMediaPlayerChange.onMediaPlayerStateChange(false);
        }
        if (mOnMiniControllerChange != null) {
            mOnMiniControllerChange.onMediaPlayerStateChange(false);
        }
        switch (mLoopType) {
            case LoopType.LOOP_ALL:
                skipNext();
                break;
            case LoopType.LOOP_ONE:
                play();
                break;
            case LoopType.NO_LOOP:
                if (mPosition != mTracks.size() - DEFAULT_VALUE_ONE) {
                    skipNext();
                }
                break;
        }
    }

    public int getCurrentDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : DEFAULT_POSITION;
    }

    public void playPauseTrack() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            if (mOnMediaPlayerChange != null) {
                mOnMediaPlayerChange.onMediaPlayerStateChange(false);
            }
            if (mOnMiniControllerChange != null) {
                mOnMiniControllerChange.onMediaPlayerStateChange(false);
            }
        } else {
            mMediaPlayer.start();
            if (mOnMediaPlayerChange != null) {
                mOnMediaPlayerChange.onMediaPlayerStateChange(true);
            }
            if (mOnMiniControllerChange != null) {
                mOnMiniControllerChange.onMediaPlayerStateChange(true);
            }
        }
        updatePlayPauseNotification(mMediaPlayer.isPlaying());
    }

    public void seekTo(int duration) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(duration);
        }
    }

    public void skipPrevious() {
        mPosition--;
        if (mPosition < DEFAULT_POSITION) {
            mPosition = mTracks.size() - DEFAULT_VALUE_ONE;
        }
        play();
    }

    public void skipNext() {
        mPosition++;
        if (mPosition == mTracks.size()) {
            mPosition = DEFAULT_POSITION;
        }
        play();
    }

    public void changeLoopType() {
        switch (mLoopType) {
            case LoopType.LOOP_ALL:
                mLoopType = LoopType.LOOP_ONE;
                break;
            case LoopType.LOOP_ONE:
                mLoopType = LoopType.NO_LOOP;
                break;
            case LoopType.NO_LOOP:
                mLoopType = LoopType.LOOP_ALL;
                break;
        }
        if (mOnMediaPlayerChange != null) {
            mOnMediaPlayerChange.setLoopType(mLoopType);
        }
    }

    public void shuffleTracks() {
        mIsShuffle = !mIsShuffle;
        int newPosition;
        if (mIsShuffle) {
            newPosition = mShuffleTracks.indexOf(mTracks.get(mPosition));
            mTracks.clear();
            mTracks.addAll(mShuffleTracks);
            mPosition = newPosition;
        } else {
            newPosition = mTemporaryTracks.indexOf(mShuffleTracks.get(mPosition));
            mTracks.clear();
            mTracks.addAll(mTemporaryTracks);
            mPosition = newPosition;
        }
        if (mOnMediaPlayerChange != null) {
            mOnMediaPlayerChange.onShuffleStateChange(mIsShuffle);
        }
    }

    public Track getCurrentTrack() {
        return mTracks.get(mPosition);
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    public boolean isMediaPlayerPlaying() {
        return mTracks != null;
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setSound(null, null);
            channel.enableVibration(false);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_controller);
        initNotificationAction();
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, PENDING_REQUEST_CODE, mainActivityIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification =
                new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.ic_music)
                        .setCustomContentView(mRemoteViews)
                        .setCustomBigContentView(mRemoteViews)
                        .setContentIntent(pendingIntent)
                        .build();
        mArtworkTarget =
                new NotificationTarget(getApplicationContext(), R.id.imageViewArtistNotification,
                        mRemoteViews, mNotification, NOTIFICATION_ID);
        startForeground(NOTIFICATION_ID, mNotification);
    }

    private void initNotificationAction() {
        createAction(ACTION_PLAY_PAUSE, R.id.imageViewPlayPauseNotification);
        createAction(ACTION_SKIP_NEXT, R.id.imageViewSkipNextNotification);
        createAction(ACTION_SKIP_PREVIOUS, R.id.imageViewSkipPreviousNotification);
    }

    private void createAction(String action, int viewId) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.setClass(getApplicationContext(), TrackService.class);
        PendingIntent playPausePendingIntent =
                PendingIntent.getService(getApplicationContext(), NOTIFICATION_ID, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(viewId, playPausePendingIntent);
    }

    private void updateNotificationUI(Track track) {
        mRemoteViews.setTextViewText(R.id.textViewTrackTitleNotification, track.getTitle());
        mRemoteViews.setTextViewText(R.id.textViewArtistNameNotification, track.getArtistName());
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(track.getArtworkUrl())
                .into(mArtworkTarget);
    }

    private void updatePlayPauseNotification(boolean isPlaying) {
        if (isPlaying) {
            mRemoteViews.setImageViewResource(R.id.imageViewPlayPauseNotification,
                    R.drawable.ic_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.imageViewPlayPauseNotification,
                    R.drawable.ic_play);
        }
        startForeground(NOTIFICATION_ID, mNotification);
    }

    public class TrackBinder extends Binder {
        public TrackService getService() {
            return TrackService.this;
        }
    }
}
