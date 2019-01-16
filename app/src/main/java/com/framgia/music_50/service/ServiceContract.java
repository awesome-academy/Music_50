package com.framgia.music_50.service;

import com.framgia.music_50.data.model.Track;

public interface ServiceContract {
    interface OnMediaPlayerChange{
        void onTrackChange(Track track);

        void onMediaPlayerStart();

        void onMediaPlayerStateChange(boolean isPlaying);
    }

    interface OnMiniControllerChange{
    }
}
