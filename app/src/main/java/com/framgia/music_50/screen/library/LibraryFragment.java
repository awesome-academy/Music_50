package com.framgia.music_50.screen.library;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music_50.R;
import com.framgia.music_50.screen.audio.AudioFragment;

public class LibraryFragment extends Fragment {
    public static final String TAG = LibraryFragment.class.getSimpleName();

    public static LibraryFragment newInstance(){
        LibraryFragment libraryFragment = new LibraryFragment();
        Bundle args = new Bundle();
        libraryFragment.setArguments(args);
        return libraryFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_library, container, false);
    }
}
