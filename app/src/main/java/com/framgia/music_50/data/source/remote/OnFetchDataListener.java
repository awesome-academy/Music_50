package com.framgia.music_50.data.source.remote;

import java.util.List;

public interface OnFetchDataListener<T> {

    void onSuccess(List<T> data);

    void onError(Exception e);
}
