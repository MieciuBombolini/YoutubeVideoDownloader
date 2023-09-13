package dev.bebomny.youtubevideodownloader.downloader.tag;

/**
 * Returns the frames per second of a video stream.
 */
public enum FPS {

    f25(25, "25fps"),
    f30(30, "30fps"),
    f50(50, "50fps"),
    f60(60, "60fps");

    private final String displayName;
    private final int fps;

    FPS(int fps, String displayName) {
        this.fps = fps;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getAsInt() {
        return fps;
    }
}
