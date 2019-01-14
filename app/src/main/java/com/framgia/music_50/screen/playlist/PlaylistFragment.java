package com.framgia.music_50.screen.playlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Genre;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;
import com.framgia.music_50.screen.BaseFragment;
import java.util.List;

public class PlaylistFragment extends BaseFragment implements PlaylistContract.View {
    public static final String TAG = PlaylistFragment.class.getSimpleName();
    private static final String EXTRA_GENRE_KEY = "EXTRA_GENRE_KEY";

    public static PlaylistFragment newInstance(Genre genre) {
        PlaylistFragment playlistFragment = new PlaylistFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_GENRE_KEY, genre);
        playlistFragment.setArguments(args);
        return playlistFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void initView(View view) {
    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            Genre genre = getArguments().getParcelable(EXTRA_GENRE_KEY);
            TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
            TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
            TrackRepository repository =
                    TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
            PlaylistPresenter playlistPresenter = new PlaylistPresenter(repository);
            playlistPresenter.setView(this);
            playlistPresenter.getTracksByGenre(genre != null ? genre.getGenreType() : null);
        }
    }

    @Override
    public void initListener() {
    }

    @Override
    public void onGetTracksByGenreSuccess(List<Track> tracks) {
    }

    @Override
    public void onGetTracksByGenreError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
