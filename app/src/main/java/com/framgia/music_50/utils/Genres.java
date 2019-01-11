package com.framgia.music_50.utils;

import android.support.annotation.StringDef;

@StringDef({
        Genres.ALTERNATIVE_ROCK, Genres.AMBIENT, Genres.CLASSICAL, Genres.COUNTRY, Genres.ALL_MUSIC,
        Genres.ALL_AUDIO, Genres.ALTERNATIVE_ROCK_TITLE, Genres.AMBIENT_TITLE,
        Genres.CLASSICAL_TITLE, Genres.COUNTRY_TITLE
})

public @interface Genres {
    String ALTERNATIVE_ROCK = "alternativerock";
    String ALTERNATIVE_ROCK_TITLE = "Alternative Rock";
    String AMBIENT = "ambient";
    String AMBIENT_TITLE = "Ambient";
    String CLASSICAL = "classical";
    String CLASSICAL_TITLE = "Classical";
    String COUNTRY = "country";
    String COUNTRY_TITLE = "Country";
    String ALL_MUSIC = "all-music";
    String ALL_AUDIO = "all-audio";
}
