package com.framgia.music_50.screen.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
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
import com.framgia.music_50.screen.library.adapter.LocalTracksAdapter;
import com.framgia.music_50.screen.play.PlayActivity;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.List;
import java.util.Objects;

public class LibraryFragment extends BaseFragment
        implements LibraryContract.View, OnItemRecyclerViewClickListener<Track>,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = LibraryFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 111;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LibraryPresenter mLibraryPresenter;
    private LocalTracksAdapter mLocalTracksAdapter;
    private List<Track> mTracks;

    public static LibraryFragment newInstance() {
        LibraryFragment libraryFragment = new LibraryFragment();
        Bundle args = new Bundle();
        libraryFragment.setArguments(args);
        return libraryFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        initView(view);
        initData();
        initListener();
        return view;
    }

    @Override
    public void initView(View view) {
        RecyclerView localTracksRecyclerView = view.findViewById(R.id.recyclerViewLocalTracks);
        localTracksRecyclerView.setNestedScrollingEnabled(false);
        localTracksRecyclerView.setHasFixedSize(true);
        mLocalTracksAdapter = new LocalTracksAdapter();
        mLocalTracksAdapter.setOnItemClickListener(this);
        localTracksRecyclerView.setAdapter(mLocalTracksAdapter);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mLibraryPresenter.getLocalTracks(
                    Objects.requireNonNull(getContext()).getContentResolver());
        }
    }

    @Override
    public void initData() {
        TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
        TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
        TrackRepository trackRepository =
                TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
        mLibraryPresenter = new LibraryPresenter(trackRepository);
        mLibraryPresenter.setView(this);
        if (checkPermission()) {
            mLibraryPresenter.getLocalTracks(
                    Objects.requireNonNull(getContext()).getContentResolver());
        }
    }

    @Override
    public void initListener() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onGetLocalTracksSuccess(List<Track> tracks) {
        mTracks = tracks;
        mLocalTracksAdapter.updateData(tracks);
    }

    @Override
    public void onGetLocalTracksError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermission() {
        if (getContext() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                            PERMISSION_REQUEST_CODE);
                    return false;
                }
                return true;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemClickListener(Track track) {
        startActivity(PlayActivity.getIntent(getContext(), track));
        if (getActivity() != null) {
            Intent intent =
                    TrackService.getServiceIntent(getContext(), mTracks, mTracks.indexOf(track));
            getActivity().startService(intent);
        }
    }

    @Override
    public void onRefresh() {
        if (checkPermission()) {
            mLibraryPresenter.getLocalTracks(
                    Objects.requireNonNull(getContext()).getContentResolver());
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
