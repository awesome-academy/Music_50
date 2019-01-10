package com.framgia.music_50.screen.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.framgia.music_50.R;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;

public class HomeFragment extends Fragment implements HomeContract.View {
    public static final String TAG = HomeFragment.class.getSimpleName();

    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        homeFragment.setArguments(args);
        return homeFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    private void initView(View view) {
    }

    private void initData() {
        TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
        TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
        TrackRepository repository =
                TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
        HomeContract.Presenter presenter = new HomePresenter(repository);
        presenter.setView(this);
        presenter.getTrendingTracks();
    }

    private void initListener() {
    }
}
