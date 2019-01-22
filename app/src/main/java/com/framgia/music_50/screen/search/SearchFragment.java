package com.framgia.music_50.screen.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.framgia.music_50.R;
import com.framgia.music_50.data.model.Track;
import com.framgia.music_50.data.repository.TrackRepository;
import com.framgia.music_50.data.source.local.TrackLocalDataSource;
import com.framgia.music_50.data.source.remote.TrackRemoteDataSource;
import com.framgia.music_50.screen.BaseFragment;
import com.framgia.music_50.screen.play.PlayActivity;
import com.framgia.music_50.screen.search.adapter.SearchAdapter;
import com.framgia.music_50.service.TrackService;
import com.framgia.music_50.utils.OnItemRecyclerViewClickListener;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends BaseFragment
        implements SearchContract.View, TextWatcher, OnItemRecyclerViewClickListener<Track>,
        TextView.OnEditorActionListener, View.OnClickListener {
    public static final String TAG = SearchFragment.class.getSimpleName();
    private static final int DEFAULT_VALUE = 0;
    private static final int ANIMATION_TIME = 1000;
    private static final float TENSION_VALUE = 1.0f;
    private ImageView mSearchImageView;
    private EditText mSearchEditText;
    private ConstraintLayout mSearchConstraintLayout;
    private Group mSearchTextGroup;
    private SearchPresenter mSearchPresenter;
    private ConstraintSet mConstraintSet;
    private SearchAdapter mSearchAdapter;
    private List<Track> mTracks;

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
        mSearchImageView = view.findViewById(R.id.imageViewSearch);
        mSearchEditText = view.findViewById(R.id.editTextSearch);
        mSearchConstraintLayout = view.findViewById(R.id.layoutConstraintSearch);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewSearchResult);
        recyclerView.setHasFixedSize(true);
        mSearchAdapter = new SearchAdapter();
        mSearchAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(mSearchAdapter);
        mSearchTextGroup = view.findViewById(R.id.groupSearchText);
        mSearchTextGroup.setVisibility(View.VISIBLE);
    }

    @Override
    public void initData() {
        mConstraintSet = new ConstraintSet();
        TrackRemoteDataSource trackRemoteDataSource = TrackRemoteDataSource.getInstance();
        TrackLocalDataSource trackLocalDataSource = TrackLocalDataSource.getInstance();
        TrackRepository repository =
                TrackRepository.getInstance(trackRemoteDataSource, trackLocalDataSource);
        mSearchPresenter = new SearchPresenter(repository);
        mSearchPresenter.setView(this);
    }

    @Override
    public void initListener() {
        mSearchEditText.addTextChangedListener(this);
        mSearchEditText.setOnEditorActionListener(this);
        mSearchImageView.setOnClickListener(this);
    }

    @Override
    public void onSearchTrackSuccess(List<Track> tracks) {
        if (tracks != null) {
            mSearchAdapter.updateData(tracks);
            setSearchTextGroupVisible(tracks.size() == DEFAULT_VALUE);
            mTracks = tracks;
        }
    }

    @Override
    public void onSearchTrackError(Exception e) {
        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mSearchEditText.getText().length() > DEFAULT_VALUE) {
            showFocusedLayout();
        } else {
            hideFocusedLayout();
        }
        setSearchTextGroupVisible(mTracks == null || mTracks.size() == DEFAULT_VALUE);
    }

    @Override
    public void onItemClickListener(Track track) {
        List<Track> tracks = new ArrayList<>();
        tracks.add(track);
        startActivity(PlayActivity.getIntent(getContext(), track));
        if (getActivity() != null) {
            Intent intent =
                    TrackService.getServiceIntent(getContext(), tracks, tracks.indexOf(track));
            getActivity().startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        searchTrack();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            searchTrack();
            return true;
        }
        return false;
    }

    private void searchTrack(){
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchEditText.getWindowToken(), DEFAULT_VALUE);
        String keyword = mSearchEditText.getText().toString();
        if (!keyword.equals("")) {
            mSearchPresenter.querySearch(keyword);
        }
        mSearchEditText.clearFocus();
    }

    private void showFocusedLayout() {
        mConstraintSet.clone(getContext(), R.layout.fragment_search_focused_edit_text);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(TENSION_VALUE));
        transition.setDuration(ANIMATION_TIME);
        TransitionManager.beginDelayedTransition(mSearchConstraintLayout, transition);
        mConstraintSet.applyTo(mSearchConstraintLayout);
    }

    private void hideFocusedLayout() {
        mConstraintSet.clone(getContext(), R.layout.fragment_search);
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(TENSION_VALUE));
        transition.setDuration(ANIMATION_TIME);
        TransitionManager.beginDelayedTransition(mSearchConstraintLayout, transition);
        mConstraintSet.applyTo(mSearchConstraintLayout);
    }

    private void setSearchTextGroupVisible(boolean isVisible) {
        if (isVisible) {
            mSearchTextGroup.setVisibility(View.VISIBLE);
        } else {
            mSearchTextGroup.setVisibility(View.GONE);
        }
    }
}
