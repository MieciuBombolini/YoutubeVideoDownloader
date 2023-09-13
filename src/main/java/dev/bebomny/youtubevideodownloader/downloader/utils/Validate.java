package dev.bebomny.youtubevideodownloader.downloader.utils;

public class Validate {

    public static void notNull(Object object) {
        notNull(object, "The validated object is null");
    }

    public static void notNull(String message, Object... objects) {
        for(Object object : objects) {
            notNull(object, message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
