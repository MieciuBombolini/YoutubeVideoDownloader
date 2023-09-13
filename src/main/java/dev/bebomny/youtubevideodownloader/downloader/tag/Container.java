package dev.bebomny.youtubevideodownloader.downloader.tag;

/**
 * Represents the file format of a stream.
 */
public enum Container {

    FLV("FLV", ".flv"),
    GP3("GP3", ".gp3"),
    MP4("MP4", ".mp4"),
    M4A("M4A", ".m4a"),
    WEBM("WEBM", ".webm");

    private final String name;
    private final String format;

    Container(String name, String format) {
        this.name = name;
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return getName().toLowerCase();
    }

    public String getFormat() {
        return format;
    }
}
