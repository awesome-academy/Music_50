package com.framgia.music_50.utils;

import java.util.concurrent.TimeUnit;

public class Common {
    private static final String TIME_FORMAT = "%02d:%02d";
    private static final String DEFAULT_IMAGE_FORMAT = "-large";
    private static final String BIG_IMAGE_FORMAT = "-t500x500";

    public static String convertTime(long millisecond) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisecond);
        long second =
                TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.valueOf(String.format(TIME_FORMAT, minutes, second));
    }

    public static String getBigImageUrl(String url) {
        return url != null ? url.replace(DEFAULT_IMAGE_FORMAT, BIG_IMAGE_FORMAT) : "";
    }
}
