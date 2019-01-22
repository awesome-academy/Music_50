package com.framgia.music_50.utils;

import android.support.annotation.IntDef;

@IntDef({ QueryType.GET_TRACKS, QueryType.SEARCH_TRACK })

public @interface QueryType {
    int GET_TRACKS = 0;
    int SEARCH_TRACK = 1;
}
