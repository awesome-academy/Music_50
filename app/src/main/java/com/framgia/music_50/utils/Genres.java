package com.framgia.music_50.utils;

import android.support.annotation.StringDef;

@StringDef({
        Genres.ALTERNATIVE_ROCK, Genres.AMBIENT, Genres.CLASSICAL, Genres.COUNTRY,Genres.ALL_MUSIC,
        Genres.ALL_AUDIO
})

public @interface Genres {
    String ALTERNATIVE_ROCK = "alternativerock";
    String AMBIENT = "ambient";
    String CLASSICAL = "classical";
    String COUNTRY = "country";
    String ALL_MUSIC = "all-music";
    String ALL_AUDIO = "all-audio";
}
