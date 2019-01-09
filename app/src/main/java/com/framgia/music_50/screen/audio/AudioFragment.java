package com.framgia.music_50.screen.audio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music_50.R;

public class AudioFragment extends Fragment {
    public static final String TAG = AudioFragment.class.getSimpleName();

    public static AudioFragment newInstance(){
        AudioFragment audioFragment = new AudioFragment();
        Bundle args = new Bundle();
        audioFragment.setArguments(args);
        return audioFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }
}
