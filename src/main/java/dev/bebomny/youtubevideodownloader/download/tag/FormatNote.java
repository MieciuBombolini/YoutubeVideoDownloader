package dev.bebomny.youtubevideodownloader.download.tag;

/**
 * Represents the format note of the stream. Some streams don't have format note.
 */
public enum FormatNote {

    NONE("NONE", "None"),
    THREE_DIMENSIONAL("THREE_DIMENSIONAL", "Three Dimensional"),
    HLS("HLS", "HLS"),
    DASH("DASH", "DASH");

    private final String name;
    private final String displayName;

    FormatNote(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

}
