package dev.bebomny.youtubevideodownloader.download.tag;

/**
 * Represents the video quality of a video channel.
 */
public enum VideoQuality {

    p3072("3072p", 0, 3072), //unknown TODO
    p2304("2304p", 0, 2304), //unknown TODO
    p2160("2160p", 4096, 2160),
    p1440("1440p", 2560, 1440),
    p1080("1080p", 1920, 1080),
    p720("720p", 1280, 720),
    p520("520p", 0, 520), //unknown TODO
    p480("480p", 854, 480),
    p360("360p", 640, 360),
    p270("270p", 0, 270), //unknown TODO
    p240("240p", 426, 240),
    p224("224p", 0, 224), //unknown TODO
    p144("144p", 256, 144),
    p72("72p", 0, 72); //unknown TODO

    private final String displayName;
    private final int resolutionWidth;
    private final int resolutionHeight;

    VideoQuality(String displayName, int width, int height) {
        this.displayName = displayName;
        this.resolutionWidth = width;
        this.resolutionHeight = height;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getResolution() {
        return getResolutionWidth() + "x" + getResolutionHeight();
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }
}
