package com.framgia.music_50.screen.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;
import com.framgia.music_50.screen.BaseFragment;
import java.util.List;

public class SearchFragment extends BaseFragment implements SearchContract.View {
    public static final String TAG = SearchFragment.class.getSimpleName();
    private SearchPresenter mSearchPresenter;

    public static SearchFragment newInstance() {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
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
        TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
        TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
        TrackRepository repository =
                TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
        mSearchPresenter = new SearchPresenter(repository);
        mSearchPresenter.setView(this);
    }

    @Override
    public void initListener() {
    }

    @Override
    public void onSearchTrackSuccess(List<Track> tracks) {
    }

    @Override
    public void onSearchTrackError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
