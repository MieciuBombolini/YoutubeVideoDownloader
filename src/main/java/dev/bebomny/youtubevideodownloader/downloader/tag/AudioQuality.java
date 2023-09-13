package dev.bebomny.youtubevideodownloader.downloader.tag;

/**
 * Represents the audio quality of an audio channel.
 */
public enum AudioQuality {

    k256("k256", "K256"),
    k192("k192", "K192"),
    k160("k160", "K160"),
    k128("k128", "K128"),
    k96("k96", "K96"),
    k70("k70", "K70"),
    k64("k64", "K64"),
    k50("k50", "K50"),
    k48("k48", "K48"),
    k36("k36", "K36"),
    k24("k24", "K24");

    private final String name;
    private final String displayName;

    AudioQuality(String name, String displayName) {
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
